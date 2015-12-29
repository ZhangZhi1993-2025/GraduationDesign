package cn.edu.njnu.infoextract.impl.activities.Tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Tool_File {
    public static void writeFile(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, false);
            writer.write(content);
            writer.write("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public static ArrayList<String> readFolder(String File_Direction) {	
		ArrayList<String> files=new ArrayList<String>();
		File file=new File(File_Direction);
		File[] web_type_List=file.listFiles(); //Active  News Project filefolder
		for (int i = 0; i < web_type_List.length; i++) {
			if(web_type_List[i].isDirectory()){
				File[] file_list=web_type_List[i].listFiles();
				for(int j=0;j<file_list.length;j++){					
					if(!file_list[j].isDirectory())
					//System.out.println("------- "+tempList[i].getPath()+"-----------");
					{	
						files.add(file_list[j].getPath());
					}
				}
			}
		}
		return files;
		}
}
