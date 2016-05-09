package com.vista;
import com.vista.ChineseSpliter;
import com.vista.ClassConditionalProbability;
import com.vista.PriorProbability;
import com.vista.TrainingDataManager;
import com.vista.StopWordsHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
* 朴素贝叶斯分类器
*/
public class BayesClassifier 
{
	private TrainingDataManager tdm;//训练集管理器
	private String trainnigDataPath;//训练集路径
	private static double zoomFactor = 10.0f;
	/**
	* 默认的构造器，初始化训练集
	*/
	public BayesClassifier() 
	{
		tdm =new TrainingDataManager();
	}

	/**
	* 计算给定的文本属性向量X在给定的分类Cj中的类条件概率
	* <code>ClassConditionalProbability</code>连乘值
	* @param X 给定的文本属性向量
	* @param Cj 给定的类别
	* @return 分类条件概率连乘值，即<br>
	*/
	float calcProd(String[] X, String Cj) 
	{
		float ret = 1.0F;
		// 类条件概率连乘
		for (int i = 0; i <X.length; i++)
		{
			String Xi = X[i];
			//因为结果过小，因此在连乘之前放大10倍，这对最终结果并无影响，因为我们只是比较概率大小而已
			ret *=ClassConditionalProbability.calculatePxc(Xi, Cj)*zoomFactor;
		}
		// 再乘以先验概率
		ret *= PriorProbability.calculatePc(Cj);
		return ret;
	}
	/**
	* 去掉停用词
	* @param text 给定的文本
	* @return 去停用词后结果
	*/
	public String[] DropStopWords(String[] oldWords)
	{
		Vector<String> v1 = new Vector<String>();
		for(int i=0;i<oldWords.length;++i)
		{
			if(StopWordsHandler.IsStopWord(oldWords[i])==false)
			{//不是停用词
				v1.add(oldWords[i]);
			}
		}
		String[] newWords = new String[v1.size()];
		v1.toArray(newWords);
		return newWords;
	}
	/**
	* 对给定的文本进行分类
	* @param text 给定的文本
	* @return 分类结果
	*/
	@SuppressWarnings("unchecked")
	public String classify(String text) 
	{
		String[] terms = null;
		terms= ChineseSpliter.split(text, " ").split(" ");//中文分词处理(分词后结果可能还包含有停用词）
		terms = DropStopWords(terms);//去掉停用词，以免影响分类
		
		String[] Classes = tdm.getTraningClassifications();//分类
		float probility = 0.0F;
		List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//分类结果
		for (int i = 0; i <Classes.length; i++) 
		{
			String Ci = Classes[i];//第i个分类
			probility = calcProd(terms, Ci);//计算给定的文本属性向量terms在给定的分类Ci中的分类条件概率
			//保存分类结果
			ClassifyResult cr = new ClassifyResult();
			cr.classification = Ci;//分类
			cr.probility = probility;//关键字在分类的条件概率
			System.out.println("In process....");
			System.out.println(Ci + "：" + probility);
			crs.add(cr);
		}
		//对最后概率结果进行排序
		java.util.Collections.sort(crs,new Comparator() 
		{
			public int compare(final Object o1,final Object o2) 
			{
				final ClassifyResult m1 = (ClassifyResult) o1;
				final ClassifyResult m2 = (ClassifyResult) o2;
				final double ret = m1.probility - m2.probility;
				if (ret < 0) 
				{
					return 1;
				} 
				else 
				{
					return -1;
				}
			}
		});
		//返回概率最大的分类
		return crs.get(0).classification;
	}
	
	//解决url中文编码问题
		@SuppressWarnings("deprecation")
		public static String encodeUrl(String url){
			if(!url.matches("[\u4e00-\u9fa5]")){
				StringBuffer bf = new StringBuffer();
				char[] array = url.toCharArray();
				for(Character c:array){
					if(c.toString().matches("[\u4e00-\u9fa5]")){
						bf.append(URLEncoder.encode(c.toString()));
					}else{
						bf.append(c);
					}
				}
				url = bf.toString();
			}
			url = url.replace(" ","");
			return url;
		}
	
	public static void main(String[] args) throws IOException
	{
		String text="";
		String url="http://www.chaihuo.org/makers/news/make";
		String filePath = null;
		// 1.生成HttpClinet 对象并设置参数
		HttpClient httpClient = new HttpClient();
		// 设置HTTP 连接超时30s
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		// 2.生成GetMethod 对象并设置参数
		System.out.println("download:"+url);
		url=encodeUrl(url);
		System.out.println("download:"+url);
		GetMethod getMethod = new GetMethod(url);
		// 设置get 请求超时30s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000);
		// 设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		// 3.执行HTTP GET 请求
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// 判断访问的状态码
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + getMethod.getStatusLine());
				filePath = null;
			}
			// 4.处理HTTP 响应内容
		//	byte[] responseBody = getMethod.getResponseBody();	
			
			//获取并设置页面编码 
			
			getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");  
			InputStream in = getMethod.getResponseBodyAsStream();  
			//这里使用8859-1读取  
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"ISO-8859-1"));  
			String tempbf;  
			StringBuffer html = new StringBuffer(100);  
			while ((tempbf = br.readLine()) != null) {  
			    html.append(tempbf +"\n");  
			}  
			//将8859-1再次转成GB2312  
			String content=new String(html.toString().getBytes("ISO-8859-1"),"UTF-8"); 
			String regex="[\\u4e00-\\u9fa5]+";
			Pattern p=Pattern.compile(regex);
			Matcher m=p.matcher(content);
			int cnt=0;
			while (m.find()) {
				cnt++;
				if(cnt<20) continue;
				if(content.substring(m.start(), m.end()).length()>0) {
					text+=content.substring(m.start(), m.end());
				}
				if(cnt==30) break;
			}
			//贝叶斯分类
			BayesClassifier classifier = new BayesClassifier();//构造Bayes分类器
			String type = classifier.classify(text);//进行分类;
			System.out.println(type);
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		
	}
}
