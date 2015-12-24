package cn.edu.njnu;

import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.infoextract.impl.ExtractNews;
import cn.edu.njnu.tidypage.TidyPage;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try {
            ExecutorService service = Executors.newCachedThreadPool();
            service.submit(() -> {
                String html = getHtml(new File("/home/system/data"));
                TidyPage tp = new TidyPage(html);
                Element root = tp.tidyPage();
                InfoExtract ie = new ExtractNews(root);
                ie.extractInformation();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHtml(File file) {
        String result;
        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(file))) {
            int pattern = (bis.read() << 8) + bis.read();
            int length = (int) file.length();
            byte[] buffer = new byte[length];
            bis.read(buffer, 0, length);
            switch (pattern) {
                case 0xefbb://UTF-8
                    result = new String(buffer, "UTF-8");
                    break;
                case 0xfffe://Unicode
                    result = new String(buffer, "Unicode");
                    break;
                case 0xfeff://UTF-16BE
                    result = new String(buffer, "UTF-16BE");
                    break;
                default://GBK
                    result = new String(buffer, "GBK");
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setHtml(File file, String html) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
