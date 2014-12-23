package metadata;

public class Track {

	private String title;
	private String artists;
	private Album album;
	private String lyrics;
	private String trackNum;
	private String discNum;
	public String getDiscNum() {
		return discNum;
	}

	public void setDiscNum(String discNum) {
		this.discNum = discNum;
	}

	private String composer;
	private String comment;
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	private String path;
	
	public Track() {
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtists() {
		return artists;
	}

	public void setArtists(String artists) {
		this.artists = artists;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}
	
	public String getTrackNum() {
		return trackNum;
	}
	
	public void setTrackNum(String trackNum) {
		this.trackNum = trackNum;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}
