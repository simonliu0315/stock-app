package com.stock.app.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "twt38u")
public class TWT38U implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "no")
    private String no;

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private String date;

    @Column(name = "sign")
    private String sign;

    @Column(name = "foreign_china_buy_number_noself")
    private BigDecimal foreignChinaBuyNumberNoself;

    @Column(name = "foreign_china_sell_number_noself")
    private BigDecimal foreignChinaSellNumberNoself;

    @Column(name = "foreign_china_buy_or_sell_number_noself")
    private BigDecimal foreignChinaBuyOrSellNumberNoself;

    @Column(name = "foreign_self_buy_number")
    private BigDecimal foreignBuyNumber;

    @Column(name = "foreign_self_sell_number")
    private BigDecimal foreignSellNumber;

    @Column(name = "foreign_self_buy_or_sell_number")
    private BigDecimal foreignBuyOrSellNumber;

    @Column(name = "foreign_china_buy_number")
    private BigDecimal foreignChinaBuyNumber;

    @Column(name = "foreign_china_sell_number")
    private BigDecimal foreignChinaSellNumber;

    @Column(name = "foreign_china_buy_or_sell_number")
    private BigDecimal foreignChinaBuyOrSellNumber;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public BigDecimal getForeignChinaBuyNumberNoself() {
        return foreignChinaBuyNumberNoself;
    }

    public void setForeignChinaBuyNumberNoself(BigDecimal foreignChinaBuyNumberNoself) {
        this.foreignChinaBuyNumberNoself = foreignChinaBuyNumberNoself;
    }

    public BigDecimal getForeignChinaSellNumberNoself() {
        return foreignChinaSellNumberNoself;
    }

    public void setForeignChinaSellNumberNoself(BigDecimal foreignChinaSellNumberNoself) {
        this.foreignChinaSellNumberNoself = foreignChinaSellNumberNoself;
    }

    public BigDecimal getForeignChinaBuyOrSellNumberNoself() {
        return foreignChinaBuyOrSellNumberNoself;
    }

    public void setForeignChinaBuyOrSellNumberNoself(BigDecimal foreignChinaBuyOrSellNumberNoself) {
        this.foreignChinaBuyOrSellNumberNoself = foreignChinaBuyOrSellNumberNoself;
    }

    public BigDecimal getForeignBuyNumber() {
        return foreignBuyNumber;
    }

    public void setForeignBuyNumber(BigDecimal foreignBuyNumber) {
        this.foreignBuyNumber = foreignBuyNumber;
    }

    public BigDecimal getForeignSellNumber() {
        return foreignSellNumber;
    }

    public void setForeignSellNumber(BigDecimal foreignSellNumber) {
        this.foreignSellNumber = foreignSellNumber;
    }

    public BigDecimal getForeignBuyOrSellNumber() {
        return foreignBuyOrSellNumber;
    }

    public void setForeignBuyOrSellNumber(BigDecimal foreignBuyOrSellNumber) {
        this.foreignBuyOrSellNumber = foreignBuyOrSellNumber;
    }

    public BigDecimal getForeignChinaBuyNumber() {
        return foreignChinaBuyNumber;
    }

    public void setForeignChinaBuyNumber(BigDecimal foreignChinaBuyNumber) {
        this.foreignChinaBuyNumber = foreignChinaBuyNumber;
    }

    public BigDecimal getForeignChinaSellNumber() {
        return foreignChinaSellNumber;
    }

    public void setForeignChinaSellNumber(BigDecimal foreignChinaSellNumber) {
        this.foreignChinaSellNumber = foreignChinaSellNumber;
    }

    public BigDecimal getForeignChinaBuyOrSellNumber() {
        return foreignChinaBuyOrSellNumber;
    }

    public void setForeignChinaBuyOrSellNumber(BigDecimal foreignChinaBuyOrSellNumber) {
        this.foreignChinaBuyOrSellNumber = foreignChinaBuyOrSellNumber;
    }

    @Override
    public String toString() {
        return "TWT38U{" +
            "id=" + id +
            ", no='" + no + '\'' +
            ", name='" + name + '\'' +
            ", date='" + date + '\'' +
            ", sign='" + sign + '\'' +
            ", foreignChinaBuyNumberNoself=" + foreignChinaBuyNumberNoself +
            ", foreignChinaSellNumberNoself=" + foreignChinaSellNumberNoself +
            ", foreignChinaBuyOrSellNumberNoself=" + foreignChinaBuyOrSellNumberNoself +
            ", foreignBuyNumber=" + foreignBuyNumber +
            ", foreignSellNumber=" + foreignSellNumber +
            ", foreignBuyOrSellNumber=" + foreignBuyOrSellNumber +
            ", foreignChinaBuyNumber=" + foreignChinaBuyNumber +
            ", foreignChinaSellNumber=" + foreignChinaSellNumber +
            ", foreignChinaBuyOrSellNumber=" + foreignChinaBuyOrSellNumber +
            '}';
    }
}
