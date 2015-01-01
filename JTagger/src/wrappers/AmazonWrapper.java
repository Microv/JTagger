package wrappers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class AmazonWrapper {
	private static final String LINK = "http://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Ddigital-music&field-keywords=";
	private static Logger logger = Logger.getLogger("global");
	private static final String NO_REVIEWS = "There are no customer reviews yet.";
	private String song, artist, album;

	public AmazonWrapper(String song, String artist, String album) {
		this.song = song;
		this.artist = artist;
		
		album = album.toLowerCase();
		if (album.endsWith("e.p.") && album.length() > 4)
			this.album = album.replace("e.p.", "ep");
		else if (album.endsWith("l.p.") && album.length() > 4)
			this.album = album.replace("l.p.", "lp");
		else
			this.album = album;
	}

	public boolean findReview() {
		String reviewInfo = "";

		int attempts = 2;
		boolean inFirstAttempt = false;
		Document dirtyDocument = null, cleanDocument = null;
		Elements results;
		String query;
		do {
			String track = replaceString(song);
			String art = replaceString(artist);
			String alb = replaceString(album);
			query = LINK + track + "+" + art + "+" + alb;

			try {
				dirtyDocument = Jsoup.connect(query).timeout(0).userAgent("Mozilla").get();
			} catch (IOException e) {
				logger.severe("Problem to linking to " + query);
				e.printStackTrace();
				System.exit(1);
			}

			results = dirtyDocument.select("tr[name]");
			if (results.size() < 1)
				artist = artist.split(" feat. ")[0];
			else
				inFirstAttempt = true;
		} while (--attempts > 0 || !inFirstAttempt);
		
		if (results.size() < 1) {
			reviewInfo = NO_REVIEWS;
			createAmazonPage(reviewInfo);
			return false;
		}
		
		String[] trNames = new String[results.size()];
		for (int i = 0; i < trNames.length; i++)
			trNames[i] = results.get(i).attr("name");

		Elements albumsLinks = results.select("tr > td.mp3tAlbum > a");
		int albumPosition = 0;
		for (Element element : albumsLinks) {
			String text = element.text().toLowerCase();

			if (text.contains(album) || album.contains(text))
				break;

			albumPosition++;
		}
		
		if (albumPosition == albumsLinks.size()) {
			reviewInfo = NO_REVIEWS;
			createAmazonPage(reviewInfo);
			return false;
		}

		Elements songLink = results.select("tr[name*=" + trNames[albumPosition]
				+ "] > td.songTitle > a");
		query = songLink.attr("href");
		try {
			dirtyDocument = Jsoup.connect(query).timeout(0).userAgent("Mozilla").get();
			cleanDocument = new Cleaner(Whitelist.basic()).clean(dirtyDocument);
		} catch (IOException e) {
			logger.severe("Problem to linking to " + query);
			e.printStackTrace();
			System.exit(1);
		}

		Elements bolds = cleanDocument.getElementsByTag("b");
		Element sibling = null;
		for (Element element : bolds) {
			if (element.text().equalsIgnoreCase("average customer review:")) {
				sibling = element.nextElementSibling();
				break;
			}
		}

		if (sibling.tagName().equalsIgnoreCase("a")) {
			reviewInfo = NO_REVIEWS;
			createAmazonPage(reviewInfo);
			return false;
		}
		
		Element link = sibling.select(sibling.tagName() + " > a[href]").first();
		query = link.attr("href");

		try {
			dirtyDocument = Jsoup.connect(query).timeout(0)
					.userAgent("Mozilla").get();
			dirtyDocument.outputSettings().charset("UTF-8");
			cleanDocument = dirtyDocument;
		} catch (IOException e) {
			logger.severe("Problem to linking at " + query);
			e.printStackTrace();
			System.exit(1);
		}

		Element span = cleanDocument.getElementsByClass("asinReviewsSummary").first();
		String avgCustomReview = span.text().split(" ")[0];
		Element div = cleanDocument.getElementsByClass("reviewText").first();
		String reviewText = div.text();
		cleanDocument = new Cleaner(Whitelist.basic()).clean(dirtyDocument);
		cleanDocument.outputSettings().charset("UTF-8");
		bolds = cleanDocument.getElementsByTag("span").select("span > b");
		Element reviewer = bolds.parents().first().nextElementSibling();
		String reviewerName = reviewer.text(), titleReview = bolds.first().text();

		reviewInfo = avgCustomReview + "\n" + reviewerName + "\n"
				+ titleReview + "\n" + reviewText;

		createAmazonPage(reviewInfo);
		return true;
	}

	private void createAmazonPage(String reviewInfo) {
		String[] info = reviewInfo.split("\n");
		
		DocumentType dt = new DocumentType("html", "", "", "amazon.html");
		Document html = Document.createShell(dt.baseUri());
		html.prependChild(dt);

		// Add elements into head element
		Element metaEl = html.head().appendElement("meta");
		metaEl.attr("http-equiv", "Content-type");
		metaEl.attr("content", "text/html; charset=UTF-8");

		Element linkEl = html.head().appendElement("link");
		linkEl.attr("rel", "stylesheet");
		linkEl.attr("type", "text/css");
		linkEl.attr("href", "css/style.css");

		Element scriptEl1 = html.head().appendElement("script").attr("type", "text/javascript");
		Element scriptEl2 = html.head().appendElement("script").attr("type", "text/javascript");
		scriptEl1.attr("src", "js/jquery-2.1.3.min.js");
		scriptEl2.attr("src", "js/starRating.js");

		// Add elements into body element
		Element avgEl = html.body().appendElement("b");
		avgEl.text("Average customer review");
		Element spanEl = html.body().appendElement("span").addClass("stars");
		if (info.length < 2) {
			spanEl.text("0.0");
			spanEl.attr("title", "0.0 out of 5 stars");
			Element h4El = html.body().appendElement("h4");
			h4El.text(info[0]);
		} else {
			spanEl.text(info[0]);
			spanEl.attr("title", info[0] + " out of 5 stars");
			Element h2 = html.body().appendElement("h2");
			h2.text("Custom review");
			Element divEl = html.body().appendElement("div");
			Element reviewTitleEl = divEl.appendElement("b");
			reviewTitleEl.text(info[2]);
			Element byEl = divEl.appendElement("div");
			byEl.appendText("by" + Entities.getCharacterByName("nbsp"));
			Element authorEl = byEl.appendElement("span").addClass("author");
			authorEl.text(info[1]);
			Element pEl = html.body().appendElement("p");
			pEl.appendText(info[3]);
		}

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(html.baseUri());
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			bw.write(html.toString());
			bw.close();
		} catch (IOException e) {
			logger.severe("File write problem: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	private String replaceString(String string) {
		return string.replaceAll("[ /]", "+").replaceAll("[']", "%27")
				.replaceAll("[,]", "%2C");
	}

}
