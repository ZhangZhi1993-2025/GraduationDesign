package cn.edu.njnu.tools;

/**
 * Created by Zhi on 12/27/2015.
 * ��ȡ��һ����ֵ�Ե�Ԫ����
 */
public class Pair<K, V> {

    //��Ԫ���ݵļ�
    public K key;

    //��Ԫ���ݵ�ֵ
    public V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

}
