package com.shivam.TeamTrack.repo;

import com.shivam.TeamTrack.model.Project;
import com.shivam.TeamTrack.model.Task;
import com.shivam.TeamTrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {

    List<Task> findByProject(Project project);

    List<Task> findByAssignedPerson(User user);
    List<Task> findByProjectAndAssignedPerson(Project project, User user);
}
