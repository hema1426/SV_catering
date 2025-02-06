package com.winapp.svcatering.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Categories implements Serializable {

    @SerializedName("item")
    @Expose
    private ArrayList<Item> item = null;

    public ArrayList<Item> getItem() {
        return item;
    }
    public void setItem(ArrayList<Item> item) {
        this.item = item;
    }

    public static class Item {

        @SerializedName("category_id")
        @Expose
        private String categoryId;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("parent_id")
        @Expose
        private String parentId;
        @SerializedName("top")
        @Expose
        private String top;
        @SerializedName("column")
        @Expose
        private String column;
        @SerializedName("sort_order")
        @Expose
        private String sortOrder;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("date_added")
        @Expose
        private String dateAdded;
        @SerializedName("date_modified")
        @Expose
        private String dateModified;
        @SerializedName("secondary_image")
        @Expose
        private String secondaryImage;
        @SerializedName("alternative_image")
        @Expose
        private String alternativeImage;
        @SerializedName("is_featured")
        @Expose
        private String isFeatured;
        @SerializedName("language_id")
        @Expose
        private String languageId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("meta_title")
        @Expose
        private String metaTitle;
        @SerializedName("meta_description")
        @Expose
        private String metaDescription;
        @SerializedName("meta_keyword")
        @Expose
        private String metaKeyword;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDateAdded() {
            return dateAdded;
        }

        public void setDateAdded(String dateAdded) {
            this.dateAdded = dateAdded;
        }

        public String getDateModified() {
            return dateModified;
        }

        public void setDateModified(String dateModified) {
            this.dateModified = dateModified;
        }

        public String getSecondaryImage() {
            return secondaryImage;
        }

        public void setSecondaryImage(String secondaryImage) {
            this.secondaryImage = secondaryImage;
        }

        public String getAlternativeImage() {
            return alternativeImage;
        }

        public void setAlternativeImage(String alternativeImage) {
            this.alternativeImage = alternativeImage;
        }

        public String getIsFeatured() {
            return isFeatured;
        }

        public void setIsFeatured(String isFeatured) {
            this.isFeatured = isFeatured;
        }

        public String getLanguageId() {
            return languageId;
        }

        public void setLanguageId(String languageId) {
            this.languageId = languageId;
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

        public String getMetaTitle() {
            return metaTitle;
        }

        public void setMetaTitle(String metaTitle) {
            this.metaTitle = metaTitle;
        }

        public String getMetaDescription() {
            return metaDescription;
        }

        public void setMetaDescription(String metaDescription) {
            this.metaDescription = metaDescription;
        }

        public String getMetaKeyword() {
            return metaKeyword;
        }

        public void setMetaKeyword(String metaKeyword) {
            this.metaKeyword = metaKeyword;
        }

    }

}
