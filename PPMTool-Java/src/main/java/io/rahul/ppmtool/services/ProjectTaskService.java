package io.rahul.ppmtool.services;

import io.rahul.ppmtool.domain.Backlog;
import io.rahul.ppmtool.domain.Project;
import io.rahul.ppmtool.domain.ProjectTask;
import io.rahul.ppmtool.exceptions.ProjectNotFoundException;
import io.rahul.ppmtool.repositories.BacklogRepository;
import io.rahul.ppmtool.repositories.ProjectRepository;
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

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

        try {
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
            if(projectTask.getPriority() == 0 || projectTask.getPriority() == null) {
                projectTask.setPriority(3);
            }

            //Initial status when status is null
            if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
        }catch (Exception e) {
            throw new ProjectNotFoundException("Project Not Found");
        }
    }

    public List<ProjectTask> findBacklogById(String backlog_id) {

        Project project = projectRepository.findByProjectIdentifier(backlog_id);

        if (project == null) {
            throw new ProjectNotFoundException("Project with ID: '" + backlog_id.toUpperCase() + "' does not exist.");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_id.toUpperCase());
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {

        //check whether backlog exist or not
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if(backlog == null) {
            throw new ProjectNotFoundException("Project with ID: '" + backlog_id + "' does not exist.");
        }

        //check whether tasks exists or not
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if (projectTask == null) {
            throw new ProjectNotFoundException("Project Task with ID: '" + pt_id + "' not found");
        }

        //check whether both backlog and task belong to same project
        if(!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project Task '" + pt_id + "' does not exist in project: '" + backlog_id);
        }

        return projectTask;
    }

    public ProjectTask updatePTByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id) {

        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);

//        Backlog backlog = projectTask.getBacklog();
//        List<ProjectTask> pts = backlog.getProjectTasks();
//        pts.remove(projectTask);
//        backlogRepository.save(backlog);

        projectTaskRepository.delete(projectTask);
    }
}
