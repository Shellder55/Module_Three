package crud;

import crud.config.HibernateConfig;
import crud.dao.UserDaoImpl;
import crud.service.UserServiceImpl;
import crud.ui.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        UserDaoImpl userDao = new UserDaoImpl();
        HibernateConfig hibernateConfig = new HibernateConfig();
        Logger logger = LoggerFactory.getLogger(UserServiceImpl.class.getName());

        try {
            UserServiceImpl userService = new UserServiceImpl(userDao, hibernateConfig.getSessionFactory(), logger);
            Console console = new Console(userService, logger);
            console.start();
        } finally {
            hibernateConfig.shutdown();
        }
    }
}

