package cn.edu.njnu.factorybean.ext;

import cn.edu.njnu.factorybean.CollectionFactoryBean;

import java.util.ArrayList;

/**
 * Created by zhangzhi on 16-3-7.
 * 线性集合资源文件注入成数组list形式的实现类
 */
public class ListFactoryBean<T> extends CollectionFactoryBean<ArrayList<T>> {

    @Override
    public Class<?> getObjectType() {
        return ArrayList.class;
    }

}
