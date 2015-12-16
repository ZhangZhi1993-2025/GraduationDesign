package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.impl.News;
import cn.edu.njnu.ie.impl.ExtractServiceImpl;
import cn.edu.njnu.ie.ExtractService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String e = getHtml("/home/zhangzhi/Documents/graduation_design/sample/meetup");
            ExtractService es = new ExtractServiceImpl();
            es.generateRule(e, new News("MEETUP创客聚会", "每周四 晚上7:30", "Meetup是创客空间会" +
                    "员们和爱好者们线下交流互动的活动。每人有3-5分钟时间介绍自己的项目或者自己想做的" +
                    "内容。可以通过交流讨论组团队完成项目。所有人都可以免费参加。"));
            ExtractServiceImpl es_impl = (ExtractServiceImpl) es;
            es_impl.printTemplate();
            e = getHtml("/home/zhangzhi/Documents/graduation_design/sample/workshop");
            Extractable e1 = es.extractResult(e);

            System.out.println(e1.toString());
            e = getHtml("/home/zhangzhi/Documents/graduation_design/sample/c2d2");
            e1 = es.extractResult(e);
            System.out.println(e1.toString());
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public static String getHtml(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        Throwable var2 = null;

        try {
            StringBuilder sb = new StringBuilder();

            String buffer;
            while ((buffer = br.readLine()) != null) {
                sb.append(buffer);
            }

            String var5 = sb.toString();
            return var5;
        } catch (Throwable var14) {
            var2 = var14;
            throw var14;
        } finally {
            if (br != null) {
                if (var2 != null) {
                    try {
                        br.close();
                    } catch (Throwable var13) {
                        var2.addSuppressed(var13);
                    }
                } else {
                    br.close();
                }
            }

        }
    }
}
