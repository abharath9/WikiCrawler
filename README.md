
WikiCrawler:
This will crawl through all the Wikipedia pages and try to find path between the source Wikipedia page and philosophy page.
It is a Clojure based application.


It follows the following rules to find the path.
The rules for crawling are as follows: 
1.	The first link in the body of Wikipedia (Introduction + Content) article is followed. 
2.	Italicized and links within parentheses are ignored. 
3.	Only valid wikipedia links are followed. 
4.	Invalid wiki links are as follows: 
o	Special links Eg. 
o	Red links Eg. 
o	Citation links 
o	File links Eg. 
o	Wiktionary links Eg. 
o	Wikimedia links Eg. 
o	Help links Eg. 
5.	Links in Tables are also ignored as we are concentrating only on Introduction + Content 
6.	Some pages have coordinates link. This is also ignored 
7.	To prevent loops, links already encountered enroute are ignored

Instructions to run this application:
I have used enlive-1.1.6 and tagsoup-1.2 libraries to extract the html tags from Wikipedia pages.
Created a function wiki-crawl-to-philosophy which sould take url as input and gives you the complete path separate by pipe "|".
After you have loaded this script to the clojure REPL, you need to run the function as follows.

(wiki-crawl-to-philosophy "https://en.wikipedia.org/wiki/Google")

