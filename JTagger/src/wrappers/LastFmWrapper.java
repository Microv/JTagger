package wrappers;

import java.util.Collection;

import de.umass.lastfm.Album;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;

public class LastFmWrapper {
	private static final String API_KEY = "84b2abff619c0e3bd04cceb4682e2348";
	private String song, artist, album;
	private Collection<Track> tracks;
	
	public LastFmWrapper(String song, String artist, String album) {
		this.song = song.toLowerCase();
		this.artist = artist;
		this.album = album;
	}
	
	public String getAlbumCoverURL(ImageSize is) {
		Album albumInfo = findAlbumInfo(artist, album, true);
		tracks = albumInfo.getTracks();
		if (tracks.isEmpty()) {
			albumInfo = findAlbumInfo(artist, album, false);
			tracks = albumInfo.getTracks();
		}
		
		return albumInfo.getImageURL(is);
	}
	
	public int getListeners() {
		String title = "", artists = "";
		for (Track track : tracks) {
			String trackName = track.getName().toLowerCase();
			
			if (song.contains(trackName) || trackName.contains(song)) {
				title = trackName;
				artists = track.getArtist();
				break;
			}
		}

		Track trackInfo = Track.getInfo(artists, title, API_KEY);
		return trackInfo.getListeners();
	}

	private Album findAlbumInfo(String artist, String album, boolean splitArtist) {
		Album albumInfo = null;
		if (splitArtist) {
			String firstArtist = artist.split("feat.")[0];
			albumInfo = Album.getInfo(firstArtist, album, API_KEY);
		}

		if (albumInfo == null)
			albumInfo = Album.getInfo(artist, album, API_KEY);

		if (albumInfo == null)
			albumInfo = Album.getInfo(artist.replace("feat.", "&"), album, API_KEY);
		
		return albumInfo;
	}

//	public static void main(String[] args) {
//		Caller.getInstance().setUserAgent("Mozilla");
//		Caller.getInstance().setProxy(Proxy.NO_PROXY);
//		
//		String apiKey = "84b2abff619c0e3bd04cceb4682e2348";
//		String artistName = "kiss", song = "strutter", albumName = "kiss";
//		Track track = Track.getInfo(artistName, song, apiKey);
//		System.out.println("Song: "+song+"\tArtist: " + artistName + "\tAlbum: " + albumName);
//		System.out.println("Listeners: " + track.getListeners());
//		Album album = Album.getInfo(artistName, albumName, apiKey);
//		String imgurl = album.getImageURL(ImageSize.MEGA);
//		System.out.println(imgurl);
//	}

}
