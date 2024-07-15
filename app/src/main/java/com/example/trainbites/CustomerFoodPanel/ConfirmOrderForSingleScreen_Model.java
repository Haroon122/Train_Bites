package com.example.trainbites.CustomerFoodPanel;

public class ConfirmOrderForSingleScreen_Model {
    String Uname;
    String Uphone;
    String Useat;
    String Ucoach;
    String UtotalAmount;
    String Udish;

    public ConfirmOrderForSingleScreen_Model(String uname, String uphone, String useat, String ucoach, String utotalAmount, String udish) {
        Uname = uname;
        Uphone = uphone;
        Useat = useat;
        Ucoach = ucoach;
        UtotalAmount = utotalAmount;
        Udish=udish;

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
}
