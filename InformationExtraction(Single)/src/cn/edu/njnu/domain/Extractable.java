package cn.edu.njnu.domain;

import java.util.HashMap;

public abstract class Extractable {

    /**
     * 存放抽取的数据
     */
    HashMap<String, String> data = new HashMap<>();

    /**
     * 持久化数据的方法
     */
    public abstract void persistData();

}
