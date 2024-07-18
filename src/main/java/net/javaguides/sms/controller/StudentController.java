package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Course;
import net.javaguides.sms.service.CourseService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final CourseService courseService;


    @Autowired
    public StudentController(CourseService courseService) {
        this.courseService = courseService;
    }



    @GetMapping("/courses")
    public String listCourses(Model model) {
        List<Course> courses = courseService.findAllCourses();
        model.addAttribute("courses", courses);
        return "student";
    }

    @GetMapping("/courses/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        Course course = courseService.findCourseById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + course.getDocumentName() + "\"")
                .body(course.getDocumentData());
    }

    @GetMapping("/courses/view/{id}")
    public String viewCourseDocument(@PathVariable Long id, Model model) throws IOException {
        Course course = courseService.findCourseById(id);
        if (course != null && course.getDocumentData() != null) {
            try (PDDocument document = PDDocument.load(course.getDocumentData())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                model.addAttribute("documentText", text);
            }
        }
        model.addAttribute("course", course);
        return "document";
    }

    @GetMapping("/search")
    public String searchCourses(@RequestParam("query") String query,
                                Model model) {
        List<Course> courses = courseService.searchCourses(query);
        model.addAttribute("courses", courses);
        return "student";
    }





}
