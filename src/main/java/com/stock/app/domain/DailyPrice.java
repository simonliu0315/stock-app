package com.stock.app.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "daily_price")
public class DailyPrice<BigDeciaml> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "no")
    private String no;

    @Column(name = "date")
    private String date;

    @Column(name = "txn_number")
    private BigDecimal txnNumber;

    @Column(name = "txn_amount")
    private BigDecimal txnAmount;

    @Column(name = "start_price")
    private BigDecimal startPrice;

    @Column(name = "high_price")
    private BigDecimal highPrice;

    @Column(name = "low_price")
    private BigDecimal lowPrice;

    @Column(name = "end_price")
    private BigDecimal endPrice;

    @Column(name = "high_low_price")
    private BigDecimal highLowPrice;

    @Column(name = "txn_count")
    private BigDecimal txnCount;

    @Column(name = "notes")
    private String notes;

    @Column(name= "high_low_price_sign")
    private String highLowPriceSign;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getTxnNumber() {
        return txnNumber;
    }

    public void setTxnNumber(BigDecimal txnNumber) {
        this.txnNumber = txnNumber;
    }

    public BigDecimal getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(BigDecimal txnAmount) {
        this.txnAmount = txnAmount;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public BigDecimal getHighLowPrice() {
        return highLowPrice;
    }

    public void setHighLowPrice(BigDecimal highLowPrice) {
        this.highLowPrice = highLowPrice;
    }

    public BigDecimal getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(BigDecimal txnCount) {
        this.txnCount = txnCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getHighLowPriceSign() {
        return highLowPriceSign;
    }

    public void setHighLowPriceSign(String highLowPriceSign) {
        this.highLowPriceSign = highLowPriceSign;
    }

    @Override
    public String toString() {
        return "DailyPrice{" +
            "id=" + id +
            ", no='" + no + '\'' +
            ", date='" + date + '\'' +
            ", txnNumber=" + txnNumber +
            ", txnAmount=" + txnAmount +
            ", startPrice=" + startPrice +
            ", highPrice=" + highPrice +
            ", lowPrice=" + lowPrice +
            ", endPrice=" + endPrice +
            ", highLowPrice=" + highLowPrice +
            ", txnCount=" + txnCount +
            ", notes='" + notes + '\'' +
            ", highLowPriceSign='" + highLowPriceSign + '\'' +
            '}';
    }
}
