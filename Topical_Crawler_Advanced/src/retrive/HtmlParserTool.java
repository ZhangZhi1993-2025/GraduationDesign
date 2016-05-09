package retrive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlParserTool {

	public static String getPlainText(String url) {
		StringBuffer context = new StringBuffer("");
		HttpURLConnection conn = null;
		try {
			URL urlPage = new URL(url);
			conn = (HttpURLConnection) urlPage.openConnection();
			conn.setConnectTimeout(100000);
			conn.setReadTimeout(150000);
			Parser parser = new Parser(conn);
			if (parser.getEncoding().equals("ISO-8859-1")) {
				String encoding = PageEncodeDetector.getCharset(url);
				if (encoding != null) {
					parser.setEncoding(encoding);
				}
			}
			NodeIterator it = parser.elements();
			while (it.hasMoreNodes()) {
				Node node = it.nextNode();
				context.append(" " + node.toPlainTextString());
			}
		} catch (Exception e) {
			context = new StringBuffer("");
		} finally {
			conn.disconnect();
			return context.toString();
		}
	}

	// 获取子链接，url为网页url，filter是链接过滤器，返回该页面子链接的HashSet及锚文本
	public static Set<Anchor> extracLinks(String url, LinkFilter filter)
			throws IOException {

		Set<Anchor> links = new HashSet<Anchor>();
		Anchor temp = null;
		HttpURLConnection conn = null;
		try {
			// 1、构造一个Parser，并设置相关的属性
			URL urlPage = new URL(url);
			conn = (HttpURLConnection) urlPage.openConnection();
			conn.setConnectTimeout(100000);
			conn.setReadTimeout(150000);
			Parser parser = new Parser(conn);
			if (parser.getEncoding().equals("ISO-8859-1")) {
				String encoding = PageEncodeDetector.getCharset(url);
				if (encoding != null) {
					parser.setEncoding(encoding);
				}
			}

			// 2.1、自定义一个Filter，用于过滤<Frame >标签，然后取得标签中的src属性值
			NodeFilter frameNodeFilter = new NodeFilter() {
				@Override
				public boolean accept(Node node) {
					if (node.getText().startsWith("frame src=")) {
						return true;
					} else {
						return false;
					}
				}
			};

			// 2.2、创建第二个Filter，过滤<a>标签
			NodeFilter aNodeFilter = new NodeClassFilter(LinkTag.class);

			// 2.3、净土上述2个Filter形成一个组合逻辑Filter。
			OrFilter linkFilter = new OrFilter(frameNodeFilter, aNodeFilter);

			// 3、使用parser根据filter来取得所有符合条件的节点
			NodeList nodeList = parser.extractAllNodesThatMatch(linkFilter);

			// 4、对取得的Node进行处理
			for (int i = 0; i < nodeList.size(); i++) {
				Node node = nodeList.elementAt(i);
				temp = new Anchor();

				String linkURL = "";
				// 如果链接类型为<a />
				if (node instanceof LinkTag) {
					LinkTag link = (LinkTag) node;
					linkURL = link.getLink();
					temp.url = linkURL;
					temp.anchor = link.getLinkText();
					// System.out.println(link.getLinkText()+"  "+linkURL);
				} else {
					// 如果类型为<frame />
					String nodeText = node.getText();
					// System.out.println(nodeText);
					int beginPosition = nodeText.indexOf("src=");
					nodeText = nodeText.substring(beginPosition);
					int endPosition = nodeText.indexOf(" ");
					if (endPosition == -1) {
						endPosition = nodeText.indexOf(">");
					}
					linkURL = nodeText.substring(5, endPosition - 1);
					temp.url = linkURL;
					temp.anchor = "";
				}
				// 判断是否属于本次搜索范围的url
				if (filter.accept(linkURL)) {
					links.add(temp);
				}
			}

		} catch (ParserException e) {
			e.printStackTrace();
		}

		finally {
			conn.disconnect();
			return links;
		}
	}

	public static void main(String[] args) {
		// System.out.println(SameSite("http://baidu.com/index.html","baidu.com/hello.html?x=12"));
		getPlainText("http://www.jia-cheng.net/Index.html");
	}
}

class Anchor {
	public String url;
	public String anchor;
}