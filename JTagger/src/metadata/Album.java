package metadata;

public class Album {
	
	private String title;
	private String year;
	private String genre;
	private String cover;
	private String albumArtist;
	private String publisher; 
	private String trackCount;
	private String release_id;
	private String mediumCount;
	
	public String getRelease_id() {
		return release_id;
	}

	public void setRelease_id(String release_id) {
		this.release_id = release_id;
	}

	public String getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(String trackCount) {
		this.trackCount = trackCount;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublisher(){
		return publisher;
	}
	
	public void setPublisher(Object pub){
		publisher = (String)pub;
	}
	
	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}

	public Album() {
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public void setMediumCount(String mCount) {
		mediumCount = mCount;
	}

	public String getMediumCount() {
		return mediumCount;
	}

	
	
}
