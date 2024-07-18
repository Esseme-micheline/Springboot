package net.javaguides.sms.repository;

import net.javaguides.sms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {


        // Recherche par catégorie et par texte
        @Query("SELECT c FROM Course c WHERE c.category = :category AND " +
                "(c.title LIKE %:query% OR c.description LIKE %:query% OR " +
                "c.presentation LIKE %:query% OR c.documentName LIKE %:query%)")
        List<Course> findByCategoryAndQuery(@Param("category") String category, @Param("query") String query);

        // Recherche seulement par texte
        @Query("SELECT c FROM Course c WHERE c.title LIKE %:query% OR c.description LIKE %:query% OR " +
                "c.presentation LIKE %:query% OR c.documentName LIKE %:query%")
        List<Course> findByQuery(@Param("query") String query);

        List<Course> findByTitleContainingOrDescriptionContainingOrPresentationContainingOrDocumentNameContainingOrCategoryContaining(
                String title, String description, String presentation, String documentName, String category);
        List<Course> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrPresentationContainingIgnoreCaseOrDocumentNameContainingIgnoreCase(String title, String description, String presentation, String documentName);
}



