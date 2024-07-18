package net.javaguides.sms.service;

import java.io.IOException;
import java.util.List;

import net.javaguides.sms.entity.Course;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
    List<Course> getAllCourses();

    Course saveCourse(Course course);
    void save(Course course);
    void saveCourse(Course course, MultipartFile file) throws IOException;
    void deleteCourseById(Long id);
    List<Course> findAllCourses();
    Course findCourseById(Long id);
    Course getCourseById(Long id);
    List<Course> searchCourses(String query);
    Course updateCourse(Course course);

    List<String> getAllCategories();
}
