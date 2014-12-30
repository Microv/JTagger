package gui;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import metadata.Track;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import utility.ImageUtil;
import wrappers.AllMusicWrapper;
import wrappers.AmazonWrapper;
import wrappers.LastFmWrapper;
import wrappers.MusicBrainzWrapper;
import wrappers.MusixMatchWrapper;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.xml.sax.SAXException;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.umass.lastfm.Caller;
import de.umass.lastfm.ImageSize;

public class ResultDialog extends Dialog {

	protected static final String SMALL = "Small";
	protected static final String MEDIUM = "Medium";
	private static final String LARGE = "Large";
	
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
	private Label lblCoverArt;
	
	private Display display;
	
	private MusicBrainzWrapper mbw;
	@SuppressWarnings("unused")
	private AllMusicWrapper amw;
	private LastFmWrapper lfmw;
	private MusixMatchWrapper mmw;
	private AmazonWrapper aw;
	
	
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
		display = getParent().getDisplay();
		
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
		shlRiepilogo.setSize(942, 566);
		shlRiepilogo.setText("Riepilogo");
		
		Label label = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(273, 44, 14, 443);
		
		Group grpMain = new Group(shlRiepilogo, SWT.NONE);
		grpMain.setText("Main info");
		grpMain.setBounds(10, 44, 257, 194);
		
		Label lblAlbum = new Label(grpMain, SWT.NONE);
		lblAlbum.setBounds(10, 110, 60, 14);
		lblAlbum.setText("Album");
		
		Label lblTitle = new Label(grpMain, SWT.NONE);
		lblTitle.setBounds(10, 65, 60, 14);
		lblTitle.setText("Title");
		
		Label lblArtist = new Label(grpMain, SWT.NONE);
		lblArtist.setBounds(10, 20, 60, 14);
		lblArtist.setText("Artist");
		
		Label lblYear = new Label(grpMain, SWT.NONE);
		lblYear.setBounds(10, 159, 60, 14);
		lblYear.setText("Year");
		
