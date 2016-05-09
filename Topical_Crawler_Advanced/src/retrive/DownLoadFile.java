package retrive;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
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

import cc.mallet.classify.Classifier;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import domain.*;


public class DownLoadFile {
	/**
	 * 获取网页title作为文件名
	 */
	public String getTitle(String path) throws IOException {
		String text = "";
		URL url = new URL(path);
		HttpURLConnection conn=null;
		try {
			// title
			conn=(HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(100000);
			conn.setReadTimeout(150000);
			Parser parser = new Parser(conn);
			NodeFilter nFilter = new TagNameFilter("title");
			NodeList nodes = parser.extractAllNodesThatMatch(nFilter);
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node node = (Node) nodes.elementAt(i);
					text += node.toHtml();
				}
			}
			String regex = "<title>(.*)</title>";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(text);
			if (m.find()) {
				text = m.group(1);
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			conn.disconnect();
			text = "";
			e.printStackTrace();
		} finally {
			return text;
		}
	}

	/**
	 * 根据URL (和网页类型)生成需要保存的网页的文件名，去除URL中的非文件名字符
	 */
	public static String getFileNameByUrl(String url) {

		url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
		return url;
	}

	/**
	 * 获得url的网站地址
	 * 
	 * @param url
	 * @return
	 */
	public static String getWebSite(String url) {
		String website = "";
		url = url.replace("http://", "");
                url=url.replace("https://","");
		String[] list = url.split("/");
		if (list.length > 0) {
			website = list[0];
		}
		return website;
	}

