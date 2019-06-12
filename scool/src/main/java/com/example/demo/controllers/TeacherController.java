package com.example.demo.controllers;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Pupil;
import com.example.demo.models.Teacher;
import com.example.demo.repositories.PupilRepository;
import com.example.demo.repositories.TeacherRepository;

import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/teachers")
public class TeacherController {

    @Autowired
    private TeacherRepository teachers;

    @Autowired
    private PupilRepository pupils;

    @PostMapping()
    public Teacher createTeacher(@Valid @RequestBody Teacher teacher){

        return this.teachers.save(teacher);
    }

    @GetMapping()
    public Page<Teacher> getTeacher(Pageable pageable){
        return this.teachers.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Teacher getTeacher(@PathVariable Long id){

        return this.teachers.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Teacher", id)
        );
    }

    @PutMapping()
    public Teacher updateTeacher(@Valid @RequestBody Teacher teacher){

        return this.teachers.findById(teacher.getId()).map((toUpdate) -> {
            toUpdate.setFirstName(teacher.getFirstName());
            toUpdate.setLastName(teacher.getLastName());
            return this.teachers.save(toUpdate);
        }).orElseThrow(() -> new ResourceNotFoundException("Teacher", teacher.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTeacher(@PathVariable Long id){

        return this.teachers.findById(id).map((toDelete) -> {
            this.teachers.delete(toDelete);
            return ResponseEntity.ok("Teacher id " + id + " deleted");
        }).orElseThrow(() -> new ResourceNotFoundException("Lecturer", id));
    }

    @GetMapping("/{teacherId}/pupils")
    public Set<Pupil> getPupils(@PathVariable Long teacherId){

        return this.teachers.findById(teacherId).map((teacher) -> {
            return teacher.getPupils();
        }).orElseThrow(() -> new ResourceNotFoundException("Lecturer", teacherId));
    }

    @PostMapping("/{id}/pupils/{pupilId}")
    public Set<Pupil> addPupil(@PathVariable Long id, @PathVariable Long pupilId){

        Pupil pupil = this.pupils.findById(pupilId).orElseThrow(
                () -> new ResourceNotFoundException("Student", pupilId)
        );


        return this.teachers.findById(id).map((teacher) -> {
            teacher.getPupils().add(pupil);
            return this.teachers.save(teacher).getPupils();
        }).orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
    }

}
