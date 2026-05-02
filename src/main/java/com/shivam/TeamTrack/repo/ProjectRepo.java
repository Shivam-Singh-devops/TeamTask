package com.shivam.TeamTrack.repo;

import com.shivam.TeamTrack.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepo extends JpaRepository<Project, Integer> {

    List<Project> findByAdmin_Id(int adminId);
}
