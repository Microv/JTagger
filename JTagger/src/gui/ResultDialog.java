package gui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ProgressMonitor;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import metadata.Track;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
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
		shlRiepilogo.setSize(942, 533);
		shlRiepilogo.setText("Riepilogo");
		
		Label label = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(273, 22, 14, 465);
		
		Group grpMain = new Group(shlRiepilogo, SWT.NONE);
		grpMain.setText("Main info");
		grpMain.setBounds(10, 10, 257, 178);
		
		Label lblAlbum = new Label(grpMain, SWT.NONE);
		lblAlbum.setBounds(10, 84, 60, 14);
		lblAlbum.setText("Album");
		
		Label lblTitle = new Label(grpMain, SWT.NONE);
		lblTitle.setBounds(10, 52, 60, 14);
		lblTitle.setText("Title");
		
		Label lblArtist = new Label(grpMain, SWT.NONE);
		lblArtist.setBounds(10, 20, 60, 14);
		lblArtist.setText("Artist");
		
		Label lblYear = new Label(grpMain, SWT.NONE);
		lblYear.setBounds(10, 114, 60, 14);
		lblYear.setText("Year");
		
		album_text = new Text(grpMain, SWT.BORDER);
		album_text.setBounds(94, 79, 149, 19);
		album_text.setText(track.getAlbum().getTitle());
		
		year_text = new Text(grpMain, SWT.BORDER);
		year_text.setBounds(94, 109, 149, 19);
		year_text.setText(track.getAlbum().getYear());
		
		title_text = new Text(grpMain, SWT.BORDER);
		title_text.setBounds(94, 47, 149, 19);
		title_text.setText(track.getTitle());
		
		artist_text = new Text(grpMain, SWT.BORDER);
		artist_text.setBounds(94, 15, 149, 19);
		artist_text.setText(track.getArtists());
		
		Group grpLyrics = new Group(shlRiepilogo, SWT.NONE);
		grpLyrics.setText("Lyrics");
		grpLyrics.setBounds(293, 10, 296, 443);
		
		textLyrics = new Text(grpLyrics, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textLyrics.setBounds(10, 20, 276, 413);
		textLyrics.setText(track.getLyrics());
		
		Label label_1 = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBounds(595, 22, 14, 465);
		
		Group grpOtherInfo = new Group(shlRiepilogo, SWT.NONE);
		grpOtherInfo.setText("Other info");
		grpOtherInfo.setBounds(10, 210, 257, 243);
		
		Label lblComposer = new Label(grpOtherInfo, SWT.NONE);
		lblComposer.setBounds(10, 22, 60, 14);
		lblComposer.setText("Composer");
		
		Label lblAlbumArtist = new Label(grpOtherInfo, SWT.NONE);
		lblAlbumArtist.setBounds(10, 54, 78, 14);
		lblAlbumArtist.setText("Album artist");
		
		Label lblGenre = new Label(grpOtherInfo, SWT.NONE);
		lblGenre.setBounds(10, 86, 60, 14);
		lblGenre.setText("Genre");
		
		Label lblPublisher = new Label(grpOtherInfo, SWT.NONE);
		lblPublisher.setBounds(10, 122, 60, 14);
		lblPublisher.setText("Publisher");
		
		composer_text = new Text(grpOtherInfo, SWT.BORDER);
		composer_text.setBounds(94, 17, 149, 19);
		composer_text.setText(track.getComposer());
		
		albumArtist_text = new Text(grpOtherInfo, SWT.BORDER);
		albumArtist_text.setBounds(94, 49, 149, 19);
		albumArtist_text.setText(track.getAlbum().getAlbumArtist());
		
		genre_text = new Text(grpOtherInfo, SWT.BORDER);
		genre_text.setBounds(94, 81, 149, 19);
		
		publisher_text = new Text(grpOtherInfo, SWT.BORDER);
		publisher_text.setBounds(94, 117, 149, 19);
		publisher_text.setText(track.getAlbum().getPublisher());
		
		Label lblTrackN = new Label(grpOtherInfo, SWT.NONE);
		lblTrackN.setBounds(10, 163, 60, 14);
		lblTrackN.setText("Track n°");
		
		trackn2_text = new Text(grpOtherInfo, SWT.BORDER);
		trackn2_text.setBounds(192, 158, 51, 19);
		trackn2_text.setText(track.getAlbum().getTrackCount());
		
		trackn1_text = new Text(grpOtherInfo, SWT.BORDER);
		trackn1_text.setBounds(123, 158, 51, 19);
		trackn1_text.setText(track.getTrackNum());
		
		Label lblDiscN = new Label(grpOtherInfo, SWT.NONE);
		lblDiscN.setBounds(10, 193, 60, 14);
		lblDiscN.setText("Disc n°");
		
		discn2_text = new Text(grpOtherInfo, SWT.BORDER);
		discn2_text.setBounds(192, 188, 51, 19);
		discn2_text.setText(track.getAlbum().getMediumCount());
		
		discn1_text = new Text(grpOtherInfo, SWT.BORDER);
		discn1_text.setBounds(123, 188, 51, 19);
		discn1_text.setText(track.getDiscNum());
		
		Group grpAmazon = new Group(shlRiepilogo, SWT.NONE);
		grpAmazon.setText("Amazon");
		grpAmazon.setBounds(615, 194, 311, 293);
		
		Browser browser = new Browser(grpAmazon, SWT.NONE);
		browser.setBounds(10, 20, 291, 243);
		
		Label lblNAscoltatori = new Label(grpAmazon, SWT.NONE);
		lblNAscoltatori.setBounds(10, 269, 78, 14);
		lblNAscoltatori.setText("N° ascoltatori");
		
		ascoltatori_text = new Text(grpAmazon, SWT.BORDER);
		ascoltatori_text.setBounds(109, 264, 105, 19);
		
		Group grpCover = new Group(shlRiepilogo, SWT.NONE);
		grpCover.setText("Cover");
		grpCover.setBounds(615, 10, 180, 178);
		
		Label lblCover = new Label(grpCover, SWT.NONE);
		lblCover.setBounds(10, 10, 160, 158);
		
		Button btnNewButton = new Button(shlRiepilogo, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.setBounds(455, 459, 95, 28);
		btnNewButton.setText("Salva");
		
		Button btnEsci = new Button(shlRiepilogo, SWT.NONE);
		btnEsci.setText("Esci");
		btnEsci.setBounds(338, 459, 95, 28);
		
		Button btnSmall = new Button(shlRiepilogo, SWT.RADIO);
		btnSmall.setBounds(804, 38, 97, 22);
		btnSmall.setText("Small");
		
		Button btnMedium = new Button(shlRiepilogo, SWT.RADIO);
		btnMedium.setSelection(true);
		btnMedium.setBounds(804, 66, 97, 22);
		btnMedium.setText("Medium");
		
		Button btnLarge = new Button(shlRiepilogo, SWT.RADIO);
		btnLarge.setBounds(804, 94, 97, 22);
		btnLarge.setText("Large");

	}

	public void setTrack(Track result2) {
		this.track = result2;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(new Shell());
		try {
			dialog.run(true, true, new InfoGetter());
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		int attempts = 10;
		while(attempts > 0) {
			try {
				mbw = new MusicBrainzWrapper();
				amw = new AllMusicWrapper();
				lfmw = new LastFmWrapper();
				mmw = new MusixMatchWrapper();
				
				mbw.setAlbumInformations(track);
				track.setLyrics(mmw.getLyricsbyScraping(track.getArtists(),track.getTitle()));
				track.getAlbum().setPublisher(mmw.getMatchingTrack(track.getTitle(), track.getArtists()).get("album_copyright"));
				
				// da sostituire	
				String composer = amw.getComposer(track.getTitle(), track.getArtists());
				track.setComposer(composer);
				break;
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Trying again...");
				attempts--;
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	class InfoGetter implements IRunnableWithProgress {

		@Override
		public void run(IProgressMonitor arg0)
				throws InvocationTargetException, InterruptedException {
			getInfo();
			
		}
		
	}
}
