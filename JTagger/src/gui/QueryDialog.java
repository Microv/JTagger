package gui;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import metadata.Album;
import metadata.Track;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.xml.sax.SAXException;

import wrappers.MusicBrainzWrapper;

public class QueryDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text recordingSearch;
	private Text artistSearch;
	private Text releaseSearch;
	private Table table;
	private Tag tag;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public QueryDialog(Shell parent, int style) {
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
		shell.setSize(508, 341);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Group grpResults = new Group(shell, SWT.NONE);
		grpResults.setText("Results");
		FormData fd_grpResults = new FormData();
		fd_grpResults.top = new FormAttachment(100, -215);
		fd_grpResults.right = new FormAttachment(0, 492);
		fd_grpResults.bottom = new FormAttachment(100, -10);
		fd_grpResults.left = new FormAttachment(0, 10);
		grpResults.setLayoutData(fd_grpResults);
		
		Label lblTitle = new Label(shell, SWT.NONE);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.left = new FormAttachment(0, 10);
		fd_lblTitle.top = new FormAttachment(0, 20);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText("Recording");
		
		recordingSearch = new Text(shell, SWT.BORDER);
		fd_lblTitle.bottom = new FormAttachment(recordingSearch, 0, SWT.BOTTOM);
		fd_lblTitle.right = new FormAttachment(recordingSearch, -6);
		FormData fd_recordingSearch = new FormData();
		fd_recordingSearch.left = new FormAttachment(0, 85);
		fd_recordingSearch.top = new FormAttachment(0, 10);
		recordingSearch.setLayoutData(fd_recordingSearch);
		recordingSearch.setText(tag.getFirst(FieldKey.TITLE));
		
		Label lblArtist = new Label(shell, SWT.NONE);
		fd_recordingSearch.right = new FormAttachment(lblArtist, -22);
		FormData fd_lblArtist = new FormData();
		fd_lblArtist.left = new FormAttachment(0, 279);
		fd_lblArtist.top = new FormAttachment(0, 20);
		fd_lblArtist.bottom = new FormAttachment(0, 33);
		lblArtist.setLayoutData(fd_lblArtist);
		lblArtist.setText("Artist");
		
		artistSearch = new Text(shell, SWT.BORDER);
		fd_lblArtist.right = new FormAttachment(100, -186);
		FormData fd_artistSearch = new FormData();
		fd_artistSearch.right = new FormAttachment(grpResults, 0, SWT.RIGHT);
		fd_artistSearch.top = new FormAttachment(0, 10);
		fd_artistSearch.left = new FormAttachment(lblArtist, 6);
		artistSearch.setLayoutData(fd_artistSearch);
		artistSearch.setText(tag.getFirst(FieldKey.ARTIST));
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(lblTitle, 23);
		fd_lblNewLabel.left = new FormAttachment(grpResults, 0, SWT.LEFT);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Release");
		
		releaseSearch = new Text(shell, SWT.BORDER);
		FormData fd_releaseSearch = new FormData();
		fd_releaseSearch.right = new FormAttachment(recordingSearch, 0, SWT.RIGHT);
		fd_releaseSearch.top = new FormAttachment(recordingSearch, 13);
		fd_releaseSearch.left = new FormAttachment(recordingSearch, 0, SWT.LEFT);
		releaseSearch.setLayoutData(fd_releaseSearch);
		releaseSearch.setText(tag.getFirst(FieldKey.ALBUM));
		
		Button button = new Button(shell, SWT.NONE);
		button.setImage(SWTResourceManager.getImage("JTagger/img/search.png"));
		FormData fd_button = new FormData();
		fd_button.bottom = new FormAttachment(grpResults, -6);
		button.addListener(SWT.Selection, new QueryListener());
		
		table = new Table(grpResults, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 20, 462, 175);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnTitle = new TableColumn(table, SWT.NONE);
		tblclmnTitle.setWidth(167);
		tblclmnTitle.setText("Title");
		
		TableColumn tblclmnArtist = new TableColumn(table, SWT.NONE);
		tblclmnArtist.setWidth(161);
		tblclmnArtist.setText("Artist");
		
		TableColumn tblclmnAlbum = new TableColumn(table, SWT.NONE);
		tblclmnAlbum.setWidth(191);
		tblclmnAlbum.setText("Album");
		
		TableColumn tblclmnYear = new TableColumn(table, SWT.NONE);
		tblclmnYear.setWidth(100);
		tblclmnYear.setText("Year");
		fd_button.right = new FormAttachment(100, -22);
		fd_button.left = new FormAttachment(100, -105);
		button.setLayoutData(fd_button);
		
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
				Point pt = new Point(arg0.x, arg0.y);
				TableItem titem = table.getItem(pt);
				
				if(titem != null) {
					ResultDialog dialog = new ResultDialog(new Shell(shell), SWT.TITLE);
					Track result = new Track();
					result.setTitle(titem.getText(0));
					result.setArtists(titem.getText(1));
					Album a = new Album();
					a.setTitle(titem.getText(2));
					a.setYear(titem.getText(3));
					result.setAlbum(a);
					dialog.setTrack(result);
					dialog.open();
				}
			}
		});
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	class QueryListener implements Listener{

		@Override
		public void handleEvent(Event arg0) {
			String recording = recordingSearch.getText();
			String artist = artistSearch.getText();
			String release = releaseSearch.getText();
			
			ArrayList<Track> tracks = null;
			try {
				
				MusicBrainzWrapper mbw = new MusicBrainzWrapper();
				if(release.length() > 0 &&
						artist.length() > 0
						&& recording.length() > 0)
					tracks = mbw.getTrackByTitleArtistAlbum(recording, artist, release);
				else if (artist.length() > 0 && recording.length() > 0)
						tracks = mbw.getTrackByTitleArtist(recording, artist);
					else if (recording.length() > 0)
						tracks = mbw.getTrackByTitle(recording);
					else if (artist.length() > 0)
						tracks = mbw.getTrackByArtist(artist);
					else if(release.length() > 0)
						tracks = mbw.getTrackByAlbum(release);
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (XPathExpressionException | SAXException
					| IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(tracks == null) return;
			for (Track t : tracks) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
		      	tableItem.setText(0, t.getTitle());
		      	tableItem.setText(1, t.getArtists());
		      	tableItem.setText(2, t.getAlbum().getTitle());
		      	tableItem.setText(3, t.getAlbum().getYear());
			}
		}
		
	}
}
