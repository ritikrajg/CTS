package com.iispl.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Button;

public class HeaderComponent extends HtmlMacroComponent {

    @Wire
    private Label userLabel;

    @Wire
    private Button logoutBtn;

    public HeaderComponent() {
        compose();
        Selectors.wireComponents(this, this, false);
    }

    @Override
    public void afterCompose() {
        super.afterCompose();

        Object user = Sessions.getCurrent().getAttribute("user");

        if (user != null) {
            userLabel.setValue("Hi, " + user.toString());
        } else {
            userLabel.setValue("User");
        }
    }

    @Listen("onClick = #logoutBtn")
    public void logout() {
        Sessions.getCurrent().invalidate();
        Executions.sendRedirect("/pages/login.zul");
    }
}