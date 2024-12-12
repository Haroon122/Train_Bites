package com.example.trainbites.ChefFoodPanel;

public class ChefPendingSingleOrderModel {
    private String Sname;
    private String Sphone;
    private String Sseat;
    private String Scoach;
    private String StotalAmount;
    private String Sdish;
    private String SorderDate;
    private String SorderNumber;
    private String Spaymentstatus;
    private String SSingleItemOrMultipleItem; // Moved this declaration to match its usage in the constructor

    public ChefPendingSingleOrderModel() {
        // Default constructor
    }

    public ChefPendingSingleOrderModel(String sname, String sphone, String sseat, String scoach, String stotalAmount,
                                       String sdish, String sorderDate, String sorderNumber, String spaymentstatus,
                                       String SSingleItemOrMultipleItem) {
        this.Sname = sname;
        this.Sphone = sphone;
        this.Sseat = sseat;
        this.Scoach = scoach;
        this.StotalAmount = stotalAmount;
        this.Sdish = sdish;
        this.SorderDate = sorderDate;
        this.SorderNumber = sorderNumber;
        this.Spaymentstatus = spaymentstatus;
        this.SSingleItemOrMultipleItem = SSingleItemOrMultipleItem; // Fixed initialization
    }

    public String getSname() {
        return Sname;
    }

    public void setSname(String sname) {
        Sname = sname;
    }

    public String getSphone() {
        return Sphone;
    }

    public void setSphone(String sphone) {
        Sphone = sphone;
    }

    public String getSseat() {
        return Sseat;
    }

    public void setSseat(String sseat) {
        Sseat = sseat;
    }

    public String getScoach() {
        return Scoach;
    }

    public void setScoach(String scoach) {
        Scoach = scoach;
    }

    public String getStotalAmount() {
        return StotalAmount;
    }

    public void setStotalAmount(String stotalAmount) {
        StotalAmount = stotalAmount;
    }

    public String getSdish() {
        return Sdish;
    }

    public void setSdish(String sdish) {
        Sdish = sdish;
    }

    public String getSorderDate() {
        return SorderDate;
    }

    public void setSorderDate(String sorderDate) {
        SorderDate = sorderDate;
    }

    public String getSorderNumber() {
        return SorderNumber;
    }

    public void setSorderNumber(String sorderNumber) {
        SorderNumber = sorderNumber;
    }

    public String getSpaymentstatus() {
        return Spaymentstatus;
    }

    public void setSpaymentstatus(String spaymentstatus) {
        Spaymentstatus = spaymentstatus;
    }

    public String getSSingleItemOrMultipleItem() {
        return SSingleItemOrMultipleItem;
    }

    public void setSSingleItemOrMultipleItem(String SSingleItemOrMultipleItem) {
        this.SSingleItemOrMultipleItem = SSingleItemOrMultipleItem;
    }
}
