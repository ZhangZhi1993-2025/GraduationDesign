package cn.edu.njnu.factorybean.collection.ext;

import cn.edu.njnu.factorybean.collection.CollectionFactoryBean;

import java.util.HashSet;

/**
 * Created by zhangzhi on 16-3-7.
 * 线性集合资源文件注入成集合set形式的实现类
 */
public class SetFactroyBean extends CollectionFactoryBean<HashSet> {

    @Override
    public Class<?> getObjectType() {
        return HashSet.class;
    }

}
