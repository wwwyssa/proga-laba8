package com.lab8.server.managers;

import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.server.Server;
import com.lab8.server.dao.UserDAO;
import com.lab8.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class AuthManager {
    private String pepper;
    private final SessionFactory sessionFactory;
    private static volatile AuthManager instance;

    private AuthManager() {
        try (FileInputStream input = new FileInputStream("dbconfig.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user"); // sXXXXXX
            String password = properties.getProperty("db.password"); // пароль из файла .pgpass
            this.sessionFactory = HibernateUtil.getSessionFactory(url, user, password);
        } catch (IOException e) {
            Server.logger.severe("Failed to load database properties: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
    }

    public ExecutionResponse<AnswerString> registerUser(String login, String password) throws SQLException {
        Server.logger.info("Создание нового пользователя " + login);

        var salt = PasswordManager.getSalt();
        var passwordHash = PasswordManager.getHash(password, salt);

        UserDAO dao = new UserDAO();
        dao.setName(login);
        dao.setPassword(passwordHash);
        dao.setSalt(salt);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.persist(dao);
                transaction.commit();

                var newId = dao.getId();
                return new ExecutionResponse<>(true, new AnswerString("Пользователь успешно создан с id#" + newId));
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                return new ExecutionResponse<>(false, new AnswerString("UserAlreadyExists"));
            }
        }
    }

    public ExecutionResponse<AnswerString> authenticateUser(String login, String password) throws SQLException {
        Server.logger.info("Аутентификация пользователя " + login);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                var query = session.createQuery("SELECT u FROM users u WHERE u.name = :name", UserDAO.class);
                query.setParameter("name", login);

                List<UserDAO> result = query.getResultList();

                if (result.isEmpty()) {
                    return new ExecutionResponse<>(false, new AnswerString("UserNotFound"));
                }

                var user = result.get(0);
                transaction.commit();

                var id = user.getId();
                var salt = user.getSalt();
                var expectedHashedPassword = user.getPassword();

                var actualHashedPassword = PasswordManager.getHash(password, salt);
                if (expectedHashedPassword.equals(actualHashedPassword)) {
                    return new ExecutionResponse<>(true, new AnswerString("Пользователь " + login + " аутентифицирован c id#" + id));
                }

                return new ExecutionResponse<>(false, new AnswerString("WrongPassword"));
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}