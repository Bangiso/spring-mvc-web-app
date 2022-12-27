package com.aphiwe.services;

import com.aphiwe.models.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StudentsService {
    public int save(Student student);
    public List<Student> fetchStudents();
    public int updateStudent(Student student);
    public int deleteStudent(int id);
    public Optional<Student> findById(int id);
}
