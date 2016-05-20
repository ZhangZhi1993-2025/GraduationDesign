package cn.edu.njnu.crawler.classify;

/**
 * Created by zhangzhi on 16-5-7.
 * 定义网页分类服务行为规范
 */
public interface PageClassifyService {

    /**
     * 将网页分类
     *
     * @param html 带分类的网页文本
     * @return 类别
     */
    String classify(String html);

}
