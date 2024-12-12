package com.example.trainbites.ChefFoodPanel;

public class ChefPendingMultipleOrderModel {
    private String Mname;
    private String Mphone;
    private String Mseat;
    private String Mcoach;
    private String MtotalAmount;
    private String Mdish;
    private String MorderDate;
    private String MorderNumber;
    private String Mpaymentstatus;

    public ChefPendingMultipleOrderModel() {
        // Default constructor
    }

    public ChefPendingMultipleOrderModel(String mname, String mphone, String mseat, String mcoach, String mtotalAmount,
                                       String mdish, String morderDate, String morderNumber, String mpaymentstatus) {
        this.Mname = mname;
        this.Mphone = mphone;
        this.Mseat = mseat;
        this.Mcoach = mcoach;
        this.MtotalAmount = mtotalAmount;
        this.Mdish = mdish;
        this.MorderDate = morderDate;
        this.MorderNumber = morderNumber;
        this.Mpaymentstatus = mpaymentstatus;
    }

    public String getMname() {
        return Mname;
    }

    public void setMname(String mname) {
        Mname = mname;
    }

    public String getMphone() {
        return Mphone;
    }

    public void setMphone(String mphone) {
        Mphone = mphone;
    }

    public String getMseat() {
        return Mseat;
    }

    public void setMseat(String mseat) {
        Mseat = mseat;
    }

    public String getMcoach() {
        return Mcoach;
    }

    public void setMcoach(String mcoach) {
        Mcoach = mcoach;
    }

    public String getMtotalAmount() {
        return MtotalAmount;
    }

    public void setMtotalAmount(String mtotalAmount) {
        MtotalAmount = mtotalAmount;
    }

    public String getMdish() {
        return Mdish;
    }

    public void setMdish(String mdish) {
        Mdish = mdish;
    }

    public String getMorderDate() {
        return MorderDate;
    }

    public void setMorderDate(String morderDate) {
        MorderDate = morderDate;
    }

    public String getMorderNumber() {
        return MorderNumber;
    }

    public void setMorderNumber(String morderNumber) {
        MorderNumber = morderNumber;
    }

    public String getMpaymentstatus() {
        return Mpaymentstatus;
    }

    public void setMpaymentstatus(String mpaymentstatus) {
        Mpaymentstatus = mpaymentstatus;
    }


}
