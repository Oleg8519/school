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
@RequestMapping("/api/pupils")
public class PupilController {

    @Autowired
    private PupilRepository pupils;

    @Autowired
    private TeacherRepository teachers;

    @PostMapping()
    public Pupil createPupil(@Valid @RequestBody Pupil pupil){

        return this.pupils.save(pupil);
    }

    @GetMapping("/{id}")
    public Pupil getPupil(@PathVariable Long id){

        return this.pupils.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Pupil", id)
        );
    }

    @GetMapping()
    public Page<Pupil> getStudents(Pageable pageable){
        return this.pupils.findAll(pageable);
    }

    @PutMapping()
    public Pupil updatePupil(@Valid @RequestBody Pupil pupil){

        return this.pupils.findById(pupil.getId()).map((toUpdate) -> {
            toUpdate.setFirstName(pupil.getFirstName());
            toUpdate.setLastName(pupil.getLastName());
            return this.pupils.save(toUpdate);
        }).orElseThrow(() -> new ResourceNotFoundException("Pupil", pupil.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePupil(@PathVariable Long id){

        return this.pupils.findById(id).map((toDelete) -> {
            this.pupils.delete(toDelete);
            return ResponseEntity.ok("Pupil id " + id + " deleted");
        }).orElseThrow(() -> new ResourceNotFoundException("Pupil", id));
    }

    @GetMapping("/{id}/teachers")
    public Set<Teacher> getTeacher(@PathVariable Long id){

        return this.pupils.findById(id).map((pupil) -> {
            return pupil.getTeachers();
        }).orElseThrow(() -> new ResourceNotFoundException("Pupil", id));
    }

}
