package com.msproject.myhome.dailycloset;

import org.joda.time.LocalDate;

public class Picture {
    String fileName;
    String imgURL;

    public Picture(String fileName, String imgURL) {
        this.fileName = fileName;
        this.imgURL = imgURL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
