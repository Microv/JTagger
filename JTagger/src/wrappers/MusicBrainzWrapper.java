package wrappers;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import metadata.Album;
import metadata.Track;

public class MusicBrainzWrapper {
	
	private final static String recordingQuery = "http://musicbrainz.org/ws/2/recording/?limit=100&query="; 
	//private final static String releaseQuery = "http://musicbrainz.org/ws/2/release/?query="; 
	
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private XPathFactory xPathfactory;
	private XPath xpath;
	
	public MusicBrainzWrapper() throws ParserConfigurationException {
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
	}

	private ArrayList<Track> executeQuery(Document doc)
			throws XPathExpressionException {
		NodeList recordings = (NodeList) xpath.evaluate(
				"//recording-list/recording/title/text()", doc, 
				XPathConstants.NODESET);
		
		ArrayList<Track> tracks = new ArrayList<Track>();
		for(int i = 0; i < recordings.getLength(); i++) {
			Track t = new Track();
			// title
			t.setTitle(recordings.item(i).getNodeValue());
			
			//album informations
			Album album = new Album();
			XPathExpression exprAlbum = xpath.compile(
					"//recording-list/recording["+(i+1)+"]"
							+ "/release-list/release/title/text()"
					);
			album.setTitle(exprAlbum.evaluate(doc));
			
			XPathExpression exprYear = xpath.compile(
					"//recording-list/recording["+(i+1)+"]"
							+ "/release-list/release/date/text()"
					);
			album.setYear(exprYear.evaluate(doc));
			
			t.setAlbum(album);
			
			// track position
			XPathExpression exprTrack = xpath.compile(
					"//recording-list/recording["+(i+1)+"]"
							+ "/release-list/release/medium-list"
							+ "/medium/track-list/track/number/text()"
					);
			t.setTrackNum(exprTrack.evaluate(doc));
			
			//artists informations
			NodeList artists = (NodeList) xpath.evaluate(
					"//recording-list/recording["+(i+1)+"]/artist-credit/"
					+ "name-credit/artist/name/text()", doc, 
					XPathConstants.NODESET);
			
			//Artist[] a = new Artist[artists.getLength()];
			String artist = "";
			for(int j = 0; j < artists.getLength(); j++) {
				artist += artists.item(j).getNodeValue();
				//a[j] = new Artist(artist);
				if(j + 1 < artists.getLength())
					artist += " feat. ";
			}
			t.setArtists(artist);
			
			tracks.add(t);
		}
		return tracks;
	}
	
	public ArrayList<Track> getTrackByTitle(String recording) 
			throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"recording:\""
				+recording.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}

	public ArrayList<Track> getTrackByTitleArtistAlbum(String recording, 
			String artist, String release) 
					throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"recording:\""
				+recording.replaceAll(" ", "%20")+"\""
				+"+artist:\""+artist.replaceAll(" ", "%20")+"\""
				+"+release:\""+release.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}

	public ArrayList<Track> getTrackByTitleArtist(String recording, String artist) 
			throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"recording:\""
				+recording.replaceAll(" ", "%20")+"\""
				+"+artist:\""+artist.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}

	public ArrayList<Track> getTrackByArtist(String artist) 
			throws SAXException, IOException, XPathExpressionException {
		
		String s = recordingQuery+"artist:\""+artist.replaceAll(" ", "%20")+"\"";
		
		Document doc = builder.parse(s);
		
		return executeQuery(doc);
	}

	public ArrayList<Track> getTrackByAlbum(String release) 
			throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"release:\""
				+release.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}
	
	public Track getTrackById(String id) 
			throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"rid:"+id);
		
		ArrayList<Track> toReturn = executeQuery(doc);
		if(toReturn.size() > 0)
			return toReturn.get(0);
		else
			return null;
	}
	
	public void setAlbumInfo(Track track) {
		
	}

	public ArrayList<Track> getTrackByTitleAlbum(String recording,
			String release) throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"recording:\""
				+recording.replaceAll(" ", "%20")+"\""
				+"+release:\""+release.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}

	public ArrayList<Track> getTrackByArtistAlbum(String artist,
			String release) throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"artist:\""
				+artist.replaceAll(" ", "%20")+"\""
				+"+release:\""+release.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}
}
