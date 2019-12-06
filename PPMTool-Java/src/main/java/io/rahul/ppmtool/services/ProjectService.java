package io.rahul.ppmtool.services;

import io.rahul.ppmtool.domain.Backlog;
import io.rahul.ppmtool.domain.Project;
import io.rahul.ppmtool.domain.User;
import io.rahul.ppmtool.exceptions.ProjectIdException;
import io.rahul.ppmtool.repositories.BacklogRepository;
import io.rahul.ppmtool.repositories.ProjectRepository;
import io.rahul.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username) {
        String requestIdentifier = project.getProjectIdentifier().toUpperCase();
        try{
            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());

            project.setProjectIdentifier(requestIdentifier);

            if(project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(requestIdentifier);
            }

            if(project.getId() != null) {
                project.setBacklog(backlogRepository.findByProjectIdentifier(requestIdentifier));
            }
            return projectRepository.save(project);
        }
        catch (Exception e) {
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists");
        }
    }

    public Project findProjectByIdentifier(String projectIdentifier) {

        Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());

        if (project == null) {
            throw new ProjectIdException("Project does not exist");
        }
        return project;
    }

    public Iterable<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectIdentifier) {
        Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());

        if (project == null) {
            throw new ProjectIdException("Cannot delete Project with ID '" + projectIdentifier + "' . This project does not exist");
        }

        projectRepository.delete(project);
    }
}
