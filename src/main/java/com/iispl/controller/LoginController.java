package com.iispl.controller;

import com.iispl.service.UserService;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class LoginController extends SelectorComposer<Window> {

    private UserService userService = new UserService();

    @Wire
    private Textbox username;

    @Wire
    private Textbox password;

    @Wire
    private Label msgLabel;

    @Wire
    private Button loginBtn;

    @Listen("onClick = #loginBtn")
    public void handleLogin() {

        String user = username.getValue() == null ? "" : username.getValue().trim();
        String pass = password.getValue() == null ? "" : password.getValue().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            msgLabel.setStyle("color: red;");
            msgLabel.setValue("Please enter username and password.");
            return;
        }

        boolean isValid = userService.login(user, pass);

        if (isValid) {
        	 Session session = Sessions.getCurrent();
             session.setAttribute("user", user);

            Executions.sendRedirect("/pages/dashboard.zul");
        } else {
            msgLabel.setStyle("color: red;");
            msgLabel.setValue("Invalid username or password.");
        }
    }
}