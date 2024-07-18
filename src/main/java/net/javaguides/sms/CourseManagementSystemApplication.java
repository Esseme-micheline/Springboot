package net.javaguides.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.javaguides.sms.entity.Course;
import net.javaguides.sms.repository.CourseRepository;

@SpringBootApplication
public class CourseManagementSystemApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(CourseManagementSystemApplication.class, args);
    }

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {

    }
}

