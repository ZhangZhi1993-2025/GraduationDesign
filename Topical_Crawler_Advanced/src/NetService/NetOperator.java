package NetService;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetOperator {
	public static HttpURLConnection getConnection(String url,int connectout,int readout)
	{
		HttpURLConnection conn=null;
		try
		{
			URL urlPage=new URL(url);
			conn=(HttpURLConnection)urlPage.openConnection();
			conn.setConnectTimeout(connectout);
			conn.setReadTimeout(readout);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return conn;
		}
	}
	public static HttpURLConnection getConnection12(String url)
	{
		return  getConnection(url,100000,150000);
	}
	public static void disconnect(HttpURLConnection con)
	{
		con.disconnect();
	}
	

}
