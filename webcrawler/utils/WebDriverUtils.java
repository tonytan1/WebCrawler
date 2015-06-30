package netd.webcrawler.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;



public class WebDriverUtils {
	
		
	public static ArrayList<String> visitedWins = new ArrayList<String>();
	
	
	public enum Type {
		FireFox, Safari, Chrome, InternetExplorer
	}


	public static WebDriver getWebDriver_new() {
		WebDriver driver = getWebDriver_new(WebDriverUtils.Type.FireFox);
		return driver;
	}

	public static WebDriver getWebDriver_new(FirefoxProfile profile) {
		WebDriver driver = new FirefoxDriver(profile);
		return driver;
	}

	public static WebDriver getWebDriver_new(Type browser_type) {
		WebDriver driver = null;
		switch (browser_type) {
		case FireFox:
			driver = new FirefoxDriver();
			break;
		case Safari:
			driver = new FirefoxDriver();
			break;
		case Chrome:
			driver = new ChromeDriver();
			break;
		case InternetExplorer:
			driver = new InternetExplorerDriver();
			break;
		}

		driver.manage().timeouts().implicitlyWait(3000, TimeUnit.MILLISECONDS);
		return driver;
	}

	public static void openURL(WebDriver driver, String url) {
		driver.get(url);
		// WebDriverUtils.checkEKPError(driver);
	}
	
	public static String getCurrentUrl(WebDriver driver){
		return driver.getCurrentUrl();
	}

	public static void fillin_textbox(WebDriver driver, By by, String str) {
		waitForAjax(driver, by);
		driver.findElement(by).clear();
		driver.findElement(by).sendKeys(str);
	}

	/**
	 * count how many webelement given with "by" object present in same page if
	 * no webelement is found then print out in console
	 * 
	 * @param by
	 * @return
	 */
	public static int getHowManyByPresntInPage(WebDriver driver,By by) {

		int size = driver.findElements(by).size();

		if (size == 0) {
			System.out.println("warning: cannot find web element:"
					+ by.toString());
		}
		return size;
	}

	public void clickLink(WebDriver driver, By by) {
		clickButton( driver,by);
	}

	public static void clickButton(WebDriver driver,By by) {
		waitForAjax(driver, by);
		driver.findElement(by).click();
	}

