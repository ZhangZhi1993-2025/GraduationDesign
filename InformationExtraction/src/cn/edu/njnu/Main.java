package cn.edu.njnu;

import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.infoextract.impl.ExtractNews;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) {
        try {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            ExecutorService service = Executors.newCachedThreadPool();
            service.submit(() -> {
                String html = getHtml(new File("/home/data/news"));
                InfoExtract ie = new ExtractNews(html);
                ie.extractInformation();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从指定文件获得内容
     *
     * @param file 指定的文件
     * @return 文件的内容
     */
    public static String getHtml(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String buffer;
            while ((buffer = br.readLine()) != null)
                sb.append(buffer);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将流写入文件,只在测试时使用
     *
     * @param file 待写入的文件
     * @param html 输入流
     */
    public static void setHtml(File file, String html) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            bw.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
