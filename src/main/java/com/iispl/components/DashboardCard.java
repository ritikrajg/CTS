package com.iispl.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zk.ui.Executions;

public class DashboardCard extends HtmlMacroComponent {

    @Wire
    private Div mc_icon;

    @Wire
    private Label mc_title;

    private String iconClass;
    private String title;
    private String url;

    public DashboardCard() {
        compose();
        Selectors.wireComponents(this, this, false);
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
        if (mc_icon != null) {
            mc_icon.setSclass("card-icon " + iconClass);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (mc_title != null) {
            mc_title.setValue(title);
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void afterCompose() {
        super.afterCompose();

        // click event
        this.addEventListener("onClick", e -> {
            if (url != null && !url.isEmpty()) {
                Executions.sendRedirect(url);
            }
        });
    }
}