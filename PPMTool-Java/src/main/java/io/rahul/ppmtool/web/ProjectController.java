package io.rahul.ppmtool.web;

import io.rahul.ppmtool.domain.Project;
import io.rahul.ppmtool.services.ErrorValidationService;
import io.rahul.ppmtool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ErrorValidationService errorValidationService;

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result, Principal principal) {

        ResponseEntity<?> errorMap = errorValidationService.ErrorValidationService(result);
        if(errorMap != null) return errorMap;
        
        projectService.saveOrUpdateProject(project, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/{projectIdentifier}")
    public ResponseEntity<?> getProjectByIdentifier(@PathVariable String projectIdentifier, Principal principal) {

        Project project = projectService.findProjectByIdentifier(projectIdentifier, principal.getName());

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllProjects(Principal principal) {
        return projectService.findAllProjects(principal.getName());
    }

    @DeleteMapping("/{projectIdentifier}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectIdentifier, Principal principal) {

        projectService.deleteProjectByIdentifier(projectIdentifier, principal.getName());

        return new ResponseEntity<String>("Project with Identifier '" + projectIdentifier.toUpperCase() + "' was deleted", HttpStatus.OK);
    }
}
