package com.example.trainbites.CustomerFoodPanel;

public class MultipleConfirmOrderModel {
    String Uname;
    String Uphone;
    String Useat;
    String Ucoach;
    String Udish;
    String Utotal;

    public MultipleConfirmOrderModel(String uname, String uphone, String useat, String ucoach, String udish, String utotal) {
        Uname = uname;
        Uphone = uphone;
        Useat = useat;
        Ucoach = ucoach;
        Udish = udish;
        Utotal = utotal;
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
