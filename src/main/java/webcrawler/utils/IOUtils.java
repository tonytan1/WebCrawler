package webcrawler.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class IOUtils {
	
	public static void mkParentDirs(File destFile){
		File dir = destFile.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
	}
	
	public synchronized static void copyFile(File srcFile, File destFile){
		IOUtils.mkParentDirs(destFile);
		
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> loadFile(String file_str){
		ArrayList<String> results = new ArrayList();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file_str));
			
			String str = "";
			while((str = br.readLine())!= null){
					results.add(str);	
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	
	/**Load data from files in a special rule: only load data contains special characters  
	 * 
	 * @param file_str
	 * @return
	 */
	public static ArrayList<String> loadFile_special(String file_str){
		ArrayList<String> results = new ArrayList();
		
		try (BufferedReader br = 
				new BufferedReader(new FileReader(file_str)))
		{
			
			String str = "";
			while((str = br.readLine())!= null){
				if(str.contains("Visit: javascript") || str.contains("Visit: http")){
					int index = str.indexOf(":");
					String line = str.substring(index+1).trim();					
					results.add(line);	
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}
	
	public synchronized static void saveIntoFile(String str, String destFile_str){
		File destFile = new File(destFile_str);
		IOUtils.mkParentDirs(destFile);
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(destFile));
			bw.write(str);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void saveIntoFile(
			ArrayList<String> visitedLinks,String destFile_str){
		StringBuilder links = new StringBuilder();
		for(String visitedLink: visitedLinks){
			links.append(visitedLink).append("\n");
		}
		IOUtils.saveIntoFile(links.toString(), destFile_str);
	}
}
