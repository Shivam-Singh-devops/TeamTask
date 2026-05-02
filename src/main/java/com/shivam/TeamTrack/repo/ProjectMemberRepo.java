package com.shivam.TeamTrack.repo;

import com.shivam.TeamTrack.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepo extends JpaRepository<ProjectMember, Integer> {

    List<ProjectMember> findByUser_Id(int userId);

    List<ProjectMember> findByProject_Id(int projectId);

    boolean existsByProject_IdAndUser_Id(int projectId, int userId);

    Optional<ProjectMember> findByProject_IdAndUser_Id(int projectId, int userId);
}
