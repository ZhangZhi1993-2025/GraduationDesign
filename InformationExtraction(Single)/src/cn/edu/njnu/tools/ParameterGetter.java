package cn.edu.njnu.tools;

import cn.edu.njnu.Main;
import cn.edu.njnu.infoextract.InfoExtract;
import org.dom4j.Document;
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
public class ParameterGetter implements Iterable<Pair<File, InfoExtract>> {

    private int poolsize;

    private List<Pair<File, InfoExtract>> list = new ArrayList<>();

    public ParameterGetter() {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File(
                    Main.class.getResource("/config.xml").getPath()));
            Element root = doc.getRootElement();
            Element pool = root.element("poolsize");
            this.poolsize = Integer.valueOf(pool.getText());
            Element files = root.element("files");
            List<Element> nodes = files.elements();
            nodes.forEach((node) -> {
                try {
                    list.add(new Pair<>(new File(node.attribute("path").getText()),
                            (InfoExtract) Class.forName(node.getText()).newInstance()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public Iterator<Pair<File, InfoExtract>> iterator() {
        return new InnerIterator();
    }

    /**
     * 迭代器的内部类实现
     */
    private class InnerIterator implements Iterator<Pair<File, InfoExtract>> {

        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < ParameterGetter.this.list.size() - 1;
        }

        @Override
        public Pair<File, InfoExtract> next() {
            if (hasNext())
                return ParameterGetter.this.list.get(cursor++);
            return null;
        }

    }

}
