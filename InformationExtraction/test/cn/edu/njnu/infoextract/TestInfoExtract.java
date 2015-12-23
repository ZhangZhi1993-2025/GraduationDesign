package cn.edu.njnu.infoextract;

import cn.edu.njnu.infoextract.impl.ExtractNews;
import cn.edu.njnu.tidypage.TidyPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static cn.edu.njnu.Main.getHtml;

/**
 * Created by zhangzhi on 15-12-21.
 * 测试InfoExtract类下面的方法
 */
public class TestInfoExtract {

    private InfoExtract ie = new ExtractNews();

    @BeforeClass
    public static void initialize() throws IOException {
        //File file = new File("/home/zhangzhi/Documents/graduation_design/samples2");
        //String html = getHtml(file);
        //TidyPage tp = new TidyPage(html);

        //Element root = tp.tidyPage();
        //ie.extractInformation(file);
    }

    @Test
    public void testcanBeTime() {
        //System.out.print(ie.canBeTime("199"));
        //Assert.assertEquals(ie.canBeTime("199"), true);
        Assert.assertEquals(ie.canBeTime("centos1099cddc90zxc"), false);
    }
}
