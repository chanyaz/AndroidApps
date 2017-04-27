package com.shenke.digest.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cloud on 2017/4/12.
 */

public class NewsDigestBean implements Serializable {
    @SerializedName("uuid")
    public String uuid;
    @SerializedName("date")
    public String date;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("poster")
    public Poster poster;

    public class Poster {
        @SerializedName("images")
        public Images images;

        public class Images {
            @SerializedName("originalUrl")
            public String originalUrl;
            @SerializedName("originalWidth")
            public int originalWidth;
            @SerializedName("originalHeight")
            public int originalHeight;
            @SerializedName("mimeType")
            public String mimeType;
            @SerializedName("provider")
            public String provider;
            @SerializedName("title")
            public String title;
            @SerializedName("caption")
            public String caption;
            @SerializedName("resolutions")
            public List<Resolution> resolutions = new ArrayList<>();

            public class Resolution {
                @SerializedName("height")
                public int height;
                @SerializedName("width")
                public int width;
                @SerializedName("url")
                public String url;
                @SerializedName("tag")
                public String tag;
            }
        }
    }

    @SerializedName("posterTablet")
    public PosterTablet posterTablet;

    public class PosterTablet {
        @SerializedName("images")
        public Images images;

        public class Images {
            @SerializedName("originalUrl")
            public String originalUrl;
            @SerializedName("originalWidth")
            public int originalWidth;
            @SerializedName("originalHeight")
            public int originalHeight;
            @SerializedName("mimeType")
            public String mimeType;
            @SerializedName("provider")
            public String provider;
            @SerializedName("title")
            public String title;
            @SerializedName("caption")
            public String caption;
            @SerializedName("resolutions")
            public List<Resolution> resolutions = new ArrayList<>();

            public class Resolution {
                @SerializedName("height")
                public int height;
                @SerializedName("width")
                public int width;
                @SerializedName("url")
                public String url;
                @SerializedName("tag")
                public String tag;
            }
        }
    }

    @SerializedName("bonus")
    public List<Bonus> bonus = new ArrayList<>();


    public class Bonus {
        @SerializedName("id")
        public String id;
        @SerializedName("text")
        public String text;
        @SerializedName("type")
        public String type;
        @SerializedName("source")
        public String source;
    }

    @SerializedName("edition")
    public String edition;
    @SerializedName("items")
    public List<NewsItem> items = new ArrayList<>();

    public class NewsItem {
        @SerializedName("uuid")
        public String uuid;
        @SerializedName("title")
        public String title;
        @SerializedName("clusterId")
        public String clusterId;
        @SerializedName("imageProvider")
        public String imageProvider;
        @SerializedName("published")
        public String published;
        @SerializedName("locations")
        public List<Location> locations = new ArrayList<>();

        public class Location {
            @SerializedName("latitude")
            public String latitude;
            @SerializedName("longitude")
            public String longitude;
            @SerializedName("zoomLevel")
            public String zoomLevel;
            @SerializedName("type")
            public String type;
            @SerializedName("caption")
            public String caption;
            @SerializedName("name")
            public String name;
        }

        @SerializedName("order")
        public List<String> order = new ArrayList<>();
        @SerializedName("colors")
        public List<Color> colors = new ArrayList<>();

        public class Color {
            @SerializedName("r")
            public String r;
            @SerializedName("g")
            public String g;
            @SerializedName("b")
            public String b;
            @SerializedName("tag")
            public String tag;
            @SerializedName("hexcode")
            public String hexcode;
        }

        //stocks、infographs、stats
        @SerializedName("stocks")
        public List<Stock> stocks = new ArrayList<>();

        public class Stock {

        }

        @SerializedName("infographs")
        public List<Infograph> infographs = new ArrayList<>();

        public class Infograph {

        }

        @SerializedName("stats")
        public List<Stat> stats = new ArrayList<>();

        public class Stat {

        }

        @SerializedName("categories")
        public List<Category> categories = new ArrayList<>();

        public class Category {
            @SerializedName("name")
            public String name;
        }

        @SerializedName("multiSummary")
        public List<Summary> multiSummary = new ArrayList<>();

        public class Summary {
            @SerializedName("text")
            public String text;
            @SerializedName("quote")
            public Quote quote;

            public class Quote {
                @SerializedName("url")
                public String url;
                @SerializedName("text")
                public String text;
                @SerializedName("source")
                public String source;
            }
        }

        @SerializedName("wikis")
        public List<Wiki> wikis = new ArrayList<>();

        public class Wiki {
            @SerializedName("id")
            public String id;
            @SerializedName("text")
            public String text;
            @SerializedName("title")
            public String title;
            @SerializedName("searchTerms")
            public List<Term> searchTerms = new ArrayList<>();

