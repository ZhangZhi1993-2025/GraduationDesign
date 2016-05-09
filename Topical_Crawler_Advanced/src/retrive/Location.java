package retrive;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Location {
	public String city;  //城市
	public String distric;  //区（县，市）
	public String road;     //路
	public int    number;   //门牌号
	public String court;    //小区名称(单位名臣）
	public int    buildingnum;  //楼号（..栋楼）
	public Location()
	{
		city="";
		distric="";
		road="";
		number=0;
		court="";
		buildingnum=0;
	}
	public void Set(String address)
	{
		String regex="(.*市)|(.*区)";
		Pattern pattern=Pattern.compile(regex);
		Matcher m=pattern.matcher(address);
		int i=0;
		while(m.find())
		{
			System.out.println(m.group());
			i++;
		}
		System.out.println(i);
	}
	public String toString()
	{
		String address="";
		address+=city+distric+road;
		if(number!=0)
		{
			address+=number+"号";
		}
		address+=court;
		if(buildingnum!=0)
		{
			address+=buildingnum+"栋";
		}
		return address;
	}
	public String getDirPath()
	{
		String dirPath="";
		dirPath+=city+"/"+distric+"/"+road;
		if(number!=0)
		{
			dirPath+="/"+number+"号";
		}
		dirPath+="/"+court;
		if(buildingnum!=0)
		{
			dirPath+="/"+buildingnum+"栋";
		}
		return dirPath;
	}
	public static void main(String[] args)
	{
		Location loc=new Location();
		loc.city="南京";
		loc.distric="栖霞区";
		loc.road="文苑路";
		loc.number=1;
		loc.court="南京师范大学";
		loc.buildingnum=11;
		loc.Set("南京市栖霞区文苑路");
	}

}