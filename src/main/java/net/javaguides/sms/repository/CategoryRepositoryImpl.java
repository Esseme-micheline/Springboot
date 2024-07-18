package net.javaguides.sms.repository;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> getAllCategories() {
        return entityManager.createQuery("SELECT c.name FROM Category c", String.class)
                .getResultList();
    }
}