            public class Term {
                @SerializedName("term")
                public String term;
            }

            @SerializedName("url")
            public String url;
            @SerializedName("images")
            public Images images ;

            public class Images {
                @SerializedName("originalUrl")
                public String originalUrl;
                @SerializedName("originalWidth")
                public int originalWidth;
                @SerializedName("originalHeight")
                public int originalHeight;
                @SerializedName("mimeType")
                public String mimeType;
                @SerializedName("provider")
                public String provider;
                @SerializedName("title")
                public String title;
                @SerializedName("caption")
                public String caption;
                @SerializedName("resolutions")
                public List<Resolution> resolutions = new ArrayList<>();

                public class Resolution {
                    @SerializedName("height")
                    public int height;
                    @SerializedName("width")
                    public int width;
                    @SerializedName("url")
                    public String url;
                    @SerializedName("tag")
                    public String tag;
                }
            }
        }

        @SerializedName("longreads")
        public List<Longread> longreads = new ArrayList<>();

        public class Longread {
            @SerializedName("title")
            public String title;
            @SerializedName("images")
            public Images images;

            public class Images {
                @SerializedName("originalUrl")
                public String originalUrl;
                @SerializedName("originalWidth")
                public int originalWidth;
                @SerializedName("originalHeight")
                public int originalHeight;
                @SerializedName("mimeType")
                public String mimeType;
                @SerializedName("provider")
                public String provider;
                @SerializedName("title")
                public String title;
                @SerializedName("caption")
                public String caption;
                @SerializedName("resolutions")
                public List<Resolution> resolutions = new ArrayList<>();

                public class Resolution {
                    @SerializedName("height")
                    public int height;
                    @SerializedName("width")
                    public int width;
                    @SerializedName("url")
                    public String url;
                    @SerializedName("tag")
                    public String tag;
                }

                @SerializedName("publisher")
                public String publisher;
                @SerializedName("description")
                public String description;
                @SerializedName("url")
                public String url;

            }
        }

        @SerializedName("images")
        public Images images;

        public class Images {
            @SerializedName("originalUrl")
            public String originalUrl;
            @SerializedName("originalWidth")
            public int originalWidth;
            @SerializedName("originalHeight")
            public int originalHeight;
            @SerializedName("mimeType")
            public String mimeType;
            @SerializedName("provider")
            public String provider;
            @SerializedName("title")
            public String title;
            @SerializedName("caption")
            public String caption;
            @SerializedName("resolutions")
            public List<Resolution> resolutions = new ArrayList<>();

            public class Resolution {
                @SerializedName("height")
                public int height;
                @SerializedName("width")
                public int width;
                @SerializedName("url")
                public String url;
                @SerializedName("tag")
                public String tag;
            }

        }

        @SerializedName("videos")
        public List<Video> videos = new ArrayList<>();

        public class Video {
            @SerializedName("thumbnail")
            public String thumbnail;
            @SerializedName("streams")
            public List<Stream> streams = new ArrayList<>();

            public class Stream {
                @SerializedName("mime_type")
                public String mime_type;
                @SerializedName("duration")
                public String duration;
                @SerializedName("bitrate")
                public String bitrate;
                @SerializedName("width")
                public int width;
                @SerializedName("height")
                public int height;
                @SerializedName("url")
                public String url;
            }

            @SerializedName("title")
            public String title;
            @SerializedName("publisher")
            public String publisher;
            @SerializedName("uuid")
            public String uuid;
            @SerializedName("summary")
            public String summary;
            @SerializedName("sponsor")
            public String sponsor;
        }

        @SerializedName("slideshow")
        public SlideShow slideshow;

        public class SlideShow {
            @SerializedName("photos")
            public Photos photos;

            public class Photos {
                @SerializedName("layout")
                public String layout;
                @SerializedName("total")
                public int total;
                @SerializedName("elements")
                public List<Element> elements = new ArrayList<>();

                public class Element {
                    @SerializedName("headline")
                    public String headline;
                    @SerializedName("provider_name")
                    public String provider_name;
                    @SerializedName("caption")
                    public String caption;
                    @SerializedName("images")
                    public Images images;

                    public class Images {
                        @SerializedName("originalUrl")
                        public String originalUrl;
                        @SerializedName("originalWidth")
                        public int originalWidth;
                        @SerializedName("originalHeight")
                        public int originalHeight;
                        @SerializedName("mimeType")
                        public String mimeType;
                        @SerializedName("provider")
                        public String provider;
                        @SerializedName("title")
                        public String title;
                        @SerializedName("caption")
                        public String caption;
                        @SerializedName("resolutions")
                        public List<Resolution> resolutions = new ArrayList<>();

