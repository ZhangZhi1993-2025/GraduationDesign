package testKD;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author : Ye Cao
 * Date : 2015/12/11
 */


public class testMyKD {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * ����ʱ���������һ������
		 */
		java.util.Random r = new java.util.Random(0);
		ArrayList<TopKPoint> points=new ArrayList<TopKPoint>();
		
		int k=5;//�������k���ڵ�
		for(int i=0;i<25;i++){
			TopKPoint temp=new TopKPoint();
			temp.lon=r.nextFloat();
			temp.lat=r.nextFloat();
			temp.pID=i+"";
			points.add(temp);
		}
		findKNearest fkn=new findKNearest(k,points);
		ArrayList<TopKPoint> result=fkn.getResult();//resultΪ�õ������е�
		System.out.println("ȡ������ľ�γ�ȣ�");
		for(int i=0;i<result.size();i++){
			System.out.println(result.get(i).lon+" "+result.get(i).lat);
		}
	}

}
