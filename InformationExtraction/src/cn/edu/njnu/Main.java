package cn.edu.njnu;

import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.tidypage.TidyPage;
import org.jsoup.nodes.Element;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        try {
            File file = new File("/home/zhangzhi/Documents/graduation_design/samples2");
            File[] list = file.listFiles();
            if (list != null) {
                for (File f : list) {
                    String html = getHtml(f);
                    TidyPage tp = new TidyPage(html);
                    html = tp.tidyPageStr();
                    setHtml(f, html);
                    //InfoExtract ie = new InfoExtract(tp.tidyPage());
                    //ie.extractInformation(f);
                }
            }

            //String html = getHtml(file);
            //TidyPage tp = new TidyPage(html);

            //Element root = tp.tidyPage();
            //InfoExtract ie = new InfoExtract(root);
            //ie.extractInformation(file);

            //html = tp.tidyPageStr();
            //setHtml(file, html);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHtml(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String buffer;
            while ((buffer = br.readLine()) != null) {
                sb.append(buffer);
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    public static void setHtml(File file, String html) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write(html);
        }
    }
}
