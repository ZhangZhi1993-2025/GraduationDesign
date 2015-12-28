package cn.edu.njnu;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Zhi on 12/27/2015.
 * 用于封装congfig.xml配置文件中的参数并提供访问的接口
 */
public class ParameterGetter implements Iterable<File> {

    private int poolsize;

    private List<File> list = new ArrayList<>();

    public ParameterGetter() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new File(
                Main.class.getResource("/config.xml").getPath()));
        Element root = doc.getRootElement();
        Element pool = root.element("poolsize");
        this.poolsize = Integer.valueOf(pool.getText());
        Element files = root.element("files");
        List<Element> nodes = files.elements();
        nodes.forEach(node -> list.add(new File(node.getText())));
    }

    /**
     * 获得线程池的线程数设置
     *
     * @return 线程配置数量
     */
    public int getPoolsize() {
        return poolsize;
    }

    @Override
    public Iterator<File> iterator() {
        return new InnerIterator();
    }

    /**
     * 迭代器的内部类实现
     */
    private class InnerIterator implements Iterator<File> {

        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < ParameterGetter.this.list.size() - 1;
        }

        @Override
        public File next() {
            if (hasNext())
                return ParameterGetter.this.list.get(cursor++);
            return null;
        }

    }

}
