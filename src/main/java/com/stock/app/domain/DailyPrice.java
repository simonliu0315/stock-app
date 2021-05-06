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

    @Column(name= "moving_average_5")
    private BigDecimal movingAverage5;

    @Column(name= "moving_average_10")
    private BigDecimal movingAverage10;

    @Column(name= "moving_average_20")
    private BigDecimal movingAverage20;

    @Column(name= "moving_average_60")
    private BigDecimal movingAverage60;

    @Column(name= "moving_average_120")
    private BigDecimal movingAverage120;

    @Column(name= "moving_average_240")
    private BigDecimal movingAverage240;

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

    public BigDecimal getMovingAverage5() {
        return movingAverage5;
    }

    public void setMovingAverage5(BigDecimal movingAverage5) {
        this.movingAverage5 = movingAverage5;
    }

    public BigDecimal getMovingAverage10() {
        return movingAverage10;
    }

    public void setMovingAverage10(BigDecimal movingAverage10) {
        this.movingAverage10 = movingAverage10;
    }

    public BigDecimal getMovingAverage20() {
        return movingAverage20;
    }

    public void setMovingAverage20(BigDecimal movingAverage20) {
        this.movingAverage20 = movingAverage20;
    }

    public BigDecimal getMovingAverage60() {
        return movingAverage60;
    }

    public void setMovingAverage60(BigDecimal movingAverage60) {
        this.movingAverage60 = movingAverage60;
    }

    public BigDecimal getMovingAverage120() {
        return movingAverage120;
    }

    public void setMovingAverage120(BigDecimal movingAverage120) {
        this.movingAverage120 = movingAverage120;
    }

    public BigDecimal getMovingAverage240() {
        return movingAverage240;
    }

    public void setMovingAverage240(BigDecimal movingAverage240) {
        this.movingAverage240 = movingAverage240;
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
            ", movingAverage5=" + movingAverage5 +
            ", movingAverage10=" + movingAverage10 +
            ", movingAverage20=" + movingAverage20 +
            ", movingAverage60=" + movingAverage60 +
            ", movingAverage120=" + movingAverage120 +
            ", movingAverage240=" + movingAverage240 +
            '}';
    }
}
