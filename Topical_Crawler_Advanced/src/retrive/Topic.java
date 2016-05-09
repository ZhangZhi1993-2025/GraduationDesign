package retrive;
import java.util.*;
import java.util.regex.Pattern;

import java.util.regex.*;
public class Topic {
	private ArrayList<String> hot_key=new ArrayList<String>();
	private ArrayList<String> normal_key=new ArrayList<String>();
	private ArrayList<String> less_key=new ArrayList<String>();
	public static double contextRate;
	public static double anchorRate;
    public static void setRate(double anRate, double conRate) {
		
		contextRate=conRate;
		anchorRate=anRate;
	}
	public void addKey(String key,int level)
	{
		switch(level)
		{
		case 1:hot_key.add(key);break;
		case 2:normal_key.add(key);break;
		case 3:less_key.add(key);break;
		default:
		}
	}
	public void print()
	{
		System.out.println("hot_key:");
		for(String s:hot_key)
		{
			System.out.println(s);
		}
		System.out.println("normal_key:");
		for(String s:normal_key)
		{
			System.out.println(s);
		}
		System.out.println("less_key:");
		for(String s:less_key)
		{
			System.out.println(s);
		}
	}
	public void setTopic(String[] topic,int level)
	{
		switch(level)
		{
		case 1:
			for(String s:topic)
			{
				hot_key.add(s);
			}
			break;
		case 2:
			for(String s:topic)
			{
				normal_key.add(s);
			}
			break;
		case 3:
			for(String s:topic)
			{
				less_key.add(s);
			}
			break;
			default:break;
		}
	}
	public void clear()
	{
		hot_key.clear();
		normal_key.clear();
		less_key.clear();
	}
	
	
	//计算文本的相似度
	public double ContextPrior(String context)
	{
		double value=0;
		double value1=0,value2=0;
		StringBuffer temp=new StringBuffer("");
		Set<String> key=new HashSet<String>();
		for(String s:hot_key)
		{
			temp.append(s+"|");
		}
		for(String s:normal_key)
		{
			temp.append(s+"|");
		}
		for(int i=0;i<less_key.size()-1;i++)
		{
			temp.append(less_key.get(i)+"|");
		}
		temp.append(less_key.get(less_key.size()-1));
		String regex=temp.toString();
		
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(context);
		while(matcher.find())
		{
			key.add(matcher.group().trim());
		}
		int flag=0;
		int cnt=0;
		for(String s:hot_key)
		{
			if(key.contains(s))
			{
				if(flag==0)
				{
					value1+=0.8;
					flag=1;
				}
				cnt++;
				
			}
		}
		flag=0;
		for(String s:normal_key)
		{
			if(key.contains(s))
			{
				if(flag==0)
				{
					value1+=0.15;
					flag=1;
				}
				cnt++;
			}
		}
		flag=0;
		for(String s:less_key)
		{
			if(key.contains(s))
			{
				if(flag==0)
				{

					value1+=0.05;
					flag=1;
				}
				cnt++;
			}
		}
		value2=(double)cnt/(hot_key.size()+normal_key.size()+less_key.size());
		value=0.5*value1+0.5*value2;
		return value;
	}
	
	
	
	//计算锚文本的相似度
	public double AnchorPrior(String anchor)
	{
		double value=0;
		StringBuffer temp=new StringBuffer("");
		Set<String> key=new HashSet<String>();
		for(String s:hot_key)
		{
			temp.append(s+"|");
		}
		for(String s:normal_key)
		{
			temp.append(s+"|");
		}
		for(int i=0;i<less_key.size()-1;i++)
		{
			temp.append(less_key.get(i)+"|");
		}
		temp.append(less_key.get(less_key.size()-1));
		String regex=temp.toString();
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(anchor);
		while(matcher.find())
		{
			key.add(matcher.group().trim());
		}
		for(String s:hot_key)
		{
			if(key.contains(s))
			{
				value+=0.8;
                                break;				
				
			}
		}
		for(String s:normal_key)
		{
			if(key.contains(s))
			{
				value+=0.15;
                               break;
			}
		}
		for(String s:less_key)
		{
			if(key.contains(s))
			{

				value+=0.05;
                                break;
			}
		}
		return value;
	}
	
	public static void main(String[] args)
	{
		Topic topic=new Topic();
		topic.addKey("创客", 1);
		topic.addKey("活动", 2);
		topic.addKey("联系方式", 3);
		topic.addKey("地址", 3);
		topic.AnchorPrior("创客活动的联系方式是：123");
	}
	
	
}