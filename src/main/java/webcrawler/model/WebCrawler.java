package webcrawler.model;

import java.util.ArrayList;
import java.util.List;

import webcrawler.config.Config;
import webcrawler.utils.CheckerUtils;
import webcrawler.utils.IOUtils;
import webcrawler.utils.WebDriverUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Apply BFS (or DFS) strategy to visit all links within a url, and check whether linked page contains errors/unsafe-data	 
 * This project employes several techniques: 
	1. Breadth-first search (or depth-first search) strategy; 
	2. Selenium interacts with HTML, e.g., parse html page to get all links; 
	click link to open a new window; 
	judge whether pages contain bugs (e.g., errors/unsafe-data);
	take screenshot for problematic pages;
 * 
 */
public class WebCrawler {

	public int counter = 0;
	public int maxVisit = 10000;
	ArrayList<String> toVisitLinks = new ArrayList<String>();
	ArrayList<String> visitedLinks = new ArrayList<String>();
	CheckerUtils checker = new CheckerUtils();
	
	public void visitAllLists() throws Exception{
		WebDriver driver = WebDriverUtils.getWebDriver_new();
		this.login(driver, Config.getInstance().getProperty("loginURL"));
		
		long startTime = System.currentTimeMillis();
		
		toVisitLinks.add(driver.getCurrentUrl());
		
		this.visitLinks(driver, Config.getInstance().getProperty("ts.instance"));
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime)/(1000*60);
		System.out.println("\nDuration=" + duration + " mins");
		
		//save all clicked links
		StringBuilder links = new StringBuilder();
		for(String visitedLink: visitedLinks){
			links.append(visitedLink).append("\n");
		}
		
		String destFile_str = System.getProperty("user.dir") +"/screenshot/LinkList.txt"; 
		IOUtils.saveIntoFile(links.toString(), destFile_str);
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
	
	public void visitLinks(WebDriver driver, String version){
		try {
			while(toVisitLinks.size() > 0){
				//1. visit the first link if not visited before
				String toVisitLink =toVisitLinks.get(0);
				if(!visitedLinks.contains(toVisitLink) && toVisitLink.contains(version)){
					WebDriverUtils.openURL(driver, toVisitLink);
					visitedLinks.add(toVisitLink);
					Thread.sleep(1000);
					
					
					if(toVisitLink.contains("http://java.oracle.com/")|| toVisitLink.contains("javascript:void(0)")){
						System.out.println(toVisitLink);
					}
					
					//Check EKP error or UNSAFE-data here
					String text = "Please contact the system administrator";
			        String saveFile =  System.getProperty("user.dir")+"/screenshot/" + counter + "_EkpError.png";
					checker.checkErrorInPage(driver, text, saveFile);
					
					
					//text = "UNSAFE";
					//saveFile =  System.getProperty("user.dir")+"/screenshot/" + counter + "_UnsafeData.png";
					//checker.checkErrorInPage(driver, text, saveFile);
					
					text = "null";
					saveFile =  System.getProperty("user.dir")+"/screenshot/" + counter + "_NullData.png";
					checker.checkErrorInPage(driver, text, saveFile);
				}
				
				
				//2. delete the first link
				toVisitLinks.remove(0);
				
				
				//re-login if necessary
				if(toVisitLink.contains("LOGOFF") || driver.getCurrentUrl().equals(Config.getInstance().getProperty("loginURL"))
						||toVisitLink.contains("login")){
					this.login(driver, Config.getInstance().getProperty("loginURL"));
				}
				
				counter ++;
				System.out.printf("Counter=" + counter + ":%s   ...", driver.getCurrentUrl() +"\n");
				
				By by = By.xpath("//a");
				List<WebElement> links = driver.findElements(by);
				
				
				//3. keep all out-going links found in this link
				for(WebElement link: links){
					String text = link.getText();
					if(counter < maxVisit){
						if(!text.equals("")){
							String url = link.getAttribute("href");
							//filter 1: no invalid url
							if(url!= null){
								//filter 2: not-visited url
								if(!toVisitLinks.contains(url) && !visitedLinks.contains(url)){
									toVisitLinks.add(url);
								}
							}
						}	
					}else{
						return;
					}
				}
				
				System.out.printf("\nFound Links:(%d);visited:(%d);toVisit:(%d)\n", links.size(), visitedLinks.size(), toVisitLinks.size());
	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String destFile_str = System.getProperty("user.dir") +"/screenshot/LinkList.txt";
			IOUtils.saveIntoFile(visitedLinks, destFile_str);
			e.printStackTrace();
		} finally{
			String destFile_str = System.getProperty("user.dir") +"/screenshot/LinkList.txt";
			IOUtils.saveIntoFile(visitedLinks, destFile_str);
			driver.close();
		}
	}
}
