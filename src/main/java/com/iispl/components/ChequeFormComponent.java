package com.iispl.components;

import java.time.ZoneId;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.iispl.model.Cheque;
import com.iispl.service.ChequeService;

/**
 * Macro component for creating a cheque.
 * Usage in ZUL:
 *   <?component name="chequeform"
 *       macroURI="/components/cheque-form.zul"
 *       class="com.iispl.components.ChequeFormComponent"?>
 *   <chequeform />
 */
public class ChequeFormComponent extends HtmlMacroComponent {

    private final ChequeService chequeService = new ChequeService();

    @Wire private Textbox  mc_payeeName;
    @Wire private Textbox  mc_accountNumber;
    @Wire private Textbox  mc_ifscCode;
    @Wire private Textbox  mc_amount;
    @Wire private Textbox  mc_bankName;
    @Wire private Textbox  mc_chequeNumber;
    @Wire private Datebox  mc_chequeDate;
    @Wire private Label    mc_formMsg;

    public ChequeFormComponent() {
        compose();
        Selectors.wireComponents(this, this, false);
        Selectors.wireEventListeners(this, this);
    }

    @Override
    public void afterCompose() {
        super.afterCompose();
        Selectors.wireComponents(this, this, false);
        Selectors.wireEventListeners(this, this);
    }

    @Listen("onClick = #mc_saveBtn")
    public void handleSave() {

        String pName = trim(mc_payeeName);
        String accNo = trim(mc_accountNumber);
        String ifsc  = trim(mc_ifscCode);
        String amt   = trim(mc_amount);
        String bank  = trim(mc_bankName);
        String chqNo = trim(mc_chequeNumber);

        if (pName.isEmpty() || accNo.isEmpty() || ifsc.isEmpty() ||
            amt.isEmpty()   || bank.isEmpty()  || chqNo.isEmpty() ||
            mc_chequeDate.getValue() == null) {
            error("All fields are required.");
            return;
        }

        double amountVal;
        try {
            amountVal = Double.parseDouble(amt);
            if (amountVal <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            error("Amount must be a positive number.");
            return;
        }

        Cheque c = new Cheque();
        c.setPayeeName(pName);
        c.setAccountNumber(accNo);
        c.setIfscCode(ifsc.toUpperCase());
        c.setAmount(amountVal);
        c.setBankName(bank);
        c.setChequeNumber(chqNo);
        c.setChequeDate(mc_chequeDate.getValue()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        long id = chequeService.save(c);

        if (id > 0) {
            Executions.sendRedirect("/pages/validate-cheque.zul");
        } else {
            error("Failed to save cheque. Please try again.");
        }
    }

    @Listen("onClick = #mc_cancelBtn")
    public void handleCancel() {
        Executions.sendRedirect("/pages/dashboard.zul");
    }

    // ── helpers ────────────────────────────────────────────────────────────
    private String trim(Textbox tb) {
        return tb.getValue() == null ? "" : tb.getValue().trim();
    }

    private void error(String msg) {
        mc_formMsg.setStyle("color: red;");
        mc_formMsg.setValue(msg);
    }
}