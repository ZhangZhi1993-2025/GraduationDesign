package cn.edu.njnu.infoextract;

import cn.edu.njnu.domain.Extractable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhi on 15-12-19.
 * 信息抽取类
 */
public class InfoExtract {

    /**
     * <K,V>
     * K:标签名
     * V:标签里的内容
     */
    protected List<Map<String, String>> dataList = new ArrayList<>();

    /**
     * 从<title></title>标签抽取出来的该页面的题目(主题)
     */
    protected String theme;

    /**
     * 从DOM树里提取可能的信息组放入dataList容器里,同时抽取<title></title>中的主题
     *
     * @param root DOM树根
     */
    protected void parseHtmlDOM(Element root) {
        theme = root.select("title").first().ownText();
        for (int i = 1; i < 7; i++) {
            Element head = root.select("h" + i).first();
            if (head != null) {
                int index = 0;
                HashMap<String, String> map = new HashMap<>();
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
    }

    public Extractable extractInformation() {
        return null;
    }

}
