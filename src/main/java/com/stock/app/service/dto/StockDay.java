package com.stock.app.service.dto;

import java.util.Arrays;

public class StockDay {

    private String stat;
    private String date;
    private String title;
    private String[] fields;
    private String[][] data;
    private String[] notes;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public String[] getNotes() {
        return notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "StockDay{" +
            "stat='" + stat + '\'' +
            ", date='" + date + '\'' +
            ", title='" + title + '\'' +
            ", fields=" + Arrays.toString(fields) +
            ", data=" + Arrays.toString(data) +
            ", notes='" + Arrays.toString(notes) + '\'' +
            '}';
    }
}
