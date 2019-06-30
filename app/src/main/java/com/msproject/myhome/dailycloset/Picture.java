package com.msproject.myhome.dailycloset;

import org.joda.time.LocalDate;

public class Picture {
    LocalDate localDate;
    String imgURL;

    public Picture(LocalDate localDate, String imgURL){
        this.localDate = localDate;
        this.imgURL = imgURL;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
