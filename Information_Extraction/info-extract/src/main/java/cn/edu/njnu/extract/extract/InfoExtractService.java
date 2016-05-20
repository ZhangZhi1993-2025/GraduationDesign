package cn.edu.njnu.extract.extract;

import cn.edu.njnu.extract.domain.Extractable;
import org.jsoup.nodes.Element;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * Created by zhangzhi on 15-12-19.
 * 定义信息抽取服务行为规范
 */
public interface InfoExtractService extends Ordered {

    /**
     * 判断所给参数是否匹配本类型抽取服务
     *
     * @param root     页面对应的DOM树
     * @param category 类别
     * @return 是否匹配本类型服务
     */
    boolean capableOf(Element root, String category);

    /**
     * 从给定的网页结构中抽取出结构化的信息
     *
     * @param root 页面对应的DOM树
     * @return 抽取出的数据单元list
     */
    List<Extractable> extract(Element root);

}
