package wrappers;

import java.io.IOException;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllMusicWrapper {
	private static final String SEARCH_ALBUM_LINK = "http://www.allmusic.com/search/albums/";
	private static final String SEARCH_SONG_LINK = "http://www.allmusic.com/search/songs/";
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

//	public String getComposers(String song, String artist, String album) {
//		String result = getAlbumURL(artist, album);
//		int pos = result.indexOf('|');
//		String albumURL = result.substring(pos + 1);
//		Document dirtyDocument = null;
//		try {
//			dirtyDocument = Jsoup.connect(albumURL).userAgent("Mozilla").timeout(0).get();
//		} catch (IOException e) {
//			logger.severe("Problem to linking at " + albumURL);
//			e.printStackTrace();
//			System.exit(1);
//		}
//
//		int attempts = 2;
//		boolean inFirstAttempt = false;
//		String songToSearch = song;
//		Elements composerElements;
//		do {
//			String cssQuery = "div.title:has(a:contains(" + songToSearch + ")) + div.composer > a";
//			composerElements = dirtyDocument.select(cssQuery);
//
//			if (composerElements.size() < 1) {
//				int index = songToSearch.lastIndexOf('(');
//				if (index > 0)
//					songToSearch = songToSearch.substring(0, index).trim();
//			}
//			else	inFirstAttempt = true;
//		} while (--attempts > 0 && !inFirstAttempt);
//
//		String composers = "";
//		for (Element element : composerElements)
//			composers += element.text() + ", ";
//
//		if (!composers.isEmpty())
//			composers = composers.substring(0, composers.length() - 2);
//
//		return composers;
//	}

	public String getComposers(String song, String artist) {
		Element songEl = getSongInfo(song, artist);
		if (songEl == null)	return "";

		Elements composerElements = songEl.select("div.composers > a");
		String composers = "";
		for (Element element : composerElements)
			composers += element.text() + ", ";

		if (!composers.isEmpty())
			composers = composers.substring(0, composers.length() - 2);
		
		return composers;
	}

	private Element getSongInfo(String song, String artist) {
		String query;
		int attempts = 3, index = -1;
		boolean inFirstAttempt= false;
		Document dirtyDocument = null;
		Element songEl;
		do {
			query = SEARCH_SONG_LINK + song + " " + artist;
			try {
				dirtyDocument = Jsoup.connect(query).timeout(0).userAgent("Mozilla").get();
			} catch (IOException e) {
				logger.severe("Problem to linking at " + query);
				e.printStackTrace();
				System.exit(1);
			}

			String cssQuery = "li.song:has(div.title:has(a:contains(" + song + "))"
					+ " + div.performers:has(a:contains(" + artist + "))"
							+ " + div.composers)";
			songEl = dirtyDocument.select(cssQuery).first();
			if (songEl == null && artist.contains(" feat. "))
				artist = artist.split(" feat. ")[0];
			else if (songEl == null) {
				index = song.lastIndexOf('(');
				if (index > 0)
					song = song.substring(0, index);
			}
			else	inFirstAttempt = true;
		} while (--attempts > 0 && !inFirstAttempt);
		
		return songEl;
	}

	public String getLabel(String artist, String album, int year) throws IOException {
		String result = getAlbumURL(artist, album);

		if (result.equals(LABEL_NOT_FOUND))	return LABEL_NOT_FOUND;

		int pos = result.indexOf('|'), index;
		index = Integer.parseInt(result.substring(0, pos));
		String albumURL = result.substring(pos + 1);
		String query = albumURL.concat("/releases");
		Document dirtyDocument = null;
		try {
			dirtyDocument = Jsoup.connect(query).timeout(0).userAgent("Mozilla").get();
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

		String label = labelEl.select("div.label").text();
		return label;
	}

	private String getAlbumURL(String artist, String album) {
		String query, albumToSearch = album;
		int attempts = 3, index = -1;
		boolean inFirstAttempt = false;
		Document dirtyDocument = null;
		Element albumEl;
		do {
			query = SEARCH_ALBUM_LINK + albumToSearch + " " + artist;

			try {
				dirtyDocument = Jsoup.connect(query).timeout(0)
						.userAgent("Mozilla").get();
			} catch (IOException e) {
				logger.severe("Problem to linking at " + query);
				e.printStackTrace();
				System.exit(1);
			}

			String cssQuery = "li.album > div.info:has(div.title:has(a:contains(" + albumToSearch + "))"
					+ " + div.artist:has(a:contains(" + artist + "))) > div.title > a";
			Elements elements = dirtyDocument.select(cssQuery);
			albumEl = elements.first();

			if (albumEl == null && artist.contains(" feat. "))
				artist = artist.split(" feat. ")[0];
			else if (albumEl == null) {
				index = albumToSearch.lastIndexOf('(');
				if (index > 0)
					albumToSearch = albumToSearch.substring(0, index).trim();
			}
			else {
				String text = albumEl.text().toLowerCase();
				if (!text.equals(album)) {
					int length = text.length();
					for (int i = 1; i < elements.size(); i++) {
						Element element = elements.get(i);
						text = element.text().toLowerCase();
						if (text.equals(album)) {
							albumEl = element;
							break;
						}
						
						if (length > text.length()) {
							albumEl = element;
							length = albumEl.text().length();
						}
					}
				}

				inFirstAttempt = true;
			}
		} while (--attempts > 0 && !inFirstAttempt);

		if (albumEl == null)	return LABEL_NOT_FOUND;

		String albumURL = albumEl.attr("href");
		String toReturn = (index + "|").concat(albumURL);

		return toReturn;
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
