package retrive;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.HTMLCodepageDetector;
import info.monitorenter.cpdetector.io.JChardetFacade;

public class PageEncodeDetector {
  private static CodepageDetectorProxy detector = CodepageDetectorProxy
      .getInstance();

  static {
    detector.add(new HTMLCodepageDetector(false));
    detector.add(JChardetFacade.getInstance());
  }

  /**
    * 测试用例
    *
    * @param args
    */
  public static void main(String[] args) {
    PageEncodeDetector web = new PageEncodeDetector();
    try {
      System.out.println(web.getCharset("http://www.kejisi.com/"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
    * @param strurl
    *                        页面url地址,需要以 http://开始，例：http://www.pujia.com
    * @return
    * @throws IOException
    */
  public static String getCharset(String strurl) throws IOException {
    String strencoding = null;
    try {
      // 定义URL对象
      URL url = new URL(strurl);
      // 获取http连接对象
      HttpURLConnection urlConnection = (HttpURLConnection) url .openConnection();
      urlConnection.setConnectTimeout(3000);
      urlConnection.connect();
      strencoding = getFileEncoding(url);
    } catch (Exception e) {

    } finally {
      return strencoding;
    }

  }

  /**
    *
    *<br>
    * 方法说明：通过网页内容识别网页编码
    *
    *<br>
    * 输入参数：strUrl 网页链接; timeout 超时设置
    *
    *<br>
    * 返回类型：网页编码
    */
  public static String getFileEncoding(URL url) {
    java.nio.charset.Charset charset = null;
    try {
      charset = detector.detectCodepage(url);
    } catch (Exception e) {
      System.out.println(e.getClass() + "分析" + "编码失败");
    }
    if (charset != null)
      return charset.name();
    return null;

  }
}
