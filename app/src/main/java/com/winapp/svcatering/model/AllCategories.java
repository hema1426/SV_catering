package com.winapp.svcatering.model;

public class AllCategories {

    public String companyCode;
    public String categoryCode;
    public String categoryName;
    public String cateGoryGroupName;
    public String displayOrder;
    public String productPrefix;
    public String errorMessage;
    public String categoryImage;
    public String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive;
    public boolean showOnPos;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCateGoryGroupName() {
        return cateGoryGroupName;
    }

    public void setCateGoryGroupName(String cateGoryGroupName) {
        this.cateGoryGroupName = cateGoryGroupName;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getProductPrefix() {
        return productPrefix;
    }

    public void setProductPrefix(String productPrefix) {
        this.productPrefix = productPrefix;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isShowOnPos() {
        return showOnPos;
    }

    public void setShowOnPos(boolean showOnPos) {
        this.showOnPos = showOnPos;
    }
}
