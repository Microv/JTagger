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
import org.eclipse.swt.widgets.ProgressBar;

public class QueryDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text recordingSearch;
	private Text artistSearch;
	private Text releaseSearch;
	private Table table;
	private ProgressBar progressBar;
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
		shell.setSize(508, 370);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Group grpResults = new Group(shell, SWT.NONE);
		grpResults.setText("Results");
		FormData fd_grpResults = new FormData();
		fd_grpResults.right = new FormAttachment(0, 492);
		grpResults.setLayoutData(fd_grpResults);
		
		Label lblTitle = new Label(shell, SWT.NONE);
		fd_grpResults.left = new FormAttachment(lblTitle, 0, SWT.LEFT);
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
		fd_artistSearch.right = new FormAttachment(100, -10);
		fd_artistSearch.left = new FormAttachment(lblArtist, 6);
		fd_artistSearch.top = new FormAttachment(0, 10);
		artistSearch.setLayoutData(fd_artistSearch);
		artistSearch.setText(tag.getFirst(FieldKey.ARTIST));
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		fd_lblNewLabel.top = new FormAttachment(lblTitle, 23);
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
		fd_grpResults.top = new FormAttachment(button, 6);
		button.setImage(SWTResourceManager.getImage(QueryDialog.class, "/gui/img/search.png"));
		FormData fd_button = new FormData();
		fd_button.bottom = new FormAttachment(100, -255);
		fd_button.right = new FormAttachment(100, -22);
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
		fd_button.left = new FormAttachment(100, -105);
		button.setLayoutData(fd_button);
		
		progressBar = new ProgressBar(shell, SWT.NONE);
		fd_grpResults.bottom = new FormAttachment(progressBar, -6);
		FormData fd_progressBar = new FormData();
		fd_progressBar.top = new FormAttachment(0, 312);
		fd_progressBar.bottom = new FormAttachment(100, -10);
		fd_progressBar.right = new FormAttachment(100, -22);
		fd_progressBar.left = new FormAttachment(0, 20);
		progressBar.setLayoutData(fd_progressBar);
		
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
			
			table.removeAll();
			
			int attempts = 20;
			int barStarPosition = 30;
			int barIncrement = (100 - barStarPosition)/attempts;
			
			String recording = recordingSearch.getText();
			String artist = artistSearch.getText();
			String release = releaseSearch.getText();
			
			ArrayList<Track> tracks = null;
			
			while(attempts > 0) {
				try {
					if(recording.length() == 0 && artist.length() == 0 && release.length() == 0)
						return;
					progressBar.setSelection(barStarPosition);
					MusicBrainzWrapper mbw = new MusicBrainzWrapper();
					if(release.length() > 0 &&
							artist.length() > 0
							&& recording.length() > 0) {
						tracks = mbw.getTrackByTitleArtistAlbum(recording, artist, release);
						break;
					}
					else if (artist.length() > 0 && recording.length() > 0) {
							tracks = mbw.getTrackByTitleArtist(recording, artist);
							break;
					}
					else if (recording.length() > 0 && release.length() > 0) {
						tracks = mbw.getTrackByTitleAlbum(recording, release);
						break;
					}
					else if (recording.length() > 0) {
							tracks = mbw.getTrackByTitle(recording);
							break;
					}
					else if (artist.length() > 0) {
							tracks = mbw.getTrackByArtist(artist);
							break;
					}
					else if(release.length() > 0) {
							tracks = mbw.getTrackByAlbum(release);
							break;
					}
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (XPathExpressionException | SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					System.out.println("IO Exception. Trying again...");
					progressBar.setSelection(progressBar.getSelection()
							+barIncrement);
					attempts--;
				}
			}
			if(tracks == null) return;
			for (Track t : tracks) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
		      	tableItem.setText(0, t.getTitle());
		      	tableItem.setText(1, t.getArtists());
		      	tableItem.setText(2, t.getAlbum().getTitle());
		      	tableItem.setText(3, t.getAlbum().getYear());
			}
			progressBar.setSelection(100);
		}
		
	}
}
