package cn.edu.njnu.domain;

import java.util.ArrayList;

public abstract class Extractable {

    /**
     * ��ų�ȡ������
     */
    ArrayList<Pair> data = new ArrayList<>();

    /**
     * �־û����ݵķ���
     */
    public abstract void persistData();

}
