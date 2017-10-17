package com.nse.model.response;

/**
 * Created by srikanth on 17/10/17.
 */
public class Annonuncements {
    private String company;

    private String date;

    private String desc;

    private String link;

    private String  symbol;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "Annonuncements{" +
                "company='" + company + '\'' +
                ", date='" + date + '\'' +
                ", desc='" + desc + '\'' +
                ", link='" + link + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
