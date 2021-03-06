package cn.edu.njnu.factorybean.collection;

import cn.edu.njnu.files.LoadFileHelper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by zhangzhi on 16-3-7.
 * 面向spring的线性集合资源文件(相对于键值对的properties文件)注入的抽象集合工厂Bean
 */
public abstract class CollectionFactoryBean<E, T extends Collection<E>>
        implements FactoryBean<T> {

    private T result;

    @Resource
    private String filePath;

    @Resource
    private LineProcessor<T> lineProcessor;

    private static final Logger logger = LoggerFactory.getLogger(CollectionFactoryBean.class);

    @PostConstruct
    public void readResourceFiles() {
        try {
            File resource = new File(LoadFileHelper.getFilePath(filePath));
            result = Files.readLines(resource, Charsets.UTF_8, lineProcessor);
        } catch (IOException e) {
            logger.error("读取文件失败", e);
        }
    }

    @Override
    public T getObject() throws Exception {
        return result;
    }

    @Override
    public abstract Class<?> getObjectType();

    @Override
    public boolean isSingleton() {
        return false;
    }

}
