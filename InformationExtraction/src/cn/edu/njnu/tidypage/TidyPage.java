package cn.edu.njnu.tidypage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by zhangzhi on 15-12-17.
 * 页面压缩规范化工具类
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
        html = html.replaceAll("&ensp;", "");
        html = html.replaceAll("&nbsp;", "");
        html = html.replaceAll("&emsp;", "");
        root = Jsoup.parse(html).select("html").first();
    }


    /**
     * 用于非递归后序遍历的辅助数据结构
     */
    private class InnerStruct {

        //DOM树的节点
        public Element node;

        //第一次入栈时 hasvisited 为 false,第二次访问时修改为 true,第三次访问时弹出堆栈
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
    protected boolean judgeClass(Set<String> set, String classString) {
        String[] list = classString.split(" ");
        for (String item : list) {
            if (set.contains(item))
                return true;
        }
        return false;
    }

    /**
     * 判断该标签是否可能不包含有价值信息(包含登录,注册,分享,首页,返回,评论等字眼且总字数不超过5)
     *
     * @param text 待判断的标签内容
     * @return 是否可能不包含有价值信息
     */
    protected boolean couldBeIrrelevantTag(String text) {
        if (text.contains("ICP备") || text.contains("©"))
            return true;

        //总字数超过5则判断该标签含有有价值信息的概率较高，返回false
        if (text.length() > 5)
            return false;

        //否则作关键词匹配
        Set<String> set = new HashSet<>();
        set.add("登录");
        set.add("登陆");
        set.add("注册");
        set.add("忘记密码");
        set.add("重置");
        set.add("验证码");
        set.add("首页");
        set.add("返回");
        set.add("分享");
        set.add("评论");
        set.add("微信");
        set.add("微博");
        set.add("人人网");
        set.add("朋友网");
        set.add("QQ");
        set.add("打印");
        set.add("导航");
        set.add("助手");
        set.add("收起");


        for (String pattern : set) {
            if (text.contains(pattern))
                return true;
        }
        return false;
    }

    /**
     * 将页面中无信息标签滤去(非递归后序遍历)
     */
    protected void dropUnnecessaryTags() {
        Set<String> set = new HashSet<>();
        set.add("script");
        set.add("style");
        set.add("link");
        set.add("img");
        set.add("form");
        set.add("button");
        set.add("input");
        set.add("select");
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
                        judgeClass(set, isnode.node.className()) ||
                        set.contains(isnode.node.id()) ||
                        isnode.node.attr("style").equals("display:none") ||
                        couldBeIrrelevantTag(isnode.node.text().trim())) {
                    isnode.node.remove();
                    continue;
                }
                isnode.node.removeAttr("class");
                isnode.node.removeAttr("style");
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
