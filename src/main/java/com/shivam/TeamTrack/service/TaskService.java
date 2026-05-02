package com.shivam.TeamTrack.service;

import com.shivam.TeamTrack.dto.CreateTaskRequest;
import com.shivam.TeamTrack.dto.TaskResponse;
import com.shivam.TeamTrack.dto.UpdateTaskRequest;
import com.shivam.TeamTrack.model.Project;
import com.shivam.TeamTrack.model.Task;
import com.shivam.TeamTrack.model.User;
import com.shivam.TeamTrack.repo.ProjectRepo;
import com.shivam.TeamTrack.repo.TaskRepository;
import com.shivam.TeamTrack.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final ProjectRepo projectRepo;
    private final UserRepo userRepository;

    // Create task (admin of project only)
    @Transactional
    public TaskResponse create(int projectId, CreateTaskRequest request, UserDetails principal) {
        User currentUser = getCurrentUser(principal);

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Check if current user is admin of this project
        if (project.getAdmin().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Only project admin can create tasks");
        }

        // Get assigned user
        User assignedUser = userRepository.findById(request.assignedToUserId())
                .orElseThrow(() -> new IllegalArgumentException("Assigned user not found"));

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setProject(project);
        task.setAssignedPerson(assignedUser);
        task.setTaskStatus(Task.TaskStatus.TODO);
        task.setDueDate(request.dueDate());
        task.setCreatedDate(LocalDateTime.now());

        Task savedTask = taskRepo.save(task);
        return toResponse(savedTask);
    }

    // Get all tasks in project
    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(int projectId, UserDetails principal) {
        User currentUser = getCurrentUser(principal);

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Check if user is part of project
        boolean isMember = project.getMembers().stream()
                .anyMatch(pm -> pm.getUser().getId() == currentUser.getId());

        if (!isMember && project.getAdmin().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Not authorized to view project tasks");
        }

        return taskRepo.findByProject(project).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get single task
    @Transactional(readOnly = true)
    public TaskResponse getById(int id, UserDetails principal) {
        User currentUser = getCurrentUser(principal);

        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Check authorization
        Project project = task.getProject();
        boolean isMember = project.getMembers().stream()
                .anyMatch(pm -> pm.getUser().getId() == currentUser.getId());

        if (!isMember && project.getAdmin().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Not authorized to view this task");
        }

        return toResponse(task);
    }

    // Update task (admin or assigned user)
    @Transactional
    public TaskResponse update(int id, UpdateTaskRequest request, UserDetails principal) {
        User currentUser = getCurrentUser(principal);

        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Project project = task.getProject();

        // Check authorization: admin or assigned user
        boolean isAdmin = project.getAdmin().getId() == currentUser.getId();
        boolean isAssignedUser = task.getAssignedPerson().getId() == currentUser.getId();

        if (!isAdmin && !isAssignedUser) {
            throw new IllegalArgumentException("Not authorized to update this task");
        }

        // Only admin can change assignment
        if (request.assignedToUserId() != task.getAssignedPerson().getId() && !isAdmin) {
            throw new IllegalArgumentException("Only admin can reassign tasks");
        }

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setTaskStatus(request.status());
        task.setDueDate(request.dueDate());

        if (isAdmin && request.assignedToUserId() != task.getAssignedPerson().getId()) {
            User newAssignee = userRepository.findById(request.assignedToUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            task.setAssignedPerson(newAssignee);
        }

        Task updatedTask = taskRepo.save(task);
        return toResponse(updatedTask);
    }

    // Delete task (admin only)
    @Transactional
    public void delete(int id, UserDetails principal) {
        User currentUser = getCurrentUser(principal);

        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Project project = task.getProject();

        // Check if current user is admin
        if (project.getAdmin().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Only project admin can delete tasks");
        }

        taskRepo.delete(task);
    }

    // Get tasks assigned to current user
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks(UserDetails principal) {
        User currentUser = getCurrentUser(principal);

        return taskRepo.findByAssignedPerson(currentUser).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get dashboard stats
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats(int projectId, UserDetails principal) {
        User currentUser = getCurrentUser(principal);

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Check authorization
        boolean isMember = project.getMembers().stream()
                .anyMatch(pm -> pm.getUser().getId() == currentUser.getId());

        if (!isMember && project.getAdmin().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Not authorized to view project stats");
        }

        List<Task> tasks = taskRepo.findByProject(project);

        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(t -> t.getTaskStatus() == Task.TaskStatus.COMPLETED).count();
        int inProgressTasks = (int) tasks.stream().filter(t -> t.getTaskStatus() == Task.TaskStatus.IN_PROGRESS).count();
        int todoTasks = (int) tasks.stream().filter(t -> t.getTaskStatus() == Task.TaskStatus.TODO).count();
        int overdueTasks = (int) tasks.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDateTime.now())
                        && t.getTaskStatus() != Task.TaskStatus.COMPLETED)
                .count();
        int teamMembers = project.getMembers().size();

        return Map.of(
                "totalTasks", totalTasks,
                "completedTasks", completedTasks,
                "inProgressTasks", inProgressTasks,
                "todoTasks", todoTasks,
                "overdueTasks", overdueTasks,
                "teamMembers", teamMembers
        );
    }

    // Helper: Get current user from JWT token
    private User getCurrentUser(UserDetails principal) {
        String email = normalizeEmail(principal.getUsername());
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Helper: Normalize email
    private static String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // Helper: Convert Task to Response
    private TaskResponse toResponse(Task task) {
        boolean isOverdue = task.getDueDate() != null
                && task.getDueDate().isBefore(LocalDateTime.now())
                && task.getTaskStatus() != Task.TaskStatus.COMPLETED;

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getProject().getId(),
                task.getAssignedPerson().getId(),
                task.getAssignedPerson().getName(),
                task.getTaskStatus(),
                task.getDueDate(),
                task.getCreatedDate(),
                isOverdue
        );
    }
}