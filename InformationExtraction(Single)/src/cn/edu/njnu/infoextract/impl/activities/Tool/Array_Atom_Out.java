package cn.edu.njnu.infoextract.impl.activities.Tool;

import java.util.ArrayList;

import Basic_Class.Atom;

public class Array_Atom_Out {
	public static void out_activity(ArrayList<Atom> active_result,String active_from)
	{
		System.out.println("^^^^^^^"+active_from+"^^^^^^");
		for(int i=0;i<active_result.size();i++)
		{	
			System.out.println(active_result.get(i).key);
			System.out.println(active_result.get(i).value);
		}
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}
	public static void out_activity_list(ArrayList<ArrayList<Atom>> some_active_result,String active_from)
	{
		ArrayList<Atom> active_result=new ArrayList<Atom>(); 
			for(int i=0;i<some_active_result.size();i++){
				active_result=some_active_result.get(i);
				System.out.println("^^^^^^^"+active_from+"^^^^^^");
				for(int j=0;j<active_result.size();j++)
				{	
					System.out.println(active_result.get(j).key);
					System.out.println(active_result.get(j).value);
				}
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			}
	}
}
