package com.sample.exchange.model;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;


@Entity
@Table(name="ExchangeRate")
public class ExchangeRate implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private BigDecimal rate;
    private String currency;
    private String date;
}
