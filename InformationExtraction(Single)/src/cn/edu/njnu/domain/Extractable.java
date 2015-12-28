package cn.edu.njnu.domain;

import cn.edu.njnu.tools.Category;
import cn.edu.njnu.tools.Pair;

import java.util.ArrayList;

/**
 * Created by Zhi on 12/27/2015.
 * 抽取的数据单元
 */
public class Extractable {

    //该数据所属类别
    public Category category;

    //所抽取的数据的集合
    public ArrayList<Pair<String, String>> data;

}
