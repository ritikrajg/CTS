package com.iispl.model;

import java.time.LocalDate;

public class Cheque {

    private long id;
    private String payeeName;
    private String accountNumber;
    private String ifscCode;
    private double amount;
    private String bankName;
    private String chequeNumber;
    private LocalDate chequeDate;
    private String status;   // PENDING | VALIDATED | REJECTED
    private boolean batched;

    public Cheque() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPayeeName() { return payeeName; }
    public void setPayeeName(String payeeName) { this.payeeName = payeeName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getChequeNumber() { return chequeNumber; }
    public void setChequeNumber(String chequeNumber) { this.chequeNumber = chequeNumber; }

    public LocalDate getChequeDate() { return chequeDate; }
    public void setChequeDate(LocalDate chequeDate) { this.chequeDate = chequeDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isBatched() { return batched; }
    public void setBatched(boolean batched) { this.batched = batched; }
}