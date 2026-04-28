package com.iispl.controller;

import java.util.List;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.iispl.model.Cheque;
import com.iispl.service.ChequeService;

public class ValidateChequeController extends SelectorComposer<Div> {

    private ChequeService chequeService = new ChequeService();

    // List
    @Wire private Listbox chequeListbox;

    // Detail modal
    @Wire private Window detailWindow;
    @Wire private Label  detailPayee;
    @Wire private Label  detailAccount;
    @Wire private Label  detailIfsc;
    @Wire private Label  detailAmount;
    @Wire private Label  detailBank;
    @Wire private Label  detailChequeNo;
    @Wire private Label  detailDate;
    @Wire private Label  detailStatus;
    @Wire private Label  actionMsg;

    private long selectedChequeId;

    @Override
    public void doAfterCompose(Div comp) throws Exception {
        super.doAfterCompose(comp);
        loadList();
    }

    private void loadList() {
        chequeListbox.getItems().clear();
        List<Cheque> list = chequeService.findPendingForValidation();

        for (Cheque c : list) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(String.valueOf(c.getId())));
            item.appendChild(new Listcell(c.getPayeeName()));
            item.appendChild(new Listcell(c.getChequeNumber()));
            item.appendChild(new Listcell(String.format("%.2f", c.getAmount())));
            item.appendChild(new Listcell(c.getBankName()));
            item.appendChild(new Listcell(c.getStatus()));

            // View Details button cell
            Listcell btnCell = new Listcell();
            org.zkoss.zul.Button viewBtn = new org.zkoss.zul.Button("View Details");
            viewBtn.setSclass("action-btn");
            final long cid = c.getId();
            viewBtn.addEventListener("onClick", e -> openDetail(cid));
            btnCell.appendChild(viewBtn);
            item.appendChild(btnCell);

            chequeListbox.appendChild(item);
        }
    }

    private void openDetail(long id) {
        Cheque c = chequeService.findById(id);
        if (c == null) return;

        selectedChequeId = id;
        detailPayee.setValue(c.getPayeeName());
        detailAccount.setValue(c.getAccountNumber());
        detailIfsc.setValue(c.getIfscCode());
        detailAmount.setValue(String.format("%.2f", c.getAmount()));
        detailBank.setValue(c.getBankName());
        detailChequeNo.setValue(c.getChequeNumber());
        detailDate.setValue(c.getChequeDate().toString());
        detailStatus.setValue(c.getStatus());
        actionMsg.setValue("");

        detailWindow.setVisible(true);
    }

    @Listen("onClick = #validateBtn")
    public void handleValidate() {
        if (selectedChequeId == 0) return;
        if (chequeService.validate(selectedChequeId)) {
            actionMsg.setStyle("color: green;");
            actionMsg.setValue("Cheque validated successfully.");
        } else {
            actionMsg.setStyle("color: red;");
            actionMsg.setValue("Cheque is no longer eligible for validation.");
        }
        detailWindow.setVisible(false);
        loadList();
    }

    @Listen("onClick = #rejectBtn")
    public void handleReject() {
        if (selectedChequeId == 0) return;
        if (chequeService.reject(selectedChequeId)) {
            actionMsg.setStyle("color: red;");
            actionMsg.setValue("Cheque rejected.");
        } else {
            actionMsg.setStyle("color: red;");
            actionMsg.setValue("Cheque is no longer eligible for rejection.");
        }
        detailWindow.setVisible(false);
        loadList();
    }

    @Listen("onClick = #closeDetailBtn")
    public void handleCloseDetail() {
        detailWindow.setVisible(false);
    }
}
