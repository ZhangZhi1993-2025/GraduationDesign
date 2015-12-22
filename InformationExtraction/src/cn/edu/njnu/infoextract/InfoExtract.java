package cn.edu.njnu.infoextract;

import cn.edu.njnu.domain.Extractable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    protected boolean canBeTime(String item) {
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
     * 从给定的request地址发出请求并返回response字符串
     *
     * @param request 请求的地址
     * @return 返回response字符串
     */
    protected String getResponseFromRequest(String request) throws IOException {
        //使用apache的开源网络工具箱请求语料库数据
        HttpGet get = new HttpGet(request);
        HttpClient http = new DefaultHttpClient();
        HttpResponse response = http.execute(get);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            byte[] buffer = new byte[4096];
            int hasread;
            String data;
            if ((hasread = in.read(buffer)) > 0) {
                data = new String(buffer, 0, hasread);
                return data;
            } else
                return null;
        }
        return null;
    }

    /**
     * 从语料库返回的json字符串中提取可能的地址
     *
     * @param json 包含可能地址的json字符串
     * @return 抽取的可能的地址的字符串
     */
    protected String[] getPosiblePlaces(String json) {
        JSONObject jsonObj = JSONObject.fromObject(json);
        JSONArray array = jsonObj.getJSONArray("results");
        String[] places = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            places[i] = item.getString("name");
        }
        return places;
    }

    /**
     * 判断一个字段项是否是地点
     *
     * @param item 待判断的字段
     * @param city 查找的城市
     * @return 是否是一个地点
     */
    protected boolean canBePlace(String item, String city) throws IOException {
        final String appkey = "dnHbgky1GB0HMRt7GReO0Sxp";
        String request = "http://api.map.baidu.com/place/v2/search?" +
                "q=" + item + "&region=" + city + "&output=json&ak=" + appkey;
        String response = getResponseFromRequest(request);
        String[] places;
        if (response != null) {
            places = getPosiblePlaces(response);
            for (String place : places) {
                if (item.equals(place))
                    return true;
            }
        }
        return false;
    }

    /**
     * 从dataList中提取出结构化信息
     *
     * @return 所抽取的结构化信息
     * @throws IOException
     */
    public abstract Extractable extractInformation(File file) throws IOException;

}
