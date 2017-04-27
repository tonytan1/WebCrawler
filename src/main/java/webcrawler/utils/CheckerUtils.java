package netd.webcrawler.utils;

import org.openqa.selenium.WebDriver;

public class CheckerUtils {
	
	public boolean checkErrorInPage(WebDriver driver, String error, String saveFile){
		boolean hasError = false;
		//Check error on base page
		hasError = this.checkError(driver, error);
		
		if(!hasError){
			if(WebDriverUtils.hasPopUpWin(driver)){
				WebDriverUtils.switchToPopUpWin(driver);
				hasError = this.checkError(driver, error);
				WebDriverUtils.closeAllPopUpWins(driver);
			}	
		}
		
		if(hasError){
			WebDriverUtils.takeScreenShot(driver,saveFile);
		}
		
		return hasError;
	}
	
	private boolean checkError(WebDriver driver, String error){
		boolean hasError = false;
		if(WebDriverUtils.textPresentInPage(driver, error)){
			hasError = true;			
		}
		
		return hasError;
	}
	

}
