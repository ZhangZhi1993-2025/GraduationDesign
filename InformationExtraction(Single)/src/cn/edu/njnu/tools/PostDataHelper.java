package cn.edu.njnu.tools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangzhi on 16-1-12.
 * 用于封装数据上传至服务器端
 */
public class PostDataHelper {

    Map<String, JSONObject> postMap = new ConcurrentHashMap<>();

    /**
     * 往指定JSONObject里添加一个内容项
     *
     * @param pid  地点对应的pid
     * @param data 待添加的内容项
     */
    public void addData(String pid, JSONArray data) {
        if (postMap.containsKey(pid)) {
            JSONObject json = postMap.get(pid);
            JSONArray array = json.getJSONArray("acs");
            for (int i = 0; i < data.length(); i++)
                array.put(data.get(i));
        } else {
            JSONObject json = new JSONObject();
            json.put("acs", data);
            postMap.put(pid, json);
        }
    }

    /**
     * 批量上传数据
     *
     * @return 各pid对应的数据是否成功上传
     */
    public boolean[] post() {
        Set<String> keySet = postMap.keySet();
        boolean[] hasPosted = new boolean[keySet.size()];
        int index = 0;
        for (String pid : keySet) {
            hasPosted[index++] = postEachPlace(postMap.get(pid));
        }
        return hasPosted;
    }

    /**
     * 上传某pid对应的所有数据
     *
     * @param data 某pid下的所有数据
     * @return 是否上传成功
     */
    protected boolean postEachPlace(JSONObject data) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost method = new HttpPost(new ParameterHelper().getPostDataURL());
            //生成参数对
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("data", data.toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            method.setEntity(entity);

            //请求post
            HttpResponse result = httpClient.execute(method);
            String resData = EntityUtils.toString(result.getEntity());
            //获得结果
            JSONObject resJson = JSONObject.fromObject(resData);
            if (resJson.getInt("code") == 1) {
                JSONObject result2 = resJson.getJSONObject("data");
                return result2.getInt("status") == 1;
            } else
                return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
