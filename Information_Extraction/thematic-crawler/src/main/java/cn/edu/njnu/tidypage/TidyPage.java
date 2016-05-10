package cn.edu.njnu.tidypage;

import org.jsoup.nodes.Element;

/**
 * Created by zhangzhi on 15-12-17.
 * 页面清洗规范化的行为规范
 */
public interface TidyPage {

    /**
     * 清洗页面并返回DOM树
     *
     * @return 清洗过后的DOM树
     */
    Element tidyPage();

}
