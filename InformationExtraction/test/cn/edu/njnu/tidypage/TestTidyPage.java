package cn.edu.njnu.tidypage;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static cn.edu.njnu.Main.getHtml;
import static cn.edu.njnu.Main.setHtml;

/**
 * Created by zhangzhi on 15-12-23.
 * 页面压缩清洗工具类的测试类
 */
public class TestTidyPage {

    @Test
    public void testTidyPage() throws IOException {
        /*File[] list = file.listFiles();
        if (list != null) {
            for (File f : list) {
                String html = getHtml(f);
                TidyPage tp = new TidyPage(html);
                html = tp.tidyPageStr();
                setHtml(f, html);
                //InfoExtract ie = new InfoExtract(tp.tidyPage());
                //ie.extractInformation(f);
            }
        }*/
        File file = new File("/home/zhangzhi/Documents/graduation_design/test");
        String html = getHtml(file);
        TidyPage tp = new TidyPage(html);
        html = tp.tidyPageStr();
        setHtml(file, html);
    }

}
