package com.shivam.TeamTrack.service;

import com.shivam.TeamTrack.dto.AddProjectMemberRequest;
import com.shivam.TeamTrack.dto.ProjectCreateRequest;
import com.shivam.TeamTrack.dto.ProjectMemberDto;
import com.shivam.TeamTrack.dto.ProjectResponse;
import com.shivam.TeamTrack.dto.ProjectUpdateRequest;
import com.shivam.TeamTrack.model.Project;
import com.shivam.TeamTrack.model.ProjectMember;
import com.shivam.TeamTrack.model.ProjectRole;
import com.shivam.TeamTrack.model.User;
import com.shivam.TeamTrack.repo.ProjectMemberRepo;
import com.shivam.TeamTrack.repo.ProjectRepo;
import com.shivam.TeamTrack.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepo projectRepo;
    private final ProjectMemberRepo projectMemberRepo;
    private final UserRepo userRepo;

    /**
     * Lists projects the logged-in user may see: ones they created, or ones they joined.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> listAccessibleProjects(UserDetails principal) {
        User loggedInUser = getCurrentUser(principal);

        Map<Integer, Project> projectsById = new LinkedHashMap<>();

        List<Project> createdByUser = projectRepo.findByAdmin_Id(loggedInUser.getId());
        for (Project project : createdByUser) {
            projectsById.put(project.getId(), project);
        }

        List<ProjectMember> memberships = projectMemberRepo.findByUser_Id(loggedInUser.getId());
        for (ProjectMember membership : memberships) {
            Project project = membership.getProject();
            int id = project.getId();
            if (!projectsById.containsKey(id)) {
                projectsById.put(id, project);
            }
        }

        List<ProjectResponse> result = new ArrayList<>();
        for (Project project : projectsById.values()) {
            ProjectResponse row = buildProjectResponse(project, loggedInUser, false);
            result.add(row);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(int projectId, UserDetails principal) {
        User loggedInUser = getCurrentUser(principal);
        Project project = getProjectByIdOrThrow(projectId);

        boolean allowed = userIsCreator(project, loggedInUser)
                || userIsMember(projectId, loggedInUser.getId());
        if (!allowed) {
            throw new AccessDeniedException("You do not have access to this project");
        }

        return buildProjectResponse(project, loggedInUser, true);
    }

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request, UserDetails principal) {
        if (!StringUtils.hasText(request.name())) {
            throw new IllegalArgumentException("Project name is required");
        }

        User creator = getCurrentUser(principal);

        Project project = new Project();
        project.setName(request.name().trim());
        if (StringUtils.hasText(request.description())) {
            project.setDescription(request.description().trim());
        } else {
            project.setDescription(null);
        }
        project.setCreatedDate(LocalDateTime.now());
        project.setAdmin(creator);

        Project saved = projectRepo.save(project);

        ProjectMember creatorAsMember = new ProjectMember();
        creatorAsMember.setProject(saved);
        creatorAsMember.setUser(creator);
        creatorAsMember.setRole(ProjectRole.ADMIN);
        projectMemberRepo.save(creatorAsMember);

        return buildProjectResponse(saved, creator, true);
    }

    @Transactional
    public ProjectResponse update(int projectId, ProjectUpdateRequest request, UserDetails principal) {
        User loggedInUser = getCurrentUser(principal);
        Project project = getProjectByIdOrThrow(projectId);

        if (!userIsCreator(project, loggedInUser)) {
            throw new AccessDeniedException("Only the project creator can update this project");
        }
        if (!StringUtils.hasText(request.name())) {
            throw new IllegalArgumentException("Project name is required");
        }

        project.setName(request.name().trim());
        if (request.description() != null) {
            project.setDescription(request.description().trim());
        }

        Project saved = projectRepo.save(project);
        return buildProjectResponse(saved, loggedInUser, true);
    }

    @Transactional
    public void delete(int projectId, UserDetails principal) {
        User loggedInUser = getCurrentUser(principal);
        Project project = getProjectByIdOrThrow(projectId);

        if (!userIsCreator(project, loggedInUser)) {
            throw new AccessDeniedException("Only the project creator can delete this project");
        }

        projectRepo.delete(project);
    }

    @Transactional
    public ProjectResponse addMember(int projectId, AddProjectMemberRequest request, UserDetails principal) {
        User loggedInUser = getCurrentUser(principal);
        Project project = getProjectByIdOrThrow(projectId);

        if (!userIsCreator(project, loggedInUser)) {
            throw new AccessDeniedException("Only the project creator can add members");
        }

        String email = normalizeEmail(request.email());
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Member email is required");
        }

        User personToAdd = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("No user with this email"));

        if (personToAdd.getId() == loggedInUser.getId()) {
            throw new IllegalArgumentException("Creator is already a member");
        }

        boolean alreadyMember = projectMemberRepo.existsByProject_IdAndUser_Id(projectId, personToAdd.getId());
        if (alreadyMember) {
            throw new IllegalArgumentException("User is already a member");
        }

        ProjectMember newRow = new ProjectMember();
        newRow.setProject(project);
        newRow.setUser(personToAdd);
        newRow.setRole(ProjectRole.MEMBER);
        projectMemberRepo.save(newRow);

        Project updated = projectRepo.findById(projectId).orElse(project);
        return buildProjectResponse(updated, loggedInUser, true);
    }

    /** Looks up the {@link User} row for whoever is logged in (JWT subject = email). */
    private User getCurrentUser(UserDetails principal) {
        String email = normalizeEmail(principal.getUsername());
        return userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
    }

    private static String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private Project getProjectByIdOrThrow(int projectId) {
        return projectRepo.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    /** True if this user created the project (stored on {@link Project#getAdmin()}). */
    private static boolean userIsCreator(Project project, User user) {
        if (project.getAdmin() == null) {
            return false;
        }
        return project.getAdmin().getId() == user.getId();
    }

    private boolean userIsMember(int projectId, int userId) {
        return projectMemberRepo.existsByProject_IdAndUser_Id(projectId, userId);
    }

    /**
     * Builds the JSON-friendly object. If {@code withMembers} is false, the member list is left empty
     * so list screens stay small.
     */
    private ProjectResponse buildProjectResponse(Project project, User loggedInUser, boolean withMembers) {
        boolean canEdit = userIsCreator(project, loggedInUser);

        String creatorEmail = null;
        if (project.getAdmin() != null) {
            creatorEmail = project.getAdmin().getEmail();
        }

        List<ProjectMemberDto> members;
        if (withMembers) {
            members = buildMemberDtoList(project.getId());
        } else {
            members = List.of();
        }

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedDate(),
                creatorEmail,
                canEdit,
                members
        );
    }

    private List<ProjectMemberDto> buildMemberDtoList(int projectId) {
        List<ProjectMember> rows = projectMemberRepo.findByProject_Id(projectId);
        List<ProjectMemberDto> out = new ArrayList<>();
        for (ProjectMember row : rows) {
            User u = row.getUser();
            ProjectMemberDto dto = new ProjectMemberDto(u.getEmail(), u.getName(), row.getRole());
            out.add(dto);
        }
        return out;
    }
}
