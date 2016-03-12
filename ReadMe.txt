
/*
*Project: WebCrawler
*
*@author TonyTan
**/
Apply BFS (or DFS) strategy to visit all links within a url, and check whether linked page contains errors/unsafe-data	 
This project utilises several techniques: 
	1. Breadth-first search (or depth-first search) strategy; 
	2. Selenium interacts with HTML, e.g., parse html page to get all links; 
	click link to open a new window; 
	judge whether pages contain bugs (e .g., errors/unsafe-data);
	take screenshot for problematic pages;
	
At the same time, creating a concurrent version of WebCrawler which aims to reduce running time.
It provides multiple mode (it depends on your computer)to execute the multi-thread.
However, this class is different WebCrawler since it loads link data from external files while WebCrawler collects link data (via DFS/BFS) at runtime. I