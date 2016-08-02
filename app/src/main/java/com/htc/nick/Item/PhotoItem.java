package com.htc.nick.Item;

/**
 * Created by nick on 8/2/16.
 */
public class PhotoItem {
    private String name;
    private String uri;

    public PhotoItem(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
