package cn.edu.njnu.infoextract;

import cn.edu.njnu.Main;
import cn.edu.njnu.domain.Extractable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangzhi on 15-12-19.
 * 信息抽取抽象类
 */
public abstract class InfoExtract {

    /**
     * DOM树根
     */
    protected Element root;

    /**
     * 从<title></title>标签抽取出来的该页面的题目(主题)
     */
    protected String theme;

    private class InnerMap {

    }

    /**
     * <K,V>: K:标签名;     V:标签里的内容
     */
    protected List<Map<String, String>> dataList = new ArrayList<>();

    /**
     * default constructor
     */
    public InfoExtract() {
    }

    /**
     * 构造器
     *
     * @param root DOM树根
     */
    public InfoExtract(Element root) {
        this.root = root;
    }

    /**
     * 从DOM树里提取可能的信息组放入dataList容器里,同时抽取<title></title>中的主题
     */
    protected void parseHtmlDOM() {
        Element title = root.select("title").first();
        if (title != null)
            theme = title.ownText();
        //优先根据<h*></h*>大标题来定位信息
        for (int i = 1; i < 7; i++) {
            Element head = root.select("h" + i).first();
            if (head != null) {
                int index = 0;
                HashMap<String, String> map = new HashMap<>();
                //map.put(head.tagName())
                if (!head.children().isEmpty()) {
                    for (Element e : head.children())
                        map.put(e.tagName() + index++, e.text());
                }
                Elements siblings = head.siblingElements();
                for (Element e : siblings)
                    map.put(e.tagName() + index++, e.text());
                dataList.add(map);
            }
        }
        //如果没有<h*></h*>标签,则根据<p>标签来定位信息
        if (dataList.isEmpty()) {
            for (Element p : root.select("p")) {
                if (!p.className().equals("hasvisited")) {
                    int index = 0;
                    HashMap<String, String> map = new HashMap<>();
                    p.attr("class", "hasvisited");
                    Elements siblings = p.siblingElements();
                    for (Element e : siblings) {
                        e.attr("class", "hasvisited");
                        map.put(e.tagName() + index++, e.text());
                    }
                    dataList.add(map);
                }
            }
        }
        //如果没有<p>标签，则根据<div>标签来定位
        if (dataList.isEmpty()) {
            for (Element div : root.select("div")) {
                if (div.children().isEmpty() && !div.ownText().equals("")
                        && !div.className().equals("hasvisited")) {
                    int index = 0;
                    HashMap<String, String> map = new HashMap<>();
                    div.attr("class", "hasvisited");
                    Elements siblings = div.siblingElements();
                    for (Element e : siblings) {
                        e.attr("class", "hasvisited");
                        map.put(e.tagName() + index++, e.text());
                    }
                    dataList.add(map);
                }
            }
        }
    }

    /**
     * 判断一个字段项是否是时间
     *
     * @param item 待判断的字段
     * @return 是否是时间
     */
    protected boolean canbeTime(String item) {
        return false;
    }

    /**
     * 判断一个字段项是否是地点
     *
     * @param item 待判断的字段
     * @return 是否是地点
     */
    protected boolean canBePlace(String item) {
        Pattern p = Pattern.compile("(.*\\d{1,4}[-|\\/|年|\\.]\\d{1,2}" +
                "[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])" +
                "?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?).*");
        Matcher matcher = p.matcher(item);
        if (matcher.matches())
            return true;
        p = Pattern.compile("\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号]).*");
        matcher = p.matcher(item);
        if (matcher.matches())
            return true;
        p = Pattern.compile("(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}" +
                "(秒)?)?)?)?(\\s)*(PM|AM)?");
        matcher = p.matcher(item);
        if (matcher.matches())
            return true;
        return false;
    }

    /**
     * 从dataList中提取出结构化信息
     * <p>
     * //* @return 所抽取的结构化信息
     *
     * @throws IOException
     */
    public void extractInformation(File file) throws IOException {
        parseHtmlDOM();
        String info = theme + '\n' + dataList.toString();
        Main.setHtml(file, info);
        //return null;
    }

}
