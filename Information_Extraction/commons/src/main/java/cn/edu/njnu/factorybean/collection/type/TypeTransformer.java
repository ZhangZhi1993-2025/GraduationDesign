package cn.edu.njnu.factorybean.collection.type;

/**
 * Created by zhangzhi on 16-3-10.
 * 类型转换器:适用于资源文件中将字符串类型转换为自定义类型
 */
public interface TypeTransformer<E> {

    /**
     * 类型自定义转换
     *
     * @param line 待转换的字符串
     * @return 由字符串转换成的自定义类型
     */
    E transformType(String line);

}
