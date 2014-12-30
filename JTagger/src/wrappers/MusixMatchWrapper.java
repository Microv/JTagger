package wrappers;

/*
 *  Per gestire in maniera corretta e pulita il file
 sarebbe opportuno spostare le costanti in una classe esterna che le possa contenere
 */

/*
 *  Per utilizzare la classe:
 *  	Istanziare un oggetto MusixMatchWrapper.
 *  	il metodo getMatchingTrack:
 *  					input: artist_name, track_name 
 *  					output: artist_id, album_id, album_name, track_rating, track_id, genre
 *  
 *  	il metodo getLyricsbyScraping:
 *  					input: artist_name, track_name
 *  					output: lyrics
 */


import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;


	public class MusixMatchWrapper {
	//Formato restituito
	public static final String FORMAT = "&format=xml";
	
	//Modalità d'interrogazione
	public static final String ARTIST_SEARCH = "artist.search";
	public static final String MATCHER_TRACK_GET = "matcher.track.get";
	public static final String MATCHER_LIRYCS_GET = "matcher.lyrics.get";
	public static final String ARTIST_ALBUMS_GET = "artist.albums.get";
	public static final String ALBUM_GET = "album.get";
	
	//Url per l'interrogazione
	public static final String apiKey = "eb2b41bc699ba7b50309a3decaed0c31"; //chiave di accesso al servizio MusixMatch
	public static final String API_URL = "http://api.musixmatch.com/ws/";
	public static final String API_VERSION = "1.1";
	public static final String URL_DELIM = "/";
	public static final String URL_LYRICS = "https://www.musixmatch.com/it/testo/"; //Link per scaricare il teso della canzone

	//Variabili temporanee
	private XPathFactory xPathfactory;
	private XPath xpath;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private org.jsoup.nodes.Document jsoup_doc;
	private org.w3c.dom.Document w3c_doc;

	public MusixMatchWrapper() throws ParserConfigurationException
	{
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		
	}
	
	// recupera le informazioni di getTrackAndArtist <--
	public Map<String,Object> getMatchingTrack( String q_track , String q_artist ) throws IOException, XPathExpressionException, SAXException
	{
		String copyright;
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("apikey", apiKey); //inizializzazione parametri per l'interrogazione
		params.put("q_track", q_track);
		params.put("q_artist", q_artist);
		
		Map<String,Object> info = getMoreInfo(MATCHER_TRACK_GET, params); //prendo la risposta dell'interrogazione
		
		copyright =getCopyright(info.get("album_id").toString()); //Recupero il copyright
		
		if(copyright.equals(""))
			copyright = "No copyright founds";
		
		info.put("album_copyright", copyright);
		
		return info;
	}
	
	
	public String getCopyright(String q_albumID) throws SAXException, IOException, XPathExpressionException
	{
		
		String url = API_URL + API_VERSION + URL_DELIM + ALBUM_GET + "?"
				+ "apikey=" + apiKey + "&" + "album_id=" + q_albumID + FORMAT; // costruzione dell'url per la richiesta	
		 w3c_doc = builder.parse(url);
		 
		 XPathExpression exprAlbumCopyright = xpath.compile("//album_copyright/text()");
		 
		return exprAlbumCopyright.evaluate(w3c_doc);
	}
	
	/*
	 * All'interno della risposta è possibile acquisire il track_ID, track_name, lyric_ID, album_ID, artist_ID, artist_name, cover_album
	 */
	private Map<String, Object> getMoreInfo(String methodName, Map<String, Object> params) throws IOException, XPathExpressionException, SAXException
	{
		//Creiamo la mappa in cui memorizziamo i parametri da restituire.
		Map<String,Object> result = new HashMap<String,Object>();
		
		String paramString = new String();  //Stringa per la richiesta 
		paramString += methodName + "?";

		for (Map.Entry<String, Object> entry : params.entrySet()) {
				try {
					paramString += entry.getKey()
							+ "="
							+ URLEncoder.encode(entry.getValue().toString(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			paramString += "&";
		}
		paramString += FORMAT; //Restituisci il formato XML
		
		
			//Send Request

			String apiUrl = API_URL + API_VERSION + URL_DELIM + paramString; // costruzione dell'url per la richiesta	
			 w3c_doc = builder.parse(apiUrl);
			
			
			//Track_ID
			XPathExpression exprTackid = xpath.compile("//track_id/text()");
			//System.out.println(exprTackid.evaluate(w3c_doc) + " Track_ID");
			result.put("track_id", exprTackid.evaluate(w3c_doc));
			
			//TrackRating
			XPathExpression exprRating = xpath.compile("//track_rating/text()");
			//System.out.println(exprRating.evaluate(w3c_doc) + " Rating_Track");
			result.put("track_rating", exprRating.evaluate(w3c_doc));
			
			//GenreName
			XPathExpression exprGenre = xpath.compile("//music_genre_name/text()");
			//System.out.println(exprGenre.evaluate(w3c_doc) + " Genre");
			result.put("music_genre", exprGenre.evaluate(w3c_doc));
			
			//AlbumName
			XPathExpression exprAlbum = xpath.compile("//album_name/text()");
			//System.out.println(exprAlbum.evaluate(w3c_doc) + " Album");
			result.put("album_name", exprAlbum.evaluate(w3c_doc));
			
			//Album_ID
			XPathExpression exprAlbumID = xpath.compile("//album_id/text()");
			//System.out.println(exprArtist.evaluate(w3c_doc) + " Artist_id");
			result.put("album_id", exprAlbumID.evaluate(w3c_doc));
			
			//Artist_ID
			XPathExpression exprArtist = xpath.compile("//artist_id/text()");
			//System.out.println(exprArtist.evaluate(w3c_doc) + " Artist_id");
			result.put("artist_id", exprArtist.evaluate(w3c_doc));

			//Cover dell'album
			XPathExpression exprCover = xpath.compile("//album_coverart_500x500/text()");
			//System.out.println(exprCover.evaluate(w3c_doc) + " Cover");
			result.put("album_cover", exprCover.evaluate(w3c_doc));
			
			//Ritornare i parametri che m'interessano
		return  result;
	
	} // end trackArtist
	
	
	//Costruisce i parametri da passare alla funzione getLirycs per recuperare il testo della canzone
	/*
	 * Preferibile effettuare scraping dal web senza utilizzare le API, poichè la chiave non permette di recuperare 
	 * il testo completo.
	 * 
	 */
	
	public Map<String,Object> getMatchingLyrics( String q_track , String q_artist ) throws IOException, XPathExpressionException, SAXException
	{
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("apikey", apiKey); //inizializzazione parametri per l'interrogazione
		params.put("q_track", q_track);
		params.put("q_artist", q_artist);
		
		Map<String,Object> response = getLyrics(MATCHER_LIRYCS_GET, params); //prendo la risposta dell'interrogazione
	
		return response;
	}
	
	
	//Recupera il testo della canzone settando artista e traccia
	private Map<String, Object> getLyrics (String methodName, Map<String, Object> params) throws SAXException, IOException, XPathExpressionException
	{
		Map<String,Object> result = new HashMap<String,Object>();
		
		String paramString = new String();  //Stringa per la richiesta 
		paramString += methodName + "?";

		for (Map.Entry<String, Object> entry : params.entrySet()) {
				try {
					paramString += entry.getKey()
							+ "="
							+ URLEncoder.encode(entry.getValue().toString(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			paramString += "&";
		}
		paramString += FORMAT; //Restituisci il formato XML
		
		
		String url = API_URL + API_VERSION + URL_DELIM + paramString; // costruzione dell'url per la richiesta	
		 w3c_doc = builder.parse(url);
		
		//Document doc = builder.parse(url);
		XPathExpression exprLyrics = xpath.compile("//lyrics_body/text()");
		//prendo l'Xpath dalla pagina appena ottenuta
		//System.out.println("\n" + exprLyrics.evaluate(w3c_doc));
		result.put("lirycs", exprLyrics.evaluate(w3c_doc));
		
		return result;
	}
	
	// recuperare il teso della canzone tramitelo scraping | utilizzo della libreria jsoup/json
	public String getLyricsbyScraping(String q_artist, String q_track)
	{
	
	String lyrics = new String("");
	String path = "#lyrics-html";
	try {
		jsoup_doc = Jsoup.connect( URL_LYRICS + q_artist + "/" + q_track).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	Elements testo = jsoup_doc.select(path);
	int i= 1;
	Element e = testo.first(); // Resituisce un unico elemento.
		for (Element test_part : e.children()){
		if(test_part.tagName() != "br")  //fuck
			lyrics = lyrics.concat(test_part.text()).concat("\n");
		if(i%5 == 0)
			lyrics= lyrics.concat("\n");
		
		i++;
		}
	return lyrics;
	}
	
	//Recuperare gli artisti legati ad una canzone
	/*
	 *  http://api.musixmatch.com/ws/1.1/track.search?apikey=eb2b41bc699ba7b50309a3decaed0c31&q_track=a%20te&format=xml
	 *  Con questo link è possibile recuperare la canzone inserita in q_track, il risultato restituito ha bisogno di essere analizzato,
	 *  e per ogni tag <tack> devo restituire i nomi dei cantanti trovati
	 */

	public	String getComposer(String q_artist, String q_track)
	{
		
		
		String writer = new String("");
		//String path ="#content > div.track-view-container > div > div.track-sidebar";
		String path = ".authors > ul:nth-child(1)";
		try {
			jsoup_doc = Jsoup.connect( URL_LYRICS + q_artist + "/" + q_track).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		Elements testo = jsoup_doc.select(path);	
		/*if(testo == null)
			return "";*/
		
		/*Element writers = testo.first();
		writers = writers.child(0).child(1).getElementsByClass("authors").first().child(0);
		
		for( Element e : writers.children())
			writer = writer.concat(e.text()).concat(", ");
			*/

		Elements writers = testo.first().children();
		for(Element e : writers)
			writer += e.text()+", ";

		writer = writer.substring(0, writer.length() -2) ;
        
		return writer;
		
	}
	

	public static void main(String []args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException
	{
		MusixMatchWrapper m = new MusixMatchWrapper();
	//	Map<String,Object> result =m.getMatchingTrack("l'amore conta", "Ligabue");
	//	System.out.println(result.get("album_copyright"));
	//	m.getArtistID(m.artistName); metodo utilizzato come interfaccia, solo per recuperare id id un cantante
	//	m.getMatchingLyrics(m.trackName, m.artistName);
		System.out.print(m.getLyricsbyScraping("Ligabue", "Certe Notti"));
		
	}

	
} // end class
