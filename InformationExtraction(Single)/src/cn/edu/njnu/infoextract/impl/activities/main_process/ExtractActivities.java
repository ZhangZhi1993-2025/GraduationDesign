package cn.edu.njnu.infoextract.impl.activities.main_process;

import java.util.ArrayList;
import java.util.List;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;

import cn.edu.njnu.infoextract.impl.activities.Active_Web_Extract.Active_HTML_Extract;

/**
 * Created by songzhenxing on 15-12-21.
 * 婵��讹拷璇ф�凤拷�锟介���ゆ�峰���锟介�╂�凤拷���凤拷锟介���ゆ�凤拷�告�烽���ワ拷锟界��锟�
 */
public class ExtractActivities extends InfoExtract {

    @Override
    public List<Extractable> extractInformation(String html) {
        getDOM(html);
        List<Extractable> result = new ArrayList<>();

        Active_HTML_Extract active_Extract = new Active_HTML_Extract();
        //��濮�锟斤拷���ゆ�峰���锟介�╂�凤拷���烽���ゆ�烽���ゆ�烽���ゆ�烽���ワ拷���ワ拷����煤��璇ф�烽��锟�
        if (active_Extract.Judge_Html_List(root)) {
            //���ゆ�峰┑���峰���锟介�╂�凤拷锟介���ゆ�烽���ゆ�烽���ゆ�峰�锟介���ワ拷����煤��璇ф�烽��锟�
            result.add(active_Extract.Active_Html_Extract(root));
        } else {
            //���ゆ�峰┑���峰���锟介�╂�凤拷锟介���ゆ�烽���ゆ�峰�㈣�规�烽��瑙ｏ拷锟藉��℃�凤拷锟介���ワ拷�℃��
            result.addAll(active_Extract.Some_Active_Html_Extract(root));
        }

        return result;
    }
}
