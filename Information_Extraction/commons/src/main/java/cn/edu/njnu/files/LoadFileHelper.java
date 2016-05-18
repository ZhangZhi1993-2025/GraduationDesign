package cn.edu.njnu.files;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Created by zhangzhi on 16-3-1.
 * 专门用于解决spring注入文件路径的问题
 */
public class LoadFileHelper {

    /**
     * 根据给定的待加载文件的相对路径返回文件的真实路径
     *
     * @return 文件的真实路径
     */
    public static String getFilePath(String filePath) throws FileNotFoundException {
        URL url = ClassLoader.getSystemClassLoader().getResource(filePath);
        if (url != null)
            return url.getPath();
        else
            throw new FileNotFoundException("filePath");
    }

}
