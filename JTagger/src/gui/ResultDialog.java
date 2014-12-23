package gui;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import metadata.Album;
import metadata.Track;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import wrappers.AllMusicWrapper;
import wrappers.LastFmWrapper;
import wrappers.MusicBrainzWrapper;
import wrappers.MusixMatchWrapper;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.xml.sax.SAXException;

public class ResultDialog extends Dialog {

	protected Object result;
	protected Shell shlRiepilogo;
	private Text textLyrics;
	private Text album_text;
	private Text year_text;
	private Text title_text;
	private Text artist_text;
	private Text composer_text;
	private Text albumArtist_text;
	private Text genre_text;
	private Text publisher_text;
	private Text ascoltatori_text;
	private Text trackn2_text;
	private Text trackn1_text;
	private Text discn2_text;
	private Text discn1_text;
	
	/*
	 * Variabili Temporanee 
	 */
	private Track track;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ResultDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlRiepilogo.open();
		shlRiepilogo.layout();
		Display display = getParent().getDisplay();
		while (!shlRiepilogo.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlRiepilogo = new Shell(getParent(), getStyle());
		shlRiepilogo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shlRiepilogo.setImage(null);
		shlRiepilogo.setSize(828, 486);
		shlRiepilogo.setText("Riepilogo");
		
		ProgressBar progressBar = new ProgressBar(shlRiepilogo, SWT.NONE);
		progressBar.setBounds(0, 437, 782, 14);
		
		Label label = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(273, 22, 36, 409);
		
		Group grpMain = new Group(shlRiepilogo, SWT.NONE);
		grpMain.setText("Main info");
		grpMain.setBounds(10, 10, 257, 147);
		
		Label lblAlbum = new Label(grpMain, SWT.NONE);
		lblAlbum.setBounds(10, 64, 60, 14);
		lblAlbum.setText("Album");
		
		Label lblTitle = new Label(grpMain, SWT.NONE);
		lblTitle.setBounds(10, 40, 60, 14);
		lblTitle.setText("Title");
		
		Label lblArtist = new Label(grpMain, SWT.NONE);
		lblArtist.setBounds(10, 10, 60, 14);
		lblArtist.setText("Artist");
		
		Label lblYear = new Label(grpMain, SWT.NONE);
		lblYear.setBounds(10, 89, 60, 14);
		lblYear.setText("Year");
		
		album_text = new Text(grpMain, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);
		album_text.setBounds(94, 61, 149, 19);
		album_text.setText(track.getAlbum().getTitle());
		
		year_text = new Text(grpMain, SWT.BORDER);
		year_text.setBounds(94, 86, 149, 19);
		year_text.setText(track.getAlbum().getYear());
		
		title_text = new Text(grpMain, SWT.BORDER);
		title_text.setBounds(94, 35, 149, 19);
		title_text.setText(track.getTitle());
		
		artist_text = new Text(grpMain, SWT.BORDER);
		artist_text.setBounds(94, 10, 149, 19);
		artist_text.setText(track.getArtists());
		
		Group grpLyrics = new Group(shlRiepilogo, SWT.NONE);
		grpLyrics.setText("Lyrics");
		grpLyrics.setBounds(315, 10, 252, 394);
		
		textLyrics = new Text(grpLyrics, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textLyrics.setBounds(10, 10, 226, 357);
		textLyrics.setText(track.getLyrics());
		
		Label label_1 = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBounds(573, 22, 14, 409);
		
		Group grpOtherInfo = new Group(shlRiepilogo, SWT.NONE);
		grpOtherInfo.setText("Other info");
		grpOtherInfo.setBounds(10, 176, 257, 217);
		
		Label lblComposer = new Label(grpOtherInfo, SWT.NONE);
		lblComposer.setBounds(10, 10, 60, 14);
		lblComposer.setText("Composer");
		
		Label lblAlbumArtist = new Label(grpOtherInfo, SWT.NONE);
		lblAlbumArtist.setBounds(10, 40, 78, 14);
		lblAlbumArtist.setText("Album artist");
		
		Label lblGenre = new Label(grpOtherInfo, SWT.NONE);
		lblGenre.setBounds(10, 70, 60, 14);
		lblGenre.setText("Genre");
		
		Label lblPublisher = new Label(grpOtherInfo, SWT.NONE);
		lblPublisher.setBounds(10, 94, 60, 14);
		lblPublisher.setText("Publisher");
		
		composer_text = new Text(grpOtherInfo, SWT.BORDER);
		composer_text.setBounds(94, 5, 149, 19);
		
		albumArtist_text = new Text(grpOtherInfo, SWT.BORDER);
		albumArtist_text.setBounds(94, 37, 149, 19);
		
		genre_text = new Text(grpOtherInfo, SWT.BORDER);
		genre_text.setBounds(94, 65, 149, 19);
		
		publisher_text = new Text(grpOtherInfo, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);
		publisher_text.setBounds(94, 94, 149, 19);
		publisher_text.setText(track.getAlbum().getPublisher());
		
		ascoltatori_text = new Text(grpOtherInfo, SWT.BORDER);
		ascoltatori_text.setBounds(94, 122, 149, 19);
		
		Label lblTrackN = new Label(grpOtherInfo, SWT.NONE);
		lblTrackN.setBounds(10, 150, 60, 14);
		lblTrackN.setText("Track n°");
		
		Label lblNAscoltatori = new Label(grpOtherInfo, SWT.NONE);
		lblNAscoltatori.setBounds(10, 125, 78, 14);
		lblNAscoltatori.setText("N° ascoltatori");
		
		trackn2_text = new Text(grpOtherInfo, SWT.BORDER);
		trackn2_text.setBounds(192, 147, 51, 19);
		
		trackn1_text = new Text(grpOtherInfo, SWT.BORDER);
		trackn1_text.setBounds(123, 147, 51, 19);
		
		Label lblDiscN = new Label(grpOtherInfo, SWT.NONE);
		lblDiscN.setBounds(10, 176, 60, 14);
		lblDiscN.setText("Disc n°");
		
		discn2_text = new Text(grpOtherInfo, SWT.BORDER);
		discn2_text.setBounds(192, 173, 51, 19);
		
		discn1_text = new Text(grpOtherInfo, SWT.BORDER);
		discn1_text.setBounds(123, 172, 51, 19);
		
		Group grpAmazon = new Group(shlRiepilogo, SWT.NONE);
		grpAmazon.setText("Amazon");
		grpAmazon.setBounds(593, 206, 229, 225);
		
		Browser browser = new Browser(grpAmazon, SWT.NONE);
		browser.setBounds(10, 10, 205, 188);
		
		Group grpCover = new Group(shlRiepilogo, SWT.NONE);
		grpCover.setText("Cover");
		grpCover.setBounds(593, 10, 225, 190);
		
		Label lblCover = new Label(grpCover, SWT.NONE);
		lblCover.setBounds(10, 10, 191, 143);
		
		Button btnNewButton = new Button(shlRiepilogo, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.setBounds(453, 403, 95, 28);
		btnNewButton.setText("Salva");
		
		Button btnEsci = new Button(shlRiepilogo, SWT.NONE);
		btnEsci.setText("Esci");
		btnEsci.setBounds(325, 403, 95, 28);

	}

	public void setTrack(Track result2) {
		this.track = result2;
		getInfo();
	}

	private void getInfo() {
		MusicBrainzWrapper mbw;
		AllMusicWrapper amw;
		LastFmWrapper lfmw;
		MusixMatchWrapper mmw;
		/*
		 *  Inserire all'interno del try le informazioni da assegnare
		 *   a 'track' ( per visualizzarle nel dialog)
		 */
		try {
			mbw = new MusicBrainzWrapper();
			amw = new AllMusicWrapper();
			lfmw = new LastFmWrapper();
			mmw = new MusixMatchWrapper();
			track.setLyrics(mmw.getLyricsbyScraping(track.getArtists(),track.getTitle()));
			track.getAlbum().setPublisher(mmw.getMatchingTrack(track.getTitle(), track.getArtists()).get("album_copyright"));
			
	
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*mbw.setAlbumInfo(track);
		
		String composer = amw.getComposer(track.getTitle(), track.getArtists());
		track.setComposer(composer);
		*/
		

	}
}
