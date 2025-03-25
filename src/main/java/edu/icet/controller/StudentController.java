package edu.icet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.icet.model.Student;
import edu.icet.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/generate-id")
    public ResponseEntity<String> generateStudentID() {
        try {
            String studentID = studentService.generateStudentID();
            return ResponseEntity.ok(studentID);
        } catch (Exception e) {
            log.error("Failed to generate student ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate student ID");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> getStudents() {
        try {
            List<Student> students = studentService.getStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            log.error("Failed to retrieve students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStudent(
            @RequestPart("student") Student student,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            if (photo != null && !photo.isEmpty()) {
                student.setPhoto(photo.getBytes());
            }
            studentService.addStudent(student);
            return ResponseEntity.ok("Student added successfully");
        } catch (Exception e) {
            log.error("Failed to add student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add student");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateStudent(
            @RequestParam("studentID") String studentID,
            @RequestParam("title") String title,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("gender") String gender,
            @RequestParam("nic") String nic,
            @RequestParam("dob") String dob,
            @RequestParam("address") String address,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("email") String email,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            Student existingStudent = studentService.findById(studentID)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found"));

            existingStudent.setTitle(title);
            existingStudent.setFirstName(firstName);
            existingStudent.setLastName(lastName);
            existingStudent.setGender(gender);
            existingStudent.setNic(nic);
            existingStudent.setDob(LocalDate.parse(dob));
            existingStudent.setAddress(address);
            existingStudent.setContactNumber(contactNumber);
            existingStudent.setEmail(email);

            if (photo != null && !photo.isEmpty()) {
                existingStudent.setPhoto(photo.getBytes());
            }

            studentService.updateStudent(existingStudent);
            return ResponseEntity.ok("Student updated successfully");
        } catch (Exception e) {
            log.error("Failed to update student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update student");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudentById(@PathVariable String id) {
        try {
            studentService.deleteStudentById(id);
            return ResponseEntity.ok("Student deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete student");
        }
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Student> findByStudentID(@PathVariable String id) {
        try {
            Optional<Student> student = studentService.findById(id);
            if (((Optional<?>) student).isPresent()) {
                return ResponseEntity.ok(student.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            log.error("Failed to find student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String query) {
        try {
            List<Student> students = studentService.searchStudents(query);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            log.error("Failed to search students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
