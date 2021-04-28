package com.stock.app.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A MainStock.
 */
@Entity
@Table(name = "main_stock")
public class MainStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "no")
    private String no;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "updated")
    private Date updated;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "tax_id")
    private String taxid;

    @Column(name = "ceo")
    private String ceo;

    @Column(name = "build_date")
    private Date buildDate;

    @Column(name = "ongoing_date")
    private Date ongoingDate;

    @Column(name = "capital")
    private BigDecimal capital;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid(String taxid) {
        this.taxid = taxid;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(Date buildDate) {
        this.buildDate = buildDate;
    }

    public Date getOngoingDate() {
        return ongoingDate;
    }

    public void setOngoingDate(Date ongoingDate) {
        this.ongoingDate = ongoingDate;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public String getCeo() {
        return ceo;
    }

    public void setCeo(String ceo) {
        this.ceo = ceo;
    }

    @Override
    public String toString() {
        return "MainStock{" +
            "id=" + id +
            ", no='" + no + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type='" + type + '\'' +
            ", updated=" + updated +
            ", fullName='" + fullName + '\'' +
            ", companyAddress='" + companyAddress + '\'' +
            ", taxid='" + taxid + '\'' +
            ", ceo='" + ceo + '\'' +
            ", taxid='" + taxid + '\'' +
            ", buildDate=" + buildDate +
            ", ongoingDate=" + ongoingDate +
            ", capital=" + capital +
            '}';
    }
}
