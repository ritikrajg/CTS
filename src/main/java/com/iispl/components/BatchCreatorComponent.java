package com.iispl.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.iispl.model.Cheque;
import com.iispl.service.BatchService;
import com.iispl.service.ChequeService;

/**
 * Macro component: shows only VALIDATED + un-batched cheques.
 * Rejected cheques are permanently excluded.
 * Usage in ZUL:
 *   <?component name="batchcreator"
 *       macroURI="/components/batch-creator.zul"
 *       class="com.iispl.components.BatchCreatorComponent"?>
 *   <batchcreator />
 */
public class BatchCreatorComponent extends HtmlMacroComponent {

    private final ChequeService chequeService = new ChequeService();
    private final BatchService  batchService  = new BatchService();

    @Wire private Listbox mc_batchListbox;
    @Wire private Label   mc_batchMsg;

    public BatchCreatorComponent() {
        compose();
        Selectors.wireComponents(this, this, false);
        Selectors.wireEventListeners(this, this);
    }

    @Override
    public void afterCompose() {
        super.afterCompose();
        Selectors.wireComponents(this, this, false);
        Selectors.wireEventListeners(this, this);
        loadList();
    }

    // ── load only VALIDATED + unbatched ────────────────────────────────────
    private void loadList() {
        mc_batchListbox.getItems().clear();
        List<Cheque> cheques = chequeService.findValidatedUnbatched();

        for (Cheque c : cheques) {
            Listitem row = new Listitem();

            // checkbox cell — stores cheque id as attribute
            Listcell checkCell = new Listcell();
            Checkbox cb = new Checkbox();
            cb.setAttribute("chequeId", c.getId());
            checkCell.appendChild(cb);
            row.appendChild(checkCell);

            row.appendChild(new Listcell(String.valueOf(c.getId())));
            row.appendChild(new Listcell(c.getPayeeName()));
            row.appendChild(new Listcell(c.getChequeNumber()));
            row.appendChild(new Listcell(String.format("%.2f", c.getAmount())));
            row.appendChild(new Listcell(c.getBankName()));
            row.appendChild(new Listcell(c.getChequeDate().toString()));

            mc_batchListbox.appendChild(row);
        }
    }

    // ── create batch ───────────────────────────────────────────────────────
    @Listen("onClick = #mc_createBatchBtn")
    public void handleCreate() {
        List<Long> selected = new ArrayList<>();

        for (Listitem item : mc_batchListbox.getItems()) {
            Checkbox cb = (Checkbox) item.getChildren().get(0).getFirstChild();
            if (cb != null && cb.isChecked()) {
                selected.add((Long) cb.getAttribute("chequeId"));
            }
        }

        if (selected.isEmpty()) {
            mc_batchMsg.setStyle("color: red;");
            mc_batchMsg.setValue("Please select at least one cheque.");
            return;
        }

        String ref = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long batchId = batchService.create(ref, selected);

        if (batchId > 0) {
            Executions.sendRedirect("/pages/npci-validation.zul");
        } else {
            mc_batchMsg.setStyle("color: red;");
            mc_batchMsg.setValue("Failed to create batch. Please try again.");
        }
    }

    @Listen("onClick = #mc_batchCancelBtn")
    public void handleCancel() {
        Executions.sendRedirect("/pages/dashboard.zul");
    }
}
