package classfication;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class RelatedProb {// 错误程序
	public static final String DEFAULT_DIR = "./doc/web/";// 所有文件目录
	public static final String SAMPLE_DATA = DEFAULT_DIR + "Sample1/";// 相关不相关分类训练样本目录

	public static String getClassifyResultName(Map<String, BigDecimal> resultMap) {
		if (resultMap.isEmpty()) {
			return "N/A";
		}
		BigDecimal result = new BigDecimal(0);
		String classifierName = "";
		Set<String> classifierSet = resultMap.keySet();
		for (String classifier : classifierSet) {
			BigDecimal classifierValue = resultMap.get(classifier);
			if (classifierValue.compareTo(result) >= 1) {
				result = classifierValue;
				classifierName = classifier;
			}
		}
		return classifierName;
	}


	public static boolean isRelated(String html) throws FileNotFoundException,
			IOException {
		String[] classifier = { "Related" };
		String classname = null;
		boolean rs=false;
		BigDecimal x = new BigDecimal(0);
		String content = TrainSampleDataManager.HtmlProcess(html);
		try {
			if (!content.equals("null")) {
				Set<String> words = ChineseTokenizer.segStr(content).keySet();
				// TrainSampleDataManager.process(SAMPLE_DATA);
				for (int j = 0; j < classifier.length; j++) {
					// System.out.print(classifier[j]+"类特征");
					Map<String, BigDecimal> p = TrainSampleDataManager.count(
							classifier[j], SAMPLE_DATA);
					BigDecimal c = new BigDecimal(2);

					int count = 0;
					for (String w : words) {
						if (p.containsKey(w)) {
							if (p.get(w).compareTo(c) == 0) {
								count++;
							}
						}
					}
					if (count >= 1) {
						classname = "related";
						rs=true;
					} else {
						classname = "disrelated";
						rs=false;
					}
				}
			}
			else
			{
				rs=false;
			}
			
		} catch (Exception e) {
			rs=false;
		} finally {
			return rs;

		}

	}

	public static void Related() throws FileNotFoundException, IOException {

		String[] classifier = { "Related" };

		File sampleDataDir = new File(DEFAULT_DIR+"/pages/");// 所有待分类文件目录
		// 得到样本分类目录
		File[] fileList = sampleDataDir.listFiles();
		if (fileList == null) {
			throw new IllegalArgumentException("page is not exists!");
		}
		for (File file : fileList) {
			// 加载所有该分类下的所有文件名
			List<String> classFileList = TrainSampleDataManager.readDirs(file,
					new ArrayList<String>());
			for (String article : classFileList) {
				// 读取文件内容
				InputStream ios = new java.io.FileInputStream(article);
				byte[] b = new byte[3];
				ios.read(b);
				ios.close();
				String code = null;
				System.out.println(b[0]);
				if ((b[0] == -17 && b[1] == -69 && b[2] == -65)
						|| (b[0] == 60 && b[1] == 33 && b[2] == 68)
						|| b[0] == 13 || b[0] == 104) {
					code = "UTF-8";
				} else {
					code = "GB2312";
				}
				BufferedReader fr = new BufferedReader(new InputStreamReader(
						new FileInputStream(article), code));
				String[] name = article.split("\\\\");
				String content = TrainSampleDataManager.readFile(article);
				// System.out.println(content);
				if (!content.equals("null")) {
					Set<String> words = ChineseTokenizer.segStr(content)
							.keySet();
					// TrainSampleDataManager.process(SAMPLE_DATA);
					for (int j = 0; j < classifier.length; j++) {
						// System.out.print(classifier[j]+"类特征");
						Map<String, BigDecimal> p = TrainSampleDataManager
								.count(classifier[j], SAMPLE_DATA);
						BigDecimal c = new BigDecimal(2);
						String classname = null;
						int count = 0;
						for (String w : words) {
							if (p.containsKey(w)) {
								if (p.get(w).compareTo(c) == 0) {
									count++;
								}
							}
						}
						if (count >= 1) {
							classname = "related";
						} else {
							classname = "disrelated";
						}
						// BigDecimal
						// probility=confp.multiply(newr.get(classifier[j]));//条件概率*先验概率
						// System.out.println(probility);
						// newresultMap.put(classifier[j],probility);
						if (classname.equals("related")) {
							BufferedWriter fw = new BufferedWriter(
									new OutputStreamWriter(
											new FileOutputStream(
													DEFAULT_DIR+"Classification/"
															+ classname
															+ "/"
															+ name[name.length - 1]),
											code));// 相关不相关结果分类存放
							int ch = 0;
							while ((ch = fr.read()) != -1) {
								fw.write(ch);
							}

							fw.close();
						}
						System.out.println(file.getName() + "\t result:"
								+ classname);
					}
				}
			}
		}
	}
}