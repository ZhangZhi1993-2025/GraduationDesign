package NetService;

import java.net.HttpURLConnection;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeIterator;

import retrive.PageEncodeDetector;

public class PageService {
	private String Url=null;
	private String html=null;
	private String plainText=null;
	private String charset=null;
	private HttpURLConnection conn=null;
	private Parser parser=null;
	public void close()
	{
		conn.disconnect();
	}
	public void open(String url)
	{
		Url=url;
		StringBuffer content=new StringBuffer("");
		StringBuffer text=new StringBuffer("");
		try
		{
			conn=NetOperator.getConnection12(url);
			parser=new Parser(conn);
			if (parser.getEncoding().equals("ISO-8859-1")) {
				charset= PageEncodeDetector.getCharset(url);
				if (charset != null) {
					parser.setEncoding(charset);
				} else {
					charset = parser.getEncoding();
				}
			}
			NodeIterator it = parser.elements();
			while (it.hasMoreNodes()) {

				Node node = it.nextNode();
				content.append(node.toHtml());
				text.append(node.toPlainTextString());
			}
			html=content.toString();
			plainText=text.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public PageService(String url)
	{
		open(url);
	}
	public String GetPage()
	{
		if(html==null)
		{
			open(Url);
			return html;
		}
		else
			return html;
	}
	public String getPlainText()
	{
		if(plainText!=null)
		{
			open(Url);
			return plainText;
		}
		else
			return plainText;
	}
	

}
