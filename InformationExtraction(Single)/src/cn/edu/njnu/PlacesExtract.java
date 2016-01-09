package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.infoextract.impl.incubators.ExtractIncubators;
import cn.edu.njnu.tools.CoordinateHelper;
import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.ParameterHelper;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhi on 16-1-7.
 * 用于提取孵化器地址
 */
public class PlacesExtract implements Runnable {

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
     * @param baseFile   页面存放的根目录
     * @param outputFile outputFile
     */
    public PlacesExtract(String baseFile, String outputFile, Map<String, String> placeToPid) {
        this.outputFile = outputFile;
        this.baseFile = new File(baseFile);
        this.folderName = "incubators";
        this.ie = new ExtractIncubators();
        this.placeToPid = placeToPid;
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
     * 上传地址数据
     *
     * @return 是否成功
     */
    protected boolean postPlace(String title, String desc, String abs, String url,
                                JSONObject other, String city) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            double lng;//经度
            double lat;//纬度
            CoordinateHelper coordinateHelper = new
                    CoordinateHelper("http://api.map.baidu.com/geocoder/v2/");
            coordinateHelper.addParam("output", "json").addParam("ak", CoordinateHelper.appkey)
                    .addParam("address", desc).addParam("city", city);
            coordinateHelper.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            coordinateHelper.addRequestProperty("Accept", "application/json");
            //请求经纬度
            String result = coordinateHelper.post();
            JSONObject jo = JSONObject.fromObject(result);
            if (jo.getInt("status") == 0) {
                JSONObject rt = jo.getJSONObject("result");
                JSONObject lc = rt.getJSONObject("location");
                lng = lc.getDouble("lng");
                lat = lc.getDouble("lat");

                HttpPost method = new HttpPost(new ParameterHelper().getPostPlaceURL());
                JSONObject data = new JSONObject();
                data.put("title", title);
                data.put("des", desc);
                data.put("abs", abs);
                data.put("pic", "http://img0.pconline.com.cn/pconline/" +
                        "1308/06/3415302_3cbxat8i1_bdls7k5b.jpg");
                data.put("url", url);
                data.put("lat", lat);
                data.put("lng", lng);
                data.put("type", "孵化器");
                data.put("other", other);
                //生成参数对
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("data", data.toString()));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                method.setEntity(entity);

                //请求post
                HttpResponse result2 = httpClient.execute(method);
                String resData = EntityUtils.toString(result2.getEntity());
                //获得结果
                JSONObject resJson = JSONObject.fromObject(resData);
                if (resJson.getInt("code") == 1) {
                    JSONObject result3 = resJson.getJSONObject("data");
                    if (result3.getInt("status") == 1) {
                        String pid = result3.getString("pid");
                        placeToPid.put(url, pid);
                        return true;
                    } else
                        return false;
                } else
                    return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 便于stream api使用方法引用特性,将处理过程封装
     *
     * @param f 待分析的页面文件
     */
    protected boolean process(File f, String url, String city) {
        String title = "";
        String desc = "";
        String abs = "";
        String html = getHtml(f);
        List<Extractable> info = ie.extractInformation(html);
        if (info != null) {
            for (Extractable extractable : info) {
                JSONObject other = new JSONObject();
                for (Pair<String, String> pair : extractable) {
                    if (pair.key.contains("标题"))
                        title = pair.value;
                    else if (pair.key.contains("名称"))
                        title = pair.value;
                    else if (pair.key.contains("地点"))
                        desc = pair.value;
                    else if (pair.key.contains("地址"))
                        desc = pair.value;
                    else if (pair.key.contains("简介"))
                        abs = pair.value;
                    else if (pair.key.contains("描述"))
                        abs = pair.value;
                    else
                        other.put(pair.key, pair.value);
                }
                if (title.equals("") || desc.equals("") || abs.equals(""))
                    continue;
                if (postPlace(title, desc, abs, url, other, city))
                    return true;
            }
        }
        return false;
    }

    /**
     * 查找所在城市
     *
     * @param current 当前目录
     * @return 城市名
     */
    protected String findCity(File current) {
        File last = current;
        while (!current.getName().equals(baseFile.getName())) {
            last = current;
            current = current.getParentFile();
        }
        //如果是个网址,则返回北京
        if (last.getName().matches("https?://[\\w./]+"))
            return "北京";
        else//否则返回本身
            return last.getName();
    }

    /**
     * 递归搜索每个文件夹,找到目标文件夹作处理过程
     *
     * @param current 当前的文件夹
     */
    protected void searchForTarget(File current) {
        //如果已经存在该地点到pid的映射则continue;
        if (placeToPid.containsKey(current.getName()))
            return;
        File[] list = current.listFiles();
        if (list != null) {
            //找到孵化器目录从中提取地点相关信息
            if (current.getName().equals(folderName)) {
                String url = current.getParentFile().getName();
                for (File file : list) {
                    if (process(file, url, findCity(current)))
                        break;
                }
            } else {
                if (list[0].isFile())
                    return;
                Arrays.stream(list).forEach(this::searchForTarget);
            }
        }
    }

    @Override
    public void run() {
        searchForTarget(baseFile);
    }

}
