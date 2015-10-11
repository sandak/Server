package GuiView;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import abstracts.MazeDisplayer;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import algorithms.search.State;
import mainMaze.Maze3D;
import mazeCube.MazeCube;
import mazePossibleMoves.PossibleMoves;
import presenter.Properties;

/**
 * This the main window in the GUI view.
 * 
 *  @author Guy Golan && Amit Sandak.
 */
public class MazeWindow extends BasicWindow{
	
	/** The current maze data. */
	protected Maze3d maze;
	
	/** The  current position. */
	protected Position charPosition;
	
	/** The solution of the maze. */
	protected ArrayList<Position> solution;
	
	/** The selected XML properties file. */
	protected String selectedXMLpropertiesFile;
	
	/** the selection listener that sets the behavior of - exit request - in the MVP */
	protected DisposeListener exitListener;
	
	/** the selection listener that sets the behavior of - generate request - in the MVP */
	protected SelectionListener generateListener;
	
	/** the key listener that sets the behavior of - movements requests - in the MVP */
	protected KeyListener keyListener;
	
	/** the selection listener that sets the behavior of - solve request - in the MVP */
	protected SelectionListener solveListener;
	
	/** the selection listener that sets the behavior of - clue request - in the MVP */
	protected SelectionListener clueListener;
	
	/** the selection listener that sets the behavior of - update properties request - in the MVP */
	protected SelectionListener propertiesUpdateListener;
	
	/** the selection listener that sets the behavior of - save request - in the MVP */
	protected SelectionListener saveListener;
	
	/** the selection listener that sets the behavior of - load request - in the MVP */
	protected SelectionListener loadListener;
	
	/** The widgets list. */
	protected ArrayList<MazeDisplayer> widgetsList;
	
	/** The maze properties. */
	protected MazeProperties mazeProperties;
	
	/** The game properties. */
	protected Properties properties;
	
	/** The clue request button. */
	protected Button clueButton;
	
	/** The solve request button. */
	protected Button solveButton;
	
	/** The maze file path. used in save or load maze scenario */
	protected String mazePath;
	

	
	

	/**
	 * Instantiates a new maze window.
	 *
	 * @param title the window title
	 * @param width the window width
	 * @param height the window height
	 * @param properties the game properties
	 */
	public MazeWindow( String title, int width, int height , Properties properties) {
		super(title, width, height);
		this.properties= properties;
		this.mazeProperties=new MazeProperties();   //default values
		selectedXMLpropertiesFile = null;
		widgetsList = new ArrayList<MazeDisplayer>();
		shell.setImage(new Image(display, "resources/pacman.png"));
	}



