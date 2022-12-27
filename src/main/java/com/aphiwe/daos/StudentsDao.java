package com.aphiwe.daos;

import com.aphiwe.services.StudentsService;
import com.aphiwe.models.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class StudentsDao implements StudentsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Logger logger = LoggerFactory.getLogger(StudentsDao.class);

    @Override
    public int save(Student student) {
        String sql = "INSERT INTO students (id, name, gpa) VALUES (?, ?, ?)";
        try {
            int result = jdbcTemplate.update(sql, student.getId(), student.getName(), student.getGpa());
            if (result > 0) {
                logger.info("new student added");
            } else {
                logger.warn("Unable to add new student.");
            }
        } catch (Throwable ex) {
            if (ex instanceof DuplicateKeyException) {
                logger.error("Unable to add new student, student already exist!");
                return 501;
            } else {
                logger.error("Unable to add new student, something went wrong!");
                return 503;
            }
        }

        return 204;
    }

    @Override
    public List<Student> fetchStudents() {
        String sql = "SELECT * FROM students";
        List<Student> students = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper(Student.class));
        return students;
    }

    @Override
    public int updateStudent(Student student) {
        String sql = "UPDATE students  set name = ?, gpa = ? where id = ?";
        int result = jdbcTemplate.update(sql, student.getName(), student.getGpa(), student.getId());
        if (result > 0) {
            logger.info("student updated.");
            return 204;
        } else {
            return save(student);
        }
    }

    @Override
    public int deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        int result = jdbcTemplate.update(sql, id);
        if (result > 0) {
            logger.info("student deleted.");
        }
        return 204;
    }

    @Override
    public Optional<Student> findById(int id) {
        try {
            String sql = "SELECT * FROM students WHERE id = ?";
            return Optional.ofNullable(
                    (Student) jdbcTemplate
                            .queryForObject(
                                    sql,
                                    new Object[]{id},
                                    new BeanPropertyRowMapper(Student.class)
                            ));
        } catch (EmptyResultDataAccessException ex) {
            logger.info("No student found with id " + id);
            return Optional.empty();
        }
    }

    @Override
    public List<Student> filterByNameOrId(String keyword) {
        Integer id;
        try {id = Integer.parseInt(keyword);}
        catch (NumberFormatException ex) {id = null;}

        if (id!=null) { return toStudentList(id);}
        else {
            List<Student> students = fetchStudents().stream()
                    .filter(student -> student.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            if(students.isEmpty()){
                logger.info("No student found with name containing " + keyword);
            }
            return students;
        }
    }

    private List<Student> toStudentList(int id) {
        List<Student> students = new ArrayList<>();
        Optional<Student> studentOpt = findById(id);
        if (studentOpt.isPresent()) {
            students.add(studentOpt.get());
        }
        return students;
    }
}
