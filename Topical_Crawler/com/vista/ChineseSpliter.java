package  com.vista;
import java.io.IOException;  	
import jeasy.analysis.MMAnalyzer;

/**
* 中文分词器：这里采用极易中文分词组件，这个中文分词组件可以免费使用，提供Lucene接口，跨平台，性能可靠。
*/
public class ChineseSpliter 
{
	/**
	* 对给定的文本进行中文分词
	* @param text 给定的文本
	* @param splitToken 用于分割的标记,如"|"
	* @return 分词完毕的文本
	*/
	public static String split(String text,String splitToken)
	{
		String result = null;
		MMAnalyzer analyzer = new MMAnalyzer();  	
		try  	
        {
			result = analyzer.segment(text, splitToken);	
		}  	
        catch (IOException e)  	
        { 	
        	e.printStackTrace(); 	
        } 	
        return result;
	}
}
