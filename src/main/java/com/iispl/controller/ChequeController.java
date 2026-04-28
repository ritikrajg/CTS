package com.iispl.controller;

import java.time.LocalDate;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.iispl.model.Cheque;
import com.iispl.service.ChequeService;

public class ChequeController extends SelectorComposer<Div> {

    private ChequeService chequeService = new ChequeService();

    @Wire
    private Textbox payeeName;

    @Wire
    private Textbox accountNumber;

    @Wire
    private Textbox ifscCode;

    @Wire
    private Textbox amount;

    @Wire
    private Textbox bankName;

    @Wire
    private Textbox chequeNumber;

    @Wire
    private Datebox chequeDate;

    @Wire
    private Label msgLabel;

    @Listen("onClick = #saveBtn")
    public void handleSave() {

        String pName  = payeeName.getValue()     == null ? "" : payeeName.getValue().trim();
        String accNo  = accountNumber.getValue()  == null ? "" : accountNumber.getValue().trim();
        String ifsc   = ifscCode.getValue()       == null ? "" : ifscCode.getValue().trim();
        String amt    = amount.getValue()         == null ? "" : amount.getValue().trim();
        String bank   = bankName.getValue()       == null ? "" : bankName.getValue().trim();
        String chqNo  = chequeNumber.getValue()   == null ? "" : chequeNumber.getValue().trim();

        if (pName.isEmpty() || accNo.isEmpty() || ifsc.isEmpty() ||
            amt.isEmpty()   || bank.isEmpty()  || chqNo.isEmpty() || chequeDate.getValue() == null) {
            msgLabel.setStyle("color: red;");
            msgLabel.setValue("All fields are required.");
            return;
        }

        double amountVal;
        try {
            amountVal = Double.parseDouble(amt);
        } catch (NumberFormatException e) {
            msgLabel.setStyle("color: red;");
            msgLabel.setValue("Amount must be a valid number.");
            return;
        }

        Cheque c = new Cheque();
        c.setPayeeName(pName);
        c.setAccountNumber(accNo);
        c.setIfscCode(ifsc.toUpperCase());
        c.setAmount(amountVal);
        c.setBankName(bank);
        c.setChequeNumber(chqNo);
        c.setChequeDate(chequeDate.getValue().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        long id = chequeService.save(c);

        if (id > 0) {
            Executions.sendRedirect("/pages/validate-cheque.zul");
        } else {
            msgLabel.setStyle("color: red;");
            msgLabel.setValue("Failed to save cheque. Please try again.");
        }
    }
}
