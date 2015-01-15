package gui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;

public class InfoDialog extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public InfoDialog(Shell parent, int style) {
		super(parent, style);
		setText("Info");
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
		shell.setSize(391, 293);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Label lblJtagger = new Label(shell, SWT.NONE);
		lblJtagger.setAlignment(SWT.CENTER);
		lblJtagger.setFont(SWTResourceManager.getFont("Sans", 15, SWT.BOLD | SWT.ITALIC));
		FormData fd_lblJtagger = new FormData();
		fd_lblJtagger.left = new FormAttachment(0, 85);
		fd_lblJtagger.right = new FormAttachment(100, -89);
		lblJtagger.setLayoutData(fd_lblJtagger);
		lblJtagger.setText("JTagger 1.0");
		
		Label lblViewAndEdit = new Label(shell, SWT.WRAP);
		fd_lblJtagger.bottom = new FormAttachment(100, -73);
		lblViewAndEdit.setAlignment(SWT.CENTER);
		FormData fd_lblViewAndEdit = new FormData();
		fd_lblViewAndEdit.top = new FormAttachment(lblJtagger, 6);
		fd_lblViewAndEdit.left = new FormAttachment(lblJtagger, 10, SWT.LEFT);
		fd_lblViewAndEdit.right = new FormAttachment(100, -94);
		lblViewAndEdit.setLayoutData(fd_lblViewAndEdit);
		lblViewAndEdit.setText("View and edit tag audio files. \nSearch informations using the net.");
		
		Label lblAlessandroStrinoGiovanni = new Label(shell, SWT.NONE);
		fd_lblViewAndEdit.bottom = new FormAttachment(100, -29);
		FormData fd_lblAlessandroStrinoGiovanni = new FormData();
		fd_lblAlessandroStrinoGiovanni.top = new FormAttachment(lblViewAndEdit, 6);
		fd_lblAlessandroStrinoGiovanni.left = new FormAttachment(0, 47);
		lblAlessandroStrinoGiovanni.setLayoutData(fd_lblAlessandroStrinoGiovanni);
		lblAlessandroStrinoGiovanni.setText("Alessandro Strino, Giovanni Festa, Michele Roviello");
		
		Label label = new Label(shell, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(100, -259);
		fd_label.right = new FormAttachment(100, -130);
		label.setLayoutData(fd_label);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		fd_lblJtagger.top = new FormAttachment(lblNewLabel, 6);
		lblNewLabel.setImage(SWTResourceManager.getImage(InfoDialog.class, "/gui/img/JTagger.png"));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(label, 6);
		fd_lblNewLabel.bottom = new FormAttachment(100, -117);
		fd_lblNewLabel.right = new FormAttachment(label, 0, SWT.RIGHT);
		fd_lblNewLabel.left = new FormAttachment(0, 120);
		lblNewLabel.setLayoutData(fd_lblNewLabel);

	}
}
