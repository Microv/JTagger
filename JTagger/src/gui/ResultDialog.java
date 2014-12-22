package gui;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import metadata.Track;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import wrappers.AllMusicWrapper;
import wrappers.LastFmWrapper;
import wrappers.MusicBrainzWrapper;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.browser.Browser;

public class ResultDialog extends Dialog {

	protected Object result;
	protected Shell shlRiepilogo;
	private Track track;
	private Text textLyrics;
	private Text text_4;
	private Text text_2;
	private Text text;
	private Text text_1;
	private Text text_3;
	private Text text_5;
	private Text text_6;
	private Text text_7;
	private Text text_8;

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
		shlRiepilogo.setBackground(SWTResourceManager.getColor(60, 179, 113));
		shlRiepilogo.setImage(null);
		shlRiepilogo.setSize(784, 473);
		shlRiepilogo.setText("Riepilogo");
		
		ProgressBar progressBar = new ProgressBar(shlRiepilogo, SWT.NONE);
		progressBar.setBounds(0, 437, 784, 14);
		
		Label label = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(227, 10, 36, 409);
		
		Group grpMain = new Group(shlRiepilogo, SWT.NONE);
		grpMain.setText("Main info");
		grpMain.setBounds(10, 10, 225, 147);
		
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
		
		text_4 = new Text(grpMain, SWT.BORDER);
		text_4.setBounds(94, 61, 117, 19);
		
		text_2 = new Text(grpMain, SWT.BORDER);
		text_2.setBounds(94, 86, 117, 19);
		
		text = new Text(grpMain, SWT.BORDER);
		text.setBounds(94, 35, 117, 19);
		
		text_1 = new Text(grpMain, SWT.BORDER);
		text_1.setBounds(94, 10, 117, 19);
		
		Group grpLyrics = new Group(shlRiepilogo, SWT.NONE);
		grpLyrics.setText("Lyrics");
		grpLyrics.setBounds(263, 10, 225, 409);
		
		textLyrics = new Text(grpLyrics, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textLyrics.setBounds(10, 10, 199, 377);
		
		Label label_1 = new Label(shlRiepilogo, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBounds(506, 10, 14, 409);
		
		Group grpOtherInfo = new Group(shlRiepilogo, SWT.NONE);
		grpOtherInfo.setText("Other info");
		grpOtherInfo.setBounds(10, 176, 225, 178);
		
		Label lblComposer = new Label(grpOtherInfo, SWT.NONE);
		lblComposer.setBounds(10, 10, 60, 14);
		lblComposer.setText("Composer");
		
		Label lblAlbumArtist = new Label(grpOtherInfo, SWT.NONE);
		lblAlbumArtist.setBounds(10, 40, 60, 14);
		lblAlbumArtist.setText("Album artist");
		
		Label lblGenre = new Label(grpOtherInfo, SWT.NONE);
		lblGenre.setBounds(10, 70, 60, 14);
		lblGenre.setText("Genre");
		
		Label lblPublisher = new Label(grpOtherInfo, SWT.NONE);
		lblPublisher.setBounds(10, 94, 60, 14);
		lblPublisher.setText("Publisher");
		
		text_3 = new Text(grpOtherInfo, SWT.BORDER);
		text_3.setBounds(94, 5, 117, 19);
		
		text_5 = new Text(grpOtherInfo, SWT.BORDER);
		text_5.setBounds(94, 37, 117, 19);
		
		text_6 = new Text(grpOtherInfo, SWT.BORDER);
		text_6.setBounds(94, 65, 117, 19);
		
		text_7 = new Text(grpOtherInfo, SWT.BORDER);
		text_7.setBounds(94, 94, 117, 19);
		
		Label lblNScoltatori = new Label(grpOtherInfo, SWT.NONE);
		lblNScoltatori.setText("N. ascoltatori");
		lblNScoltatori.setBounds(10, 127, 75, 14);
		
		text_8 = new Text(grpOtherInfo, SWT.BORDER);
		text_8.setBounds(94, 122, 117, 19);
		
		Group grpAmazon = new Group(shlRiepilogo, SWT.NONE);
		grpAmazon.setText("Amazon");
		grpAmazon.setBounds(533, 206, 229, 201);
		
		Browser browser = new Browser(grpAmazon, SWT.NONE);
		browser.setBounds(10, 10, 205, 164);
		
		Group grpCover = new Group(shlRiepilogo, SWT.NONE);
		grpCover.setText("Cover");
		grpCover.setBounds(535, 10, 225, 190);
		
		Label lblNewLabel = new Label(grpCover, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 191, 143);

	}

	public void setTrack(Track result2) {
		this.track = track;
		getInfo();
	}

	private void getInfo() {
		
		MusicBrainzWrapper mbw;
		AllMusicWrapper amw;
		LastFmWrapper lfmw;
		try {
			mbw = new MusicBrainzWrapper();
			amw = new AllMusicWrapper();
			lfmw = new LastFmWrapper();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*mbw.setAlbumInfo(track);
		
		String composer = amw.getComposer(track.getTitle(), track.getArtists());
		track.setComposer(composer);
		*/
		

	}
}
