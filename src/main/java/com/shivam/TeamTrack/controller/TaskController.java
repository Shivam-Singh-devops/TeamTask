package com.shivam.TeamTrack.controller;

import com.shivam.TeamTrack.dto.CreateTaskRequest;
import com.shivam.TeamTrack.dto.TaskResponse;
import com.shivam.TeamTrack.dto.UpdateTaskRequest;
import com.shivam.TeamTrack.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Create task in project
    @PostMapping("/projects/{projectId}")
    public ResponseEntity<TaskResponse> create(@PathVariable int projectId,
                                               @RequestBody CreateTaskRequest request,
                                               @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(projectId, request, principal));
    }

    // Get all tasks in project
    @GetMapping("/projects/{projectId}")
    public List<TaskResponse> getProjectTasks(@PathVariable int projectId,
                                              @AuthenticationPrincipal UserDetails principal) {
        return taskService.getProjectTasks(projectId, principal);
    }

    // Get task by id
    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable int id,
                                @AuthenticationPrincipal UserDetails principal) {
        return taskService.getById(id, principal);
    }

    // Update task
    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable int id,
                               @RequestBody UpdateTaskRequest request,
                               @AuthenticationPrincipal UserDetails principal) {
        return taskService.update(id, request, principal);
    }

    // Delete task (admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                       @AuthenticationPrincipal UserDetails principal) {
        taskService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    // Get tasks assigned to me
    @GetMapping("/assigned-to-me")
    public List<TaskResponse> getMyTasks(@AuthenticationPrincipal UserDetails principal) {
        return taskService.getMyTasks(principal);
    }

    // Get dashboard stats for project
    @GetMapping("/projects/{projectId}/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@PathVariable int projectId,
                                                                 @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(taskService.getDashboardStats(projectId, principal));
    }
}