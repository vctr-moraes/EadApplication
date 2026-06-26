package com.ead.course.controllers;

import com.ead.course.dtos.CourseRecordDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {

    final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid CourseRecordDto courseRecordDto) {
        if (courseService.existsByName(courseRecordDto.name())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Course Name already exists");
        }

        var newCourse = courseService.save(courseRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
    }

    @GetMapping
    public ResponseEntity<List<CourseModel>> getAllCourses() {
        var courses = courseService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable(value = "courseId") UUID courseId) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable UUID courseId) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        courseService.delete(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable UUID courseId,
                                               @RequestBody @Valid CourseRecordDto courseRecordDto) {

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        var courseUpdated = courseService.update(courseRecordDto, courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(courseUpdated);
    }
}
