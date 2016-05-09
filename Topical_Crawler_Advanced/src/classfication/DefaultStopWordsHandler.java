package classfication;


import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultStopWordsHandler {//是否为停用词检测

	private static Set<String> stopWordsSet =new HashSet<String>();
	static {
		stopWordsSet.add("日");
		stopWordsSet.add("期");
		stopWordsSet.add("号");
		stopWordsSet.add("版");
		stopWordsSet.add("题");
		stopWordsSet.add("标");
		stopWordsSet.add("第一");
		stopWordsSet.add("第");
		stopWordsSet.add("在");
		stopWordsSet.add("等");
		stopWordsSet.add("使");
		stopWordsSet.add("得");
		stopWordsSet.add("可以");
		stopWordsSet.add("一个");
		stopWordsSet.add("备");
		stopWordsSet.add("并");
		stopWordsSet.add("同时");
		stopWordsSet.add("对");
		stopWordsSet.add("从");
		stopWordsSet.add("来");
		stopWordsSet.add("把");
		stopWordsSet.add("用于");
		stopWordsSet.add("而");
		stopWordsSet.add("小");
		stopWordsSet.add("做");
		stopWordsSet.add("们");
		stopWordsSet.add("都");
		stopWordsSet.add("但");
		stopWordsSet.add("并");
		stopWordsSet.add("大");
		stopWordsSet.add("对于");
		stopWordsSet.add("这样");
		stopWordsSet.add("它");
		stopWordsSet.add("来");
		stopWordsSet.add("请");
		stopWordsSet.add("由");
		stopWordsSet.add("你");
		stopWordsSet.add("和");
		stopWordsSet.add("与");
		stopWordsSet.add("为");
		stopWordsSet.add("已");
		stopWordsSet.add("及");
		stopWordsSet.add("极");
		stopWordsSet.add("了");
		stopWordsSet.add("用");
		stopWordsSet.add("以");
		stopWordsSet.add("上");
		stopWordsSet.add("可");
		stopWordsSet.add("最");
		stopWordsSet.add("让");
		stopWordsSet.add("成");
		stopWordsSet.add("也");
		stopWordsSet.add("的");
		stopWordsSet.add("我们");
		stopWordsSet.add("要");
		stopWordsSet.add("自己");
		stopWordsSet.add("之");
		stopWordsSet.add("将");
		stopWordsSet.add("后");
		stopWordsSet.add("应");
		stopWordsSet.add("到");
		stopWordsSet.add("某");
		stopWordsSet.add("某");
		stopWordsSet.add("后");
		stopWordsSet.add("个");
		stopWordsSet.add("是");
		stopWordsSet.add("位");
		stopWordsSet.add("新");
		stopWordsSet.add("您");
		stopWordsSet.add("还");
		stopWordsSet.add("一");
		stopWordsSet.add("被");
		stopWordsSet.add("我");
		stopWordsSet.add("如");
		stopWordsSet.add("就");
		stopWordsSet.add("两");
		stopWordsSet.add("中");
		stopWordsSet.add("或");
		stopWordsSet.add("有");
		stopWordsSet.add("更");
		stopWordsSet.add("好");
		stopWordsSet.add("及");
		stopWordsSet.add("二");
		stopWordsSet.add("才");
		stopWordsSet.add(" ");
		stopWordsSet.add(",");
		stopWordsSet.add("“");
		stopWordsSet.add("”");
		stopWordsSet.add("。");
		stopWordsSet.add(".");
	}
	

	public static boolean isStopWord(String word)//检查单词是否为停用词
	{
		return stopWordsSet.contains(word);
	}
	
	/**
	* 去掉停用词
	* @param text 给定的文本
	* @return 去停用词后结果
	*/
	public static Set<String> dropStopWords(Set<String> oldWords){
		Set<String> set = new HashSet<String>();
		for(String word: oldWords){
			word=word.replaceAll("[^(\\u4e00-\\u9fa5)]", "");
			if(DefaultStopWordsHandler.isStopWord(word)==false){
				//不是停用词
				set.add(word);
			}
		}
		return set;
	}
}