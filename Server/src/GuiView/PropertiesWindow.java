package GuiView;


import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import presenter.Properties;

/**
 * This class collecting all the data of the current game in a GUI window.
 * 
 *  @author Guy Golan && Amit Sandak.
 */
public class PropertiesWindow {

	/** The main window shell. */
	protected Shell main;
	
	/** The game properties. */
	protected Properties properties;
	
	/** The new XML properties file path. */
	protected String XMLpath;
	
	/**
	 * Instantiates a new properties window.
	 *
	 * @param parent the parent shell
	 */
	public PropertiesWindow(Shell parent) {
		main = new Shell(parent);
		this.properties = new Properties();
		properties.setDefaults();

		main.setText("Game Properties");
		main.setSize(200, 250);
		main.setLayout(new GridLayout(6, true));
		
		Label uiTitle = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		uiTitle.setText("UI: ");
		uiTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));
		Combo uiBox = new Combo(main, SWT.DROP_DOWN);
		uiBox.setItems(new String[] { "Command line", "Graphic user interface" });
		if (properties.getUi().equals("Command line"))
			uiBox.select(1);
		else
			uiBox.select(0);

		uiBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));

		uiTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));

		
		Label dimensionsTitle = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		dimensionsTitle.setText("Maximum dimensions value: ");
		dimensionsTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));

		Label xTitle = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		xTitle.setText(" X");

		Text xTextBox = new Text(main, SWT.BORDER);
		xTextBox.setText("" + properties.getMazeMaxAxisX());
		xTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		xTextBox.setToolTipText("numbers only, greater than 3");
		xTextBox.setTextLimit(2);
		xTextBox.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				
				
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				xTextBox.setText("");
				
			}
		});

		Label yTitle = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		yTitle.setText("Y");

		Text yTextBox = new Text(main, SWT.BORDER);
		yTextBox.setText("" + properties.getMazeMaxAxisY());
		yTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		yTextBox.setToolTipText("numbers only, greater than 3");
		yTextBox.setTextLimit(2);
		yTextBox.addFocusListener(new FocusListener() {
					
					@Override
					public void focusLost(FocusEvent arg0) {
						
						
					}
					
					@Override
					public void focusGained(FocusEvent arg0) {
						yTextBox.setText("");
						
					}
				});
		
		Label zTitle = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		zTitle.setText("Z");

		Text zTextBox = new Text(main, SWT.BORDER);
		zTextBox.setText("" + properties.getMazeMaxAxisZ());
		zTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		zTextBox.setToolTipText("numbers only, greater than 3");
		zTextBox.setTextLimit(2);
		zTextBox.addFocusListener(new FocusListener() {
					
					@Override
					public void focusLost(FocusEvent arg0) {
						
						
					}
					
					@Override
					public void focusGained(FocusEvent arg0) {
						zTextBox.setText("");
						
					}
				});
		
		Label generateTitle = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		generateTitle.setText("Generate Algorithm: ");
		generateTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));
		Combo generateBox = new Combo(main, SWT.DROP_DOWN);
		generateBox.setItems(new String[] { "SimpleGenerator", "MyMaze3dGenerator" });
		if (properties.getGenerateAlgorithm().equals("MyMaze3dGenerator"))
			generateBox.select(1);
		else
			generateBox.select(0);

		generateBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));

		generateTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));

		Label searchTitle = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		searchTitle.setText("Solving Algorithm: ");
		searchTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));
		Combo searchBox = new Combo(main, SWT.DROP_DOWN);
		searchBox.setItems(new String[] { "BFS", "A* manhattan", "A* air distance" });
		switch (properties.getSolveAlgorithm()) {
		case ("BFS"):
			searchBox.select(0);
			break;
		case ("AstarManhattan"):
			searchBox.select(1);
			break;
		case ("AstarAirDistance"):
			searchBox.select(2);
		}
		searchTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));

		searchBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));

		
		
		Button debugMode = new Button(main, SWT.CHECK);
		debugMode.setText("debug mode");
		
		
		Label threadsNum = new Label(main, SWT.COLOR_WIDGET_DARK_SHADOW);
		threadsNum.setText("Thread num:");

		Text threadsNumBox = new Text(main, SWT.BORDER);
		threadsNumBox.setText("" + properties.getMaxThreads());
		threadsNumBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		threadsNumBox.setToolTipText("positive numbers only");
		threadsNumBox.setTextLimit(3);
		threadsNumBox.addFocusListener(new FocusListener() {
					
					@Override
					public void focusLost(FocusEvent arg0) {
						
						
					}
					
					@Override
					public void focusGained(FocusEvent arg0) {
						threadsNumBox.setText("");
						
					}
				});
		
		
		Button saveButton = new Button(main, SWT.PUSH);
		saveButton.setText(" Save ");
		saveButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));
		debugMode.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 6, 1));
		
	
		
		ModifyListener checkAxisData = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				try {
					if (Integer.parseInt(((Text) arg0.getSource()).getText()) < 3)
						saveButton.setEnabled(false);
					else
						saveButton.setEnabled(true);
				} catch (NumberFormatException e) {
					saveButton.setEnabled(false);
				}
			}
		};

		xTextBox.addModifyListener(checkAxisData);
		yTextBox.addModifyListener(checkAxisData);
		zTextBox.addModifyListener(checkAxisData);
		threadsNumBox.addModifyListener(checkAxisData);

		////////////Save sequence/////////////////////////////////////////////////////////////////////////////
		saveButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				try {
					properties.setMazeMaxAxisX(Integer.parseInt(xTextBox.getText()));
					properties.setMazeMaxAxisY(Integer.parseInt(yTextBox.getText()));
					properties.setMazeMaxAxisZ(Integer.parseInt(zTextBox.getText()));
					properties.setMaxThreads(Integer.parseInt(threadsNumBox.getText()));
					properties.setDebug(debugMode.getSelection());
					properties.setGenerateAlgorithm(generateBox.getText());
					properties.setUi(uiBox.getText());
					switch (searchBox.getText()) {
					case ("BFS"):
						properties.setSolveAlgorithm("BFS");
					break;
					case ("A* manhattan"):
						properties.setSolveAlgorithm("AstarManhattan");	
					break;
					case ("A* air distance"):
						properties.setSolveAlgorithm("AstarAirDistance");	
					break;
					default:
						properties.setSolveAlgorithm("AstarAirDistance");
					}

					FileDialog fd = new FileDialog(main, SWT.SAVE);
					
					
					try {
						FileOutputStream out = new FileOutputStream(fd.open());
						XMLpath = fd.getFileName();
						XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out));
							
							
						encoder.writeObject(properties);
						encoder.flush();
						encoder.close();
						main.dispose();
					} catch (FileNotFoundException e) {
						MessageBox err = new MessageBox(main, SWT.ICON_ERROR);
						err.setText("Error ");
						err.setMessage(e.getStackTrace().toString());
						err.open();
						
					}
					
				} catch (NumberFormatException e) {
					MessageBox err = new MessageBox(main, SWT.ICON_ERROR);
					err.setText("Error ");
					err.setMessage("Invalid parameters");
					err.open();
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// nothing

			}
		});
	
		main.pack();
	}

	/**
	 * Open the form window.
	 *
	 * @return the new XML properties file path 
	 */
	public String open() {
		main.open();
		while (!main.isDisposed()) {
		    if (!main.getDisplay().readAndDispatch()) {
		    	main.getDisplay().sleep();
		    }
		}
		return XMLpath;

	}

	/**
	 * Gets the new XML properties file path.
	 *
	 * @return the new XML properties file path
	 */
	public String getXMLpath() {
		return XMLpath;
		
	}

}
