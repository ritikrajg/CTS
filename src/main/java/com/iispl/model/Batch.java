package com.iispl.model;

import java.time.LocalDateTime;
import java.util.List;

public class Batch {

    private long id;
    private String batchRef;
    private String status;           // PENDING | ACCEPTED | REJECTED
    private LocalDateTime createdAt;
    private List<Cheque> cheques;

    public Batch() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getBatchRef() { return batchRef; }
    public void setBatchRef(String batchRef) { this.batchRef = batchRef; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Cheque> getCheques() { return cheques; }
    public void setCheques(List<Cheque> cheques) { this.cheques = cheques; }
}
