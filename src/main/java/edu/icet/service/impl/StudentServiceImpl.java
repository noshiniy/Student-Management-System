package edu.icet.service.impl;

import edu.icet.model.Student;
import edu.icet.repository.StudentRepository;
import edu.icet.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;


    @Override
    public List<Student> getStudents() {
        List<Student> student = studentRepository.findAll();
        return student;
    }

    @Override
    public void addStudent(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void updateStudent(Student student) {
        Optional<Student> existingStudentOpt = studentRepository.findById(student.getStudentID());
        if (existingStudentOpt.isPresent()) {
            Student existingStudent = existingStudentOpt.get();

            existingStudent.setTitle(student.getTitle());
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());
            existingStudent.setGender(student.getGender());
            existingStudent.setNic(student.getNic());
            existingStudent.setDob(student.getDob());
            existingStudent.setAddress(student.getAddress());
            existingStudent.setContactNumber(student.getContactNumber());
            existingStudent.setEmail(student.getEmail());

            if (student.getPhoto() != null) {
                existingStudent.setPhoto(student.getPhoto());
            }

            studentRepository.save(existingStudent);
        } else {
            throw new EntityNotFoundException("Student not found");
        }
    }

    @Override
    public void deleteStudentById(String id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Optional<Student> findById(String id) {
        return studentRepository.findById(id);
    }

    @Override
    public String generateStudentID() {
        int currentYear = Year.now().getValue();
        String yearPrefix = String.valueOf(currentYear).substring(2);
        String prefix = "STU" + yearPrefix;

        List<Student> students = studentRepository.findByStudentIDStartingWith(prefix);

        int maxNumber = students.stream()
                .map(student -> student.getStudentID().substring(5))
                .mapToInt(number -> Integer.parseInt(number))
                .max()
                .orElse(0);

        String newNumber = String.format("%04d", maxNumber + 1);

        return prefix + newNumber;
    }

    @Override
    public List<Student> searchStudents(String query) {
        List<Student> studentsById = studentRepository.findByStudentIDContainingIgnoreCase(query);

        List<Student> studentsByName = studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);

        List<Student> combinedList = new ArrayList<>(studentsById);
        for (Student student : studentsByName) {
            if (!combinedList.contains(student)) {
                combinedList.add(student);
            }
        }

        return combinedList;
    }
}
