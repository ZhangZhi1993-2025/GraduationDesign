package classfication;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FeatureExtration{
	private static Map<String,BigDecimal> classifierResult=new HashMap<String,BigDecimal>();//分类结果Map

	/**
	 * 
	* @Title: FeatureExtration
	* @Description: 特征提取
	* @param     
	* @return    
	* @throws
	 */
	public static void FeatureExtration(Set<String> words,String classifier) throws FileNotFoundException, IOException
	{
		Map<String,Long> Features=new HashMap<String,Long>();//特征
//		Set<String> classifierSet=TrainSampleDataManager.getAllClassifiers();
		words=DefaultStopWordsHandler.dropStopWords(words);
			for(String word: words){
				Long count=TrainSampleDataManager.wordInClassCount(word, classifier);
				Features.put(word,count);
			}			
			List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String,Long>>(Features.entrySet());
			Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
	            //升序排序
				@Override
				public int compare(java.util.Map.Entry<String, Long> o1, java.util.Map.Entry<String, Long> o2) {
					// TODO Auto-generated method stub
					return o2.getValue().compareTo(o1.getValue());
				}
	        });
			BufferedWriter filewrite = new BufferedWriter (new OutputStreamWriter (new FileOutputStream ("./src/web/"+classifier+".txt"), "UTF-8"));
			BufferedWriter filewriteword = new BufferedWriter (new OutputStreamWriter (new FileOutputStream ("./src/web/"+classifier+"word.txt"), "UTF-8"));
			int count=0;
			for(Map.Entry<String,Long> mapping:list){ 
				filewriteword.write(mapping.getKey()+"\t"+mapping.getValue()+"\n");				
	        } 
			filewriteword.close();
			for(Map.Entry<String,Long> mapping:list){ 
				if(count<100)
				{
					count++;
					filewrite.write(mapping.getKey()+"\t"+mapping.getValue()+"\n");
				}
				else
				{
					break;
				}				
	        } 		
			filewrite.close();		
	}


	
}