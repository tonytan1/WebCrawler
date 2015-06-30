package netd.webcrawler.model;

import java.util.ArrayList;

import netd.config.Config;
import netd.webcrawler.utils.CheckerUtils;
import netd.webcrawler.utils.IOUtils;
import netd.webcrawler.utils.WebDriverUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**This is a concurrent version of WebCrawler which aims to reduce
 * running time. However, this class is different WebCrawler since it
 * loads link data from external files while WebCrawler collects link
 * data (via DFS/BFS) at runtime.  
 * 
 */
public class WebCrawlerThread implements Runnable {
	
	int start = 0, end = 0;
	ArrayList<String> toVisitLinks = new ArrayList<String>();
	ArrayList<String> visitedLinks = new ArrayList<String>();
	
	WebDriver driver = null;
	CheckerUtils checker = null;
	String ekpVersion = "";
	
	public WebCrawlerThread(String ekpVersion, ArrayList<String> linkSet, int start, int end){
		driver = WebDriverUtils.getWebDriver_new();
		checker = new CheckerUtils();
		this.ekpVersion = ekpVersion;
		this.start = start;
		this.end = end;
		for(int i = start; i <= end && i < linkSet.size(); i ++){			
			toVisitLinks.add(linkSet.get(i));
		}
	}
	
	public void visitLinks(){
		try {
			int counter = 0;
			this.login(driver, Config.getInstance().getProperty("loginURL"));
			while(toVisitLinks.size() > 0){
				//1. visit the first link if not visited before
				String toVisitLink =toVisitLinks.get(0);
				if(!visitedLinks.contains(toVisitLink) && toVisitLink.contains(ekpVersion)){
					WebDriverUtils.openURL(driver, toVisitLink);
					visitedLinks.add(toVisitLink);
					Thread.sleep(1000);

					if(toVisitLink.contains("http://java.oracle.com/")|| toVisitLink.contains("javascript:void(0)")){
						System.out.println(toVisitLink);
					}
						 		  
					//Check EKP error or UNSAFE-data here
					String text = "Please contact the system administrator";
			        String saveFile =  System.getProperty("user.dir")+"/screenshot/" + (start+counter) + "_EkpError.png";
					boolean hasError = checker.checkErrorInPage(driver, text, saveFile);
					if(hasError){
						System.out.println("Found ekp error in:" + (start+counter));
					}
					
					text = "UNSAFE";
					saveFile =  System.getProperty("user.dir")+"/screenshot/" + (start+counter) + "_UnsafeData.png";
					hasError = checker.checkErrorInPage(driver, text, saveFile);
					if(hasError){
						System.out.println("Found unsafe data error in:" + (start+counter));
					}
					
					text = "null";
					saveFile =  System.getProperty("user.dir")+"/screenshot/" + (start+counter) + "_NullData.png";
					hasError = checker.checkErrorInPage(driver, text, saveFile);
					if(hasError){
						System.out.println("Found null error in:" + (start+counter));
					}
				}
				
				
				//2. delete the first link
				toVisitLinks.remove(0);
				
				//re-login if the executed link is logout
				if(toVisitLink.contains("LOGOFF") || WebDriverUtils.getCurrentUrl(driver).contains("login")){
					this.login(driver, Config.getInstance().getProperty("loginURL"));
				}
				System.out.printf("Counter=" + (start+counter) + ":%s", WebDriverUtils.getCurrentUrl(driver) +"\n");
				counter++;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			String destFile_str = System.getProperty("user.dir") +"/screenshot/LinkList_"+start+ "_" + end +".txt";
			IOUtils.saveIntoFile(visitedLinks, destFile_str);
			
			//driverUtils.close();
		}
	}
	
	private void login(WebDriver driver, String url){
		WebDriverUtils.openURL(driver, url);
		String UID = "uma_feng";
		String PWD = "11111111";
		
		By by = By.id("UID");
		WebDriverUtils.fillin_textbox(driver,by, UID);
		
		by = By.id("PWD");
		WebDriverUtils.fillin_textbox(driver, by, PWD);
		
		by = By.name("login");
		WebDriverUtils.clickButton(driver,by);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.visitLinks();
	}

}