                        public class Resolution {
                            @SerializedName("height")
                            public int height;
                            @SerializedName("width")
                            public int width;
                            @SerializedName("url")
                            public String url;
                            @SerializedName("tag")
                            public String tag;
                        }
                    }

                }
            }
        }

        @SerializedName("tweetKeywords")
        public List<TweetKeyword> tweetKeywords = new ArrayList<>();

        public class TweetKeyword {

        }

        @SerializedName("tumblrUrl")
        public String tumblrUrl;
        @SerializedName("advertisement")
        public Advertisement advertisement;

        public class Advertisement {
            @SerializedName("enabled")
            public Boolean enabled;
        }

        @SerializedName("weatherLocations")
        public List<WeatherLocation> weatherLocations = new ArrayList<>();

        public class WeatherLocation {

        }

        @SerializedName("sources")
        public List<Source> sources = new ArrayList<>();

        public class Source {
            @SerializedName("published")
            public String published;
            @SerializedName("title")
            public String title;
            @SerializedName("publisher")
            public String publisher;
            @SerializedName("url")
            public String url;
            @SerializedName("uuid")
            public String uuid;
        }

        @SerializedName("statDetail")
        public List<StatDetail> statDetail = new ArrayList<>();

        public class StatDetail {
            @SerializedName("layout")
            public String layout;
            @SerializedName("title")
            public Title title;

            public class Title {
                @SerializedName("text")
                public String text;
                @SerializedName("color")
                public String color;

            }

            @SerializedName("value")
            public Value value;

            public class Value {
                @SerializedName("text")
                public String text;
                @SerializedName("color")
                public String color;

            }

            @SerializedName("units")
            public Units units;

            public class Units {
                @SerializedName("text")
                public String text;
                @SerializedName("color")
                public String color;

            }

            @SerializedName("description")
            public Description description;

            public class Description {
                @SerializedName("text")
                public String text;
                @SerializedName("color")
                public String color;

            }

            @SerializedName("tragedy")
            public Tragedy tragedy;

            public class Tragedy {
                @SerializedName("enabled")
                public Boolean enabled;
                @SerializedName("color")
                public String color;

            }
        }

        @SerializedName("tabletImages")
        public TabletImages tabletImages;

        public class TabletImages {
            @SerializedName("originalUrl")
            public String originalUrl;
            @SerializedName("originalWidth")
            public int originalWidth;
            @SerializedName("originalHeight")
            public int originalHeight;
            @SerializedName("mimeType")
            public String mimeType;
            @SerializedName("provider")
            public String provider;
            @SerializedName("title")
            public String title;
            @SerializedName("caption")
            public String caption;
            @SerializedName("resolutions")
            public List<Resolution> resolutions = new ArrayList<>();

            public class Resolution {
                @SerializedName("height")
                public int height;
                @SerializedName("width")
                public int width;
                @SerializedName("url")
                public String url;
                @SerializedName("tag")
                public String tag;
            }
        }

        @SerializedName("webpageUrl")
        public String webpageUrl;

        @SerializedName("deeplinkIOS")
        public String deeplinkIOS;
        @SerializedName("deeplinkAndroid")
        public String deeplinkAndroid;
        @SerializedName("watchImage")
        public WatchImage watchImage;

        public class WatchImage {
            @SerializedName("originalUrl")
            public String originalUrl;
            @SerializedName("originalWidth")
            public int originalWidth;
            @SerializedName("originalHeight")
            public int originalHeight;
            @SerializedName("mimeType")
            public String mimeType;
            @SerializedName("provider")
            public String provider;
            @SerializedName("title")
            public String title;
            @SerializedName("caption")
            public String caption;
            @SerializedName("resolutions")
            public List<Resolution> resolutions = new ArrayList<>();

            public class Resolution {
                @SerializedName("height")
                public int height;
                @SerializedName("width")
                public int width;
                @SerializedName("url")
                public String url;
                @SerializedName("tag")
                public String tag;
            }
        }

        @SerializedName("speedReading")
        public String speedReading;
        @SerializedName("selectedHourlyAtom")
        public String selectedHourlyAtom;
        @SerializedName("subtitle")
        public String subtitle;
        @SerializedName("metadata")
        public String metadata;
        @SerializedName("keywords")
        public List<Keyword> keywords = new ArrayList<>();

        public class Keyword {
            @SerializedName("text")
            public String text;
        }
    }

    @SerializedName("lang")
    public String lang;
    @SerializedName("regionEdition")
    public String regionEdition;
    @SerializedName("more_stories")
    public String more_stories;
    @SerializedName("region")
    public String region;
    @SerializedName("timezone")
    public String timezone;
    @SerializedName("videos")
    public List<Vedio> videos = new ArrayList<>();

    public class Vedio {

    }

    @SerializedName("digestTopKey")
    public String digestTopKey;

}
