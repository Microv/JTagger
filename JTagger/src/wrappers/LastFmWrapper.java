package wrappers;

import java.util.Collection;

import de.umass.lastfm.Album;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;

public class LastFmWrapper {
	private static final String API_KEY = "84b2abff619c0e3bd04cceb4682e2348";
	private String song, artist, album;
	private Album albumInfo;
	
	public LastFmWrapper(String song, String artist, String album) {
		this.song = replaceString(song).toLowerCase();
		this.artist = artist;
		this.album = album;
	}
	
	private String replaceString(String string) {
		return string.replaceAll("[’]", "'");
	}

	public String getAlbumCoverURL(ImageSize is) {
		return albumInfo.getImageURL(is);
	}
	
	public int getListeners() {
		findAlbumInfo(artist, album, true);
		Collection<Track> tracks = albumInfo.getTracks();
		if (tracks.isEmpty()) {
			findAlbumInfo(artist, album, false);
			tracks = albumInfo.getTracks();
		}

		String title = "", artists = "";
		int attempts = 2;
		boolean inFirstAttempt = false;
		do {
			for (Track track : tracks) {
				String trackName = track.getName().toLowerCase();
				
				if (attempts < 2) {
					if (song.contains(trackName) || trackName.contains(song)) {
						title = trackName;
						artists = track.getArtist();
						break;
					}
				}
				
				if (trackName.equalsIgnoreCase(song)) {
					title = trackName;
					artists = track.getArtist();
					inFirstAttempt = true;
					break;
				}
			}
		} while (--attempts > 0 && !inFirstAttempt);

		Track trackInfo = Track.getInfo(artists, title, API_KEY);
		if(trackInfo == null) return 0;
		return trackInfo.getListeners();
	}

	private void findAlbumInfo(String artist, String album, boolean splitArtist) {
		albumInfo = null;
		if (splitArtist) {
			String firstArtist = artist.split("feat.")[0];
			albumInfo = Album.getInfo(firstArtist, album, API_KEY);
		}

		if (albumInfo == null)
			albumInfo = Album.getInfo(artist, album, API_KEY);

		if (albumInfo == null)
			albumInfo = Album.getInfo(artist.replace("feat.", "&"), album, API_KEY);
	}

}
