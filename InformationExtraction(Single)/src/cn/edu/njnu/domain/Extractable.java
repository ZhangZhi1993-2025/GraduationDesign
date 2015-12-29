package cn.edu.njnu.domain;

import cn.edu.njnu.tools.Pair;

import java.util.ArrayList;

/**
 * Created by Zhi on 12/27/2015.
 * 抽取的数据单元的抽象类
 */
public abstract class Extractable {

    //所抽取的数据的集合
    protected ArrayList<Pair<String, String>> data;

    /**
     * 向data里面放入数据对
     *
     * @param key   字段
     * @param value 字段对应的值
     */
    public void put(String key, String value) {
        this.data.add(new Pair<>(key, value));
    }

    /**
     * 用于持久化抽取的数据
     */
    public abstract void persistData();

}
