package cn.edu.njnu.domain;

import java.util.ArrayList;

public abstract class Extractable {

    /**
     * 存放抽取的数据
     */
    ArrayList<Pair> data = new ArrayList<>();

    /**
     * 持久化数据的方法
     */
    public abstract void persistData();

}
