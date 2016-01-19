package cn.edu.njnu;

import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.ParameterHelper;
import cn.edu.njnu.tools.PostDataHelper;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExtractModule {

    public static void main(String[] args) {
        try {
            //从ParameterGetter中获取配置文件的配置参数
            ParameterHelper helper = new ParameterHelper();

            //加载地点与pid的映射文件
            ConcurrentHashMap<String, String> IncubatorsToPid =
                    loadPlaceToPId(helper.getIncubatorsPlaces());
            ConcurrentHashMap<String, String> ActivitiesToPid =
                    loadPlaceToPId(helper.getActivitiesPlaces());

            //上传数据的地址
            PostDataHelper postDataHelper = new PostDataHelper(IncubatorsToPid);

            //加载新地点信息
            new PlacesExtract(helper.getRootFile(), helper.getOutputFile(),
                    IncubatorsToPid).run();
            new ActivityExtract(helper.getRootFile(), helper.getOutputFile(),
                    ActivitiesToPid, postDataHelper).run();

            //向线程池提交任务
            ExecutorService handlePage = Executors.newFixedThreadPool(helper.getPoolsize());
            CountDownLatch latch = new CountDownLatch(helper.getPoolsize());

            for (Pair<String, String> pair : helper)
                handlePage.submit(new ProcessUnit
                        (pair, new File(helper.getRootFile()), helper.getOutputFile(),
                                IncubatorsToPid, postDataHelper, latch));
            handlePage.shutdown();
            latch.await();

            //System.out.println("开始向服务器端写数据");
            //向服务器端提交数据
            //postDataHelper.post();
            //postDataHelper.printLog();

            //写入地点与pid映射文件
            persisitPlaceToId(helper.getIncubatorsPlaces(), IncubatorsToPid);
            persisitPlaceToId(helper.getActivitiesPlaces(), ActivitiesToPid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载地点与pid的映射文件
     *
     * @param place 参数获得类
     * @return 地点到pid的映射map
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static ConcurrentHashMap<String, String> loadPlaceToPId(String place)
            throws IOException, ClassNotFoundException {
        File placesFile = new File(place);
        if (placesFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(placesFile));
            return (ConcurrentHashMap<String, String>) ois.readObject();
        } else
            return new ConcurrentHashMap<>();
    }

    /**
     * 写入地点与pid映射文件
     *
     * @param place       参数获得类
     * @param placesToPid 地点到pid的映射map
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static void persisitPlaceToId
    (String place, ConcurrentHashMap<String, String> placesToPid)
            throws IOException, ClassNotFoundException {
        File placesFile = new File(place);
        if (!placesFile.exists())
            placesFile.createNewFile();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(placesFile));
        oos.writeObject(placesToPid);
    }

}
