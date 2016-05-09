package com.vista;
/**
* 停用词处理器：去掉文档中无意思的词语也是必须的一项工作,这里简单的定义了一些常见的停用词，并根据这些常用停用词在分词时进行判断。
* @author phinecos 
* 
*/
public class StopWordsHandler 
{
	private static String stopWordsList[] ={"的", "我们","要","自己","之","将","“","”","，","（","）","后","应","到","某","后","个","是","位","新","一","两","在","中","或","有","更","好",""};//常用停用词
	public static boolean IsStopWord(String word)
	{
		for(int i=0;i<stopWordsList.length;++i)
		{
			if(word.equalsIgnoreCase(stopWordsList[i]))
				return true;
		}
		return false;
	}
}
