package cn.edu.njnu.domain;

import cn.edu.njnu.tools.Pair;

import java.util.ArrayList;

/**
 * Created by Zhi on 12/27/2015.
 * 抽取的数据单元的抽象类
 */
public abstract class Extractable {

    //所抽取的数据的集合
    public ArrayList<Pair<String, String>> data;

    /**
     * 用于持久化抽取的数据
     */
    public abstract void persistData();

}
