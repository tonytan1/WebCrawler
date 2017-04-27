package src.main.java.webcrawler.controller;

import java.util.ArrayList;

import netd.config.Config;
import netd.webcrawler.model.WebCrawler;
import netd.webcrawler.model.WebCrawlerThread;
import netd.webcrawler.utils.IOUtils;

public class TestDriver {
	
	public ArrayList<String> loadLinkSet(String file_str){
		return IOUtils.loadFile_special(file_str);		
	}
	
	public void startWebCrawlerThreads(int threadNo){
		String file_str = Config.getInstance().getProperty("screenShotDir")+"/"+"WebWalker.log";
		ArrayList<String> links = this.loadLinkSet(file_str);
		
		int start = 0;
		int end = -1;
		int interval = links.size()/threadNo;
		for(int i = 1; i <= threadNo; i ++){
			start = (i-1) * interval;
			end = i*interval;
			Thread thread = new Thread(new WebCrawlerThread(Config.getInstance().getProperty("ts.instance"), links, start, end));
			thread.start();
		}
	}
	
	public void dispatcher(int flag){
		switch(flag){
			case 1: 
				WebCrawler ins = new WebCrawler();
				ins.visitAllLists();
				break;
			case 2:
				int threadNo = 8;
				this.startWebCrawlerThreads(threadNo);
				break;
			default:
				break;
		}
	}
	
	public static void main(String[] args){
		int flag = 1;
		TestDriver driver = new TestDriver();
		driver.dispatcher(flag);
	}
}
