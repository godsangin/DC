package com.msproject.myhome.dailycloset;

public class SettingItem {
    String title;
    String content;
    boolean checkboxVisibility;

    public SettingItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCheckboxVisibility() {
        return checkboxVisibility;
    }

    public void setCheckboxVisibility(boolean checkboxVisibility) {
        this.checkboxVisibility = checkboxVisibility;
    }
}
