package cn.edu.njnu.infoextract.impl.activities.main_process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.njnu.infoextract.impl.activities.Tool.Tool_File;
import cn.edu.njnu.infoextract.impl.activities.Tool.Tool_HTML_Clear;
import cn.edu.njnu.infoextract.impl.activities.Active_Web_Extract.Active_HTML_Extract;
import cn.edu.njnu.infoextract.impl.activities.Basic_Class.Atom;
import cn.edu.njnu.infoextract.impl.activities.Basic_Class.Web_Class;


public class Web_Extract {

    /**
     * @param file_Direction
     * @return
     * @throws IOException
     */
    public ArrayList<Atom> Html_info_Extract(String file_Direction) throws IOException {
        ArrayList<Atom> Extract_Result = new ArrayList<Atom>();
        ArrayList<String> file_list = new ArrayList<String>();
        Tool_File file_tool = new Tool_File();
        Web_Class web_class = null;
        HTML_Classify html_Classify_tool = new HTML_Classify();

        //读取文件夹 获取文件内的文件
        file_list = file_tool.readFolder(file_Direction);
        for (int i = 0; i < file_list.size(); i++) {
            String file_path = file_list.get(i);
            //针对文件进行分类 最后返回HTML文件的类型 如：活动类型或新闻类型
            web_class = html_Classify_tool.HTML_Classify_from_content(file_path);
            File input = new File(file_path);
            Document doc = Jsoup.parse(input, "utf-8");
            //对HTML进行清洗操作
            Tool_HTML_Clear html_Clear_Tool = new Tool_HTML_Clear();
            Document doc_clear = html_Clear_Tool.HTML_Clear(doc);
            switch (web_class) {
                case Active:
                    Active_HTML_Extract active_Extract = new Active_HTML_Extract();
                    //判断页面是否只有一个活动
                    if (active_Extract.Judge_Html_List(doc_clear)) {
                        //假如页面内只有一个活动
                        ArrayList<Atom> active_result = active_Extract.Active_Html_Extract(doc_clear);
                    } else {
                        //假如页面内有多条活动记录
                        ArrayList<ArrayList<Atom>> active_some_result = active_Extract.Some_Active_Html_Extract(doc_clear);
                    }

                    break;
                case News:

                    break;
                case Project:
                    break;
                case Incubation:
                    break;

            }

        }

        return Extract_Result;
    }
}
