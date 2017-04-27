package src.main.java.webcrawler.model;

import src.main.java.webcrawler.utils.WebDriverUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import netd.config.Config;
import netd.webcrawler.utils.WebDriverUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxProfile;

public class WebWalker {
	
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
	
	/**Iterate all links in Talent Suite.
	 * 
	 * @param driver
	 */
	public void runClickAllLinks(){
		
		String destDir = Config.getInstance().getProperty("screenShotDir");
		FirefoxProfile fxProfile = new FirefoxProfile();
		fxProfile.setPreference("browser.download.folderList",2);//0: desktop; 1: default dir; 2: browser.download.dir;
	    fxProfile.setPreference("browser.download.dir",  destDir);
	    fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/vnd.ms-excel");
	    fxProfile.setPreference("browser.download.manager.showWhenStarting",false);
	    WebDriver driver = WebDriverUtils.getWebDriver_new(fxProfile);
		
		
		ArrayList<String> toVisitLinks = new ArrayList<String>();
		ArrayList<String> visitedLinks = new ArrayList<String>();
		toVisitLinks.add(Config.getInstance().getProperty("loginURL"));
		int maxVisit = 10;
		int counter = 1;
		this.clickLinks(driver, toVisitLinks, visitedLinks, counter, maxVisit);
		
	}
	
	public void clickLinks(WebDriver driver, ArrayList<String> toVisitLinks, ArrayList<String> visitedLinks, int counter, int maxVisit){
		StringBuilder sb = new StringBuilder();
		String msg = "";
		
		try {
			long startTime = System.currentTimeMillis();
			while(toVisitLinks.size() > 0){
				String toVisitLink =toVisitLinks.get(0);
				toVisitLinks.remove(0); //delete first un-visited link
				if(!visitedLinks.contains(toVisitLink) && toVisitLink.contains(Config.getInstance().getProperty("ts.instance"))){
					//Visit valid link
					msg = "Counter:" + counter + "\n";
					System.out.print(msg);
					sb.append(msg);
					
					driver.get(toVisitLink);
					Thread.sleep(1000);
					visitedLinks.add(toVisitLink);	
					
					//Check EKP error or UNSAFE-data here
					String text = "Please contact the system administrator";					
					if(WebDriverUtils.textPresentInPage(driver, text)){
						msg = "EKP error was found in test case\n";
						System.out.print(msg);
						sb.append(msg);
						String userDirectory = Config.getInstance().getProperty("screenShotDir");
				        String destFile = userDirectory + "/" + counter + "_EkpError.png";
						WebDriverUtils.takeScreenShot(driver, destFile);
					}
					
					text = "UNSAFE";					
					if(WebDriverUtils.textPresentInPage(driver, text)){
						msg = "UNSAFE data was found in test case\n";
						System.out.print(msg);
						sb.append(msg);
						String userDirectory = Config.getInstance().getProperty("screenShotDir");
				        String destFile = userDirectory + "/" + counter + "_UnsafeData.png";
						WebDriverUtils.takeScreenShot(driver, destFile);
					}
					
					//re-login if necessary
					if(toVisitLink.contains("LOGOFF") || driver.getCurrentUrl().equals(Config.getInstance().getProperty("loginURL"))
							||toVisitLink.contains("login")){
						this.login(driver, Config.getInstance().getProperty("loginURL"));
					}
					
					counter ++;
					msg = "Visit: " + toVisitLink + "\n";
					System.out.print(msg);
					sb.append(msg);
					
					By by = By.xpath("//a");
					List<WebElement> links = driver.findElements(by);
					
					
					//2. keep all out-going links found in this link
					for(WebElement link: links){
						text = link.getText();
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
					
					msg = "Found Links:"+ links.size() + ";visited:"+visitedLinks.size() +";toVisit:" + toVisitLinks.size() + "\n\n";
					System.out.print(msg);
					sb.append(msg);
				}				
			}
			
			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime)/(1000*60);
			msg = "\nDuration=" + duration + " mins";
			System.out.println(msg);			
			sb.append(msg);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(Config.getInstance().getProperty("screenShotDir")+"/"+"WebWalker.log", true));
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				driver.close();
			}
		}
	}
	
	public static void main(String[] args){
		WebWalker ins = new WebWalker();
		
		ins.runClickAllLinks();
	}
}
