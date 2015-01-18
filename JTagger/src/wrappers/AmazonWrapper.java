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

		int attempts = 3;
		boolean inFirstAttempt = false;
		Document dirtyDocument = null, cleanDocument = null;
		Element elementToExtract;
		String query, songToSearch = song, albumToSearch = album;
		do {
			String track = replaceString(song);
			String art = replaceString(artist);
			String alb = replaceString(album);
			query = LINK + track + "+" + art + "+" + alb + "&sort=review-rank";

			try {
				dirtyDocument = Jsoup.connect(query).timeout(0).userAgent("Mozilla").get();
			} catch (IOException e) {
				logger.severe("Problem to linking to " + query);
				e.printStackTrace();
				System.exit(1);
			}

			String cssQuery = "tr:has(td.songTitle:has(a:contains(" + songToSearch + "))"
					+ "+td.mp3tArtist:has(a:contains(" + artist + "))"
					+ "+td.mp3tAlbum:has(a:contains(" + albumToSearch + "))) > td.songTitle > a";
			elementToExtract = dirtyDocument.select(cssQuery).first();

			if (elementToExtract == null && artist.contains(" feat. "))
				artist = artist.split(" feat. ")[0];
			else if (elementToExtract == null) {
				int indexSong = songToSearch.lastIndexOf('(');
				int indexAlbum = albumToSearch.lastIndexOf('(');
				if (indexSong > 0)
					songToSearch = songToSearch.substring(0, indexSong);
				if (indexAlbum > 0)
					albumToSearch = albumToSearch.substring(0, indexAlbum);
			}
			else	inFirstAttempt = true;
		} while (--attempts > 0 && !inFirstAttempt);

		if (elementToExtract == null) {
			reviewInfo = NO_REVIEWS;
			createAmazonPage(reviewInfo);
			return false;
		}

		query = elementToExtract.attr("href");
		try {
			dirtyDocument = Jsoup.connect(query).userAgent("Mozilla").timeout(0).get();
			dirtyDocument.outputSettings().charset("UTF-8");
		} catch (IOException e) {
			logger.severe("Problem to linking at " + query);
			e.printStackTrace();
			System.exit(1);
		}

		String cssQuery = "li > b:contains(average customer review:) + a";
		elementToExtract = dirtyDocument.select(cssQuery).first();

		if (elementToExtract != null) {
			reviewInfo = NO_REVIEWS;
			createAmazonPage(reviewInfo);
			return false;
		}

		cssQuery = "li > b:contains(average customer review:) ~ span > span > a";
		elementToExtract = dirtyDocument.select(cssQuery).first();
		Element avgToExtract = elementToExtract.child(0);
		String avgCustomerReview = avgToExtract.attr("title").split(" ")[0];

		query = elementToExtract.attr("href");
		try {
			dirtyDocument = Jsoup.connect(query).userAgent("Mozilla").timeout(0).get();
			dirtyDocument.outputSettings().charset("UTF-8");
		} catch (IOException e) {
			logger.severe("Problem to linking at " + query);
			e.printStackTrace();
			System.exit(1);
		}

		elementToExtract = dirtyDocument.select("div.reviewText").first();
		String reviewText = elementToExtract.text();

		cleanDocument = new Cleaner(Whitelist.basic()).clean(dirtyDocument);
		cleanDocument.outputSettings().charset("UTF-8");
		Elements bolds = cleanDocument.getElementsByTag("span").select("span > b");
		Element reviewer = bolds.parents().first().nextElementSibling();
		String reviewerName = reviewer.text(), titleReview = bolds.first().text();

		reviewInfo = avgCustomerReview + "\n" + reviewerName + "\n"
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
