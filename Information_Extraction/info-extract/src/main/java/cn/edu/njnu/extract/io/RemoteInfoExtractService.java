package cn.edu.njnu.extract.io;

/**
 * Created by zhangzhi on 16-5-10.
 * 信息抽取服务的远程调用接口
 */
public interface RemoteInfoExtractService {

    /**
     * 远程调用抽取服务
     *
     * @param html     内容相关的网页字符流
     * @param category 该网页对应的类别
     * @return 远程是否抽取成功
     */
    boolean extract(String html, String category);

    /**
     * 远程调用抽取服务(省略类别,将默认调用category为null的重载方法)
     *
     * @param html 内容相关的网页字符流
     * @return 远程是否抽取成功
     */
    default boolean extract(String html) {
        return extract(html, null);
    }

}
