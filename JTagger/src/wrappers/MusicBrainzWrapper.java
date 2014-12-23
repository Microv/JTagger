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
	
	private final static String recordingQuery = "http://musicbrainz.org/ws/2/recording/?limit=25&query="; 
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
			
			//album title
			Album album = new Album();
			XPathExpression exprAlbum = xpath.compile(
					"//recording-list/recording["+(i+1)+"]"
							+ "/release-list/release/title/text()"
					);
			album.setTitle(exprAlbum.evaluate(doc));
			
			// year
			XPathExpression exprYear = xpath.compile(
					"//recording-list/recording["+(i+1)+"]"
							+ "/release-list/release/date/text()"
					);
			album.setYear(exprYear.evaluate(doc));
			
			t.setAlbum(album);
			
			//artists informations
			NodeList artists = (NodeList) xpath.evaluate(
					"//recording-list/recording["+(i+1)+"]/artist-credit/"
					+ "name-credit/artist/name/text()", doc, 
					XPathConstants.NODESET);
			
			String artist = "";
			for(int j = 0; j < artists.getLength(); j++) {
				artist += artists.item(j).getNodeValue();
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
				+"+AND+artist:\""+artist.replaceAll(" ", "%20")+"\""
				+"+AND+release:\""+release.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}

	public ArrayList<Track> getTrackByTitleArtist(String recording, String artist) 
			throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"recording:\""
				+recording.replaceAll(" ", "%20")+"\""
				+"+AND+artist:\""+artist.replaceAll(" ", "%20")+"\"");
		
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

	public ArrayList<Track> getTrackByTitleAlbum(String recording,
			String release) throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"recording:\""
				+recording.replaceAll(" ", "%20")+"\""
				+"+AND+release:\""+release.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}

	public ArrayList<Track> getTrackByArtistAlbum(String artist,
			String release) throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(recordingQuery+"artist:\""
				+artist.replaceAll(" ", "%20")+"\""
				+"+AND+release:\""+release.replaceAll(" ", "%20")+"\"");
		
		return executeQuery(doc);
	}
	
	public Track getFullInfo(String recording, 
			String artist, String release, String year) 
					throws SAXException, IOException, XPathExpressionException {
		int a;
		if((a = artist.indexOf("feat.")) > 0)
			artist = artist.substring(0, a);
		
		Document doc = builder.parse(recordingQuery+"recording:\""
				+recording.replaceAll(" ", "%20")+"\""
				+"+AND+artist:\""+artist.replaceAll(" ", "%20")+"\""
				+"+AND+release:\""+release.replaceAll(" ", "%20")+"\"");
		
		ArrayList<Track> result = executeQuery(doc);
		
		Track t = result.get(0);
		getOtherInfo(doc, t);
		return t;
	}

	private void getOtherInfo(Document doc, Track t) 
			throws XPathExpressionException {

		// track position
		XPathExpression expr = xpath.compile(
				"//recording-list/recording[1]"
						+ "/release-list/release/medium-list"
						+ "/medium/track-list/track/number/text()"
				);
		t.setTrackNum(expr.evaluate(doc));
		
		// track count
		expr = xpath.compile(
				"string(//recording-list/recording[1]"
						+ "/release-list/release/medium-list"
						+ "/medium/track-list/@count)"
				);
		
		t.getAlbum().setTrackCount(expr.evaluate(doc));
		
		// disc number
		expr = xpath.compile(
				"//recording-list/recording[1]"
						+ "/release-list/release/medium-list"
						+ "/medium/position/text()"
				);
		
		t.setDiscNum(expr.evaluate(doc));
		
		// disc count
		expr = xpath.compile(
				"//recording-list/recording[1]"
						+ "/release-list/release/medium-list"
						+ "/medium/position/text()"
				);
				
		//t.getAlbum().setDiskCount(expr.evaluate(doc));
	}
}
