package com.gess.textvideo;

public class TextVideoBean {
    private String id;
    private long moment;//单位毫秒
    private String content;

    public long getMoment() {
        return moment;
    }

    public void setMoment(long moment) {
        this.moment = moment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TextVideoBean) {
            return ((TextVideoBean) obj).id.equals(this.id);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "TextVideoBean{" +
                "id='" + id + '\'' +
                ", moment='" + moment + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
