package pl.edu.agh.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.util.SessionUtil;

import java.util.List;

public class SubcategoryDao implements ISubcategoryDao {

    @Override
    public void saveSubcategory(Subcategory subcategory) {
        CommonDaoSave.save(subcategory);
    }

    @Override
    public List<Subcategory> getAllSubcategories() {
        Transaction transaction = null;
        SessionUtil.openSession();

        try (Session session = SessionUtil.getSession()) {
            transaction = session.beginTransaction();

            return session.createQuery("FROM Subcategories", Subcategory.class).getResultList();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public List<Subcategory> getSubcategoriesFromCategory(Category category) {
        Transaction transaction = null;
        SessionUtil.openSession();

        try (Session session = SessionUtil.getSession()) {
            transaction = session.beginTransaction();

            return session.createQuery("FROM Subcategories where category.id = :id", Subcategory.class).
                    setParameter("id", category.getId()).
                    getResultList();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }
}
