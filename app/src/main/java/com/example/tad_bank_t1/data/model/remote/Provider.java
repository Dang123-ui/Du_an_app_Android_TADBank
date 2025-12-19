package com.example.tad_bank_t1.data.model.remote;

import java.util.List;

public class Provider {
    public String providerId;
    public String type;
    public String name;
    public String logo;
    public List<String> keywords;
    public List<String> phones;
    public int fee;

    public Provider(String providerId, String type, String name, String logo, List<String> keywords, List<String> phones, int fee) {
        this.providerId = providerId;
        this.type = type;
        this.name = name;
        this.logo = logo;
        this.keywords = keywords;
        this.phones = phones;
        this.fee = fee;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "providerId='" + providerId + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", keywords=" + keywords +
                ", phones=" + phones +
                ", fee=" + fee +
                '}';
    }
}
