package com.stock.app.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "weekly_holder")
public class WeeklyHolder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "no")
    private String no;

    @Column(name = "date")
    private String date;

    @Column(name = "level")
    private BigDecimal level;

    @Column(name = "level_name")
    private String levelName;

    @Column(name = "sum_people")
    private BigDecimal sum_people;

    @Column(name = "sum_stock")
    private BigDecimal sum_stock;

    @Column(name = "percentage")
    private BigDecimal percentage;


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

    public BigDecimal getLevel() {
        return level;
    }

    public void setLevel(BigDecimal level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public BigDecimal getSum_people() {
        return sum_people;
    }

    public void setSum_people(BigDecimal sum_people) {
        this.sum_people = sum_people;
    }

    public BigDecimal getSum_stock() {
        return sum_stock;
    }

    public void setSum_stock(BigDecimal sum_stock) {
        this.sum_stock = sum_stock;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "WeeklyHolder{" +
            "id=" + id +
            ", no='" + no + '\'' +
            ", date='" + date + '\'' +
            ", level='" + level + '\'' +
            ", levelName='" + levelName + '\'' +
            ", sum_people=" + sum_people +
            ", sum_stock=" + sum_stock +
            ", percentage=" + percentage +
            '}';
    }
}
