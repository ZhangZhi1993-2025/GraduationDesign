package cn.edu.njnu.extract.extract;

import cn.edu.njnu.extract.domain.Extractable;

import java.util.List;

/**
 * Created by zhangzhi on 15-12-19.
 * 定义信息抽取行为规范
 */
public interface InfoExtract {

    /**
     * 从给定的页面文本中抽取出结构化的信息
     *
     * @param html 给定的页面文本
     * @return 抽取出的数据单元list
     */
    List<Extractable> extract(String html);

}
