package com.example.collaborative.to_do_list.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
// import java.util.stream.Collector;
import java.util.stream.Collectors;

// import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.collaborative.to_do_list.adapter.UserAdapter;
import com.example.collaborative.to_do_list.dto.list.ReorderListDto;
// import com.example.collaborative.to_do_list.dto.task.TaskDto.TaskResponse;
// import com.example.collaborative.to_do_list.dto.list.ListDto.ListId;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListRequest;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListResponse;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListResponseAndCount;
import com.example.collaborative.to_do_list.dto.list.ListDto.UpdateListRequest;
import com.example.collaborative.to_do_list.exception.ResourceNotFoundException;
import com.example.collaborative.to_do_list.exception.UserNotFoundException;
import com.example.collaborative.to_do_list.model.TaskList;
// import com.example.collaborative.to_do_list.model.Task;
import com.example.collaborative.to_do_list.model.User;
import com.example.collaborative.to_do_list.repo.ListRepo;
// import com.example.collaborative.to_do_list.repo.TaskRepo;
import com.example.collaborative.to_do_list.repo.UserRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ListService {
    
    private final ListRepo listRepo;
    private final UserRepo userRepo;

    @Transactional
    public ListResponse createList(ListRequest listRequest,Authentication authentication){

        UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();
        User creator = userRepo.findById(userDetails.getId())
        .orElseThrow(() -> new UserNotFoundException(userDetails.getId()));

        int newPosition = listRepo.findMaxPosition(creator.getId()).orElse(0)+1;
        TaskList list = TaskList.builder()
        .name(listRequest.name())
        .color(listRequest.color())
        .position(newPosition)
        .createdBy(creator)
        .build();

        return ListResponse.from(listRepo.save(list));
    }

    @Transactional
    public List<ListResponseAndCount> getAllLists(Authentication authentication){
        UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();
        List<TaskList> lists =listRepo.findByCreatedByIdWithTasks(userDetails.getId());
        
        return lists.stream()
        .map(list -> ListResponseAndCount.from(
            list, 
            list.getTasks().size())
        )
        .collect(Collectors.toList());
    }

    @Transactional
    public ListResponse updateList(UUID id,UpdateListRequest updateListRequest,Authentication authentication){
        UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();

        TaskList list = listRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));

        if(!list.getCreatedBy().getId().equals(userDetails.getId())){
            throw new AccessDeniedException("You don't own this list.");
        }
        if (updateListRequest.name() != null) {
            list.setName(updateListRequest.name());
        }
        if (updateListRequest.color() != null) {
            list.setColor(updateListRequest.color());
        }

            list.setUpdatedAt(OffsetDateTime.now());
    
            return ListResponse.from(list);
    }

    @Transactional
    public void deleteList(UUID id,Authentication authentication){
        UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();
        TaskList list = listRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
        if(!list.getCreatedBy().getId().equals(userDetails.getId())){
            throw new AccessDeniedException("You don't own this list.");
        }

        listRepo.decrementPositionsGreaterThenPositionId(list.getCreatedBy().getId(), list.getPosition());
        
        listRepo.delete(list);
    }

    @Transactional
public void reorderList(List<ReorderListDto> dtos, Authentication authentication) {
    UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();

    for(ReorderListDto reorderList:dtos){
        TaskList list = listRepo.findById(reorderList.getId())
        .orElseThrow(() -> new ResourceNotFoundException(reorderList.getId()));

        if (!list.getCreatedBy().getId().equals(userDetails.getId())) {
            throw new AccessDeniedException("You do not own this list.");
        }

        list.setPosition(reorderList.getPosition());
    }

}

@Transactional
public ListResponse getList(UUID id,Authentication authentication){
    UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();

    TaskList list = listRepo.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException(id));

    if (!list.getCreatedBy().getId().equals(userDetails.getId())) {
            throw new AccessDeniedException("You do not own this list.");
        }
        return ListResponse.from(list);
}



}
