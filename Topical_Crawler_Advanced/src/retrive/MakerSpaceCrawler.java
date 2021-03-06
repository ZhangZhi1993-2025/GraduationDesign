package retrive;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeIterator;

import cc.mallet.classify.Classifier;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import classfication.*;
//import cn.edu.njnu.*;

public class MakerSpaceCrawler {
	/**
	 * 爬虫主题
	 * 
	 */
	public static Topic topic = new Topic();

	public void Write(String s, String filepath) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
		bw.write(s);
		bw.flush();
		bw.close();
	}

	// 初始化爬虫主题
	public void initCrawlerWithTopic(String[] hot, String[] normal,
			String[] less, double[] rate) {
		topic.setTopic(hot, 1);
		topic.setTopic(normal, 2);
		topic.setTopic(less, 3);
		Topic.setRate(rate[0], rate[1]);
	}

	/**
	 * * 判断链接是否有效 　　
	 * * 输入链接 　　
	 * * 返回true或者false 　　
	 */
	public static boolean isValid(String strLink) {
		URL url;
		boolean flag = true;
		try {
			url = new URL(strLink);

			HttpURLConnection connt = (HttpURLConnection) url.openConnection();
			// connt.setReadTimeout(3000);
			connt.setConnectTimeout(3000);
			connt.setReadTimeout(10000);
			
			connt.setRequestMethod("GET");
			connt.connect();
			String strMessage = connt.getResponseMessage();
			if (connt.getErrorStream() != null)
				flag = false;
			connt.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			return flag;
		}
	}

	/**
	 * 抓取过程
	 * 
	 * @return
	 * @param seeds
	 * @throws IOException
	 */
	public void crawling() throws Exception {
		// System.out.println("into crawling");
		// 定义过滤器
		LinkFilter filter = new LinkFilter() {
			public boolean accept(String url) {
				if (url.contains("http"))
					return true;
				else
					return false;
			}
		};
		int cnt = 0;
		// 循环条件：待抓取的链接不空
		// System.out.println("queue is empty="+PriorQueue.isEmpty());
		// PriorQueue.print();
		while (!PriorQueue.isEmpty()) {
			System.out.println("QueueSize=" + PriorQueue.size());
			try {

				// System.out.println("cnt="+cnt);
				// 队头URL 出队列
				String visitUrl = PriorQueue.next();

				System.out.println(visitUrl);
				if (visitUrl == null || visitUrl.equals("")) {
					System.out.println(visitUrl);
					continue;
				}
				if (!isValid(visitUrl)) {
					System.out.println("url inValid");
					continue;
				}

				DownLoadFile downLoader = new DownLoadFile();
				// 判断网页是否与创客空间相关
				// *****************采用分类器判断

				String[] pageCon = GetPage(visitUrl);
				if (pageCon[0] == null || pageCon[0].length() == 0) {
					System.out.println("pageCon empty");
					continue;
				}

				String context = HtmlParserTool.getPlainText(visitUrl);
				if (context == null || context.length() == 0)
					continue;
			
				// System.out.println(context);
				System.out.println("start judge");
				boolean flag = false;
				
				try {
					flag=RelatedProb.isRelated(pageCon[0]);
				} catch (Exception e) {
					flag = false;
				}
				System.out.println("flag="+flag);
				if (flag) {
					// 下载网页并提取创客信息
					double contextValue = topic.ContextPrior(context);
					String rs = "";
					rs=NaiveBayesMain.Result(pageCon[0]);
					downLoader.downloadFile(visitUrl, rs);
					cnt++;
					if (cnt % 50 == 0) {
						PriorQueue.end("doc/Init");
						// Thread.sleep(30000);
						cnt = 0;
					}

					UrlValue uv = null;

					// 提取出下载网页中的URL
					Set<Anchor> links = HtmlParserTool.extracLinks(visitUrl,
							filter);
					System.out.println("提取出url=" + links.size());
					// 将提取出的URL加入到优先队列
					for (Anchor link : links) {
						// System.out.println(visitUrl+"  "+link.url);
						uv = new UrlValue();
						uv.url = link.url.trim();
						if (uv.url.equals("")
								|| link.url.contains("genshuixue")
								|| link.url.contains("tieba.baidu")
								|| link.url.contains("douban"))
							continue;
						// System.out.println(contextValue+" "+topic.AnchorPrior(link.anchor)+" "+uv.url);
						uv.value = contextValue * topic.contextRate
								+ topic.AnchorPrior(link.anchor)
								* topic.anchorRate;
						if (uv.value > 1) {
							System.out.println("value>1=" + uv.value);
							System.out.println(contextValue + " "
									+ topic.AnchorPrior(link.anchor));
							return;
						}
						PriorQueue.add(uv);
					}
					// ***************提取url并计算优先级

				} else {
					System.out.println("disrelated");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public static String[] GetPage(String url) {
		String[] result = new String[2];
		String encoding = null;
		String content = null;
		HttpURLConnection conn=null;
		StringBuffer html = new StringBuffer("");
		try {

			URL urlPage = new URL(url);
			 conn = (HttpURLConnection) urlPage
					.openConnection();
			conn.setConnectTimeout(100000);
			conn.setReadTimeout(100000);
			Parser parser = new Parser(conn);

			encoding = parser.getEncoding();
			if (parser.getEncoding().equals("ISO-8859-1")) {
				encoding = PageEncodeDetector.getCharset(url);
				if (encoding != null) {
					parser.setEncoding(encoding);
				} else {
					encoding = parser.getEncoding();
				}
			}
			result[1] = encoding;
			System.out.println(encoding);
			NodeIterator it = parser.elements();

			while (it.hasMoreNodes()) {

				Node node = it.nextNode();
				html.append(node.toHtml());

			}
			content = html.toString();
			result[0] = content;

		} catch (Exception e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			content = null;
			e.printStackTrace();
		} finally {
			conn.disconnect();
			return result;
		}

	}

	public static void main(String[] args) {
		run();
		// System.out.println(isValid("http://www.bjmakerspace.com/"));
	}

	public static void InitKeyWords(MakerSpaceCrawler crawler) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"doc/Init/keywords.txt"));
		String line = null;
		String[] hot_key = null;
		String[] normal_key = null;
		String[] less_key = null;
		line = br.readLine();
		hot_key = line.split(" ");
		line = br.readLine();
		normal_key = line.split(" ");
		line = br.readLine();
		less_key = line.split(" ");

		double[] rate = { 0.5, 0.4, 0.1 };

		crawler.initCrawlerWithTopic(hot_key, normal_key, less_key, rate);
		br.close();
	}

	public static void run() {
		//while (true) 
                {
			MakerSpaceCrawler crawler = new MakerSpaceCrawler();
			try {
				PriorQueue.InitQueue("doc/Init");
				InitKeyWords(crawler);
				NaiveBayesMain.Init();
				System.out.println("Init over");
				// PriorQueue.print();
				crawler.crawling();
                                System.out.println("start");
                               // Main.main(null);

			} catch (Exception e) {
				System.out.println(e);
			} finally {
				if (PriorQueue.isEmpty())
					System.out.println("queue is empty");
				PriorQueue.end("doc/Init");
				try {
					//Thread.sleep(1000*60*60*24*3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}