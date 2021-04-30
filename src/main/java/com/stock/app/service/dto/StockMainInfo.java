package com.stock.app.service.dto;

import com.google.gson.annotations.SerializedName;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StockMainInfo implements Serializable {

    @SerializedName("出表日期")
    private String no;

    @SerializedName("公司代號")
    private String name;

    @SerializedName("公司名稱")
    private String description;

    @SerializedName("公司簡稱")
    private String type;

    @SerializedName("產業別")
    private Date updated;

    @SerializedName("住址")
    private String fullName;

    @SerializedName("住址1")
    private String companyAddress;

    @SerializedName("營利事業統一編號")
    private String taxid;

    @SerializedName("数据")
    private String ceo;

    @SerializedName("網址")
    private Date buildDate;

    @SerializedName("数据13")
    private Date ongoingDate;

    @SerializedName("数据3")
    private BigDecimal capital;

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

    public String getCeo() {
        return ceo;
    }

    public void setCeo(String ceo) {
        this.ceo = ceo;
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

    @Override
    public String toString() {
        return "StockMainInfo{" +
            "no='" + no + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type='" + type + '\'' +
            ", updated=" + updated +
            ", fullName='" + fullName + '\'' +
            ", companyAddress='" + companyAddress + '\'' +
            ", taxid='" + taxid + '\'' +
            ", ceo='" + ceo + '\'' +
            ", buildDate=" + buildDate +
            ", ongoingDate=" + ongoingDate +
            ", capital=" + capital +
            '}';
    }
}
