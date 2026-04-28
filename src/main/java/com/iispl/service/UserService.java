package com.iispl.service;

import com.iispl.dao.UserDAO;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public boolean login(String username, String password) {
        return userDAO.validateUser(username, password);
    }
}