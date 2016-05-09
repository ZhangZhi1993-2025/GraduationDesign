package cn.edu.njnu.ie.impl;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.ie.ExtractService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractServiceImpl implements ExtractService {

    protected Map<String, Extractable> template = new HashMap<>();

    @Override
    public void serialize(String path) {
        try {
            ObjectOutputStream e = new ObjectOutputStream(new FileOutputStream(path));
            Throwable var3 = null;

            try {
                e.writeObject(this.template);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (e != null) {
                    if (var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (IOException var15) {
            System.out.print(path);
            var15.printStackTrace();
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void unSerialize(String path) {
        try {
            ObjectInputStream e = new ObjectInputStream(new FileInputStream(path));
            Throwable var3 = null;

            try {
                this.template = (HashMap<String, Extractable>) e.readObject();
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if (e != null) {
                    if (var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (IOException var16) {
            System.out.print(path);
            var16.printStackTrace();
        } catch (ClassNotFoundException var17) {
            var17.printStackTrace();
        }

    }

    public void printTemplate() {
        System.out.println(this.template);
    }

    boolean canBeTarget(String mainSeq, String patternSeq) {
        int i = 0;
        int j = 0;

        while (i < mainSeq.length() && j < patternSeq.length()) {
            if (mainSeq.charAt(i) == patternSeq.charAt(j)) {
                ++i;
                ++j;
            } else {
                i = i - j + 1;
                j = 0;
            }
        }

        return j == patternSeq.length();
    }

    Element findNodeByContent(Element root, String content) {
        Stack<Element> stk = new Stack<>();
        stk.add(root);

        while (stk.size() != 0) {
            Element node = stk.peek();
            stk.pop();
            if (this.canBeTarget(node.ownText(), content)) {
                return node;
            }

            Elements children = node.children();
            children.forEach(stk::push);
        }

        return null;
    }

    String generatePathByElement(Element element) {
        Stack<String> stk = new Stack<>();

        while (true) {
            while (element.parent() != null) {
                int index = -1;
                Element parent = element.parent();
                Elements siblings = parent.children();
                Iterator sb = siblings.iterator();

                while (sb.hasNext()) {
                    Element e = (Element) sb.next();
                    ++index;
                    if (e == element) {
                        String node;
                        if (element.className().equals("")) {
                            node = element.tagName() + ":null:" + index;
                        } else {
                            node = element.tagName() + ":" + element.className() + ":" + index;
                        }

                        stk.push(node);
                        element = element.parent();
                        break;
                    }
                }
            }

            StringBuilder var9 = new StringBuilder();

            while (!stk.empty()) {
                var9.append((String) stk.peek());
                var9.append('/');
                stk.pop();
            }

            return var9.deleteCharAt(var9.length() - 1).toString();
        }
    }

    @Override
    public void generateRule(String html, Extractable e) {
        StringBuilder sb = new StringBuilder();
        Document root = Jsoup.parse(html);
        List samples = e.targetSamples();
        Iterator result = samples.iterator();

        while (result.hasNext()) {
            String sample = (String) result.next();
            Element element = this.findNodeByContent(root, sample);
            sb.append(this.generatePathByElement(element));
            sb.append("&&");
        }

        String result1 = sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).toString();
        this.template.put(result1, e.getInstance());
    }

    /* 以下为抽取部分 ****************************************/

    public boolean haveSimilarClassAttr(String str1, String str2) {
        if ((double) Math.abs(str1.length() - str2.length()) / (double) str1.length() > 0.15D) {
            return false;
        } else {
            int[][] dp = new int[str1.length()][str2.length()];
            dp[0][0] = str1.charAt(0) == str2.charAt(0) ? 1 : 0;

            int i;
            for (i = 1; i < str1.length(); ++i) {
                dp[i][0] = Math.max(dp[i - 1][0], str1.charAt(i) == str2.charAt(0) ? 1 : 0);
            }

            for (i = 1; i < str2.length(); ++i) {
                dp[0][i] = Math.max(dp[0][i - 1], str1.charAt(0) == str2.charAt(i) ? 1 : 0);
            }

            for (i = 1; i < str1.length(); ++i) {
                for (int j = 1; j < str2.length(); ++j) {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                    if (str1.charAt(i) == str2.charAt(j)) {
                        dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 1] + 1);
                    }
                }
            }

            return (double) dp[str1.length() - 1][str2.length() - 1] / (double) str1.length() > 0.85D;
        }
    }

    Element getTargetElement(String[] patterns, Element element) {
        for (int i = 1; i < patterns.length; ++i) {
            String[] tmp = patterns[i].split(":");
            String tag = tmp[0];
            String clazz = tmp[1];
            int number = Integer.valueOf(tmp[2]);
            Elements children = element.children();
            if (!(children.get(number)).tagName().equals(tag) ||
                    !clazz.equals("null") &&
                            !this.haveSimilarClassAttr(clazz, (children.get(number)).className())) {
                return null;
            }

            element = children.get(number);
        }

        return element;
    }

    String findRulePattern(Element root) {
        Iterator var3 = this.template.keySet().iterator();

        String[] patterns;
        String rule;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            rule = (String) var3.next();
            patterns = rule.split("&&");
            patterns = patterns[0].split("/");
        } while (this.getTargetElement(patterns, root) == null);

        return rule;
    }

    String extract(Element root, String pattern) {
        String[] patterns = pattern.split("/");
        Element element = this.getTargetElement(patterns, root);
        return element != null ? element.ownText() : null;
    }

    @Override
    public Extractable extractResult(String html) {
        Element root = Jsoup.parse(html).select("html").first();
        String rule = this.findRulePattern(root);
        Extractable info = this.template.get(rule);
        String[] patterns = rule.split("&&");
        ArrayList<String> list = new ArrayList<>();
        String[] var7 = patterns;
        int var8 = patterns.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            String pattern = var7[var9];
            list.add(extract(root, pattern));
        }

        info.generateObject(list);
        return info;
    }
}
