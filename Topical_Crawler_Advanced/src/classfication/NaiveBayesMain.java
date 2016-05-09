package classfication;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class NaiveBayesMain {
	public static final String DEFAULT_DIR = "./doc/web/";// 存放所有文件的目录
	public static final String SAMPLE_DATA = DEFAULT_DIR + "Sample/";// 存放四个分类的训练样本目录
	public static final String SAMPLE_DATA1 = DEFAULT_DIR + "Sample1/";// 存放相关不相关分类的训练样本目录
	private static Map<String, BigDecimal> preprobility = null;
	private static List<String> featurelist = null;

	public static String getClassifyResultName(Map<String, BigDecimal> resultMap) {// 获取分类结果名
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

	

	public static void run() throws FileNotFoundException, IOException {
		// Mallet.Related();//mallet代码
		RelatedProb.Related();
		// String[] classifier = { "news", "activities", "incubators",
		// "projects" };// 定义分类
		Map<String, BigDecimal> preprobility = TrainSampleDataManager
				.preprob(SAMPLE_DATA);// 读取训练样本的先验概率
		// TrainSampleDataManager.prob(SAMPLE_DATA);//训练特征的模型文件，存放在Features文件夹下
		// BigDecimal x = new BigDecimal(0);
		File sampleDataDir = new File(DEFAULT_DIR+"Classification/related");// 待分类文件目录
		// 得到样本分类目录
		File[] fileList = sampleDataDir.listFiles();
		if (fileList == null) {
			throw new IllegalArgumentException("page is not exists!");
		}
		BufferedReader featureread = new BufferedReader(
				new InputStreamReader(new FileInputStream(
						DEFAULT_DIR+"Features/features.txt"), "UTF-8"));
		String ch = null;
		List<String> featurelist = new ArrayList<String>();
		while ((ch = featureread.readLine()) != null) {
			featurelist.add(ch);
		}
		for (File file : fileList) {
			// 加载所有该分类下的所有文件名
			List<String> classFileList = TrainSampleDataManager.readDirs(file,
					new ArrayList<String>());
			for (String article : classFileList) {
				// 读取文件内容
				// System.out.println(article);
				InputStream ios = new java.io.FileInputStream(article);
				byte[] b = new byte[3];
				ios.read(b);
				ios.close();
				String code = null;
				// System.out.println(b[0]);
				if ((b[0] == -17 && b[1] == -69 && b[2] == -65)
						|| (b[0] == 60 && b[1] == 33 && b[2] == 68)
						|| b[0] == 13 || b[0] == 104) {
					code = "UTF-8";
				} else {
					code = "GB2312";
				}
				BufferedReader fr = new BufferedReader(new InputStreamReader(
						new FileInputStream(article), code));// 读取文件的全部内容
				String[] name = article.split("\\\\");
				String content = TrainSampleDataManager.readFile(article);
				Map<String, BigDecimal> result = TrainSampleDataManager
						.preprob(SAMPLE_DATA);
				// System.out.println(content);
				if (!content.equals("null")) {
					Set<String> words = ChineseTokenizer.segStr(content)
							.keySet();
					BigDecimal probility = new BigDecimal(1);
					BigDecimal probility2 = new BigDecimal(1);
					BigDecimal probility3 = new BigDecimal(1);
					BigDecimal probility4 = new BigDecimal(1);
					for (String w : words) {
						for (String f : featurelist) {
							String[] feature = f.split("\t");
							if (w.equals(feature[0])) {
								// System.out.println(w);
								probility = probility.multiply(new BigDecimal(
										feature[1]));// 属于news类的概率
								probility2 = probility2
										.multiply(new BigDecimal(feature[2]));// 属于news类的概率
								probility3 = probility3
										.multiply(new BigDecimal(feature[3]));// 属于news类的概率
								probility4 = probility4
										.multiply(new BigDecimal(feature[4]));// 属于news类的概率
							}
						}
					}
					probility = probility.multiply(preprobility.get("news"));
					probility2 = probility2.multiply(preprobility
							.get("activities"));
					probility3 = probility3.multiply(preprobility
							.get("incubators"));
					probility4 = probility4.multiply(preprobility
							.get("projects"));
					result.put("news", probility);
					result.put("activities", probility2);
					result.put("incubators", probility3);
					result.put("projects", probility4);

				}
				// System.out.println(article);
				String resultname = getClassifyResultName(result);// 分类名
				if (content.contains("新闻")) {
					resultname = "news";
				}
				System.out.println(name[name.length - 1] + "\tresult: "
						+ resultname);
				featureread.close();
				BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(DEFAULT_DIR+"Classification/"
								+ resultname + "/" + name[name.length - 1]),
						code));// 最后四个分类结果文件复制到对应文件夹
				int readline = 0;
				while ((readline = fr.read()) != -1) {
					fw.write(readline);
				}
				fw.close();
			}
		}
		System.exit(0);
	}

	public static void Init() {
		System.out.println("Inital");
		BufferedReader featureread = null;
		try {
			preprobility = TrainSampleDataManager.preprob(SAMPLE_DATA);// 读取训练样本的先验概率
			featureread = new BufferedReader(new InputStreamReader(
					new FileInputStream(DEFAULT_DIR+"Features/features.txt"),
					"UTF-8"));
			String ch = null;
			featurelist = new ArrayList<String>();
			while ((ch = featureread.readLine()) != null) {
				featurelist.add(ch);
			}
		} catch (Exception e) {
			if (featureread != null)
				try {
					featureread.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
	}

	public static String Result(String html) throws FileNotFoundException,
			IOException {
		String resultname = "";
		if (preprobility == null || preprobility.isEmpty()) {
			Init();
		}
		if(featurelist==null||featurelist.isEmpty())
		{
			Init();
		}
		String content = TrainSampleDataManager.HtmlProcess(html);
		Map<String, BigDecimal> result = new HashMap<String,BigDecimal>();
		if (!content.equals("null")) {
			Set<String> words = ChineseTokenizer.segStr(content).keySet();
			BigDecimal probility = new BigDecimal(1);
			BigDecimal probility2 = new BigDecimal(1);
			BigDecimal probility3 = new BigDecimal(1);
			BigDecimal probility4 = new BigDecimal(1);
			for (String w : words) {
				for (String f : featurelist) {
					String[] feature = f.split("\t");
					if (w.equals(feature[0])) {
						// System.out.println(w);
						probility = probility.multiply(new BigDecimal(
								feature[1]));// 属于news类的概率
						probility2 = probility2.multiply(new BigDecimal(
								feature[2]));// 属于news类的概率
						probility3 = probility3.multiply(new BigDecimal(
								feature[3]));// 属于news类的概率
						probility4 = probility4.multiply(new BigDecimal(
								feature[4]));// 属于news类的概率
					}
				}
			}
			probility = probility.multiply(preprobility.get("news"));
			probility2 = probility2.multiply(preprobility.get("activities"));
			probility3 = probility3.multiply(preprobility.get("incubators"));
			probility4 = probility4.multiply(preprobility.get("projects"));
			result.put("news", probility);
			result.put("activities", probility2);
			result.put("incubators", probility3);
			result.put("projects", probility4);
			resultname = getClassifyResultName(result);// 分类名
			if (content.contains("新闻")) {
				resultname = "news";
			}
			System.out.println("\tresult: " + resultname);
		}
		return resultname;
	}
	public static void main(String[] args) throws Exception
	{
		//run();
	BufferedReader br=new BufferedReader(new FileReader("test.htm"));
		String line=null;
		String s="";
		while((line=br.readLine())!=null)
		{
			s+=line+"\n";
		}
		System.out.println(s);
		System.out.println(Result(s));
		RelatedProb.isRelated(s);
	}
}