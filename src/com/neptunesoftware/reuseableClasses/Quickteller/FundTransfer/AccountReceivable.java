package com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer;

public class AccountReceivable {
    
    private String accountNumber;
    private String accountType;
    
    public AccountReceivable() {
    }
    
    public AccountReceivable(String accountNumber, String accountType) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
