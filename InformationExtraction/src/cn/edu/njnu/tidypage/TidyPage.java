package cn.edu.njnu.tidypage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * Created by zhangzhi on 15-12-17.
 * 清洗页面工具类
 */
public class TidyPage {

    /**
     * 将页面中<script></script>,<style></style>,<link />等无用标签滤去
     */
    protected String dropUnnecessaryTags(String html) {
        int begin = 0;
        int end;
        StringBuilder sb = new StringBuilder(html);
        int i = 0;
        try {
            for (i = 0; i < sb.length() - 1; i++) {
                if (sb.charAt(i) == '/' && sb.charAt(i + 1) == '*')
                    begin = i;
                if (sb.charAt(i) == '*' && sb.charAt(i + 1) == '/') {
                    end = i + 1;
                    sb.delete(begin, end + 1);
                }
                if (sb.charAt(i) == '/')
                    if (sb.charAt(i + 1) == '/')
                        if (i != 0)
                            if (sb.charAt(i - 1) != ':') {
                                begin = i;
                                while (i < sb.length() && sb.charAt(i) != '\n')
                                    i++;
                                end = i;
                                sb.delete(begin, end + 1);
                            }
            }
        } catch (Exception e) {
            int l = sb.length();
            int ii = i;
            System.out.print(l + ii);
        }
        html = sb.toString();
        return html.replaceAll("<!DOCTYPE html>", "")
                .replaceAll("<style[^<]*</style>", "")
                .replaceAll("<script[^<]*</script>", "")
                .replaceAll("<link[^<]*>", "")
                .replaceAll("<img[^<]*>", "")
                .replaceAll("<input[^<]*>", "")
                .replaceAll("<!--[^<]*-->", "");
    }

    /**
     * 将只有一个子标签的元素简化为它唯一的那个子标签
     *
     * @param element 待处理的DOM树
     */
    protected void dropSingleWrapTags(Element element) {

    }

    /**
     * 清洗页面并返回DOM树
     *
     * @return 清洗过后的DOM树
     */
    public Element tidyPageDom(String html) {
        html = dropUnnecessaryTags(html);
        Element root = Jsoup.parse(html);
        dropSingleWrapTags(root);
        return root;
    }

    /**
     * 清洗页面并返回html页面流
     */
    //public String tidyPageStr(String html) {
        //return dropUnnecessaryTags(html);
    //}

}
