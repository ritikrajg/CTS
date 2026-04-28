package com.iispl.service;

import java.util.List;

import com.iispl.dao.ChequeDAO;
import com.iispl.model.Cheque;

public class ChequeService {

    private ChequeDAO chequeDAO = new ChequeDAO();

    public long save(Cheque c) {
        return chequeDAO.save(c);
    }

    public List<Cheque> findAll() {
        return chequeDAO.findAll();
    }

    public List<Cheque> findPendingForValidation() {
        return chequeDAO.findPendingForValidation();
    }

    public List<Cheque> findValidatedUnbatched() {
        return chequeDAO.findValidatedUnbatched();
    }

    public Cheque findById(long id) {
        return chequeDAO.findById(id);
    }

    public boolean validate(long id) {
        return chequeDAO.updateStatusIfPending(id, "VALIDATED");
    }

    public boolean reject(long id) {
        return chequeDAO.updateStatusIfPending(id, "REJECTED");
    }

    public List<Cheque> findByBatchId(long batchId) {
        return chequeDAO.findByBatchId(batchId);
    }
}