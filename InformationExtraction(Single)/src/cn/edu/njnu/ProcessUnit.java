package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.tools.Pair;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Zhi on 12/28/2015.
 * main方法里线程池的调度单元
 */
public class ProcessUnit implements Runnable {

    //某类网页所在的文件夹
    protected File folder;

    //所需要使用的信息抽取实例
    protected InfoExtract ie;

    /**
     * 构造器
     *
     * @param config 用于获得目标文件夹与信息抽取实例
     */
    public ProcessUnit(Pair<File, String> config) {
        try {
            this.folder = config.key;
            this.ie = (InfoExtract) Class.forName(config.value).newInstance();
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
    protected String getHtml(File file) {
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
    protected void setHtml(File file, String html) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            bw.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 便于stream api使用方法引用特性,将处理过程封装
     *
     * @param f 待分析的页面文件
     */
    protected void process(File f) {
        String html = getHtml(f);
        List<Extractable> info = ie.extractInformation(html);
        if (info != null)
            info.forEach(e -> e.persistData());
    }

    @Override
    public void run() {
        File[] list = folder.listFiles();
        if (list != null)
            Arrays.stream(list).parallel().forEach(this::process);
    }

}
