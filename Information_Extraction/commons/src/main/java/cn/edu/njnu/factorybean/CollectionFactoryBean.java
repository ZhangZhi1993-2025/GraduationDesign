package cn.edu.njnu.factorybean;

import org.springframework.beans.factory.FactoryBean;

import java.util.Collection;

/**
 * Created by zhangzhi on 16-3-7.
 * 面向spring的线性集合资源文件(相对于properties文件)注入的抽象集合工厂Bean
 */
public class CollectionFactoryBean<T extends Collection> implements FactoryBean<T> {

    @Override
    public T getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
