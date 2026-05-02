package com.shivam.TeamTrack.controller;

import com.shivam.TeamTrack.dto.AddProjectMemberRequest;
import com.shivam.TeamTrack.dto.ProjectCreateRequest;
import com.shivam.TeamTrack.dto.ProjectResponse;
import com.shivam.TeamTrack.dto.ProjectUpdateRequest;
import com.shivam.TeamTrack.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> list(@AuthenticationPrincipal UserDetails principal) {
        return projectService.listAccessibleProjects(principal);
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable int id,
                                   @AuthenticationPrincipal UserDetails principal) {
        return projectService.getById(id, principal);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@RequestBody ProjectCreateRequest request,
                                                  @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request, principal));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable int id,
                                  @RequestBody ProjectUpdateRequest request,
                                  @AuthenticationPrincipal UserDetails principal) {
        return projectService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                         @AuthenticationPrincipal UserDetails principal) {
        projectService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    public ProjectResponse addMember(@PathVariable int id,
                                     @RequestBody AddProjectMemberRequest request,
                                     @AuthenticationPrincipal UserDetails principal) {
        return projectService.addMember(id, request, principal);
    }
}
