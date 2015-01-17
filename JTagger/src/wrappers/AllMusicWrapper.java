package wrappers;

import java.io.IOException;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllMusicWrapper {
	private static final String SEARCH_ALBUM_LINK = "http://www.allmusic.com/search/album/";
	private static final String LABEL_NOT_FOUND = "Label not found";
	
	private static Logger logger = Logger.getLogger("global");
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
	
	public String getLabel(String artist, String album, int year) throws IOException {
		String label = "";
		String query, albumToSearch = album;
		int attempts = 2, index = -1;
		boolean inFirstAttempt = false;
		Document dirtyDocument = null;
		Element elementToExtract;
		do {
			query = SEARCH_ALBUM_LINK + albumToSearch + " " + artist;

			dirtyDocument = Jsoup.connect(query).timeout(0).userAgent("Mozilla").get();

			String cssQuery = "li.album > div.info:has(div.title:has(a:contains("
			+ albumToSearch + ")) + div.artist:has(a:contains("
			+ artist + "))) > div.title > a";
			Elements elements = dirtyDocument.select(cssQuery);
			elementToExtract = elements.first();

			if (elementToExtract == null) {
				index = albumToSearch.lastIndexOf('(');
				if (index > 0)
					albumToSearch = albumToSearch.substring(0, index).trim();
			} else {
				String text = elementToExtract.text().toLowerCase();
				if (!text.equals(album)) {
					int length = text.length();
					for (int i = 1; i < elements.size(); i++) {
						Element element = elements.get(i);
						text = element.text().toLowerCase();
						if (text.equals(album)) {
							elementToExtract = element;
							break;
						}

						if (length > text.length()) {
							elementToExtract = element;
							length = elementToExtract.text().length();
						}
					}
				}

				inFirstAttempt = true;
			}
		} while (--attempts > 0 && !inFirstAttempt);
		
		if (elementToExtract == null)	return LABEL_NOT_FOUND;

		query = elementToExtract.attr("href").concat("/releases");
		try {
			dirtyDocument = Jsoup.connect(query).timeout(0)
					.userAgent("Mozilla").get();
		} catch (IOException e) {
			logger.severe("Problem to linking at " + query);
			e.printStackTrace();
			System.exit(1);
		}
		
		if (index > 0) {
			album = album.replace('(', '[');
			album = album.replace(')', ']');
		}

		String cssQuery = "tr:has(td.format:contains(CD) + td[data-sort-value*="
				+ album + "] ~ td.year:contains(" + year + "))";
		Element labelEl = dirtyDocument.select(cssQuery).first();
		if (labelEl == null) {
			cssQuery = "tr:has(td.format:contains(CD))";
			Elements yearEls = dirtyDocument.select(cssQuery + " > td.year");
			Element yearEl = yearEls.get(0);
			int yearToExtract = Integer.parseInt(yearEl.text());
			int diff = ~(yearToExtract - year - 1);
			for (int i = 1; i < yearEls.size(); i++) {
				yearEl = yearEls.get(i);
				if (yearEl.text().isEmpty())
					continue;

				int yearVal = Integer.parseInt(yearEl.text());
				if (diff > yearVal - year) {
					if (yearVal - year < 0)
						diff = ~(yearVal - year - 1);
					else
						diff = yearVal - year;
					yearToExtract = yearVal;
				}
			}

			cssQuery = "tr:has(td.format:contains(CD) ~ td.year:contains("
					+ yearToExtract + "))";
			labelEl = dirtyDocument.select(cssQuery).first();
		}

		label = labelEl.select("div.label").text();
		return label;
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
