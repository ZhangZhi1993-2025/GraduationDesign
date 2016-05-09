package classfication;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TrainSampleDataManager {//训练样本管理器

	private static Map<String,Map<String,Map<String,Long>> > allWordsMap=new HashMap<String,Map<String,Map<String,Long>> >();//所有单词数统计Map
	
	/**
	 * 
	* @Title: wordInClassCount
	* @Description: 类c下单词tk在各个文档中出现过的次数之和
	* @param @param word
	* @param @param classifier
	* @param @return    
	* @return Long   
	* @throws
	 */
	public static Long wordInClassCount(String word,String classifier){
		Long sum=1L;
		Map<String,Map<String,Long>> classifierMap=allWordsMap.get(classifier);
		Set<String> articleSet=classifierMap.keySet();
		for(String article: articleSet){
			Map<String,Long> articleMap=classifierMap.get(article);
			Long value=articleMap.get(word);
			if(value!=null && value>0){
				sum+=articleMap.get(word);
			}			
		}
		return sum;
	}
    
	public static int countFeaturesum(String classifier) throws IOException{
		String[] str = new String[]{};
		String[] prob=new String[]{};
		BufferedReader fr = new BufferedReader (new InputStreamReader (new FileInputStream (NaiveBayesMain.DEFAULT_DIR+"/"+classifier+".txt"), "UTF-8"));
		String ch=null;
		List<String> list = new ArrayList<String>();
		int count=0;
		while ((ch = fr.readLine() ) != null) {
			   list.add(ch);
		}
		String[] array;
		array = new String[list.size()];
		int featuresum=0;
		for(String l :list)
		{
			l=l.replace("\r", "");
			array=l.split("\t");
//			System.out.println(array[1]);
			featuresum+=Integer.parseInt(array[1]);			
		}
		return featuresum;
	}
	
    /**
	 * 
	* @Title: readDirs
	* @Description: 递归获取文件
	* @param @param filepath,fileList
	* @param @return List<String>
	* @param @throws FileNotFoundException
	* @param @throws IOException    
	* @return List<String>   
	* @throws
	 */
    public static List<String> readDirs(String filepath,List<String> fileList) throws FileNotFoundException, IOException {  
    	
        try {  
            File file = new File(filepath);  
            if (!file.isDirectory()) {  
//                System.out.println("输入的参数应该为[文件夹名]");  
//                System.out.println("filepath: " + file.getAbsolutePath());
                fileList.add(file.getAbsolutePath());
            } else if (file.isDirectory()) {  
                String[] filelist = file.list();  
                for (int i = 0; i < filelist.length; i++) {  
                    File readfile = new File(filepath + File.separator + filelist[i]);  
                    if (!readfile.isDirectory()) {  
                        fileList.add(readfile.getAbsolutePath());  
                    } else if (readfile.isDirectory()) {  
                        readDirs(filepath + File.separator + filelist[i],fileList);  
                    }  
                }  
            }  
  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();
        }  
        return fileList;  
    }
    
    public static List<String> readDirs(File file,List<String> fileList) throws FileNotFoundException, IOException {  
    	String filePah=file.getAbsolutePath();
    	return readDirs(filePah, fileList);
    }
    
    /**
     * 
    * @Title: readFile
    * @Description: 读取文件转化成string
    * @param @param file
    * @param @return String
    * @param @throws FileNotFoundException
    * @param @throws IOException    
    * @return String   
    * @throws
     */
    public static String readFile(String file) throws FileNotFoundException, IOException {  
         File input = new File(file); 
         InputStream ios=new java.io.FileInputStream(file);
         byte[] b=new byte[3];
         ios.read(b);
         ios.close();
         String code=null;
         
         if((b[0]==-17&& b[1]==-69&& b[2]==-65)||(b[0]==60 && b[1]==33 && b[2]==68)||b[0]==13||b[0]==104)
         {
        	 code="UTF-8";
         }
         else
         {
        	 code="GB2312";
         }
         Document doc = Jsoup.parse(input, code);
   		 StringBuffer sb = new StringBuffer();
//   		
//   		 BufferedReader fr = new BufferedReader (new InputStreamReader (new FileInputStream (input), code));
//   		 String ch=null;
//   		 while((ch=fr.readLine())!=null)
//   		 {
//			sb.append(ch);
//   		 }
//   		 fr.close();
   		 if(!doc.data().equals(""))
   		 {
   			 String text = doc.body().text();
   			 Elements p = doc.select("p");
   			 String part=p.text();
   			 Elements h1 = doc.select("h1");
  			 String htext="";
  			 Elements h2 = doc.select("h2");
  			 Elements h3 = doc.select("h3");
  			 Elements h4 = doc.select("h4");
  			 Elements h5 = doc.select("h5");
 			 Elements h6 = doc.select("h6");
 			 htext=htext+h1.text()+h2.text()+h3.text()+h4.text()+h5.text()+h6.text();
   	   		 Elements content1 = doc.select("title"); 
   	   		 String title = content1.text(); 
   	   		 Elements eMETA = doc.select("meta[http-equiv=keywords]");
   	   		 String meta = eMETA.attr("content");
   	   		 Elements eMETA1 = doc.select("meta[http-equiv=description]");
   	   		 Elements eMETA2 =doc.select("meta[name=description]");
   	   		 Elements eMETA3 =doc.select("meta[name=keywords]");
   	   		 Elements div =doc.select("div[class=manager_Text]");
   	   		 String meta1 = eMETA1.attr("content");
   	   		 String meta2 = eMETA2.attr("content");
   	   		 String meta3 = eMETA3.attr("content");
   	   		 String maintext = div.text();
   	   	     sb.append(title).append("\r\n");
   	   	     sb.append(htext).append("\r\n");
   	      	 sb.append(meta2).append("\r\n");
   	      	 sb.append(meta3).append("\r\n");
   	   		 sb.append(part).append("\r\n");
   	   		 sb.append(maintext).append("\r\n");
   	   		 sb.append(meta).append("\r\n");
	      	 sb.append(meta1).append("\r\n");
   		 }
   		 else
   		{
   			sb.append("null");
   		}
         return sb.toString();
    }
    
    public static String HtmlProcess(String html)
    {
        Document doc = Jsoup.parse(html);
  		 StringBuffer sb = new StringBuffer();
  		 if(!doc.data().equals(""))
  		 {
  			 String text = doc.body().text();
  			 Elements p = doc.select("p");
  			 String part=p.text();
  			 Elements h1 = doc.select("h1");
 			 String htext="";
 			 Elements h2 = doc.select("h2");
 			 Elements h3 = doc.select("h3");
 			 Elements h4 = doc.select("h4");
 			 Elements h5 = doc.select("h5");
			 Elements h6 = doc.select("h6");
			 htext=htext+h1.text()+h2.text()+h3.text()+h4.text()+h5.text()+h6.text();
  	   		 Elements content1 = doc.select("title"); 
  	   		 String title = content1.text(); 
  	   		 Elements eMETA = doc.select("meta[http-equiv=keywords]");
  	   		 String meta = eMETA.attr("content");
  	   		 Elements eMETA1 = doc.select("meta[http-equiv=description]");
  	   		 Elements eMETA2 =doc.select("meta[name=description]");
  	   		 Elements eMETA3 =doc.select("meta[name=keywords]");
  	   		 Elements div =doc.select("div[class=manager_Text]");
  	   		 String meta1 = eMETA1.attr("content");
  	   		 String meta2 = eMETA2.attr("content");
  	   		 String meta3 = eMETA3.attr("content");
  	   		 String maintext = div.text();
  	   	     sb.append(title).append("\r\n");
  	   	     sb.append(htext).append("\r\n");
  	      	 sb.append(meta2).append("\r\n");
  	      	 sb.append(meta3).append("\r\n");
  	   		 sb.append(part).append("\r\n");
  	   		 sb.append(maintext).append("\r\n");
  	   		 sb.append(meta).append("\r\n");
	      	 sb.append(meta1).append("\r\n");
  		 }
  		 else
  		{
  			sb.append("null");
  		}
        return sb.toString();
    }
    
    /**
     * 
    * @Title: readFile
    * @Description: 获取网页标题
    * @param @param file
    * @param @return String
    * @param @throws FileNotFoundException
    * @param @throws IOException    
    * @return String   
    * @throws
     */
	 public static String readtitle(String file) throws FileNotFoundException, IOException {   
		 File input = new File(file); 
		 Document doc = Jsoup.parse(input, "UTF-8"); 
  		 Elements content1 = doc.select("title"); 
  		 String title = content1.text();  
  		 Elements eMETA = doc.select("meta[http-equiv=keywords]");
	     String meta = eMETA.attr("content");
//	   		 System.out.println("keywords:"+meta);
	   	 Elements eMETA1 = doc.select("meta[http-equiv=description]");
	   	 String meta1 = eMETA1.attr("content");
         return title+meta+meta1;
   }
	 
	 public static String getTitle(String html)
	 {
		 Document doc = Jsoup.parse(html); 
  		 Elements content1 = doc.select("title"); 
  		 String title = content1.text();  
  		 Elements eMETA = doc.select("meta[http-equiv=keywords]");
	     String meta = eMETA.attr("content");
//	   		 System.out.println("keywords:"+meta);
	   	 Elements eMETA1 = doc.select("meta[http-equiv=description]");
	   	 String meta1 = eMETA1.attr("content");
         return title+meta+meta1;
	 }
	 
    /**
     * 
    * @Title: process
    * @Description: 对训练样本进行处理，获取特征
    * @param     
    * @return void   
    * @throws
     */
    public static void process(String SAMPLE_DATA){
    	
    	try{
    		File sampleDataDir=new File(SAMPLE_DATA);
    		//得到样本分类目录
    		File[] fileList=sampleDataDir.listFiles();
    		if(fileList==null){
    			throw new IllegalArgumentException("Sample data is not exists!");
    		}
    		for(File file:fileList ){  			   			
    			//加载所有该分类下的所有文件名
    			Set<String> words=new HashSet<String>();
    			List<String> classFileList=readDirs(file, new ArrayList<String>());
    			for(String article: classFileList){
    				//读取文件内容
    				String content=readFile(article);
    				//es-ik分词
    				Map<String,Long> wordsMap=ChineseTokenizer.segStr(content);
    				for (Map.Entry<String, Long> entry : wordsMap.entrySet()) { 
    					words.add(entry.getKey());
    				}
    				if(allWordsMap.containsKey(file.getName())){
    					Map<String,Map<String,Long>> classifierValue=allWordsMap.get(file.getName());
    					classifierValue.put(article, wordsMap);
    					allWordsMap.put(file.getName(), classifierValue);
    				}else{
    					Map<String,Map<String,Long>> classifierValue=new HashMap<String,Map<String,Long>>();
    					classifierValue.put(article, wordsMap);
    					allWordsMap.put(file.getName(), classifierValue);
    				}
    			}
    			FeatureExtration.FeatureExtration(words,file.getName());//特征提取
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * @throws IOException 
     * @throws FileNotFoundException 
	 * 
	 * @Title: classWordCount
	 * @Description: 类c下文档总数目/训练样本总数（先验概率）
	 * @param @param 
	 * @param @return    
	 * @return Map<String,BigDecimal>   
	 * @throws
	 */
	public static Map<String,BigDecimal> preprob(String SAMPLE_DATA) throws FileNotFoundException, IOException{
		int totalcount=0;
		Map<String,BigDecimal> preprob=new HashMap<String,BigDecimal>();
		File sampleDataDir=new File(SAMPLE_DATA);
		//得到样本分类目录
		File[] fileList=sampleDataDir.listFiles();
		if(fileList==null){
			throw new IllegalArgumentException("Sample data is not exists!");
		}
		for(File file:fileList ){
			int filecount=0;
			List<String> classFileList=readDirs(file, new ArrayList<String>());
			filecount=classFileList.size();
			totalcount+=filecount;				
		}
		for(File file:fileList ){
			int filecount=0;
			List<String> classFileList=readDirs(file, new ArrayList<String>());
			filecount=classFileList.size();
			BigDecimal x1 = new BigDecimal(Integer.toString(filecount));
			BigDecimal x2 = new BigDecimal(Integer.toString(totalcount));
			preprob.put(file.getName(), x1.divide(x2,10, BigDecimal.ROUND_CEILING));
		}
		return preprob;
	}
    
	/**
     * @throws IOException 
     * @throws FileNotFoundException 
	 * 
	 * @Title: classWordCount
	 * @Description: 条件概率
	 * @param @param 
	 * @param @return    
	 * @return Map<String,BigDecimal>   
	 * @throws
	 */
	public static void prob(String SAMPLE_DATA) throws FileNotFoundException, IOException{
			String[] classifier={"news","activities","incubators","projects"};
			int sumnews=countFeaturesum("news");//总词频
			int sumactivities=countFeaturesum("activities");
			int sumincubators=countFeaturesum("incubators");
			int sumprojects=countFeaturesum("projects");
			BigDecimal x3 = new BigDecimal(sumnews);
			BigDecimal x4 = new BigDecimal(sumactivities);
			BigDecimal x5 = new BigDecimal(sumincubators);
			BigDecimal x6 = new BigDecimal(sumprojects);
			int[] countlist=new int[4];

			for(int j=0;j<classifier.length;j++)
			{
				BufferedReader fr = new BufferedReader (new InputStreamReader (new FileInputStream (NaiveBayesMain.DEFAULT_DIR+"/"+classifier[j]+".txt"), "UTF-8"));
				String ch=null;
				Map<String,BigDecimal> prob=new HashMap<String,BigDecimal>();
				List<String> list = new ArrayList<String>();
				int count=0;
				while ((ch = fr.readLine() ) != null) {
					   list.add(ch);
				}
				countlist[j]=list.size();	//特征的总数
				fr.close();
			}
			BigDecimal countlist1 = new BigDecimal(countlist[0]);//news类的特征总词频
			BigDecimal countlist2 = new BigDecimal(countlist[1]);//activities类的特征总词频
			BigDecimal countlist3 = new BigDecimal(countlist[2]);//incubators类的特征总词频
			BigDecimal countlist4 = new BigDecimal(countlist[3]);//projects类的特征总词频
			BufferedReader fr = new BufferedReader (new InputStreamReader (new FileInputStream (NaiveBayesMain.DEFAULT_DIR+"/projects.txt"), "UTF-8"));
			String ch=null;
			List<String> list = new ArrayList<String>();
			while ((ch = fr.readLine() ) != null) {
				   list.add(ch);
			}
			fr.close();
			String[] array;
			array = new String[list.size()];
			for(String l :list)
			{
				l=l.replace("\r", "");
				array=l.split("\t");
			}
			BufferedWriter fw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream ("./src/web/Features/projects.txt"), "UTF-8"));
			for(String l :list)
			{		
				l=l.replace("\r", "");
				array=l.split("\t");			
				BigDecimal x2 = new BigDecimal(array[1]);
				BigDecimal x = new BigDecimal("1");
				x2=x2.add(x);
				BigDecimal probility=x.divide(countlist1.add(x3),10, BigDecimal.ROUND_CEILING);
				BigDecimal probility1=x.divide(countlist2.add(x4),10, BigDecimal.ROUND_CEILING);
				BigDecimal probility2=x.divide(countlist3.add(x5),10, BigDecimal.ROUND_CEILING);
				BigDecimal probility3=x2.divide(countlist4.add(x6),10, BigDecimal.ROUND_CEILING);	
				fw.write(array[0]+"\t"+probility+"\t"+probility1+"\t"+probility2+"\t"+probility3+"\n");
			}
			fw.close();		
	}
	
	public static Map<String,BigDecimal> count(String classifier,String SAMPLE_DATA) throws FileNotFoundException, IOException{
		String[] str = new String[]{};
		Map<String,BigDecimal> prob=new HashMap<String,BigDecimal>();
		BufferedReader fr = new BufferedReader (new InputStreamReader (new FileInputStream (NaiveBayesMain.DEFAULT_DIR+"/Features/"+classifier+".txt"), "UTF-8"));
		String ch=null;
		List<String> list = new ArrayList<String>();
		int count=0;
		while ((ch = fr.readLine() ) != null) {
			   list.add(ch);
		}
		String[] array;
		array = new String[list.size()];
		Long sum=0L;
		for(String l :list)
		{
			l=l.replace("\r", "");
			array=l.split("\t");
			sum+=Long.parseLong(array[1]);			
		}
		for(String l :list)
		{
			l=l.replace("\r", "");
			array=l.split("\t");
			int NumofFeature=list.size();
			BigDecimal x2 = new BigDecimal(array[1]);
			prob.put(array[0],x2);		
		}
	return prob;
	}
	
	
    /**
     * 
    * @Title: getAllClassifiers
    * @Description: 所有文本分类
    * @param @return    
    * @return Set<String>   
    * @throws
     */
    public static Set<String> getAllClassifiers(){
    	return allWordsMap.keySet();
    }
}