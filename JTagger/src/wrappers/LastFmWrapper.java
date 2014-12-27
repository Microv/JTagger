package wrappers;

import java.net.Proxy;

import de.umass.lastfm.Album;
import de.umass.lastfm.Caller;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;

public class LastFmWrapper {

	public static void main(String[] args) {
		Caller.getInstance().setUserAgent("tst");
		Caller.getInstance().setProxy(Proxy.NO_PROXY);
		
		String apiKey = "84b2abff619c0e3bd04cceb4682e2348";
		String artistName = "kiss", song = "strutter", albumName = "kiss";
		Track track = Track.getInfo(artistName, song, apiKey);
		System.out.println("Song: "+song+"\tArtist: " + artistName + "\tAlbum: " + albumName);
		System.out.println("Listeners: " + track.getListeners());
		Album album = Album.getInfo(artistName, albumName, apiKey);
		String imgurl = album.getImageURL(ImageSize.MEGA);
		System.out.println(imgurl);
	}

}
