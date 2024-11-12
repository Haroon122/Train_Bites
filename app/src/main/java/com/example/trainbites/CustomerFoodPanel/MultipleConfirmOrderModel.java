package com.example.trainbites.CustomerFoodPanel;

public class MultipleConfirmOrderModel {
    String Uname;
    String Uphone;
    String Useat;
    String Ucoach;
    String Udish;
    String Utotal;
    String orderDate;
    String orderNumber;
    String paymentstatus;
    String SingleItemOrMultipleItem;

    public MultipleConfirmOrderModel(){

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
        SingleItemOrMultipleItem = singleItemOrMultipleItem;
    }
    public MultipleConfirmOrderModel(String uname, String uphone, String useat, String ucoach, String udish, String utotal, String orderdate, String ordernumber, String singleItemOrMultipleItem, String paymentstatus) {
        Uname = uname;
        Uphone = uphone;
        Useat = useat;
        Ucoach = ucoach;
        Udish = udish;
        Utotal = utotal;
        orderDate = orderdate;
        orderNumber = ordernumber;
        this.paymentstatus = paymentstatus;
        this.SingleItemOrMultipleItem = singleItemOrMultipleItem;
    }

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

    public String getUdish() {
        return Udish;
    }

    public void setUdish(String udish) {
        Udish = udish;
    }

    public String getUtotal() {
        return Utotal;
    }

    public void setUtotal(String utotal) {
        Utotal = utotal;
    }
}
