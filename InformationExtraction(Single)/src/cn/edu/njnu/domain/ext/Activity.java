package cn.edu.njnu.domain.ext;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.tools.Pair;

import java.io.*;

/**
 * Created by zhangzhi on 15-12-29.
 * 抽取数据单元之活动类
 */
public class Activity extends Extractable {

    @Override
    public void persistData(String location) throws IOException {
        File file = new File("/home/zhangzhi/Documents/result/activities/" + location);
        if (!file.exists())
            file.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), "UTF-8"))) {

            for (Pair pair : data)
                bw.write(pair.key + ":" + pair.value + '\n');
            bw.write('\n');

        }
    }

}
