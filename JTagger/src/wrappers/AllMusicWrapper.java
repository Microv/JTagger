package wrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllMusicWrapper {
	
	private static String link = "http://www.allmusic.com/search/all/";
	private static String path = "#cmn_wrap > div.content-container > div.content > div > ul > li";
	private Document doc;
	
	public AllMusicWrapper() {
		
	}
	
	public String getComposer(String title, String artist) throws IOException {
    
        doc = Jsoup.connect(link+title).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
        String composer = "";
        Elements titles = doc.select(path);
        for(Element e : titles) {
        	String title1 = e.select("div.title > a").text().replace('"', ' ').trim();
        	String performer = e.select("div.performers > a").text().replace('"', ' ').trim();
        	
        	if(performer.equalsIgnoreCase(artist) &&
        			title1.equalsIgnoreCase(title)) {
        		composer = e.select("div.composers > a").text();
        		if(!composer.equals("")) break;
        	}
        	//System.out.println("Title: "+title1+" Performer: "+performer+" Composer: "+composer);
        }
        return composer;
       
	}
	
	public static void main(String[] args) {
		AllMusicWrapper amw = new AllMusicWrapper();
		String c = "";
		try {
			c = amw.getComposer("Wasted time", "skid row");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("COMPOSER: "+ c);
		
	}
    
}
