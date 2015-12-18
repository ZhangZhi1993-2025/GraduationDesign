package cn.edu.njnu.tidypage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.prefs.NodeChangeEvent;

/**
 * Created by zhangzhi on 15-12-17.
 * 清洗页面工具类
 */
public class TidyPage {

    // html流对应的DOM树
    private Element root;

    /**
     * 得到html流构建DOM树
     *
     * @param html 输入的html流
     */
    public TidyPage(String html) {
        html = html.replaceAll("<!--[^<]*-->", "");
        root = Jsoup.parse(html).select("html").first();
    }

    /**
     * 将页面中无信息标签滤去
     */
    protected void dropUnnecessaryTags() {
        Set<String> set = new HashSet<>();
        set.add("script");
        set.add("style");
        set.add("link");
        set.add("img");
        set.add("button");
        //set.add("ul");
        set.add("header");
        set.add("footer");
        Stack<Element> stk = new Stack<>();
        stk.push(root);
        Element node;
        outer:
        while (!stk.isEmpty()) {
            node = stk.peek();
            stk.pop();
            if (set.contains(node.tagName()) || node.attr("style").equals("display:none")) {
                node.remove();
                continue;
            }
            if (node.children().size() == 0) {
                if (node.text().replaceAll("[\f\n\r\t]*", "").equals(""))
                    node.remove();
                continue;
            }
            while (node.children().size() == 1) {
                Element parent = node.parent();
                Element child = node.child(0);
                Elements children = parent.children();
                int index = -1;
                for (Element e : children) {
                    index++;
                    if (e == node) {
                        List<Element> item = new ArrayList<>();
                        item.add(child);
                        parent.insertChildren(index, item);
                        node.remove();
                        break;
                    }
                }
                stk.push(child);
                continue outer;
            }
            Elements children = node.children();
            children.forEach(stk::push);
        }
    }

    /**
     * 清洗页面并返回DOM树
     *
     * @return 清洗过后的DOM树
     */
    public Element tidyPage() {
        dropUnnecessaryTags();
        return root;
    }

    /**
     * 清洗页面并返回html页面流
     */
    public String tidyPageStr() {
        dropUnnecessaryTags();
        return root.toString();
    }

}
