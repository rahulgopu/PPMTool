package io.rahul.ppmtool.services;

import io.rahul.ppmtool.domain.Backlog;
import io.rahul.ppmtool.domain.ProjectTask;
import io.rahul.ppmtool.repositories.BacklogRepository;
import io.rahul.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {


        //PTs to be added to a specific project, project!= null, BL exists
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

        //set the BL to PT
        projectTask.setBacklog(backlog);

        //Project Sequence
        Integer backlogSequence = backlog.getPTSequence();

        //Update the BL sequence
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);

        //Add sequence to project task
        projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        //Initial priority when priority is null
        if(projectTask.getPriority() == null) {
            projectTask.setPriority(3);
        }

        //Initial status when status is null
        if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
            projectTask.setStatus("TO-DO");
        }

        return projectTaskRepository.save(projectTask);
    }

    public List<ProjectTask> findBacklogById(String backlog_id) {
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_id.toUpperCase());
    }
}
