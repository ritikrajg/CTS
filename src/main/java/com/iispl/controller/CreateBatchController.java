package com.iispl.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.iispl.model.Cheque;
import com.iispl.service.BatchService;
import com.iispl.service.ChequeService;

public class CreateBatchController extends SelectorComposer<Div> {

    private ChequeService chequeService = new ChequeService();
    private BatchService  batchService  = new BatchService();

    @Wire private Listbox batchListbox;
    @Wire private Label   msgLabel;

    @Override
    public void doAfterCompose(Div comp) throws Exception {
        super.doAfterCompose(comp);
        loadList();
    }

    private void loadList() {
        batchListbox.getItems().clear();
        List<Cheque> list = chequeService.findValidatedUnbatched();

        for (Cheque c : list) {
            Listitem item = new Listitem();

            // Checkbox cell
            Listcell checkCell = new Listcell();
            Checkbox cb = new Checkbox();
            cb.setAttribute("chequeId", c.getId());
            checkCell.appendChild(cb);
            item.appendChild(checkCell);

            item.appendChild(new Listcell(String.valueOf(c.getId())));
            item.appendChild(new Listcell(c.getPayeeName()));
            item.appendChild(new Listcell(c.getChequeNumber()));
            item.appendChild(new Listcell(String.format("%.2f", c.getAmount())));
            item.appendChild(new Listcell(c.getBankName()));
            item.appendChild(new Listcell(c.getChequeDate().toString()));

            batchListbox.appendChild(item);
        }
    }

    @Listen("onClick = #createBatchBtn")
    public void handleCreateBatch() {
        List<Long> selected = new ArrayList<>();

        for (Listitem item : batchListbox.getItems()) {
            Checkbox cb = (Checkbox) item.getChildren().get(0).getFirstChild();
            if (cb != null && cb.isChecked()) {
                selected.add((Long) cb.getAttribute("chequeId"));
            }
        }

        if (selected.isEmpty()) {
            msgLabel.setStyle("color: red;");
            msgLabel.setValue("Please select at least one cheque.");
            return;
        }

        String batchRef = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long batchId = batchService.create(batchRef, selected);

        if (batchId > 0) {
            Executions.sendRedirect("/pages/npci-validation.zul");
        } else {
            msgLabel.setStyle("color: red;");
            msgLabel.setValue("Failed to create batch. Please try again.");
        }
    }
}
