package cn.edu.njnu.infoextract.impl.activities.main_process;

import java.util.ArrayList;
import java.util.List;

import cn.edu.njnu.domain.ext.Activity;
import org.jsoup.Jsoup;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.infoextract.impl.activities.Active_Web_Extract.Active_HTML_Extract;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by songzhenxing on 15-12-21.
 * 活动类型页面抽取实现类
 */
public class ExtractActivities extends InfoExtract {

    @Override
    public List<Extractable> extractInformation(String html) {
        //getDOM(html);
        int index = 0;
        while (html.charAt(index) != ' ')
            index++;
        String city = html.substring(0, index - 1);
        html = html.substring(index, html.length() - 1);

        root = Jsoup.parse(html);
        Activity info = new Activity();
        List<Extractable> result = new ArrayList<>();

        boolean flag = ExtractAddress(root, info, city);

        Active_HTML_Extract active_Extract = new Active_HTML_Extract();
        //判断页面是否只有一个活动
        if (active_Extract.Judge_Html_List(root)) {
            //假如页面内只有一个活动
            result.add(active_Extract.Active_Html_Extract(root));
        } else {
            //假如页面内有多条活动记录
            result.addAll(active_Extract.Some_Active_Html_Extract(root));
        }

        if (result.size() > 0 && flag) {
            //result.get(0).put("finaltitle", info.get("finaltitle"));
            result.get(0).put("finaladdress", info.get("finaladdress"));
        }

        return result;
    }

    @Override
    public String getType() {
        return "众创活动";
    }

    public boolean ExtractAddress(Element root, Activity info, String city) {
        Elements citytaglist = root.select(":containsOwn(" + city + ")");
        for (Element element : citytaglist) {
            String pattern = element.ownText().replaceAll("\\(*.\\)", "");
            String[] patterns = pattern.split(" ");
            for (String p : patterns) {
                if (canBePlace(p, city, info))
                    return true;
            }
        }
        return true;
    }

}
