package cn.edu.njnu.infoextract.impl.activities.Tool;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularMatch {
    public static String reg_match(String pattern, String matcher) {
        Pattern p = Pattern.compile(pattern);    //".*妤�锟介��锟�.*���ゆ��.*���ゆ��"
        Matcher m = p.matcher(matcher);            //"2015妤�锟介��锟�10���ゆ��19���ゆ�� 13:00 锟斤拷��锟� 2015妤�锟介��锟�10���ゆ��19���ゆ�� 17:00"
        ArrayList<String> strs = new ArrayList<String>();
        while (m.find()) {
            strs.add(m.group(0));
        }
        /**
         for (String s : strs){
         System.out.println(s);

         }
         **/
        if (strs.size() > 0) {
            return strs.get(0);
        } else {
            return null;
        }


    }
}
