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

    @GetMapping
    private String index(Model model) {
        return "index";
    }

    @GetMapping("/students")
    private String getStudents(Model model) {
        model.addAttribute("students", studentsService.fetchStudents());
        return "students";
    }

    @GetMapping("/students/{id}")
    private String getStudent(@PathVariable int id, Model model) {
        model.addAttribute("student", studentsService.findById(id).get());
        return "student";
    }

    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public ModelAndView student() {
        return new ModelAndView("create", "command", new Student());
    }

    @PostMapping("/addStudent")
    private String addStudent(@ModelAttribute Student student, ModelMap model) {
        logger.info("Adding student [id: " + student.getId() + " name: " + student.getName() + " gpa: " + student.getGpa() + "]");
        int ret = studentsService.save(student);
        if(ret==501){
            return "duplicate_error";
        } else if (ret==502) {
            return "error";
        }
        model.addAttribute("students", studentsService.fetchStudents());
        return "students";
    }

    @PutMapping
    public @ResponseBody
    int updateStudent(@RequestBody Student student) {
        return studentsService.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody
    int deleteStudent(@PathVariable int id) {
        return studentsService.deleteStudent(id);
    }
}
