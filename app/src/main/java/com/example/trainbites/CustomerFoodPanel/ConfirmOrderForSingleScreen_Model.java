package com.example.trainbites.CustomerFoodPanel;

public class ConfirmOrderForSingleScreen_Model {
    String Uname;
    String Uphone;
    String Useat;
    String Ucoach;
    String UtotalAmount;
    String Udish;
    String orderDate;
    String orderNumber;
    String paymentstatus;
    String SingleItemOrMultipleItem;
    // Default constructor (required for Firebase)
    public ConfirmOrderForSingleScreen_Model() {
    }

    // Parameterized constructor
    public ConfirmOrderForSingleScreen_Model(String uname, String uphone, String useat, String ucoach,
                                             String utotalAmount, String udish, String orderDate,
                                             String orderNumber, String paymentstatus, String singleItemOrMultipleItem) {
        this.Uname = uname;
        this.Uphone = uphone;
        this.Useat = useat;
        this.Ucoach = ucoach;
        this.UtotalAmount = utotalAmount;
        this.Udish = udish;
        this.orderDate = orderDate;
        this.orderNumber = orderNumber;
        this.paymentstatus = paymentstatus;
        this.SingleItemOrMultipleItem = singleItemOrMultipleItem;
    }

    // Getters and Setters
    public String getUname() {
        return Uname;
    }

    public void setUname(String uname) {
        Uname = uname;
    }

    public String getUphone() {
        return Uphone;
    }

    public void setUphone(String uphone) {
        Uphone = uphone;
    }

    public String getUseat() {
        return Useat;
    }

    public void setUseat(String useat) {
        Useat = useat;
    }

    public String getUcoach() {
        return Ucoach;
    }

    public void setUcoach(String ucoach) {
        Ucoach = ucoach;
    }

    public String getUtotalAmount() {
        return UtotalAmount;
    }

    public void setUtotalAmount(String utotalAmount) {
        UtotalAmount = utotalAmount;
    }

    public String getUdish() {
        return Udish;
    }

    public void setUdish(String udish) {
        Udish = udish;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public String getSingleItemOrMultipleItem() {
        return SingleItemOrMultipleItem;
    }

    public void setSingleItemOrMultipleItem(String singleItemOrMultipleItem) {
        this.SingleItemOrMultipleItem = singleItemOrMultipleItem;
    }

}
