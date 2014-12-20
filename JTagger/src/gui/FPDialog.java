package gui;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import metadata.Album;
import metadata.Track;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jaudiotagger.tag.Tag;
import org.json.JSONException;
import org.xml.sax.SAXException;

import wrappers.AcoustIDWrapper;
import wrappers.MusicBrainzWrapper;

public class FPDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Table table;
	private String absolutePath;
	private AcoustIDWrapper aiw;
	private MusicBrainzWrapper mbw;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 */
	public FPDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		try {
			aiw = new AcoustIDWrapper();
			mbw = new MusicBrainzWrapper();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		fd_grpResults.top = new FormAttachment(100, -306);
		fd_grpResults.right = new FormAttachment(0, 492);
		fd_grpResults.bottom = new FormAttachment(100, -10);
		fd_grpResults.left = new FormAttachment(0, 10);
		grpResults.setLayoutData(fd_grpResults);
		
		table = new Table(grpResults, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 20, 462, 266);
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
		
		fillTable();
	}
	
	private void fillTable() {
		
		try {
			aiw.genAudioFingerPrintInfo(absolutePath, 120);
			
			ArrayList<String> ids = aiw.getMusicBrainzID();
			for(String id : ids) {
				Track t = mbw.getTrackById(id);
				if(t != null) {
					TableItem tableItem = new TableItem(table, SWT.NONE);
			      	tableItem.setText(0, t.getTitle());
			      	tableItem.setText(1, t.getArtists());
			      	tableItem.setText(2, t.getAlbum().getTitle());
			      	tableItem.setText(3, t.getAlbum().getYear());

				}
			}
				
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
		    messageBox.setMessage("Connection error, please retry");
		    messageBox.open();
		}
	}

	public void setPath(String path) {
		absolutePath = "\""+path+"\"";
	}
}
