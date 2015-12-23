package cn.edu.njnu;

import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.tidypage.TidyPage;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try {
            ExecutorService service = Executors.newCachedThreadPool();


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
