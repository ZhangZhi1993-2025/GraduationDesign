package cn.edu.njnu.domain.impl;

import cn.edu.njnu.domain.Extractable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class News implements Extractable, Serializable {
    private static final long serialVersionUID = 7373984972572414692L;
    private String title;
    private String time;
    private String content;

    public News() {
    }

    public News(String title, String time, String content) {
        this.title = title;
        this.time = time;
        this.content = content;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTime() {
        return this.time;
    }

    public String getContent() {
        return this.content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        return "News{title=\'" + this.title + '\'' + ", time=" + this.time + ", content=\'" + this.content + '\'' + '}';
    }

    public List<String> targetSamples() {
        List<String> list = new ArrayList<>();
        list.add(this.getTitle());
        list.add(this.getTime());
        list.add(this.getContent());
        return list;
    }

    public void generateObject(List<String> list) {
        this.setTitle(list.get(0));
        this.setTime(list.get(1));
        this.setContent(list.get(2));
    }

    public Extractable getInstance() {
        return new News();
    }
}
