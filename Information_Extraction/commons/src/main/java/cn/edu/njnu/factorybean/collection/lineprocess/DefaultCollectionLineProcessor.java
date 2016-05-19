package cn.edu.njnu.factorybean.collection.lineprocess;

import cn.edu.njnu.factorybean.collection.type.TypeTransformer;
import com.google.common.io.LineProcessor;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by zhangzhi on 16-3-8.
 * 为CollectionFactoryBean设计的默认的com.google.common.io.Files类的行处理器
 */
public class DefaultCollectionLineProcessor<E, T extends Collection<E>>
        implements LineProcessor<T>,TypeTransformer<E> {

    T result;

    @Override
    public E transformType(String line) {
        return null;
    }

    @Override
    public boolean processLine(String line) throws IOException {
        result.add(transformType(line));
        return true;
    }

    @Override
    public T getResult() {
        return result;
    }

}
