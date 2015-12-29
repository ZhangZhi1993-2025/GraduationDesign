package cn.edu.njnu.infoextract.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.ext.Project;
import cn.edu.njnu.infoextract.InfoExtract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by yangyang && nannan on 15-12-29.
 * 项目类型页面抽取实现类
 */
public class ExtractProjects extends InfoExtract {

    static boolean flag = false;
    static boolean flag_t = false;
    static String content = "";
    static String time = "";
    static String s_match = "";
    static boolean isdiv = true;
    static String img_url = "";

    @Override
    public List<Extractable> extractInformation(String html) {

        List<Extractable> result = new ArrayList<>();
        Project news = new Project();

        Document doc = Jsoup.parse(html); //闁硅泛锕ｇ粩瀛樼▔椤忓懏鐎俊妤嬬到椤曨喚鎸掗敍鍕埗閻熸瑱绲鹃悗浠嬪礄閻戞ɑ闄�
        String str = doc.html().replaceAll("&nbsp;", "");
        doc = Jsoup.parse(str);
        doc.select("div[class~=active*]").remove();
        //doc.select("div[class~=box*]").remove();
        doc.select("div[class=tit clr]").remove();
        doc.select("div[class~=comment*]").remove();
        doc.select("div[class=des]").remove();
        doc.select("div[class~=logic*]").remove();
        Elements div_main1 = doc.select("div[class~=(main*)|(content*)|(container*)|(article*)|(detail*)|(project*)]");  //闁硅泛锕ュ〒鑸靛緞瑜忓▓鎱竔v闁秆勵殕鐎ｄ線宕欓悜妯婚檷
        int i = 1;
        //System.out.println("div闁秆勵殕閺嗙喖鏁嶉敓锟�"+div_main1.size());
        for (Element element : div_main1) {
            //element.select("a").remove();
            //element.select("div[class=inner]").remove();
            element.select("div[class=pic]").remove();
            element.select("div[class=tit]").remove();
            element.select("div[class=txt]").remove();
            element.select("button").remove();
            //element.select("i").remove();
            element.select("span[class~=nav*]").remove();
            element.select("span[class=text-primary fs12]").remove();
            ///////////////////////img_url = img_URL(element);    //闁兼儳鍢茶ぐ鍥亹閹惧啿顤卍iv闁秆勩仦閼垫垹鎲版担鐟邦棟闁汇劌鍩噈g闁汇劌瀚惌鎯ь嚗閿燂拷
            //element.select("ul").remove();
            //element.select("ol").remove();
            //System.out.println("\r\n"+(i++)+"\r\n");
            flag = false;
            flag_t = false;
            traverse_my(element, news);
            //time.trim();
            //System.out.println(time);
            //String title=news.hm.get("闁哄秴娲。锟�").trim();
            if (time.trim().isEmpty() | content.trim().isEmpty()) {
                isdiv = false;  //濞戞挸绉靛Σ鎼佸箣閹存粍绮﹂悷鏇氱劍婢规﹢鎯冮崚顤痸闁秆嶆嫹
            }
            if (isdiv) {
                news.put("闁告劕鎳庨锟�", content);
                news.put("闁哄啫鐖煎Λ锟�", time);

                //write_Img(img_url, filename);  //閻忓繐妫楀ù姗�鎮ч崶锔剧憮閺夌偞鍨濈粭鍛村级閿燂拷
                content = "";
                time = "";
                isdiv = true;
                break;
            }
            content = "";
            time = "";
            isdiv = true;
        }
        result.add(news);
        return result;
    }

