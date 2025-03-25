package edu.icet.service;

import edu.icet.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<Student> getStudents();
    void addStudent(Student student);
    void updateStudent(Student student);
    void deleteStudentById(String id);
    Optional<Student> findById(String id);
    String generateStudentID();
    List<Student> searchStudents(String query);
}
