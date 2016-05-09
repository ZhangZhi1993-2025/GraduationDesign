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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class NaiveBayesMain {
	public static final String DEFAULT_DIR = "doc/web/"; //存放所有文件的目录
	public static void Init() {
		TrainSampleDataManager.process();
	}

	/**
	 * 获得网页的标题
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readtitle(String file) throws FileNotFoundException, IOException {
		File input = new File(file);
		Document doc = Jsoup.parse(input, "UTF-8");
		Elements content1 = doc.select("title");
		String title = content1.text();
		return title;
	}
	public static String getTitle(String html) {
		Document doc = Jsoup.parse(html);
		Elements content1 = doc.select("title");
		String title = content1.text();
		return title;
	}

	public static void main(String[] args) throws Exception {
		//System.out.println(Result(""));

		run();
	}
	public static String Result(String html) {
//		 TrainSampleDataManager.process();
		String content = TrainSampleDataManager.ContentParser(html);
		String title = getTitle(html);
		Set<String> words = ChineseTokenizer.segStr(content).keySet();
		Map<String, BigDecimal> resultMap = MultinomialModelNaiveBayes.classifyResult(DefaultStopWordsHandler.dropStopWords(words));
		String filepath = null;
		if (MultinomialModelNaiveBayes.getClassifyResultName().equals("disrelated")) {
			if (content.contains("创业")) {
				System.out.println("	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
				if (MultinomialModelNaiveBayes.reClassifyResultName().equals("news")) {
					//					System.out.println("新闻");
					if (content.contains("新闻") || content.contains("news")) {
						System.out.println("	result:" + "news");
						filepath = "news";

					} else {
						System.out.println("	result:" + MultinomialModelNaiveBayes.reClassifyResultName2());
						filepath = MultinomialModelNaiveBayes.reClassifyResultName2();
					}
				} else {
					if ((content.contains("项目") || content.contains("project")) && MultinomialModelNaiveBayes.reClassifyResultName().equals("projects")) {
						System.out.println("	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
						filepath = MultinomialModelNaiveBayes.reClassifyResultName();
					} else {
						if ((content.contains("新闻") || content.contains("news")) && MultinomialModelNaiveBayes.reClassifyResultName().equals("news")) {
							System.out.println("	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
							filepath = MultinomialModelNaiveBayes.reClassifyResultName();
						} else {
							if (MultinomialModelNaiveBayes.reClassifyResultName().equals("indicators") && title.contains("新闻") == false && title.contains("活动") == false && title.contains("项目") == false) {
								System.out.println("	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
								filepath = MultinomialModelNaiveBayes.reClassifyResultName();
							} else {
								if (MultinomialModelNaiveBayes.reClassifyResultName().equals("activities") && (title.contains("activities") || title.contains("活动"))) {
									System.out.println("	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
									filepath = MultinomialModelNaiveBayes.reClassifyResultName();
								} else {
									System.out.println("	result:" + MultinomialModelNaiveBayes.reClassifyResultName2());
									filepath = MultinomialModelNaiveBayes.reClassifyResultName2();
								}
							}
						}
					}
				}
			} else {
				System.out.println("	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
				filepath = MultinomialModelNaiveBayes.getClassifyResultName();
//
			}
		} else {
			if (MultinomialModelNaiveBayes.getClassifyResultName().equals("news")) {
//					System.out.println("新闻");
				if (content.contains("新闻") || content.contains("news")) {
					System.out.println("	result:" + "news");
					filepath = "news";

				} else {
					System.out.println("	result:" + MultinomialModelNaiveBayes.getClassifyResultName2());
					filepath = MultinomialModelNaiveBayes.getClassifyResultName2();
				}
			} else {
				if ((content.contains("项目") || content.contains("project")) && MultinomialModelNaiveBayes.getClassifyResultName().equals("projects")) {
					System.out.println("	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
					filepath = MultinomialModelNaiveBayes.getClassifyResultName();
				} else {
					if ((content.contains("新闻") || content.contains("news")) && MultinomialModelNaiveBayes.getClassifyResultName().equals("news")) {
						System.out.println("	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
						filepath = MultinomialModelNaiveBayes.getClassifyResultName();
					} else {
						if (MultinomialModelNaiveBayes.getClassifyResultName().equals("incubators") && title.contains("新闻") == false && title.contains("活动") == false && title.contains("项目") == false) {
							System.out.println("	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
							filepath = MultinomialModelNaiveBayes.getClassifyResultName();
						} else {
							if (MultinomialModelNaiveBayes.getClassifyResultName().equals("activities") && (title.contains("activities") || title.contains("活动"))) {
								System.out.println("	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
								filepath = MultinomialModelNaiveBayes.getClassifyResultName();
							} else {
								System.out.println("	result:" + MultinomialModelNaiveBayes.getClassifyResultName2());
								filepath = MultinomialModelNaiveBayes.getClassifyResultName2();
							}

						}

					}
				}
			}
		}
		return filepath;
	}
	public static void run() throws FileNotFoundException, IOException {
		TrainSampleDataManager.process();
		File sampleDataDir = new File("./src/web/pages/"); //待分类文件目录
		//得到样本分类目录
		File[] fileList = sampleDataDir.listFiles();
		if (fileList == null) {
			throw new IllegalArgumentException("page is not exists!");
		}
		for (File file : fileList ) {
			//加载所有该分类下的所有文件名
			List<String> classFileList = TrainSampleDataManager.readDirs(file, new ArrayList<String>());
			for (String article : classFileList) {
				//读取文件内容
				String content = TrainSampleDataManager.readFile(article);
				String title = readtitle(article);
//				System.out.println(content);
				Set<String> words = ChineseTokenizer.segStr(content).keySet();
				Map<String, BigDecimal> resultMap = MultinomialModelNaiveBayes.classifyResult(DefaultStopWordsHandler.dropStopWords(words));
				//Set<String> set=resultMap.keySet();
//				for(String str: set){
//					System.out.println("classifer:"+str+"     probability:"+resultMap.get(str));
//				}
				BufferedReader fr = new BufferedReader (new InputStreamReader (new FileInputStream (article), "UTF-8"));
				String[] name = article.split("\\\\");
				String filepath = null;
				if (MultinomialModelNaiveBayes.getClassifyResultName().equals("disrelated")) {
					if (content.contains("创业")) {
						System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
						if (MultinomialModelNaiveBayes.reClassifyResultName().equals("news")) {
							//					System.out.println("新闻");
							if (content.contains("新闻") || content.contains("news")) {
								System.out.println(name[name.length - 1] + "	result:" + "news");
								filepath = "news";

							} else {
								System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.reClassifyResultName2());
								filepath = MultinomialModelNaiveBayes.reClassifyResultName2();
							}
						} else {
							if ((content.contains("项目") || content.contains("project")) && MultinomialModelNaiveBayes.reClassifyResultName().equals("projects")) {
								System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
								filepath = MultinomialModelNaiveBayes.reClassifyResultName();
							} else {
								if ((content.contains("新闻") || content.contains("news")) && MultinomialModelNaiveBayes.reClassifyResultName().equals("news")) {
									System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
									filepath = MultinomialModelNaiveBayes.reClassifyResultName();
								} else {
									if (MultinomialModelNaiveBayes.reClassifyResultName().equals("indicators") && title.contains("新闻") == false && title.contains("活动") == false && title.contains("项目") == false) {
										System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
										filepath = MultinomialModelNaiveBayes.reClassifyResultName();
									} else {
										if (MultinomialModelNaiveBayes.reClassifyResultName().equals("activities") && (title.contains("activities") || title.contains("活动"))) {
											System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.reClassifyResultName());
											filepath = MultinomialModelNaiveBayes.reClassifyResultName();
										} else {
											System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.reClassifyResultName2());
											filepath = MultinomialModelNaiveBayes.reClassifyResultName2();
										}
									}
								}
							}
						}
					} else {
						System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
						filepath = MultinomialModelNaiveBayes.getClassifyResultName();
//
					}
				} else {
					if (MultinomialModelNaiveBayes.getClassifyResultName().equals("news")) {
						//					System.out.println("新闻");
						if (content.contains("新闻") || content.contains("news")) {
							System.out.println(name[name.length - 1] + "	result:" + "news");
							filepath = "news";

						} else {
							System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.getClassifyResultName2());
							filepath = MultinomialModelNaiveBayes.getClassifyResultName2();
						}
					} else {
						if ((content.contains("项目") || content.contains("project")) && MultinomialModelNaiveBayes.getClassifyResultName().equals("projects")) {
							System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
							filepath = MultinomialModelNaiveBayes.getClassifyResultName();
						} else {
							if ((content.contains("新闻") || content.contains("news")) && MultinomialModelNaiveBayes.getClassifyResultName().equals("news")) {
								System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
								filepath = MultinomialModelNaiveBayes.getClassifyResultName();
							} else {
								if (MultinomialModelNaiveBayes.getClassifyResultName().equals("incubators") && title.contains("新闻") == false && title.contains("活动") == false && title.contains("项目") == false) {
									System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
									filepath = MultinomialModelNaiveBayes.getClassifyResultName();
								} else {
									if (MultinomialModelNaiveBayes.getClassifyResultName().equals("activities") && (title.contains("activities") || title.contains("活动"))) {
										System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.getClassifyResultName());
										filepath = MultinomialModelNaiveBayes.getClassifyResultName();
									} else {
										System.out.println(name[name.length - 1] + "	result:" + MultinomialModelNaiveBayes.getClassifyResultName2());
										filepath = MultinomialModelNaiveBayes.getClassifyResultName2();
									}

								}

							}
						}
					}
				}

//				System.out.println(name[name.length-1]);
				BufferedWriter fw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream ("./src/web/Classification/" + filepath + "/" + name[name.length - 1]), "UTF-8"));
				int ch = 0;
				while ((ch = fr.read()) != -1) {
					fw.write(ch);
				}
				fw.flush();
				fr.close();
				fw.close();
			}
		}
		System.exit(0);
	}
}
