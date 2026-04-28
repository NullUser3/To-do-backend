package com.example.collaborative.to_do_list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.collaborative.to_do_list.adapter.UserAdapter;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListRequest;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListResponse;
import com.example.collaborative.to_do_list.model.TaskList;
import com.example.collaborative.to_do_list.model.User;
import com.example.collaborative.to_do_list.repo.ListRepo;
import com.example.collaborative.to_do_list.repo.TaskRepo;
import com.example.collaborative.to_do_list.repo.UserRepo;
import com.example.collaborative.to_do_list.service.ListService;
import org.springframework.security.core.Authentication;


@ExtendWith(MockitoExtension.class)
class ListServiceTest {

    @Mock
    private ListRepo listRepo;

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ListService listService;

    @Test
    void shouldCreateListSuccessfully() {

    // given
    UUID userId = UUID.randomUUID();

    User user = User.builder()
    .id(userId)
    .build();

    UserAdapter authUser = mock(UserAdapter.class);
    when(authUser.getId()).thenReturn(userId);

    Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(authUser);

    ListRequest request = new ListRequest(
        null,
        "My List",
        "#FFFFFF",
        1,
        null
    );

    when(userRepo.findById(userId)).thenReturn(Optional.of(user));
    when(listRepo.findMaxPosition(userId)).thenReturn(Optional.of(0));

    TaskList savedList = TaskList.builder()
        .id(UUID.randomUUID())
        .name("My List")
        .color("#FFFFFF")
        .position(1)
        .createdBy(user)
        .build();

    when(listRepo.save(any(TaskList.class))).thenReturn(savedList);

    // when
    ListResponse response = listService.createList(request, auth);

    // then
    assertEquals("My List", response.name());
    assertEquals("#FFFFFF", response.color());
}

    
}