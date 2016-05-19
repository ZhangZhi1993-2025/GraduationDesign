package cn.edu.njnu.extract.extract;

import cn.edu.njnu.extract.domain.Extractable;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by zhangzhi on 15-12-19.
 * 定义信息抽取服务行为规范
 */
public interface InfoExtractService {

    boolean capableOf();

    /**
     * 从给定的网页结构中抽取出结构化的信息
     *
     * @param root 页面对应的DOM树
     * @return 抽取出的数据单元list
     */
    List<Extractable> extract(Element root);

}
