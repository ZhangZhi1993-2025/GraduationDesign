package cn.edu.njnu.tidypage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by zhangzhi on 15-12-17.
 * 清洗页面工具类
 */
public class TidyPage {

    /**
     * 页面流对应的DOM树
     */
    private Element root;

    /**
     * 得到页面流构建DOM树
     *
     * @param html 输入的页面流
     */
    public TidyPage(String html) {
        html = html.replaceAll("<!--[^<]*-->", "");
        html = html.replaceAll("&nbsp;", "");
        root = Jsoup.parse(html).select("html").first();
    }


    /**
     * 用于非递归后序遍历的辅助数据结构
     */
    private class InnerStruct {

        /**
         * DOM树的节点
         */
        public Element node;

        /**
         * 第一次入栈时 hasvisited 为 false,第二次访问时修改为 true,第三次访问时弹出堆栈
         */
        public boolean hasVisited;

        /**
         * 初始构造时 hasvisited 为 false
         *
         * @param node node为DOM树的节点
         */
        public InnerStruct(Element node) {
            this.node = node;
            hasVisited = false;
        }
    }


    /**
     * 判断某节点的class属性是否含有该被过滤的关键词
     *
     * @param set         过滤关键词集合
     * @param classString 待审查的class属性
     * @return 某节点的class属性是否含有该被过滤的关键词
     */
    protected boolean judgeIfHasClass(Set<String> set, String classString) {
        String[] list = classString.split(" ");
        for (String item : list) {
            if (set.contains(item))
                return true;
        }
        return false;
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
        set.add("ul");
        set.add("footer");

        //初始化
        Stack<InnerStruct> stk = new Stack<>();
        InnerStruct isnode;
        for (Element e : root.children()) {
            isnode = new InnerStruct(e);
            stk.push(isnode);
        }

        while (!stk.isEmpty()) {
            isnode = stk.peek();
            if (!isnode.hasVisited) {
                isnode.hasVisited = true;
                Elements children = isnode.node.children();
                for (Element e : children) {
                    InnerStruct is = new InnerStruct(e);
                    stk.push(is);
                }
            } else {
                stk.pop();
                if (set.contains(isnode.node.tagName()) ||
                        judgeIfHasClass(set, isnode.node.className()) ||
                        set.contains(isnode.node.id()) ||
                        isnode.node.attr("style").equals("display:none")) {
                    isnode.node.remove();
                    continue;
                }
                if (isnode.node.children().size() == 0) {
                    if (isnode.node.text().trim().equals(""))
                        isnode.node.remove();
                    continue;
                }
                if (isnode.node.children().size() == 1
                        && isnode.node.ownText().trim().equals(""))
                    isnode.node.replaceWith(isnode.node.child(0));
            }
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
