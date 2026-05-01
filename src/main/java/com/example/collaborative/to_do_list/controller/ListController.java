package com.example.collaborative.to_do_list.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.collaborative.to_do_list.dto.list.ReorderListDto;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListRequest;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListResponse;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListResponseAndCount;
import com.example.collaborative.to_do_list.dto.list.ListDto.UpdateListRequest;
import com.example.collaborative.to_do_list.service.ListService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/list")
@AllArgsConstructor
public class ListController {

	private final ListService listService;

	@PostMapping("/createList")
	@ResponseStatus(HttpStatus.CREATED)
	public ListResponse createList(@Valid @RequestBody ListRequest listRequest, Authentication authentication) {
		return listService.createList(listRequest, authentication);
	}

	@GetMapping("/getLists")
	public ResponseEntity<List<ListResponseAndCount>> getLists(Authentication authentication) {
		List<ListResponseAndCount> listResponse = listService.getAllLists(authentication);
		return ResponseEntity.ok(listResponse);
	}

	@PutMapping("/editList/{id}")
	public ResponseEntity<ListResponse> editList(@PathVariable UUID id,
			@Valid @RequestBody UpdateListRequest updateListRequest, Authentication authentication) {
		ListResponse listResponse = listService.updateList(id, updateListRequest, authentication);
		return ResponseEntity.ok(listResponse);
	}

	@DeleteMapping("/deleteList/{id}")
	public ResponseEntity<Void> deleteList(@PathVariable UUID id, Authentication authentication) {
		listService.deleteList(id, authentication);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/reorderList")
	public ResponseEntity<Void> reorderList(@RequestBody List<ReorderListDto> reorderList,
			Authentication authentication) {
		listService.reorderList(reorderList, authentication);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/getList/{id}")
	public ResponseEntity<ListResponse> getList(@PathVariable UUID id, Authentication authentication) {
		ListResponse list = listService.getList(id, authentication);
		return ResponseEntity.ok(list);
	}

}
