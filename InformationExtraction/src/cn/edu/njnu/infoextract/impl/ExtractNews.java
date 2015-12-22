package cn.edu.njnu.infoextract.impl;

import cn.edu.njnu.Main;
import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangzhi on 15-12-21.
 * 新闻类型页面抽取实现类
 */
public class ExtractNews extends InfoExtract {

    @Override
    public Extractable extractInformation(File file) throws IOException {
        parseHtmlDOM();
        String info = theme + '\n' + dataList.toString();
        Main.setHtml(file, info);
        return null;
    }

}
