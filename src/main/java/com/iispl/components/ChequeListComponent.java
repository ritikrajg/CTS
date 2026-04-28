package com.iispl.components;

import java.util.List;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.iispl.model.Cheque;
import com.iispl.service.ChequeService;

/**
 * Macro component that lists all cheques and allows Validate / Reject per row.
 * Usage in ZUL:
 *   <?component name="chequelist"
 *       macroURI="/components/cheque-list.zul"
 *       class="com.iispl.components.ChequeListComponent"?>
 *   <chequelist />
 */
public class ChequeListComponent extends HtmlMacroComponent {

    private final ChequeService chequeService = new ChequeService();

    @Wire private Listbox mc_chequeListbox;
    @Wire private Window  mc_detailWindow;

    @Wire private Label mc_dPayee;
    @Wire private Label mc_dAccount;
    @Wire private Label mc_dIfsc;
    @Wire private Label mc_dAmount;
    @Wire private Label mc_dBank;
    @Wire private Label mc_dChequeNo;
    @Wire private Label mc_dDate;
    @Wire private Label mc_dStatus;
    @Wire private Label mc_actionMsg;

    private long selectedId;

    public ChequeListComponent() {
        compose();
        Selectors.wireComponents(this, this, false);
        wireModalComponents();
        Selectors.wireEventListeners(this, this);
    }

    @Override
    public void afterCompose() {
        super.afterCompose();
        Selectors.wireComponents(this, this, false);
        wireModalComponents();
        Selectors.wireEventListeners(this, this);
        loadList();
    }

    private void wireModalComponents() {
        if (mc_detailWindow != null) {
            Selectors.wireComponents(mc_detailWindow, this, false);
            Selectors.wireEventListeners(mc_detailWindow, this);
        }
    }

    // ── load table ─────────────────────────────────────────────────────────
    private void loadList() {
        mc_chequeListbox.getItems().clear();
        List<Cheque> cheques = chequeService.findPendingForValidation();

        for (Cheque c : cheques) {
            Listitem row = new Listitem();
            row.appendChild(new Listcell(String.valueOf(c.getId())));
            row.appendChild(new Listcell(c.getPayeeName()));
            row.appendChild(new Listcell(c.getChequeNumber()));
            row.appendChild(new Listcell(String.format("%.2f", c.getAmount())));
            row.appendChild(new Listcell(c.getBankName()));

            // coloured status cell
            Listcell statusCell = new Listcell(c.getStatus());
            statusCell.setSclass("status-" + c.getStatus().toLowerCase());
            row.appendChild(statusCell);

            // View Details button
            Listcell actionCell = new Listcell();
            Button btn = new Button("View Details");
            btn.setSclass("action-btn");
            final long cid = c.getId();
            btn.addEventListener("onClick", e -> openDetail(cid));
            actionCell.appendChild(btn);
            row.appendChild(actionCell);

            mc_chequeListbox.appendChild(row);
        }
    }

    // ── open modal ─────────────────────────────────────────────────────────
    private void openDetail(long id) {
        Cheque c = chequeService.findById(id);
        if (c == null) return;

        selectedId = id;
        mc_dPayee.setValue(c.getPayeeName());
        mc_dAccount.setValue(c.getAccountNumber());
        mc_dIfsc.setValue(c.getIfscCode());
        mc_dAmount.setValue(String.format("%.2f", c.getAmount()));
        mc_dBank.setValue(c.getBankName());
        mc_dChequeNo.setValue(c.getChequeNumber());
        mc_dDate.setValue(c.getChequeDate().toString());
        mc_dStatus.setValue(c.getStatus());
        mc_actionMsg.setValue("");

        mc_detailWindow.doModal();
    }

    // ── validate ───────────────────────────────────────────────────────────
    @Listen("onClick = #mc_validateBtn")
    public void handleValidate() {
        if (selectedId == 0) return;
        if (chequeService.validate(selectedId)) {
            mc_actionMsg.setStyle("color: green;");
            mc_actionMsg.setValue("Cheque validated successfully.");
        } else {
            mc_actionMsg.setStyle("color: red;");
            mc_actionMsg.setValue("Cheque is no longer eligible for validation.");
        }
        mc_detailWindow.setVisible(false);
        loadList();
    }

    // ── reject ─────────────────────────────────────────────────────────────
    @Listen("onClick = #mc_rejectBtn")
    public void handleReject() {
        if (selectedId == 0) return;
        if (chequeService.reject(selectedId)) {
            mc_actionMsg.setStyle("color: #dc2626;");
            mc_actionMsg.setValue("Cheque rejected.");
        } else {
            mc_actionMsg.setStyle("color: red;");
            mc_actionMsg.setValue("Cheque is no longer eligible for rejection.");
        }
        mc_detailWindow.setVisible(false);
        loadList();
    }

    // ── close modal ────────────────────────────────────────────────────────
    @Listen("onClick = #mc_closeDetailBtn")
    public void handleClose() {
        mc_detailWindow.setVisible(false);
    }
}