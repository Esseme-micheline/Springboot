package net.javaguides.sms.controller;

import net.javaguides.sms.entity.Category;
import net.javaguides.sms.entity.Comment;
import net.javaguides.sms.entity.Course;
import net.javaguides.sms.repository.CourseRepository;
import net.javaguides.sms.service.CommentService;
import net.javaguides.sms.service.CourseService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CoursesController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "courses";
    }

    @GetMapping("/search")
    public String searchCourses(@RequestParam("query") String query,
                                @RequestParam(value = "category", required = false) String category,
                                Model model) {
        List<Course> courses;

        if (category != null && !category.isEmpty()) {
            // Recherche par catégorie et par texte
            courses = courseRepository.findByCategoryAndQuery(category, query);
        } else {
            // Recherche seulement par texte
            courses = courseRepository.findByQuery(query);
        }

        model.addAttribute("courses", courses);
        return "courses";
    }


    @GetMapping("/create_course")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "create_course";
    }

    @PostMapping("/create_course")
    public String createCourse(Course course) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        course.setPresentation(userDetails.getUsername());
        courseRepository.save(course);
        return "redirect:/courses";
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + course.getDocumentName() + "\"")
                .body(course.getDocumentData());
    }

    @GetMapping("/displayDocument/{id}")
    public String displayDocument(@PathVariable Long id, Model model) throws IOException {
        Course course = courseService.getCourseById(id);

        try (PDDocument document = PDDocument.load(course.getDocumentData())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            model.addAttribute("documentText", text);
        }

        return "display_document";
    }

    @GetMapping("/{id}/comments")
    public String getCourseComments(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        List<Comment> comments = commentService.getCommentsByCourseId(id);
        model.addAttribute("course", course);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        return "course_comments";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @ModelAttribute Comment newComment,
                             RedirectAttributes redirectAttributes) {
        Course course = courseService.getCourseById(id);
        newComment.setCourse(course);
        newComment.setTimestamp(LocalDateTime.now());
        commentService.saveComment(newComment);

        redirectAttributes.addFlashAttribute("message", "Comment added successfully!");
        return "redirect:/courses/" + id + "/comments";
    }

    @GetMapping("/documents/{id}")
    public String viewDocument(@PathVariable Long id, Model model) throws IOException {
        Course course = courseService.getCourseById(id);

        if (course != null && course.getDocumentData() != null) {
            try (PDDocument document = PDDocument.load(course.getDocumentData())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                model.addAttribute("documentText", text);
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("comments", commentService.getCommentsByCourseId(id));

        return "view_documents";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.deleteCourseById(id);
        redirectAttributes.addFlashAttribute("message", "Course deleted successfully!");
        return "redirect:/courses";
    }
    @GetMapping("/edit_course/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "edit_course";
    }

    @PostMapping("/update/{id}")
    public String updateCourse(@PathVariable Long id, @ModelAttribute Course course,
                               @RequestParam("document") MultipartFile documentFile,
                               RedirectAttributes redirectAttributes) throws IOException {

        if (!documentFile.isEmpty()) {
            course.setDocumentName(documentFile.getOriginalFilename());
            course.setDocumentData(documentFile.getBytes());
        }

        course.setId(id);
        courseService.saveCourse(course);

        redirectAttributes.addFlashAttribute("message", "Course updated successfully!");
        return "redirect:/courses";
    }

    @PostMapping("/documents/{id}/addComment")
    public String addComment(@PathVariable Long id, @RequestParam String text, RedirectAttributes redirectAttributes) {
        Course course = courseService.getCourseById(id);
        if (course != null) {
            Comment comment = new Comment();
            comment.setText(text);
            comment.setTimestamp(LocalDateTime.now());
            comment.setCourse(course);
            commentService.saveComment(comment);
            redirectAttributes.addFlashAttribute("message", "Comment added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Course not found!");
        }
        return "redirect:/courses/documents/" + id;
    }





    @PostMapping("/edit_course/{id}")
    public String editCourse(@PathVariable Long id, Course updatedCourse) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid course ID:" + id));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!course.getPresentation().equals(userDetails.getUsername())) {
            throw new IllegalArgumentException("You don't have permission to edit this course.");
        }
        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        courseRepository.save(course);
        return "redirect:/courses";
    }


    @GetMapping("/create")
    public String showCreateCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", Category.values()); // Pass all enum values to the form
        return "create_course"; // Thymeleaf template name
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute("course") Course course,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("category") Category category) throws IOException {
        course.setCategory(category);
        courseService.saveCourse(course, file);
        return "redirect:/courses";
    }

}
