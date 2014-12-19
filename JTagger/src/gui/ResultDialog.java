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

public class ResultDialog extends Dialog {

	protected Object result;
	protected Shell shell;
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
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());

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
