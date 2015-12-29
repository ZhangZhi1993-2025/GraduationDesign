package cn.edu.njnu.infoextract.impl;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;

import java.util.List;

/**
 * Created by zhangzhi on 15-12-21.
 * 新闻类型页面抽取实现类
 */
public class ExtractNews extends InfoExtract {

    public ExtractNews() {
    }

    @Override
    public void parseHtmlDOM() {
        super.parseHtmlDOM();
        //TODO:write your code here(of course you can delete this override method either)
    }

    @Override
    public List<Extractable> extractInformation(String html) {
        this.getDOM(html, root -> {
            //TODO:write your code here if you want to search for the target <div></div>
            return null;
        });
        //TODO:write your code here
        return null;
    }

}
