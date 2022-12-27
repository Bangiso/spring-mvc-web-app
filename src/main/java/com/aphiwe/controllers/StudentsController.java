package com.aphiwe.controllers;

import com.aphiwe.models.Student;
import com.aphiwe.services.StudentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StudentsController {
    @Autowired
    private StudentsService studentsService;
    Logger logger = LoggerFactory.getLogger(StudentsController.class);

    public StudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("/students")
    private String getStudents(Model model) {
        model.addAttribute("students", studentsService.fetchStudents());
        return "students/students";
    }

    @GetMapping("/students/{id}")
    private String getStudent(@PathVariable int id, Model model) {
        model.addAttribute("student", studentsService.findById(id).get());
        return "students/student";
    }

    @RequestMapping(value = "/students/add", method = RequestMethod.GET)
    public ModelAndView student() {
        return new ModelAndView("students/create", "command", new Student());
    }
    @RequestMapping(value = "/students/update", method = RequestMethod.POST)
    public String studentUpdate(@ModelAttribute Student student, ModelMap model) {
        model.addAttribute("student", student);
        return "students/update_student";
    }

    @PostMapping("/students/addStudent")
    private String addStudent(@ModelAttribute Student student, ModelMap model) {
        logger.info("Adding student [id: " + student.getId() + " name: " + student.getName() + " gpa: " + student.getGpa() + "]");
        int ret = studentsService.save(student);
        if(ret==501){
            return "students/duplicate_error";
        } else if (ret==502) {
            return "students/error";
        }
        model.addAttribute("students", studentsService.fetchStudents());
        return "students/students";
    }

    @PostMapping("/students/update-successful")
    public String updateStudent(@ModelAttribute Student student) {
         studentsService.updateStudent(student);
         return "students/update_success";
    }

    @PostMapping("/students/remove/{id}")
    public String deleteStudent(@PathVariable int id) {
        logger.info("Removing student with id = "+id);
        studentsService.deleteStudent(id);
        return "students/delete_success";
    }
    @PostMapping("/students/search-results")
    public String searchStudent(String keyword, Model model) {
        logger.info("looking for student with name : "+keyword);
        model.addAttribute("students", studentsService.filterByNameOrId(keyword));
        return "students/students";
    }
}
