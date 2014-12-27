package wrappers;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import metadata.Track;

import org.xml.sax.SAXException;

public class Tester {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
		
		String recording = "At my most beautiful";
		String artist = "R.E.M.";
		String album = "Up";
		
		MusicBrainzWrapper mbw = new MusicBrainzWrapper();
		AllMusicWrapper amw = new AllMusicWrapper();
		RawLastFmWrapper lfmw = new RawLastFmWrapper();
		ArrayList<Track> tracks = mbw.getTrackByTitle(recording);
		//ArrayList<Track> tracks = mbw.getTrackByTitleAndArtist("At my most beautiful", "R.E.M.");
		//ArrayList<Track> tracks = mbw.getTrackByTitleAndAlbum(recording, album);
	
		System.out.println("Number of results: "+tracks.size());
		
		for (Track t : tracks) {
			System.out.println("Result");
		    System.out.println("\tTitle: "+t.getTitle());
		    System.out.print("\tArtists: ");
		    //Artist[] a = t.getArtists();
		    System.out.println(t.getArtists());
		    /*for(int i = 0; i < a.length; i++) {
		    	System.out.print(a[i].getName());
		    	if(i+1 < a.length)
		    		System.out.print(" feat. ");
		    }*/
		    String composer = amw.getComposer(t.getTitle(), t.getArtists()/*a[0].getName()*/);
		    System.out.println("\n\tComposer: "+composer);
		    System.out.println("\tAlbum: "+t.getAlbum().getTitle());	
		    System.out.println("\tYear: "+t.getAlbum().getYear());
		    System.out.println("\tTrack position: "+t.getTrackNum());
		    String cover = "Not found";
		    try {
		    	cover = lfmw.getCover(t.getTitle(), t.getArtists()/*t.getArtists()[0].getName()*/);
		    } catch(Exception e) {
		    	
		    }
		    System.out.println("\tCover: "+cover);
		    System.out.println("\n");
		}
	
	}

}