	public void highlightElement(WebDriver driver,By by) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement elem = driver.findElement(by);
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);", elem,
					"color: red; border: 2px solid red;");
			int time = 3000;
			Thread.sleep(time);
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);", elem,
					"");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean textPresentInPage(WebDriver driver, String text) {
		By by = By.xpath("//body");
		int size = driver.findElements(by).size();
		if(size > 0){
			return driver.findElement(by).getText().contains(text);	
		}else{
			return false;
		}
	}
	
	public static synchronized void takeScreenShot(WebDriver driver,String destFile) {
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;

		File scrFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
		IOUtils.copyFile(scrFile, new File(destFile));
		//this.closeAllPopUpWins();
	}

	
	/**Close all pop-up windows and switch to the base(first) window
	 * 
	 */
	public static void closeAllPopUpWins(WebDriver driver) {
		Set<String> wins = driver.getWindowHandles();

		String[] wins_temp = wins.toArray(new String[0]);
		if (visitedWins.size() > 0) {
			for (int i = 0; i < wins_temp.length; i++) {
				String currentWin = wins_temp[i];
				if (!currentWin.equals(visitedWins.get(0))) {
					driver.switchTo().window(currentWin);
					driver.close();
				}
			}

			
			driver.switchTo().window(visitedWins.get(0));
			clearVisitedWins();
			addVisitedWin(driver);
		}else{
			switchToBaseWin(driver);	
		}
	}
	
	/**Switch to the first window but not close pop-up window
	 * 
	 * @param driver
	 */
	public static void switchToBaseWin(WebDriver driver) {
		String currentWin = driver.getWindowHandle();
		clearVisitedWins();
		addVisitedWin(currentWin);
		driver.switchTo().window(currentWin); //switch to the current window
	}
	
	public static void switchToPopUpWin(WebDriver driver) {
		Set<String> wins = driver.getWindowHandles();
		wins.removeAll(visitedWins);
		String[] wins_temp = wins.toArray(new String[0]);
		if (wins_temp.length == 1) {
			String currentWin = wins_temp[0];
			driver.switchTo().window(currentWin);
			visitedWins.add(currentWin);
		}
	}
	
	public static boolean hasPopUpWin(WebDriver driver){
		boolean hasPopUpWin = false;
		Set<String> wins = driver.getWindowHandles();
		wins.removeAll(visitedWins);
		String[] wins_temp = wins.toArray(new String[0]);
		if (wins_temp.length == 1) {
			hasPopUpWin = true;
		}
		
		return hasPopUpWin;
	}

	public static void clearVisitedWins() {
		visitedWins.clear();
	}
	
	public static void addVisitedWin(WebDriver driver) {
		addVisitedWin(driver.getWindowHandle());
	}
	
	public static void addVisitedWin(String currentWin){
		if(!visitedWins.contains(currentWin)){
			visitedWins.add(currentWin);	
		}
	}
	
	public void close(WebDriver driver){
		driver.close();
	}
	
	public List<WebElement> findElements(WebDriver driver,By by){
		return driver.findElements(by);
	}
	
	/**Wait for Elements to occur
	 * 
	 * @param driver
	 * @param by
	 */
	public static void waitForElementLoad(WebDriver driver, final By by){
		/*Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(Integer.parseInt(Config.getInstance().getProperty("WaitAjaxElment_millis")), TimeUnit.MILLISECONDS)
				.pollingEvery(300, TimeUnit.MILLISECONDS)
				.ignoring(Exception.class);
		wait.until(ExpectedConditions.presenceOfElementLocated(by));*/
		
		double  startTime;
		double  endTime,totalTime;
		double 	period = 3000;
		int size = getHowManyByPresntInPage(driver, by);
		startTime = System.currentTimeMillis();
		 while(size<=0){
		  	
		  	endTime = System.currentTimeMillis();
		  	totalTime = endTime - startTime;
		  	
		  	if (totalTime>period){
		  		//explicitWait();
		         throw new RuntimeException("Timeout finding webelement "+ by.toString() +" PLS CHECK report.xls for screen captured");
		  	}
		    try {
		    	explicitWait(300);
			  	size = getHowManyByPresntInPage(driver, by);
		      } catch ( StaleElementReferenceException ser ) {	
		    	  System.out.println("waitForElementLoad: "+ ser.getMessage() );
		      } catch ( NoSuchElementException nse ) {	
		    	  System.out.println( "waitForElementLoad: "+ nse.getMessage() );
		      } catch ( Exception e ) {
		    	  System.out.println("waitForElementLoad: "+  e.getMessage() );
		      }
		 }
	}
	public static void explicitWait(){
		explicitWait(1000);
	}


	public static void explicitWait(long wait_millis){
		try {
			Thread.sleep(wait_millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**Wait for javascript Ajax to finish at back-end in browser. 
	 * 
	 * @param driver
	 * @param by
	 */
	public static void waitForAjax(WebDriver driver, final By by)  {
		int timeoutInSeconds =30;
		 // System.out.println("Checking active ajax calls by calling jquery.active");	
	    if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor jsDriver = (JavascriptExecutor)driver;
					
	        for (int i = 0; i< timeoutInSeconds; i++) 
	        {
	        	try {
	        		Object numberOfAjaxConnections = jsDriver.executeScript("return jQuery.active");
	        		  // return should be a number
	 			   if (numberOfAjaxConnections instanceof Long) {
	 			       Long n = (Long)numberOfAjaxConnections;
	 			       //check n=0
	 			       if (n.intValue()==0){
	 			    	   break;
	 			       }
	 			       //System.out.println("\t wait for "+by.toString());
	 			       //System.out.println("\t No. of active jquery ajax calls: " + n.intValue());
	 			       explicitWait(500);
	 			   }
	        	}catch (WebDriverException e){
	        		//System.out.println("\t  jQuery is not used in current (pop up) page");
	        		break;
	        	}
		       
		    }
	        // after finish loading Ajax Element, then look for it
	        waitForElementLoad(driver, by);
		}
		else {
			System.out.println("Web driver: " + driver + " cannot execute javascript");
		}
		
	}
}
