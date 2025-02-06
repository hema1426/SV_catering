package com.winapp.svcatering.model;

public class UserRoll {

    private String formCode;
    private String formName;
    private String havePermission;
    private String isActive;

    public String getHavePermission() {
        return havePermission;
    }

    public void setHavePermission(String havePermission) {
        this.havePermission = havePermission;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }


    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
