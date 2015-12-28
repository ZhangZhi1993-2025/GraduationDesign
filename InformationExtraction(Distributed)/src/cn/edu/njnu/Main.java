package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.infoextract.impl.ExtractNews;

import java.io.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        try {
            new Main().planA();
            //new Main().planB();
            //作为读取的页面内容的缓冲区,各线程从其中拿待抽取的页面
            BlockingQueue<QueueStruct> buffer = new LinkedBlockingQueue<>();

            File news = new File("/home/data/news");
            File projects = new File("/home/data/projects");


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
     * 方案A：适合于非分布式环境
     */
    public void planA() {
        //新闻类
        File news = new File("/home/data/news");
        //孵化器类
        File incubators = new File("/home/data/incubators");
        //项目类
        File projects = new File("/home/data/projects");
        //活动
        File activities = new File("/home/data/activities");

        //作为线程池满负荷拒绝接受新线程的页面流存放缓冲区
        BlockingQueue<QueueStruct> buffer = new LinkedBlockingQueue<>();

        //盛放结果的线程安全容器
        CopyOnWriteArrayList<Extractable> result = new CopyOnWriteArrayList<>();

        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    QueueStruct struct = buffer.take();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        ExecutorService newsService = Executors.newCachedThreadPool();
        File[] list = news.listFiles();
        if (list != null) {
            for (File f : list) {
                newsService.submit(() -> {
                    String html = getHtml(f);
                    InfoExtract ie = new ExtractNews(html);
                    //f.delete();
                    return ie.extractInformation();
                });
            }
        }

    }

    /**
     * 方案B：适合于分布式环境
     */
    public void planB() {

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
