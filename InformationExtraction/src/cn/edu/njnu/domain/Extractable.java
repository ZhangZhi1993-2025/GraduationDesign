package cn.edu.njnu.domain;

import java.util.HashMap;

public abstract class Extractable {

    /**
     * ��ų�ȡ������
     */
    HashMap<String, String> data = new HashMap<>();

    /**
     * �־û����ݵķ���
     */
    public abstract void persistData();

}
