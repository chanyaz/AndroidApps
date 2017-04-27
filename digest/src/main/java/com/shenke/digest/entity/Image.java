package com.shenke.digest.entity;


import io.realm.RealmList;
import io.realm.RealmObject;

public class Image extends RealmObject {
    private String url;
    private int width;
    private int height;
    private String mimeType;
    private String provider;
    private String title;
    private String caption;
    private RealmList<ImageAsset> image_assets;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public RealmList<ImageAsset> getImage_assets() {
        return image_assets;
    }

    public void setImage_assets(RealmList<ImageAsset> image_assets) {
        this.image_assets = image_assets;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", mimeType='" + mimeType + '\'' +
                ", provider='" + provider + '\'' +
                ", title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                ", image_assets=" + image_assets +
                '}';
    }
}
