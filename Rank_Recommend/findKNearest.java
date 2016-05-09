package testKD;

import edu.wlu.cs.levy.CG.KDTree;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author : Ye Cao
 * Date : 2015/12/11
 */

public class findKNearest {
	int k;//最近的k个点
	ArrayList<TopKPoint> points=new ArrayList<TopKPoint>();//所有点的经纬度
	
	findKNearest(int k,ArrayList<TopKPoint> points){
		this.k=k;
		for(int i=0;i<points.size();i++){
			TopKPoint temp=new TopKPoint();
			temp.lon=points.get(i).lon;
			temp.lat=points.get(i).lat;
			temp.pID=points.get(i).pID;
			this.points.add(temp);
		}
	
	}
	
	public ArrayList<TopKPoint> getResult(){
		ArrayList<TopKPoint> result=new ArrayList<TopKPoint>();//返回k个最近点的经纬度
		int m=points.size();//总节点数
//		int d=2;//数据维数为2
		int n=k;//最近的n个节点
		
		double [][] keys = new double [m][2];

		double [] targ = new double [2];
		for (int k=0; k<2; ++k) {
		    targ[k] = 0.5;
		}

		// make a D-dimensional KD-tree
		KDTree<Integer> kd = new KDTree<Integer>(2);

		try {

		    // add M randomly keyed nodes

		    for (int i=0; i<m; ++i) {

		    	keys[i][0]=points.get(i).lon;
		    	keys[i][1]=points.get(i).lat;
		    	kd.insert(keys[i], i);
		    }

		

		    List<Integer> nbrs = kd.nearest(targ, n);
		

		    System.out.println("类中节点经纬度：");
		    for (int j : nbrs) {
			for (int k=0; k<2; ++k) {
			    System.out.print(keys[j][k] + " ");		    
			}
			TopKPoint temp=new TopKPoint();
		    temp.lon=(float) keys[j][0];
		    temp.lat=(float) keys[j][1];
		    result.add(temp);
			System.out.println();
		    }
		}
		catch (Exception e) {
		    System.err.println(e);
		}
	    	
		
		
		
		
		return result;
	}
	
	
}
