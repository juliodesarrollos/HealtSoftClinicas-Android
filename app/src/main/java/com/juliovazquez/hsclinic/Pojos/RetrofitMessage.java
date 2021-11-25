package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetrofitMessage {

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
