package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by Zhi on 12/27/2015.
 * ʵ��Callable<T>�ӿڵ��߳�ִ�е�Ԫ
 */
public class RunExtract implements Callable<Extractable> {

    private InfoExtract ie;

    public RunExtract() {

    }

    public RunExtract(File file, InfoExtract ie) {
        this.ie = ie;
    }

    @Override
    public Extractable call() throws Exception {
        return null;
    }

}
