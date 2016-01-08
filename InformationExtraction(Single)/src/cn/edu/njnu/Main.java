package cn.edu.njnu;

import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.ParameterGetter;

import java.io.*;
import java.util.concurrent.*;

public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            //从ParameterGetter中获取配置文件的配置参数
            ParameterGetter helper = new ParameterGetter();

            //加载地点与pid的映射文件
            ConcurrentHashMap<String, String> placesToPid;
            File placesFile = new File(helper.getPlaces());
            if (placesFile.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(placesFile));
                placesToPid = (ConcurrentHashMap<String, String>) ois.readObject();
            } else
                placesToPid = new ConcurrentHashMap<>();

            //页面判重表
            //Set<String> visited = Collections.synchronizedSet(new HashSet<>());

            //加载地点信息
            ExecutorService handlePlace = Executors.newFixedThreadPool(1);
            handlePlace.submit(new PlacesExtract
                    (helper.getRootFile(), helper.getOutputFile(), placesToPid));
            handlePlace.shutdown();

            //调度线程池
            ExecutorService handlePage = Executors.newFixedThreadPool(helper.getPoolsize());
            //向线程池提交任务
            for (Pair<String, String> pair : helper)
                handlePage.submit(new ProcessUnit
                        (pair, new File(helper.getRootFile()), helper.getOutputFile()));
            handlePage.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
