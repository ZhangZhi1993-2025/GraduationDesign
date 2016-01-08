package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.ParameterGetter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhi on 12/28/2015.
 * 线程池的调度单元
 */
public class ProcessUnit implements Runnable {

    //页面存放的根目录
    protected File baseFile;

    //特定类型网页所在的文件夹名
    protected String folderName;

    //所需要使用的信息抽取实例
    protected InfoExtract ie;

    //输出地址
    protected String outputFile;

    //地点与pid的映射
    protected Map<String, String> placeToPid;

    /**
     * 构造器
     *
     * @param config 用于获得目标文件夹与信息抽取实例
     */
    public ProcessUnit(Pair<String, String> config, File file, String outputFile) {
        try {
            this.outputFile = outputFile;
            this.baseFile = file;
            this.folderName = config.key;
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
     * 上传数据接口
     *
     * @param pid  地点的id号
     * @param info 待上传的数据
     */
    protected void postData(String pid, List<Extractable> info) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost method = new HttpPost(new ParameterGetter().getPostDataURL());
            JSONObject data = new JSONObject();
            JSONArray array = new JSONArray();
            for (Extractable extractable : info) {
                String title = "";
                String time = "";
                String content = "";
                JSONObject other = new JSONObject();
                for (Pair<String, String> pair : extractable) {
                    if (pair.key.equals("标题"))
                        title = pair.value;
                    if (pair.key.equals("时间"))
                        time = pair.value;
                    if (pair.key.equals("内容"))
                        content = pair.value;
                    other.put(pair.key, pair.value);
                }
                JSONObject item = new JSONObject();
                item.put("title", title);
                item.put("type", ie.getType());
                item.put("time", time);
                item.put("content", content);
                item.put("pid", pid);
                item.put("pic", "");
                item.put("other", other);
                array.put(item);
            }
            data.put("acs", array);

            StringEntity entity = new StringEntity(data.toString(), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
            HttpResponse result = httpClient.execute(method);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 便于stream api使用方法引用特性,将处理过程封装
     *
     * @param f 待分析的页面文件
     */
    protected void process(File f, String outputFile, String place) {
        String html = getHtml(f);
        List<Extractable> info = ie.extractInformation(html);
        if (info != null) {
            if (placeToPid.containsKey(place)) {
                postData(placeToPid.get(place), info);
                info.forEach(extraction -> {
                    try {
                        extraction.persistData(outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * 递归搜索每个文件夹,找到目标文件夹作处理过程
     *
     * @param current 当前的文件夹
     */
    protected void searchForTarget(File current) {
        String url = current.getParentFile().getName();
        File[] list = current.listFiles();
        if (list != null && current.getName().equals(folderName)) {
            for (File file : list)
                process(file, outputFile, url);
        } else if (list != null && list[0].isFile())
            return;
        else if (list != null)
            Arrays.stream(list).forEach(this::searchForTarget);
    }

    @Override
    public void run() {
        searchForTarget(baseFile);
    }

}
