package com.iispl.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

import org.zkoss.zul.Label;
import org.zkoss.zul.Div;

public class HeaderController extends SelectorComposer<Div> {

    @Wire
    private Label userLabel;

    @Override
    public void doAfterCompose(Div comp) throws Exception {
        super.doAfterCompose(comp);

        String user = (String) Sessions.getCurrent().getAttribute("user");

        if (user != null) {
            userLabel.setValue("Welcome, " + user);
        }
    }

    @Listen("onClick = #logoutBtn")
    public void logout() {
        Sessions.getCurrent().invalidate();
        Executions.sendRedirect("/pages/login.zul");
    }
}