package cn.edu.njnu.infoextract.impl;

import cn.edu.njnu.infoextract.InfoExtract;
import org.jsoup.nodes.Element;

/**
 * Created by zhangzhi on 15-12-21.
 * 新闻类型页面抽取实现类
 */
public class ExtractNews extends InfoExtract {

    public ExtractNews(Element root) {
        this.root = root;
    }

    public ExtractNews() {
    }

    @Override
    public void extractInformation() {
        parseHtmlDOM();
        //String info = theme + '\n' + dataList.toString();
    }

}
