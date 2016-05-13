package cn.edu.njnu.tidypage.impl;

import cn.edu.njnu.tidypage.TidyPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by zhangzhi on 16-5-7.
 * 页面清洗规范化实现类
 */
@Component
public class DefaultTidyPageImpl implements TidyPage {

    //对于信息抽取无效的html标签集
    @Resource
    private static Set<String> unnecessaryTags;

    //对于信息抽取无效的字面值文本集
    @Resource
    private static Set<String> unnecessaryTexts;

    /**
     * 基于页面流去除无效字面值文本
     *
     * @param html 输入的页面流
     * @return 处理过的页面流
     */
    protected String trimBasedOnString(String html) {
        html = html.replaceAll("<!--[^<]*-->", "");
        html = html.replaceAll("&ensp;", "");
        html = html.replaceAll("&nbsp;", "");
        html = html.replaceAll("&emsp;", "");
        return html;
    }

    /**
     * 用于非递归后序遍历的辅助数据结构
     */
    private class InnerStruct {

        //DOM树的节点
        public Element node;

        //第一次入栈时hasVisited初始化为false,第二次访问时修改为true,第三次访问时弹出堆栈
        public boolean hasVisited;

        /**
         * 构造时hasVisited初始化为false
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
        set.add("上一页");
        set.add("下一页");
        set.add("尾页");
        set.add("联系我们");
        set.add("关于我们");


        for (String pattern : set) {
            if (text.contains(pattern))
                return true;
        }
        return false;
    }

    /**
     * 将页面中无信息标签滤去(非递归后序遍历)
     */
    protected void dropUnnecessaryTags(Element root) {
        Set<String> set = new HashSet<>();
        set.add("script");
        set.add("style");
        set.add("link");
        set.add("img");
        set.add("button");
        set.add("input");
        set.add("select");
        set.add("ul");
        set.add("footer");
        set.add("form");

        //初始化
        Stack<InnerStruct> stk = new Stack<>();
        InnerStruct isNode;
        for (Element e : root.children()) {
            isNode = new InnerStruct(e);
            stk.push(isNode);
        }

        while (!stk.isEmpty()) {
            isNode = stk.peek();
            if (!isNode.hasVisited) {
                isNode.hasVisited = true;
                Elements children = isNode.node.children();
                for (Element e : children) {
                    InnerStruct is = new InnerStruct(e);
                    stk.push(is);
                }
            } else {
                stk.pop();
                if (set.contains(isNode.node.tagName()) ||
                        judgeClass(set, isNode.node.className()) ||
                        set.contains(isNode.node.id()) ||
                        isNode.node.attr("style").equals("display:none") ||
                        couldBeIrrelevantTag(isNode.node.text().trim())) {
                    isNode.node.remove();
                    continue;
                }
                isNode.node.removeAttr("class");
                isNode.node.removeAttr("style");
                if (isNode.node.children().size() == 0) {
                    if (isNode.node.text().trim().equals(""))
                        isNode.node.remove();
                    continue;
                }
                if (isNode.node.children().size() == 1
                        && isNode.node.ownText().trim().equals(""))
                    isNode.node.replaceWith(isNode.node.child(0));
            }
        }
    }

    @Override
    public Element tidyPage(String html) {
        html = trimBasedOnString(html);
        Element root = Jsoup.parse(html).select("html").first();
        dropUnnecessaryTags(root);
        return root;
    }

}
