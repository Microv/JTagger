package gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import metadata.Track;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.id3.ID3v22Frame;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v24Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.jaudiotagger.tag.id3.framebody.FrameBodyPIC;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TagWindow {
	
	protected Shell shell;
	private Text textTitle;
	private Text textComposer;
	private Text textLyrics;
	private Text textArtist;
	private Text textComment;
	private Text textAlbum;
	private Text textYear;
	private Text textGenre;
	private Text textPublisher;
	private Text textTrackNo2;
	private Text textTrackno1;
	
	private Display display;
	private Label lblCoverArt;
	private Label lblCoverProp;
	private Label lblBitRate;
	private Label lblEncoder;
	private Label lblFormat;
	private Label lblTrLength;
	private Label lblChannels;
	private Label lblSamplerate;
	private String selectedDir;
	private Text textDiscNo1;
	private Text textDiscNo2;
	private Table table;
	
	
	private HashMap<String, Track> fileExplorer;
	private Text textAlbumArtist;
	
	private Tag tag;
	private Track toSaveTrack;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TagWindow window = new TagWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		fileExplorer = new HashMap<String, Track>();
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		//shell.setImage(SWTResourceManager.getImage("../../JTagger/img/floppy13.png"));
		shell.setSize(1098, 647);
		shell.setText("SWT Application");
		FormLayout fl_shell = new FormLayout();
		shell.setLayout(fl_shell);
		
		Group group = new Group(shell, SWT.NONE);
		group.setText("Recording");
		FormData fd_group = new FormData();
		fd_group.left = new FormAttachment(0, 288);
		group.setLayoutData(fd_group);
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Title");
		label.setBounds(10, 28, 62, 14);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setText("Artist");
		label_1.setBounds(10, 63, 62, 14);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("Composer");
		label_2.setBounds(10, 100, 62, 14);
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setText("Comment");
		label_3.setBounds(10, 134, 62, 14);
		
		textTitle = new Text(group, SWT.BORDER);
		textTitle.setBounds(108, 20, 176, 22);
		
		textComposer = new Text(group, SWT.BORDER);
		textComposer.setBounds(108, 92, 176, 22);
		
		textArtist = new Text(group, SWT.BORDER);
		textArtist.setBounds(108, 55, 176, 22);
		
		textComment = new Text(group, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		textComment.setBounds(108, 134, 176, 70);
		
		Group group_1 = new Group(shell, SWT.NONE);
		fd_group.bottom = new FormAttachment(group_1, -6);
		group_1.setText("Release");
		FormData fd_group_1 = new FormData();
		fd_group_1.left = new FormAttachment(0, 288);
		fd_group_1.bottom = new FormAttachment(100, -26);
		fd_group_1.top = new FormAttachment(0, 303);
		group_1.setLayoutData(fd_group_1);
		
		Label label_4 = new Label(group_1, SWT.NONE);
		label_4.setText("Album");
		label_4.setBounds(10, 28, 62, 14);
		
		Label label_5 = new Label(group_1, SWT.NONE);
		label_5.setText("Year");
		label_5.setBounds(10, 104, 62, 14);
		
		Label label_6 = new Label(group_1, SWT.NONE);
		label_6.setText("Genre");
		label_6.setBounds(10, 135, 62, 14);
		
		Label label_7 = new Label(group_1, SWT.NONE);
		label_7.setText("Publisher");
		label_7.setBounds(10, 176, 62, 14);
		
		textAlbum = new Text(group_1, SWT.BORDER);
		textAlbum.setBounds(96, 20, 188, 22);
		
		textYear = new Text(group_1, SWT.BORDER);
		textYear.setBounds(96, 93, 188, 22);
		
		textGenre = new Text(group_1, SWT.BORDER);
		textGenre.setBounds(96, 127, 188, 22);
		
		textPublisher = new Text(group_1, SWT.BORDER);
		textPublisher.setBounds(96, 168, 188, 22);
		
		Label label_8 = new Label(group_1, SWT.NONE);
		label_8.setText("Track n°");
		label_8.setBounds(10, 204, 62, 14);
		
		textTrackNo2 = new Text(group_1, SWT.BORDER);
		textTrackNo2.setBounds(248, 196, 36, 22);
		
		textTrackno1 = new Text(group_1, SWT.BORDER);
		textTrackno1.setBounds(196, 196, 36, 22);
		
		Label label_9 = new Label(group_1, SWT.NONE);
		label_9.setText("/");
		label_9.setFont(SWTResourceManager.getFont("Sans", 11, SWT.NORMAL));
		label_9.setBounds(238, 196, 4, 22);
		
		
		
		Group grpCoverArt = new Group(shell, SWT.NONE);
		fd_group.top = new FormAttachment(grpCoverArt, 0, SWT.TOP);
		grpCoverArt.setText("Cover Art");
		FormData fd_grpCoverArt = new FormData();
		fd_grpCoverArt.top = new FormAttachment(0, 43);
		fd_grpCoverArt.right = new FormAttachment(100, -10);
		grpCoverArt.setLayoutData(fd_grpCoverArt);
		
		Button browseImageButton = new Button(grpCoverArt, SWT.NONE);
		browseImageButton.addListener(SWT.Selection, new OpenIMGListener());
		browseImageButton.setText("Add cover");
		browseImageButton.setBounds(137, 255, 75, 22);
		
		Button btnNext = new Button(shell, SWT.NONE);
		FormData fd_btnNext = new FormData();
		fd_btnNext.bottom = new FormAttachment(100, -26);
		fd_btnNext.right = new FormAttachment(100, -10);
		
		Label lblDiscN = new Label(group_1, SWT.NONE);
		lblDiscN.setBounds(10, 232, 62, 14);
		lblDiscN.setText("Disc n°");
		
		textDiscNo1 = new Text(group_1, SWT.BORDER);
		textDiscNo1.setBounds(196, 224, 36, 22);
		
		textDiscNo2 = new Text(group_1, SWT.BORDER);
		textDiscNo2.setBounds(248, 224, 36, 22);
		
		Label label_10 = new Label(group_1, SWT.NONE);
		label_10.setText("/");
		label_10.setFont(SWTResourceManager.getFont("Sans", 11, SWT.NORMAL));
		label_10.setBounds(238, 224, 4, 22);
		btnNext.setLayoutData(fd_btnNext);
		btnNext.setBounds(735, 426, 75, 18);
		btnNext.setText("Next ->");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		fd_btnNext.left = new FormAttachment(btnCancel, 6);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -92);
		fd_btnCancel.top = new FormAttachment(btnNext, 0, SWT.TOP);
		fd_btnCancel.bottom = new FormAttachment(100, -26);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setBounds(654, 426, 75, 18);
		btnCancel.setText("Cancel");
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmOpen = new MenuItem(menu_1, SWT.NONE);
		mntmOpen.addListener(SWT.Selection, new OpenFolderListener());
		mntmOpen.setText("Open");
		
		MenuItem mntmOpenFolder = new MenuItem(menu_1, SWT.NONE);
		mntmOpenFolder.addListener(SWT.Selection, new OpenListener());
		mntmOpenFolder.setText("Open Folder");
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.setText("Exit");
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_2 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_2);
		
		MenuItem mntmInfo = new MenuItem(menu_2, SWT.NONE);
		mntmInfo.setText("Info");
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		fd_group_1.right = new FormAttachment(tabFolder, -6);
		fd_group.right = new FormAttachment(tabFolder, -6);
		fd_btnCancel.left = new FormAttachment(tabFolder, 70);
		fd_grpCoverArt.left = new FormAttachment(tabFolder, 6);
		
		lblCoverArt = new Label(grpCoverArt, SWT.NONE);
		lblCoverArt.setBounds(10, 20, 202, 194);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(0, 43);
		fd_tabFolder.bottom = new FormAttachment(100, -26);
		fd_tabFolder.left = new FormAttachment(0, 591);
		fd_tabFolder.right = new FormAttachment(100, -238);
		tabFolder.setLayoutData(fd_tabFolder);
		
		TabItem tbpmLyrics = new TabItem(tabFolder, SWT.NONE);
		tbpmLyrics.setText("Lyrics");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbpmLyrics.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		textLyrics = new Text(scrolledComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		scrolledComposite.setContent(textLyrics);
		scrolledComposite.setMinSize(textLyrics.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		TabItem tbpmWikipedia = new TabItem(tabFolder, SWT.NONE);
		tbpmWikipedia.setText("Wikipedia");
		
		Browser browser = new Browser(tabFolder, SWT.NONE);
		tbpmWikipedia.setControl(browser);
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		
		Label lblAlbumArtist = new Label(group_1, SWT.NONE);
		lblAlbumArtist.setBounds(10, 65, 83, 14);
		lblAlbumArtist.setText("Album artist");
		
		textAlbumArtist = new Text(group_1, SWT.BORDER);
		textAlbumArtist.setBounds(96, 57, 188, 22);
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(group, 0, SWT.TOP);
		fd_table.right = new FormAttachment(group, -6);
		fd_table.left = new FormAttachment(0, 10);
		fd_table.bottom = new FormAttachment(100, -25);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnFileName = new TableColumn(table, SWT.NONE);
		tblclmnFileName.setWidth(212);
		tblclmnFileName.setText("File name");
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(502);
		tblclmnNewColumn.setText("Path");
		
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT);
		toolBar.setTouchEnabled(true);
		
		ToolItem toolItem_open = new ToolItem(toolBar, SWT.NONE);
		toolItem_open.setToolTipText("Open file");
		toolItem_open.setImage(SWTResourceManager.getImage(TagWindow.class, "/gui/img/open.png"));
		toolItem_open.addListener(SWT.Selection, new OpenListener());
		
		ToolItem toolItem_openfolder = new ToolItem(toolBar, SWT.NONE);
		toolItem_openfolder.setToolTipText("Open directory");
		toolItem_openfolder.setImage(SWTResourceManager.getImage(TagWindow.class, "/gui/img/openf.png"));
		toolItem_openfolder.addListener(SWT.Selection, new OpenFolderListener());
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem toolItem_search = new ToolItem(toolBar, SWT.NONE);
		toolItem_search.setToolTipText("Get informations");
		toolItem_search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(tag == null) {
					MessageBox msgBox = new MessageBox(shell);
					msgBox.setMessage("Please select an Mp3 file");
					msgBox.open();
				}
				else {
					QueryDialog dialog = new QueryDialog(new Shell(shell), SWT.TITLE);
					dialog.setTag(tag);
					dialog.open();
				}
			}
		});
		toolItem_search.setImage(SWTResourceManager.getImage(TagWindow.class, "/gui/img/search.png"));
		toolItem_search.setWidth(2);
		
		ToolItem toolItem_fp = new ToolItem(toolBar, SWT.NONE);
		toolItem_fp.setToolTipText("Search by fingerprint");
		toolItem_fp.setWidth(2);
		toolItem_fp.setImage(SWTResourceManager.getImage(TagWindow.class, "/gui/img/audio-headset.png"));
		toolItem_fp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(tag == null) {
					MessageBox msgBox = new MessageBox(shell);
					msgBox.setMessage("Please select an Mp3 file");
					msgBox.open();
				}
				else {
					FPDialog dialog = null;
					dialog = new FPDialog(new Shell(shell), SWT.TITLE);
					dialog.setPath(toSaveTrack.getPath());
					dialog.open();
					
				}
			}
		});
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem toolItem_save = new ToolItem(toolBar, SWT.NONE);
		toolItem_save.setToolTipText("Save");
		toolItem_save.setWidth(2);
		toolItem_save.setImage(SWTResourceManager.getImage(TagWindow.class, "/gui/img/save.png"));
		
		Group grpInfo = new Group(shell, SWT.NONE);
		fd_grpCoverArt.bottom = new FormAttachment(grpInfo, -6);
		
		lblCoverProp = new Label(grpCoverArt, SWT.NONE);
		lblCoverProp.setText("Dimensions");
		lblCoverProp.setBounds(10, 236, 202, 13);
		
		Label label_12 = new Label(grpCoverArt, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_12.setBounds(0, 220, 212, 2);
		fd_btnNext.top = new FormAttachment(grpInfo, 6);
		grpInfo.setText("Info");
		FormData fd_grpInfo = new FormData();
		fd_grpInfo.top = new FormAttachment(0, 345);
		fd_grpInfo.bottom = new FormAttachment(100, -62);
		fd_grpInfo.right = new FormAttachment(100, -10);
		fd_grpInfo.left = new FormAttachment(tabFolder, 6);
		grpInfo.setLayoutData(fd_grpInfo);
		
		lblBitRate = new Label(grpInfo, SWT.NONE);
		lblBitRate.setBounds(10, 25, 202, 13);
		lblBitRate.setText("Bit Rate");
		
		lblEncoder = new Label(grpInfo, SWT.NONE);
		lblEncoder.setBounds(10, 83, 202, 13);
		lblEncoder.setText("Encoder");
		
		lblTrLength = new Label(grpInfo, SWT.NONE);
		lblTrLength.setBounds(10, 171, 202, 13);
		lblTrLength.setText("Length");
		
		lblFormat = new Label(grpInfo, SWT.NONE);
		lblFormat.setBounds(10, 113, 202, 13);
		lblFormat.setText("Format");
		
		lblSamplerate = new Label(grpInfo, SWT.NONE);
		lblSamplerate.setBounds(10, 53, 202, 13);
		lblSamplerate.setText("Sample Rate");
		
		lblChannels = new Label(grpInfo, SWT.NONE);
		lblChannels.setBounds(10, 142, 58, 13);
		lblChannels.setText("Channels");
		
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
				Point pt = new Point(arg0.x, arg0.y);
				TableItem titem = table.getItem(pt);
				if(titem != null)
					getInfo(fileExplorer.get(titem.getText(1)+"/"+titem.getText(0)
								).getPath());
					
				toSaveTrack = new Track();
				toSaveTrack.setPath(titem.getText(1)+"/"+titem.getText(0));
			}
			
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	private void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
		      	Track t = new Track();
		      	t.setPath(folder.getAbsolutePath()+"/"+fileEntry.getName());
		      	String fileName = fileEntry.getName();
		      	fileExplorer.put(folder.getAbsolutePath()+"/"+fileName, t);
		      	TableItem tableItem = new TableItem(table, SWT.NONE);
		      	tableItem.setText(0, fileEntry.getName());
		      	tableItem.setText(1, folder.getAbsolutePath());
	        }
	    }
	}
	
	private void getInfo(String path) {
		File file = new File(path);
		AudioFile f = null;
		try {
			f = AudioFileIO.read(file);
		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tag = f.getTag();
		
		textTitle.setText(tag.getFirst(FieldKey.TITLE));
		textArtist.setText(tag.getFirst(FieldKey.ARTIST));
		textComposer.setText(tag.getFirst(FieldKey.COMPOSER));
		textComment.setText(tag.getFirst(FieldKey.COMMENT));
		textAlbum.setText(tag.getFirst(FieldKey.ALBUM));
		textAlbumArtist.setText(tag.getFirst(FieldKey.ALBUM_ARTIST));
		textYear.setText(tag.getFirst(FieldKey.YEAR));
		textGenre.setText(tag.getFirst(FieldKey.GENRE));
		textPublisher.setText(tag.getFirst(FieldKey.PRODUCER));
		textTrackno1.setText(tag.getFirst(FieldKey.TRACK));
		textTrackNo2.setText(tag.getFirst(FieldKey.TRACK_TOTAL));
		textDiscNo1.setText(tag.getFirst(FieldKey.DISC_NO));
		textDiscNo2.setText(tag.getFirst(FieldKey.DISC_TOTAL));
		textLyrics.setText(tag.getFirst(FieldKey.LYRICS));
		
		
		TagField coverArtField = tag.getFirstField(FieldKey.COVER_ART);
	
		BufferedImage bi = null;
		try {
			ImageInputStream iis = null;
			if(coverArtField instanceof ID3v22Frame) {
				FrameBodyPIC body = (FrameBodyPIC)((ID3v22Frame)coverArtField ).getBody();
				byte[] imageRawData = (byte[])body.getObjectValue(DataTypes.OBJ_PICTURE_DATA);
				iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageRawData));
			}
			else
				if(coverArtField instanceof ID3v23Frame) {
					FrameBodyAPIC body = (FrameBodyAPIC)((ID3v23Frame)coverArtField ).getBody();
					byte[] imageRawData = (byte[])body.getObjectValue(DataTypes.OBJ_PICTURE_DATA);
					iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageRawData));
				}
				else
					if(coverArtField instanceof ID3v24Frame) {
						FrameBodyAPIC body = (FrameBodyAPIC)((ID3v24Frame)coverArtField ).getBody();
						byte[] imageRawData = (byte[])body.getObjectValue(DataTypes.OBJ_PICTURE_DATA);
						iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageRawData));
					}
			bi = ImageIO.read(iis);
			lblCoverProp.setText("Dimensions\t"+bi.getWidth()+"x"+bi.getHeight());
		} catch(IllegalArgumentException e) {
			
			try {
				bi = ImageIO.read(getClass().getResource("img/nocover.png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			lblCoverProp.setText("Dimensions");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ImageData imgdata = makeSWTImage(display, bi).getImageData()
					.scaledTo(lblCoverArt.getBounds().width, lblCoverArt.getBounds().height);
			lblCoverArt.setImage(new Image(display, imgdata));
			lblCoverArt.pack();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lblBitRate.setText(   "Bit Rate\t\t"+f.getAudioHeader().getBitRateAsNumber()+" bps");
		lblSamplerate.setText("Sample Rate\t\t"+f.getAudioHeader().getSampleRate()+" Hz");
		lblFormat.setText(    "Format\t\t"+f.getAudioHeader().getFormat());
		lblTrLength.setText(  "Length\t\t"+getLength(f));
		lblEncoder.setText(   "Encoder\t\t"+f.getAudioHeader().getEncodingType());
		lblChannels.setText(  "Channels\t\t"+f.getAudioHeader().getChannels());
	}
	
	private String getLength(AudioFile f) {
		int seconds = f.getAudioHeader().getTrackLength();
		int min = (seconds % 3600) / 60, sec = seconds % 3600 % 60;
		
		return min + ":" + (sec < 10 ? "0" : "") + sec;
	}

	private Image makeSWTImage(Display display, java.awt.Image ai) throws Exception { 
		int width = ai.getWidth(null); 
		int height = ai.getHeight(null); 
		BufferedImage bufferedImage = 
				new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		Graphics2D g2d = bufferedImage.createGraphics(); 
		g2d.drawImage(ai, 0, 0, null); 
		g2d.dispose(); 
		int[] data = 
			((DataBufferInt)bufferedImage.getData().getDataBuffer()) 
			.getData(); 
		ImageData imageData = 
			new ImageData(width, height, 24, 
			new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)); 
			imageData.setPixels(0, 0, data.length, data, 0); 
		Image swtImage = new Image(display, imageData); 
		return swtImage; 
	} 
	
	class OpenFolderListener implements Listener {
		public void handleEvent(Event event) {
	        DirectoryDialog directoryDialog = new DirectoryDialog(shell);
	        
	        directoryDialog.setFilterPath(selectedDir);
	        directoryDialog.setMessage("Please select a directory and click OK");
	        
	        String dir = directoryDialog.open();
	        if(dir != null) {
	        	selectedDir = dir;
	        	File folder = new File(dir);
	          	listFilesForFolder(folder);
	        }
	      }
	}
	
	class OpenListener implements Listener {
		
		public void handleEvent(Event event) {
	        FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);

	        
	        fileDialog.setFilterExtensions(new String[]{"*.mp3", "*.*"});
	        fileDialog.setFilterNames(new String[]{"MP3 file", "Any"});
	        
	        String firstFile = fileDialog.open();

	        if(firstFile != null) {
	        	selectedDir = fileDialog.getFilterPath();
	        	String[] selectedFiles = fileDialog.getFileNames();
	          	for(int i=0; i<selectedFiles.length; i++) {	
	          		Track t = new Track();
			      	t.setPath(selectedDir+"/"+selectedFiles[i]);
			      	String fileName = selectedFiles[i];
			      	fileExplorer.put(selectedDir+"/"+fileName, t);
			      	TableItem tableItem = new TableItem(table, SWT.NONE);
			      	tableItem.setText(0, fileName);
			      	tableItem.setText(1, selectedDir);
	         }
	          
	        }
	      }
	}
	
	class OpenIMGListener implements Listener {
		
		public void handleEvent(Event event) {
	        FileDialog fileDialog = new FileDialog(shell, SWT.SINGLE);
	
	        
	        fileDialog.setFilterExtensions(new String[]{"*.png", "*.jpg", "*.bmp", "*.*"});
	        fileDialog.setFilterNames(new String[]{"PNG", "JPG", "BMP",  "ANY"});
	        
	        String path = fileDialog.open();
	
	        if(path != null) {
	        	//toSaveTag.getAlbum().setCover(path);
	          	File img = new File(path);
	    		BufferedImage bi = null;
				try {
					bi = ImageIO.read(img);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	    		lblCoverProp.setText("Dimensions\t"+bi.getWidth()+"x"+bi.getHeight());
	    		
	    		try {
	    			ImageData imgdata = makeSWTImage(display, bi).getImageData()
	    					.scaledTo(lblCoverArt.getBounds().width, lblCoverArt.getBounds().height);
	    			lblCoverArt.setImage(new Image(display, imgdata));
	    			lblCoverArt.pack();
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	         }
	          
	     }
     }
	
}
