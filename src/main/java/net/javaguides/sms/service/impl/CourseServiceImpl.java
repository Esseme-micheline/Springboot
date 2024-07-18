package net.javaguides.sms.service.impl;

import java.io.IOException;
import java.util.List;

import net.javaguides.sms.entity.Comment;
import net.javaguides.sms.repository.CategoryRepository;
import net.javaguides.sms.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.javaguides.sms.entity.Course;
import net.javaguides.sms.repository.CourseRepository;
import net.javaguides.sms.service.CourseService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CourseServiceImpl implements CourseService{
    private CourseRepository courseRepository;
    private CommentRepository commentRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        super();
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> searchCourses(String query) {
        return courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrPresentationContainingIgnoreCaseOrDocumentNameContainingIgnoreCase(query, query, query, query);
    }

    @Override
    public List<String> getAllCategories() {
        return categoryRepository.getAllCategories(); // Adjust this based on your repository logic
    }
    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).get();
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }
    public void save(Course course) {
        courseRepository.save(course);
    }



    @Override
    public void saveCourse(Course course, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            course.setDocumentName(file.getOriginalFilename());
            course.setDocumentData(file.getBytes());
        }
        courseRepository.save(course);
    }

    @Override
    public void deleteCourseById(Long id) {

        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }




}