		album_text = new Text(grpMain, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		album_text.setBounds(94, 110, 149, 38);
		album_text.setText(track.getAlbum().getTitle());
		
		
		year_text = new Text(grpMain, SWT.BORDER);
		year_text.setBounds(94, 154, 149, 19);
		year_text.setText(track.getAlbum().getYear());
		
		title_text = new Text(grpMain, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		title_text.setBounds(94, 65, 149, 39);
		title_text.setText(track.getTitle());
		
		artist_text = new Text(grpMain, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		artist_text.setBounds(94, 15, 149, 44);
		artist_text.setText(track.getArtists());
		
		Group grpLyrics = new Group(shlRiepilogo, SWT.NONE);
		grpLyrics.setText("Lyrics");
		grpLyrics.setBounds(293, 44, 296, 443);
		
		textLyrics = new Text(grpLyrics, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textLyrics.setFont(SWTResourceManager.getFont("Sans", 10, SWT.NORMAL));
		textLyrics.setBounds(10, 20, 276, 413);
		textLyrics.setText(track.getLyrics());
		
		Label label_1 = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBounds(595, 44, 14, 443);
		
		Group grpOtherInfo = new Group(shlRiepilogo, SWT.NONE);
		grpOtherInfo.setText("Other info");
		grpOtherInfo.setBounds(10, 272, 257, 243);
		
		Label lblComposer = new Label(grpOtherInfo, SWT.NONE);
		lblComposer.setBounds(10, 22, 60, 14);
		lblComposer.setText("Composer");
		
		Label lblAlbumArtist = new Label(grpOtherInfo, SWT.NONE);
		lblAlbumArtist.setBounds(10, 69, 78, 14);
		lblAlbumArtist.setText("Album artist");
		
		Label lblGenre = new Label(grpOtherInfo, SWT.NONE);
		lblGenre.setBounds(10, 120, 60, 14);
		lblGenre.setText("Genre");
		
		Label lblPublisher = new Label(grpOtherInfo, SWT.NONE);
		lblPublisher.setBounds(10, 145, 60, 14);
		lblPublisher.setText("Publisher");
		
		composer_text = new Text(grpOtherInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		composer_text.setBounds(94, 17, 149, 40);
		composer_text.setText(track.getComposer());
		
		albumArtist_text = new Text(grpOtherInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		albumArtist_text.setBounds(94, 69, 149, 40);
		albumArtist_text.setText(track.getAlbum().getAlbumArtist());
		
		genre_text = new Text(grpOtherInfo, SWT.BORDER);
		genre_text.setBounds(94, 115, 149, 19);
		
		publisher_text = new Text(grpOtherInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		publisher_text.setBounds(94, 145, 149, 36);
		publisher_text.setText(track.getAlbum().getPublisher());
		
		Label lblTrackN = new Label(grpOtherInfo, SWT.NONE);
		lblTrackN.setBounds(10, 194, 96, 14);
		lblTrackN.setText("Track number");
		
		trackn2_text = new Text(grpOtherInfo, SWT.BORDER);
		trackn2_text.setBounds(192, 189, 51, 19);
		trackn2_text.setText(track.getAlbum().getTrackCount());
		
		trackn1_text = new Text(grpOtherInfo, SWT.BORDER);
		trackn1_text.setBounds(123, 189, 51, 19);
		trackn1_text.setText(track.getTrackNum());
		
		Label lblDiscN = new Label(grpOtherInfo, SWT.NONE);
		lblDiscN.setBounds(10, 219, 78, 14);
		lblDiscN.setText("Disc number");
		
		discn2_text = new Text(grpOtherInfo, SWT.BORDER);
		discn2_text.setBounds(192, 214, 51, 19);
		discn2_text.setText(track.getAlbum().getMediumCount());
		
		discn1_text = new Text(grpOtherInfo, SWT.BORDER);
		discn1_text.setBounds(123, 214, 51, 19);
		discn1_text.setText(track.getDiscNum());
		
		Group grpAmazon = new Group(shlRiepilogo, SWT.NONE);
		grpAmazon.setText("Amazon");
		grpAmazon.setBounds(615, 228, 311, 291);
		
		Browser browser = new Browser(grpAmazon, SWT.NONE);
		browser.setBounds(10, 14, 291, 243);
		
		//	Get real URL without specifying the absolute path (computer file system path)
		try {
			File amazon = new File("amazon.html");
			String url = amazon.getCanonicalPath();
			browser.setUrl(url);
			amazon.deleteOnExit();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Label lblNAscoltatori = new Label(grpAmazon, SWT.NONE);
		lblNAscoltatori.setBounds(10, 269, 126, 14);
		lblNAscoltatori.setText("Number of listeners");
		
		ascoltatori_text = new Text(grpAmazon, SWT.BORDER);
		ascoltatori_text.setBounds(142, 264, 75, 19);
		ascoltatori_text.setText(track.getListeners() + "");
		
		Group grpCover = new Group(shlRiepilogo, SWT.NONE);
		grpCover.setText("Cover");
		grpCover.setBounds(615, 44, 180, 178);
		
		lblCoverArt = new Label(grpCover, SWT.NONE);
		lblCoverArt.setBounds(10, 13, 160, 158);
		
		Button btnNewButton = new Button(shlRiepilogo, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.setBounds(449, 503, 95, 28);
		btnNewButton.setText("Save");
		
		Button btnCancel = new Button(shlRiepilogo, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(336, 503, 95, 28);
		btnCancel.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				shlRiepilogo.dispose();
			}
		});
		
		SelectionListener selectionButtons = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				if(button.getText().equals(SMALL))
					track.getAlbum().setCover(lfmw.getAlbumCoverURL(ImageSize.MEDIUM));
				if(button.getText().equals(MEDIUM))
					track.getAlbum().setCover(lfmw.getAlbumCoverURL(ImageSize.LARGE));
				if(button.getText().equals(LARGE))
					track.getAlbum().setCover(lfmw.getAlbumCoverURL(ImageSize.EXTRALARGE));
				updateImage();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				System.out.print(button.getText());
				System.out.println(" selected = " + button.getSelection());
			}
		};
		
		Button btnSmall = new Button(shlRiepilogo, SWT.RADIO);
		btnSmall.setBounds(801, 63, 97, 22);
		btnSmall.setText(SMALL);
		btnSmall.addSelectionListener(selectionButtons);
		
		Button btnMedium = new Button(shlRiepilogo, SWT.RADIO);
		btnMedium.setSelection(true);
		btnMedium.setBounds(801, 91, 97, 22);
		btnMedium.setText(MEDIUM);
		btnMedium.addSelectionListener(selectionButtons);
		
		Button btnLarge = new Button(shlRiepilogo, SWT.RADIO);
		btnLarge.setBounds(801, 119, 97, 22);
		btnLarge.setText(LARGE);
		btnLarge.addSelectionListener(selectionButtons);
		
		ToolBar toolBar = new ToolBar(shlRiepilogo, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(700, 0, 236, 38);
		
		ToolItem toolItem_mb = new ToolItem(toolBar, SWT.NONE);
		toolItem_mb.setToolTipText("MusicBrainz");
		toolItem_mb.setImage(SWTResourceManager.getImage(ResultDialog.class, "/gui/img/icon-32.png"));
		toolItem_mb.addListener(SWT.Selection, new BrowserListener("http://musicbrainz.org/"));
		
		ToolItem toolItem_amz = new ToolItem(toolBar, SWT.NONE);
		toolItem_amz.setToolTipText("Amazon");
		toolItem_amz.setImage(SWTResourceManager.getImage(ResultDialog.class, "/gui/img/am-icon.png"));
		toolItem_amz.addListener(SWT.Selection, new BrowserListener("http://www.amazon.com/"));
		
		ToolItem toolItem_lf = new ToolItem(toolBar, SWT.NONE);
		toolItem_lf.setToolTipText("Last.fm");
		toolItem_lf.setImage(SWTResourceManager.getImage(ResultDialog.class, "/gui/img/lfm-icon.png"));
		toolItem_lf.addListener(SWT.Selection, new BrowserListener("http://www.last.fm"));
		
		ToolItem toolItem_mm = new ToolItem(toolBar, SWT.NONE);
		toolItem_mm.setToolTipText("MusixMatch");
		toolItem_mm.setImage(SWTResourceManager.getImage(ResultDialog.class, "/gui/img/mm-icon.png"));
		toolItem_mm.addListener(SWT.Selection, new BrowserListener("https://www.musixmatch.com/"));
		
		ToolItem toolItem_am = new ToolItem(toolBar, SWT.NONE);
		toolItem_am.setToolTipText("AllMusic");
		toolItem_am.setImage(SWTResourceManager.getImage(ResultDialog.class, "/gui/img/am-logo.png"));
		toolItem_am.addListener(SWT.Selection, new BrowserListener("http://www.allmusic.com/"));
		
		ToolItem toolItem_aid = new ToolItem(toolBar, SWT.NONE);
		toolItem_aid.setToolTipText("AcoustID");
		toolItem_aid.setImage(SWTResourceManager.getImage(ResultDialog.class, "/gui/img/aid-icon.png"));
		toolItem_aid.addListener(SWT.Selection, new BrowserListener("https://acoustid.org/"));
		
		Label lblPoweredBy = new Label(shlRiepilogo, SWT.NONE);
		lblPoweredBy.setFont(SWTResourceManager.getFont("Sans", 10, SWT.NORMAL));
		lblPoweredBy.setBounds(616, 10, 78, 13);
		lblPoweredBy.setText("Powered By");
		
		updateImage();

	}

	private void updateImage() {
		try {
			BufferedImage bi = ImageIO.read(new URL(track.getAlbum().getCover()));
			ImageData imgdata = ImageUtil.makeSWTImage(display, bi).getImageData()
					.scaledTo(lblCoverArt.getBounds().width, lblCoverArt.getBounds().height);
			lblCoverArt.setImage(new Image(display, imgdata));
			lblCoverArt.pack();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		/*
		 *  Inserire all'interno del try le informazioni da assegnare
		 *   a 'track' ( per visualizzarle nel dialog)
		 */
		int attempts = 10;
		while(attempts > 0) {
			try {
				mbw = new MusicBrainzWrapper();
				amw = new AllMusicWrapper();
				mmw = new MusixMatchWrapper();
				
				String artist = "";
				int ind = track.getArtists().indexOf(" feat. ");
				if(ind > 0)
					artist = track.getArtists().substring(0, ind);
				else 
					artist = track.getArtists();
				
				// last.fm wrapper
				Caller.getInstance().setUserAgent("Mozilla");
				Caller.getInstance().setProxy(Proxy.NO_PROXY);
				lfmw = new LastFmWrapper(track.getTitle(), artist, track.getAlbum().getTitle());
				track.setListeners(lfmw.getListeners());
				track.getAlbum().setCover(lfmw.getAlbumCoverURL(ImageSize.LARGE));
				
				// amazon wrapper
				aw = new AmazonWrapper(track.getTitle(), artist, track.getAlbum().getTitle());
				System.out.println("Review" + (aw.findReview() ? " " : " not ") + "found.");
				
				mbw.setAlbumInformations(track);
				track.setLyrics(mmw.getLyricsbyScraping(artist,track.getTitle()));
//				album.setPublisher(mmw.getMatchingTrack(track.getTitle(), track.getArtists()).get("album_copyright"));
				track.getAlbum().setPublisher(mmw.getMatchingTrack(track.getTitle(), artist).get("album_copyright"));
				track.setComposer(mmw.getComposer(artist, track.getTitle()));
				
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
	
	class BrowserListener implements Listener {
		
		private String url;
		public BrowserListener(String url) {
			this.url = url;
		}
		
		@Override
		public void handleEvent(Event arg0) {
			
			if(Desktop.isDesktopSupported())
			{
			  try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
	}
}