	// 描述的详细地址
	private String detailAddress(String content) {
		String detailAddr = "";
		int flag1 = 0;
		// 查找网页中的详细地址信息
		String regex = "地点：(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		if (m.find()) {
			flag1 = 1;
			detailAddr = m.group(1);
			detailAddr = HtmlParser.safeHtml(detailAddr);
			detailAddr = HtmlParser.stripHtmlTags(detailAddr);
			detailAddr = HtmlParser.restoreSpecialCharacters(detailAddr);
		}
		if (flag1 == 0) {
			regex = "地址：(.*)";
			p = Pattern.compile(regex);
			m = p.matcher(content);
			if (m.find()) {
				detailAddr = m.group(1);
				detailAddr = HtmlParser.safeHtml(detailAddr);
				detailAddr = HtmlParser.stripHtmlTags(detailAddr);
				detailAddr = HtmlParser.restoreSpecialCharacters(detailAddr);
			}
		}
		return detailAddr;
	}

	// 获得城市名称的正则表达式
	private String ReadCityRegex(String filePath) {
		String regex = "";
		String path = "doc/chinesecity.txt";
		int begin = 1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			while ((line = br.readLine()) != null) {
				if (begin == 1) {
					regex += line;
					begin = 0;
				} else
					regex += "|" + line;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return regex;
	}

	/**
	 * 获取相关城市
	 * 
	 * @param url
	 * @param content
	 * @param regex
	 * @param detailAddr
	 * @return
	 */
	private String getCity(String url, String content, String regex,
			String detailAddr) {
		String city = "";
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int flag = 0;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(detailAddr);
		// 所在地区已经存在
		if (m.find()) {
			city = m.group();
		}
		// 所在地区不存在
		else {
			m = p.matcher(content);// 从网页本身寻找已经存在地址
			while (m.find()) // 统计每个地址出现的频度
			{
				flag = 1;
				String loc = m.group();
				if (map.containsKey(loc)) {
					int cnt = map.get(loc);
					cnt++;
					map.put(loc, cnt);
				} else
					map.put(loc, 1);
			}

			ArrayList<Entry<String, Integer>> keys = new ArrayList<Entry<String, Integer>>(
					map.entrySet());// 得到key集合
			// 把keys排序，但是呢，要按照后面这个比较的规则
			Collections.sort(keys, new Comparator<Object>() {
				@SuppressWarnings("unchecked")
				public int compare(Object o1, Object o2) {
					// 按照value的值降序排列
					if (((Entry<String, Integer>) o1).getValue() < ((Entry<String, Integer>) o2)
							.getValue())
						return 1;
					else if (((Entry<String, Integer>) o1).getValue() == ((Entry<String, Integer>) o2)
							.getValue())
						return 0;
					else
						return -1;
				}
			});
			// 如果网页中存在地址
			if (flag == 1)
				city = keys.get(0).getKey();
			// 网页中不存在地址
			else {

				int time = 30000, cnt = 1;
				while (city.isEmpty()) {
					regex = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";// 域名匹配
					p = Pattern.compile(regex);
					m = p.matcher(url);
					if (m.find()) {
						AddressUtils addressUtils = new AddressUtils();
						// System.out.println(m.group());
						String ip = addressUtils.getIP(m.group());
						String address = "";
						if (ip != null) {
							try {
								Thread.sleep(time);
								address = addressUtils.getAddresses("ip=" + ip,
										"utf-8");
								String regex1 = "([\\u4e00-\\u9fa5]+)市";
								Pattern p1 = Pattern.compile(regex1);
								Matcher m1 = p1.matcher(address);
								while (m1.find()) {
									city = m1.group(1);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					time += 30000;
					cnt++;
					if (cnt == 3)
						break;
				}
			}
		}
		return city;
	}

	/**
	 * 创建目录和保存网页
	 * 
	 * @param content
	 *            网页html文本
	 * @param url
	 * @param city
	 *            网站所在城市
	 * @param detailAddr
	 *            网站地址描述
	 * @param fileName
	 *            文件名称
	 */
	private void MakeDirAndSave(String content, String url, String city,
			String fileName, String encoding, String type) {
		try {
			String dirPath;
			String website;
			website=getWebSite(url);
			if(city.length()==0)
			{
				dirPath = "doc/创客网页/"+website+"/"+type;
			}
			else
			{
				dirPath = "doc/创客网页/"+city+"/"+website+"/"+type;
				
			}
			File file = new File(dirPath);
			if (!file.exists()) {
				System.out.println(file.mkdirs());
			} else {
				System.out.println("file exist!");
			}

			MakerSpace ms = new MakerSpace();
			// 保存地址
			if (fileName.length() > 200) {
				fileName = fileName.substring(0, 200);
			}
			String filePath = dirPath + "/" + fileName;
			if (!filePath.contains(".html")) {
				filePath += ".html";
			}
			// System.out.println("filePath="+filePath);

			BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(filePath)), "utf-8"));

			fw.write(url);
			fw.newLine();
			fw.write(content);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存网页到本地
	 * 
	 * @param content
	 *            网页html文本
	 * @param url
	 * @param fileName
	 *            网页保存本地文件名
	 * @throws InterruptedException
	 */
	private void saveToLocal(String content, String url, String fileName,
			String encoding, String type) throws InterruptedException {
		if (content == "")
			return;
		// 获得网页中相关地址描述
		String detailAddr = this.detailAddress(content);
		if (detailAddr != null) {
			detailAddr = detailAddr.replaceAll("\r|\n", "");
			String[] split = detailAddr.split("\\pP|\\pS");

			detailAddr = split[0];

			String city = "";
			int flag = 0;

			String path = "doc/chinesecity.txt";
			String regex = ReadCityRegex(path);
			city = getCity(url, content, regex, detailAddr);
			System.out.println("city=" + city);

			// 网页保存到本地
			MakeDirAndSave(content, url, city, fileName, encoding, type);
		}
	}

	// 解决url中文编码问题
	@SuppressWarnings("deprecation")
	public static String encodeUrl(String url) {
		if (!url.matches("[\u4e00-\u9fa5]")) {
			StringBuffer bf = new StringBuffer();
			char[] array = url.toCharArray();
			for (Character c : array) {
				if (c.toString().matches("[\u4e00-\u9fa5]")) {
					bf.append(URLEncoder.encode(c.toString()));
				} else {
					bf.append(c);
				}
			}
			url = bf.toString();
		}
		url = url.replace(" ", "");
		return url;
	}

	// 下载URL 指向的网页
	public String downloadFile(String url, String type)
			throws InterruptedException, UnsupportedEncodingException {
		String fileName = null;
		url = encodeUrl(url);
		try {
			String[] temp = MakerSpaceCrawler.GetPage(url);
			String content = temp[0];
			if (content == null || content.equals(""))
				return null;
			fileName = getFileNameByUrl(url);
			saveToLocal(content, url, fileName, temp[1], type);
		} catch (Exception e) {
			fileName = null;
			e.printStackTrace();
		} finally {
			return fileName;
		}

	}

	public static void main(String[] args) throws Exception {
		DownLoadFile dlf = new DownLoadFile();
		System.out.println(getWebSite("http://1010.njnu.edu.cn/fjdi.html"));
		
	}
}