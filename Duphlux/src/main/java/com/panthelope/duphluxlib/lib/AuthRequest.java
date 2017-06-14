package com.panthelope.duphluxlib.lib;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by ikenna on 05/06/2017.
 */

public class AuthRequest {

    protected String phone_number;
    protected String timeout;
    protected String transaction_reference;
    protected String redirect_url;
    protected String full_name;
    protected String email_address;

    public AuthRequest() {
        setTransaction_reference(generateReference());
    }

    public AuthRequest(String reference) {
        if (!reference.isEmpty()) {
            setTransaction_reference(reference);
        }
    }

    private String generateReference() {
        final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }

    public Boolean isValid() throws DuphluxException {
        if (getPhone_number() == null) {
            throw new DuphluxException("Please provide a valid phone number to authenticate");
        }
        return true;
    }

    protected String toJsonString() {
        String json;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_reference", getTransaction_reference());
            jsonObject.put("phone_number", getPhone_number());
            jsonObject.put("redirect_url", getRedirect_url());
            if (getTimeout() != null) {
                jsonObject.put("timeout", getTimeout());
            }
            if (getFull_name() != null) {
                jsonObject.put("name", getFull_name());
            }
            if (getEmail_address() != null) {
                jsonObject.put("email_address", getEmail_address());
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getTransaction_reference() {
        return transaction_reference;
    }

    public void setTransaction_reference(String transaction_reference) {
        this.transaction_reference = transaction_reference;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }
}
