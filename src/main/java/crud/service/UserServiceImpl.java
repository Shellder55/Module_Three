package crud.service;

import crud.config.DaoException;
import crud.config.EntityNotFoundException;
import crud.config.HibernateConfig;
import crud.dao.UserDaoImpl;
import crud.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {
    private final UserDaoImpl userDao = new UserDaoImpl();
    private static Transaction transaction = null;
    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class.getName());

    @Override
    public void createUsersTable() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            userDao.createUsersTable(session);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Error creating table: " + e.getMessage());
            rollbackMethod();
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            userDao.dropUsersTable(session);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Error removing table: " + e.getMessage());
            rollbackMethod();
        }
    }

    @Override
    public void createUser(User user) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            userDao.createUser(user, session);
            transaction.commit();
        } catch (Exception e) {
            logger.error("Error saving user: " + e.getMessage());
            rollbackMethod();
        }
    }

    @Override
    public void updateUser(Long id, String name, String email, Integer age) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            userDao.updateUser(id, session, name, email, age);
            transaction.commit();
        } catch (Exception e) {
            rollbackMethod();
            throw new DaoException("Failed changing user data: " + id, e);
        }
    }

    @Override
    public void getUserById(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = userDao.getUserById(id, session);
            transaction.commit();
            if (user == null) {
                throw new EntityNotFoundException("User with ID: " + id + " not found");
            }
            System.out.println(user);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoException("Failed to get user by ID: " + id, e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = userDao.getUserById(id, session);
            if (user == null) {
                throw new EntityNotFoundException("User with ID: " + id + " not found");
            }
            userDao.deleteUser(id, session);
            transaction.commit();
        } catch (EntityNotFoundException e) {
            logger.warn("Attempt to delete non-existent user with ID: {}", id);
            rollbackMethod();
            throw e;
        } catch (Exception e) {
            logger.error("Error delete user: " + e.getMessage());
            rollbackMethod();
            throw e;
        }
    }

    private static void rollbackMethod() {
        if (transaction != null) {
            transaction.rollback();
        }
    }
}
