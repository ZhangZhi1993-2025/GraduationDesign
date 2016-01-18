package cn.edu.njnu.infoextract;

import cn.edu.njnu.Main;
import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.impl.ExtractNews;
import cn.edu.njnu.infoextract.impl.ExtractProjects;
import cn.edu.njnu.infoextract.impl.activities.main_process.ExtractActivities;
import cn.edu.njnu.infoextract.impl.incubators.ExtractIncubators;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhangzhi on 15-12-30.
 * 测试InfoExtract的四个实现类
 */
public class TestInfoExtract {

    @Test
    public void testExtractNews() {
        /*try {
            File folder = new File("/home/zhangzhi/Documents/folder");
            File[] list = folder.listFiles();
            if (list != null) {
                for (File file : list) {
                    String html = Main.getHtml(file);
                    InfoExtract ie = new ExtractNews();
                    List<Extractable> result = ie.extractInformation(html);
                    if (result != null) {
                        for (Extractable e : result)
                            e.persistData("/home/zhangzhi/Documents/folder2", "", true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void testExtractActivities() {
        //String html = Main.getHtml(new File("/home/zhangzhi/Documents/test.htm"));
        InfoExtract ie = new ExtractActivities();
        //List<Extractable> result = ie.extractInformation(html);
        //System.out.print(result);
    }

    @Test
    public void testExtractIncubators() {
        //String html = Main.getHtml(new File("/home/zhangzhi/Documents/test.htm"));
        InfoExtract ie = new ExtractIncubators();
        //List<Extractable> result = ie.extractInformation(html);
        //System.out.print(result);
    }

    @Test
    public void testExtractProjects() {
        /*try {
            File folder = new File("/home/zhangzhi/Documents/folder");
            File[] list = folder.listFiles();
            if (list != null) {
                for (File file : list) {
                    String html = Main.getHtml(file);
                    InfoExtract ie = new ExtractProjects();
                    List<Extractable> result = ie.extractInformation(html);
                    if (result != null) {
                        for (Extractable e : result)
                            e.persistData("/home/zhangzhi/Documents/", "", true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*try {
            File file = new File("/home/zhangzhi/Documents/test.htm");
            String html = Main.getHtml(file);
            InfoExtract ie = new ExtractProjects();
            List<Extractable> result = ie.extractInformation(html);
            if (result != null) {
                for (Extractable e : result)
                    e.persistData("/home/zhangzhi/Documents/", "", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
