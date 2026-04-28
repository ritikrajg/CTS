package com.iispl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.iispl.util.Db;

public class UserDAO {

    public boolean validateUser(String username, String password) {

        String query = "SELECT 1 FROM users WHERE username = ? AND password = ?";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next(); 

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}