    public static void traverse_my(Element root, Project news) { //root濞戞挾鍎ゅ〒鑸靛緞瑜忓▓鎱竔v闁煎搫鍊婚崑锟�
        Elements nodes_in = root.children();
        int i = nodes_in.size(); //闁兼儳鍢茶ぐ鍥╋拷娑欏姌婵☆參鎮欓崷顓熺暠濞戞搩浜濋弳锟�
        if (i == 0) { //闁兼眹鍎遍崣鐐▔椤撶喐锟ラ悗娑欏姌婵☆參鎮欓惂鍝ョ闁告帗鐟ラ惃銏ゅ礂閺堜絻鍘柣銊ュ閺嬪啴寮甸锟借ぐ鍥礄閿燂拷
            String s = root.text();
            //System.out.println("@@@@@@@@"+root.tagName()+"  "+root.text());
            if (!isTitle(root, news)) {
                if (!s.trim().isEmpty()) {

                    if (flag_t == false) { //閺夆晜蓱閻ュ懘寮垫径瀣棟闁告帞澧楀鍌炴⒒閿燂拷
                        //System.out.println("########"+s);
                        if (isTimeAll(s)) {

                            //System.out.println(root.tagName()+"  "+root.text());
                            if (s.contains(":")) {
                                flag_t = true;
                            }
                            time += s_match;
                            time += " ";
                            s_match = "";

                        } else {
                            if (isTime(s)) {
                                time += s_match;
                                flag_t = true;
                                s_match = "";
                            } else {
                                /*if(root.text().contains("\\b")){
                                    System.out.println("###########"+root.text());
								}*/
                                //root.text().replaceAll("\0x3F", "");
                                root.text().trim();

                                content = content + "\r\n" + root.text();
                            }
                        }
                    } else {
                        String s1 = root.text().trim();
                        if (!(s1.isEmpty() || s1 == "")) {
                            if (!isSundry(s1)) {
                                content = content + "\r\n" + s1;
                            }
                        }

                    }
                }
            }


        } else {
            for (Element node : nodes_in) {
                traverse_my(node, news);
            }
            root.children().remove();
            String s2 = root.text().trim();
            //闁告帇鍊栭弻鍥及椤栨碍鍎婇柡鍕靛灟缂嶆棃鎳撻敓锟�
            //System.out.println("############"+root.tagName()+"   "+root.className()+"   "+s2);
            if ((root.tagName().equals("a")) && (root.className().equals("user-hd"))) {
                //System.out.println("what is @@@@@@@@@@@@@");
                news.put("濞达絾绮忛敓鏂ゆ嫹", s2);
            } else {
                //System.out.println("*********"+s2);
                if (!s2.isEmpty()) {
                    if (isTimeAll(s2)) {
                        //System.out.println("@@@@@@@@@@"+s2);
                        if (s2.contains(":")) {

                            flag_t = true;
                        }
                        time += s_match;
                        time += " ";
                        s_match = "";
                    } else {
                        if (isTime(s2)) {
                            time += s_match;
                            flag_t = true;
                            s_match = "";
                        } else {
                            if (!isSundry(s2)) {
                                content = content + "\r\n" + s2;
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isTimeAll(String time) {
        Pattern p = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)");
        Matcher matcher = p.matcher(time);
        if (matcher.find()) {
            s_match = matcher.group();
            return true;
        }
        return false;
    }

    public static boolean isDate(String Date) {
        Pattern p = Pattern.compile("\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号]).*");
        Matcher matcher = p.matcher(Date);
        if (matcher.find()) {
            s_match = matcher.group();
            return true;
        }
        return false;
    }

    public static boolean isTime(String Time) {
        Pattern p = Pattern.compile("\\d{1,2}[点|时|:]\\d{1,2}(分)?(:)?(\\d{1,2}(秒)?)?");
        Matcher matcher = p.matcher(Time);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean isTitle(Element root, Project news) {
        if (flag) {
            return false;   //鐎规瓕灏欑划锟犲嫉婢跺鍨煎Λ鐗堬公缁辨繈宕氬▎搴ｇ憹闁哄嫷鍨遍悥锝嗭紣閿燂拷
        }
        String tagName = root.tagName();
        if (tagName.equals("h1") | tagName.equals("h2") | tagName.equals("h3") | tagName.equals("h4")) {
            flag = true;  //闁瑰灚鍎抽崺宀勫冀閸ヮ剦鏆�
            //System.out.println("闁哄秴娲。浠嬫晬閿燂拷"+root.text());
            news.put("闁哄秴娲。锟�", root.text());
            return flag;
        }
        return false;
    }

    public static boolean isSundry(String s) {   //闁硅泛锕ｇ粭鍌涚▔閿熺晫寮ч崶锔剧憮濞戞搫鎷风紒鈥虫搐楠炴捇骞掗敓锟�
        Pattern p = Pattern.compile("濞戞挸锕ｇ粩瀵稿姬閸ラ绐�.*");
        Matcher matcher = p.matcher(s);
        boolean pre = matcher.matches();
        p = Pattern.compile("濞戞挸顑勭粩瀵稿姬閸ラ绐�.*");
        matcher = p.matcher(s);
        boolean next = matcher.matches();
        if (pre | next) {
            return true;
        }
        return false;
    }

    public static String img_URL(Element doc) throws IOException {  //閺夊牊鎸搁崣鍡涘矗閸屾稒娈堕柡鍕靛灡濞撹埖寰勯—鐚闁煎搫鍊婚崑锟�

        int width = 0;  //閻庣妫勭�癸拷
        int height = 0; //濡ゅ倹锚鐎癸拷
        int area = 0; //闂傚牄鍨昏ⅶ
        String url = ""; //闁哄牞鎷峰鍫嗗啯绂堥柣妤�娲ㄥ▓鎱L
        Elements image = doc.select("img");
        int count = image.size();
        if (count == 0) {
            return "";
        }
        for (Element element : image) {
            String widths = element.attr("width");
            String heights = element.attr("height");
            if ((!widths.trim().isEmpty()) && (!heights.trim().isEmpty())) {
                width = Integer.parseInt(element.attr("width"));
                height = Integer.parseInt(element.attr("height"));
                if (width * height > area) {
                    area = width * height;
                    url = element.absUrl("src");
                }
            }
        }
        return url;
    }

    public static void write_Img(String url, String filename) throws IOException {  //濞戞挸顑堝ù鍥炊閸撗冾暬
        if (!url.trim().isEmpty()) {  //闁兼眹鍎卞ù姗�鎮ч崶顏嗙唴鐎垫澘瀚粭澶嬬▔閾忓厜鏁�
            URL url1 = new URL(url);
            URLConnection uc = url1.openConnection();
            InputStream is = uc.getInputStream();
            File file = new File(filename + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            int i = 0;
            while ((i = is.read()) != -1) {
                out.write(i);
            }
        }
    }

}