	/* (non-Javadoc)
	 * @see GuiView.BasicWindow#initWidgets()
	 */
	@Override
	void initWidgets() {
		shell.addDisposeListener(exitListener);			//for X button and 'Exit' button
		shell.setLayout(new GridLayout(2,false));	
		Image image= new Image(display,"resources/background.jpg");
		shell.setBackgroundImage(image);
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		//shell.setCursor(new Cursor(shell.getDisplay(), new ImageData("resources/Cursor_Greylight.png").scaledTo(27, 25), 16, 0));
	
		
		Menu menuBar = new Menu(shell, SWT.BAR);			//the main window menu bar.
		MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);		//menu bar header button.
		fileMenuHeader.setText("&File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		MenuItem fileOpenPropItem = new MenuItem(fileMenu, SWT.PUSH);   //button used to load new properties during runtime.
		fileOpenPropItem.setText("Open properties file");
		fileOpenPropItem.addSelectionListener(new SelectionListener() {
				
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog fd=new FileDialog(shell,SWT.OPEN);			//opening a new file dialog widget.
				fd.setText("open");
				String[] filterExt = { "*.xml" };
				fd.setFilterExtensions(filterExt);
				selectedXMLpropertiesFile = fd.open();
				if(selectedXMLpropertiesFile!=null)
					propertiesUpdateListener.widgetSelected(arg0);
				
			}
				
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
        //button for editing an existing properties.
		MenuItem fileEditPropItem = new MenuItem(fileMenu, SWT.PUSH);		
		fileEditPropItem.setText("Edit properties");
		fileEditPropItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				PropertiesWindow window = new PropertiesWindow(shell);
				selectedXMLpropertiesFile = window.open();
				if(selectedXMLpropertiesFile!=null)
					propertiesUpdateListener.widgetSelected(arg0);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// do nothing
				
			}
		});
		
		
		//button for safe exit.    
		MenuItem fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("Exit");										
		fileExitItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();		//activates the DisposeListener.						
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				//  do nothing
				
			}
		});
		
		//window menu Maze header.
		MenuItem MazeMenuHeader = new MenuItem(menuBar, SWT.CASCADE);		
		MazeMenuHeader.setText("Maze");

		Menu MazeMenu = new Menu(shell, SWT.DROP_DOWN);						
		MazeMenuHeader.setMenu(MazeMenu);

		MenuItem mazePropItem = new MenuItem(MazeMenu, SWT.PUSH);			
		mazePropItem.setText("Maze properties");
		mazePropItem.addSelectionListener(new SelectionListener() {
				
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Shell mazeProp = new Shell(shell);
				mazeProp.setText("Maze Properties");
				mazeProp.setSize(130,120);
				mazeProp.setLayout(new GridLayout(6, false));
				Label xTitle = new Label(mazeProp, SWT.COLOR_WIDGET_DARK_SHADOW);
				xTitle.setText("Dimensions: ");
				xTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1));	
				Label xxTitle = new Label(mazeProp, SWT.COLOR_WIDGET_DARK_SHADOW);
				xxTitle.setText("X");
				Text xTextBox =	new Text(mazeProp, SWT.BORDER);
				xTextBox.setText("  ");
				Label yyTitle = new Label(mazeProp, SWT.COLOR_WIDGET_DARK_SHADOW);
				yyTitle.setText("Y");
				Text yTextBox =	new Text(mazeProp, SWT.BORDER);
				yTextBox.setText("  ");
				Label ZZTitle = new Label(mazeProp, SWT.COLOR_WIDGET_DARK_SHADOW);
				ZZTitle.setText("Z");
				Text zTextBox =	new Text(mazeProp, SWT.BORDER);
				zTextBox.setText("  ");
				Button saveButton =  new Button(mazeProp, SWT.PUSH);
				saveButton.setText(" Save ");
				saveButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 6, 1));	
					
				mazeProp.open();
			}
				
			@Override				
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			
		});
		
		// save maze button
		MenuItem mazeSave = new MenuItem(MazeMenu, SWT.PUSH);			
		mazeSave.setText("Save maze");
		mazeSave.addSelectionListener(new SelectionListener() {
			
			

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog fd = new FileDialog(shell,SWT.SAVE);
				mazePath = fd.open();
				if(mazePath!=null)
					saveListener.widgetSelected(arg0);
				else
					displayError("Save canceled.");
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// do nothing
				
			}
		});
		
		//load maze button
		MenuItem mazeLoad= new MenuItem(MazeMenu, SWT.PUSH);			
		mazeLoad.setText("Load maze");
		
		mazeLoad.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog fd = new FileDialog(shell,SWT.OPEN);
				mazePath = fd.open();
				if(mazePath!=null)
					loadListener.widgetSelected(arg0);				
				else
					displayError("Load canceled.");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// do nothing
				
			}
		});
		    
		shell.setMenuBar(menuBar);
        
		Button generateButton=new Button(shell, SWT.PUSH);
		generateButton.setText("  Generate new maze  ");
		generateButton.setLayoutData(new GridData(SWT.NONE, SWT.None, false, false, 1, 1));
		generateButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				MazePropertiesWindow propwindow = new MazePropertiesWindow(shell,mazeProperties,generateListener);
		    	propwindow.open();
			}
				
			@Override				
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			
		});
		
		//Main Maze display widget.
		MazeDisplayer mazeWidget=new Maze3D(shell, SWT.NULL);
		widgetsList.add(mazeWidget);
		mazeWidget.setFocus();
		mazeWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true,1,5));
		
		
		clueButton=new Button(shell, SWT.PUSH);
		clueButton.setText("        give a clue         ");
		clueButton.setLayoutData(new GridData(SWT.None, SWT.None, false, false, 1, 1));
		clueButton.setEnabled(false);
		clueButton.addSelectionListener(clueListener);
		

		solveButton=new Button(shell, SWT.PUSH);
		solveButton.setText("     Solve the maze     ");
		solveButton.setLayoutData(new GridData(SWT.None, SWT.None, false, false, 1, 1));
		solveButton.setEnabled(false);
		solveButton.addSelectionListener(solveListener);
		
		// cube widget.
		MazeCube mazeCube = new MazeCube(shell, SWT.BORDER);
		mazeCube.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		widgetsList.add(mazeCube);
		
		// possibleMoves widget.
		MazeDisplayer possibleMoves=new PossibleMoves(shell,SWT.BORDER);
		possibleMoves.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, true, 1, 1));
		widgetsList.add(possibleMoves);
		
		for (MazeDisplayer mazeDisplayer : widgetsList) {
			mazeDisplayer.addKeyListener(keyListener);
		}
		
	}
	
	/**
	 * the Exit request behavior.
	 */
	protected void exitRequest() {
		shell.dispose();

	}
	
	/**
	 * Widgets refresh all the relevant data and redraw.
	 */
	public void widgetsRefresh()
	{
		for (MazeDisplayer canvasWidget : widgetsList) {
			if(maze!=null)
				canvasWidget.setMazeData(maze);
			if(charPosition!=null)
				canvasWidget.setCharPosition(charPosition);
			
			canvasWidget.setSolution(solution);
		}
	}
	
	/**
	 * Sets the character position data.
	 *
	 * @param charPosition the new character position data
	 */
	public void setPositionData(Position charPosition) {
		this.charPosition = charPosition;
		widgetsRefresh();
		
	}
	
	/**
	 * Sets the maze data.
	 *
	 * @param maze the new maze data
	 */
	public void setMazeData(Maze3d maze){
		this.maze = maze;
		this.solution = new ArrayList<Position>(); //reset the solution map
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	solveButton.setEnabled(true);
		    	clueButton.setEnabled(true);
		    }
		});
		
		widgetsRefresh();
	
	}
	
	/**
	 * Sets the solution data.
	 *
	 * @param solution the new solution
	 */
	public void setSolution(Solution<Position> solution) {
		ArrayList<Position> arr= new ArrayList<Position>();
    	for ( State<Position> s: solution.getArr()) {
			arr.add(s.getState());
		}
		this.solution= arr;
		widgetsRefresh();
		
	}
	
	/**
	 * Display error.
	 *
	 * @param string the message
	 */
	public void displayError(String string) {
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageBox errorBox =  new MessageBox(shell, SWT.ICON_ERROR); 
				errorBox.setMessage(string);
				errorBox.setText("Error");
				errorBox.open();				
			}
		});
	}
	
	/**
	 * Display a string.
	 *
	 * @param string the string to display
	 */
	public void display(String string) {
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
		    public void run() {
		    	MessageBox messageBox =  new MessageBox(shell, SWT.ICON_INFORMATION); 
		    	messageBox.setMessage(string);
		    	messageBox.setText("information message");
		    	messageBox.open();		
		    	
		    }
		});
	}
	

	/**
	 * Sets the selection listener that sets the behavior of - generate request - in the MVP.
	 *
	 * @param generateListener the new generate listener
	 */
	public void setGenerateListener(SelectionListener generateListener) {
		this.generateListener = generateListener;
	}
	
	/**
	 * Sets the key listener that sets the behavior of - movements requests - in the MVP.
	 *
	 * @param keyListener the new key listener
	 */
	public void setKeyListener(KeyListener keyListener) {
		this.keyListener = keyListener;
		
	}
	
	/**
	 * Sets the selection listener that sets the behavior of - solve request - in the MVP.
	 *
	 * @param selectionListener the new solve listener
	 */
	public void setSolveListener(SelectionListener selectionListener) {
		this.solveListener = selectionListener;
		
	}

	/**
	 * Sets the selection listener that sets the behavior of - exit request - in the MVP.
	 *
	 * @param exitListener the new exit listener
	 */
	public void setExitListener(DisposeListener exitListener) {
		this.exitListener = exitListener;
	}



	/**
	 * Sets the selection listener that sets the behavior of - clue request - in the MVP.
	 *
	 * @param selectionListener the new clue listener
	 */
	public void setClueListener(SelectionListener selectionListener) {
		this.clueListener = selectionListener;
		
	}



	/**
	 * Sets the selection listener that sets the behavior of - update properties request - in the MVP.
	 *
	 * @param selectionListener the new properties update listener
	 */
	public void setPropertiesUpdateListener(SelectionListener selectionListener) {
		this.propertiesUpdateListener = selectionListener;
		
	}



	/**
	 * Gets the selected XML properties file.
	 *
	 * @return the selected XML properties file
	 */
	public String getSelectedXMLpropertiesFile() {
		return selectedXMLpropertiesFile;
		
	}



	/**
	 * Sets the selection listener that sets the behavior of - save maze request - in the MVP.
	 *
	 * @param selectionListener the new save listener
	 */
	public void setSaveListener(SelectionListener selectionListener) {
		this.saveListener= selectionListener;
		
	}



	/**
	 * Gets the maze file path.
	 *
	 * @return the maze path
	 */
	public String getMazePath() {
		return this.mazePath;
	}



	/**
	 * Sets the selection listener that sets the behavior of - load maze request - in the MVP.
	 *
	 * @param selectionListener the new load listener
	 */
	public void setLoadListener(SelectionListener selectionListener) {
		this.loadListener = selectionListener;
		
	}
}