package retrive;
public class MakerSpace {
	private String name;   //网页名称
	private String address;  //地址
	private String introduction;  //简介
	
	public MakerSpace(){}
	
	public void setName(String name){
		this.name=name;
	}
	
	public void setAddress(String address){
		this.address=address;
	}
	
	public void setIntroduction(String introduction){
		this.introduction=introduction;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public String getIntroduction(){
		return this.introduction;
	}
}