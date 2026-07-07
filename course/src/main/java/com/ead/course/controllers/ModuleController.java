package com.ead.course.controllers;

import com.ead.course.dtos.ModuleRecordDto;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ModuleController {

    final ModuleService moduleService;
    final CourseService courseService;

    public ModuleController(ModuleService moduleService, CourseService courseService) {
        this.moduleService = moduleService;
        this.courseService = courseService;
    }

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@PathVariable(value = "courseId") UUID courseId,
                                             @RequestBody @Valid ModuleRecordDto moduleRecordDto) {

        var courseModel = courseService.findById(courseId).get();
        var newModule = moduleService.save(moduleRecordDto, courseModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(newModule);
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleModel>> getAllModules(@PathVariable UUID courseId) {
        var modules = moduleService.findAllModulesIntoCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(modules);
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(@PathVariable UUID courseId, @PathVariable UUID moduleId) {
        Optional<ModuleModel> moduleModel = moduleService.findModuleIntoCourse(courseId, moduleId);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModel.get());
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable UUID courseId, @PathVariable UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        moduleService.delete(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable UUID courseId,
                                               @PathVariable UUID moduleId,
                                               @RequestBody @Valid ModuleRecordDto moduleRecordDto) {

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        var moduleUpdated = moduleService.update(moduleRecordDto, moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(moduleUpdated);
    }
}
