package com.iispl.service;

import java.util.List;

import com.iispl.dao.BatchDAO;
import com.iispl.model.Batch;

public class BatchService {

    private BatchDAO batchDAO = new BatchDAO();

    public long create(String batchRef, List<Long> chequeIds) {
        return batchDAO.create(batchRef, chequeIds);
    }

    public List<Batch> findPending() {
        return batchDAO.findPending();
    }

    public List<Batch> findProcessed() {
        return batchDAO.findProcessed();
    }

    public List<Batch> findAll() {
        return batchDAO.findAll();
    }

    public Batch findById(long id) {
        return batchDAO.findById(id);
    }

    public void accept(long id) {
        batchDAO.updateStatus(id, "ACCEPTED");
    }

    public boolean reject(long id) {
        return batchDAO.rejectAndReleaseCheques(id);
    }
}
