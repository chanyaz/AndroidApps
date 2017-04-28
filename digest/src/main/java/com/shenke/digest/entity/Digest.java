package com.shenke.digest.entity;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Cloud on 2017/4/27.
 */

public class Digest {
    private RealmList<ItemRealm> itemRealms;

    private String uuid;

    private String date;

    private String createTime;

    private Poster poster;

    private List<Bonu> bonus = new ArrayList<>();

    private String edition;
    private String lang;
    private String regionEdition;
    private String more_stories;
    private String region;
    private String timezone;
    private String digestTopKey;

    public RealmList<ItemRealm> getItemRealms() {
        return itemRealms;
    }

    public void setItemRealms(RealmList<ItemRealm> itemRealms) {
        this.itemRealms = itemRealms;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Poster getPoster() {
        return poster;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public List<Bonu> getBonus() {
        return bonus;
    }

    public void setBonus(List<Bonu> bonus) {
        this.bonus = bonus;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRegionEdition() {
        return regionEdition;
    }

    public void setRegionEdition(String regionEdition) {
        this.regionEdition = regionEdition;
    }

    public String getMore_stories() {
        return more_stories;
    }

    public void setMore_stories(String more_stories) {
        this.more_stories = more_stories;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDigestTopKey() {
        return digestTopKey;
    }

    public void setDigestTopKey(String digestTopKey) {
        this.digestTopKey = digestTopKey;
    }

    @Override
    public String toString() {
        return "Digest{" +
                "itemRealms=" + itemRealms +
                ", uuid='" + uuid + '\'' +
                ", date='" + date + '\'' +
                ", createTime='" + createTime + '\'' +
                ", poster=" + poster +
                ", bonus=" + bonus +
                ", edition='" + edition + '\'' +
                ", lang='" + lang + '\'' +
                ", regionEdition='" + regionEdition + '\'' +
                ", more_stories='" + more_stories + '\'' +
                ", region='" + region + '\'' +
                ", timezone='" + timezone + '\'' +
                ", digestTopKey='" + digestTopKey + '\'' +
                '}';
    }
}
