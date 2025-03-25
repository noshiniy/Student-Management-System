package edu.icet.repository;

import edu.icet.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByStudentID(String studentID);
    List<Student> findByStudentIDStartingWith(String prefix);
    List<Student> findByStudentIDContainingIgnoreCase(String query);
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
