package cn.edu.njnu.infoextract.impl.incubators;

import java.util.*;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.ext.Incubator;
import cn.edu.njnu.infoextract.InfoExtract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public class ExtractIncubators extends InfoExtract {

    public List<Extractable> extractInformation(String html) {
        String safe = Jsoup.clean(html, Whitelist.relaxed().addAttributes(":all", "class"));
        Element root = Jsoup.parse(safe);
        Incubator info = new Incubator();
        List<Extractable> result = new ArrayList<>();
        info = Traverse(root, info);
        result.add(info);
        return result;
    }

    public static Incubator Traverse(Element root, Incubator info) {
        for (Element i : root.children()) {
            info = Extract(i, info);
            info = Traverse(i, info);
        }
        return info;
    }

    public static Incubator Extract(Element root, Incubator Info) {
        Element p = root;
        Element kp = root;
        Element vp = root;
        for (Feature i : PatternStore.allpattern) {
            /*
             * 抽取算法
			 * 1.先提取大divclass，在分别提取小div,
			 * 1.判断节点路径是否匹配
			 * 2.把key value都提出来
			 * 3.
			 *
			 */
            if (!root.select("*." + i.getKey_parent_class()).isEmpty())
                p = root.select("*." + i.getKey_parent_class()).first();
            else
                continue;
            if (!p.select("*." + i.getKey_class()).isEmpty())
                kp = p.select("*." + i.getKey_class()).first();
            else
                continue;
            if (!p.select("*." + i.getValue_class()).isEmpty())
                vp = p.select("*." + i.getValue_class()).first();
            else continue;
            String key = "";
            String value = "";
            boolean kflag = false;
            boolean vflag = false;
            for (Element atom : kp.select(i.getKey_tag())) {
                String path = PatternStore.CreatePath(atom);
                int len = i.getKey_path().length();
                if (path.substring(path.length() - len, path.length()).equals(i.getKey_path())) {
                    kflag = true;
                    key = atom.text();
                    atom.remove();
                    break;
                }
            }
            for (Element vatom : vp.select(i.getValue_tag())) {
                String path = PatternStore.CreatePath(vatom);
                int len = i.getValue_path().length();
                if (path.substring(path.length() - len, path.length()).equals(i.getValue_path())) {
                    vflag = true;
                    value = vatom.text();
                    vatom.remove();
                    break;
                }
            }
            if (kflag && vflag) {
                Info.put(key, value);
                return Info;
            }
                    /*
                     * 匹配兄弟节点的代码
					String pre_tag="";
					String next_tag="";
					if(root.previousElementSibling()==null)
						pre_tag="";
					else
						pre_tag=root.previousElementSibling().tagName();
					if(root.nextElementSibling()==null)
						next_tag="";
					else
						pre_tag=root.nextElementSibling().tagName();
					
					if(pre_tag.equals(i.getKey_right_tag())&&next_tag.equals(i.getKey_left_tag())){
					
					
						key=p.text();
						//提取value信息,怎么筛选
						for(Element e:root.parent().select(i.getValue_tag())){
							String epath=PatternStore.CreatePath(e);
							if(epath.substring(epath.length()-i.getValue_path().length(),epath.length()).equals(i.getValue_path())){
								value=e.text();
								e.remove();
								Info.addvalue(key, value);
								return true;
							}
						}
					*/
        }
        return Info;
    }
}
