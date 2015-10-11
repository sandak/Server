package GuiView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import presenter.Properties;

/**
 * This class represents the GUI view in the MVP.
 *
 * @author Guy Golan && Amit Sandak.
 */
public class MyObservableGuiView extends ObservableCommonGuiView {

	/** The main window of the view. */
	protected MazeWindow mainWindow;
	
	
	/**
	 * Instantiates a new observable GUI view.
	 *
	 * @param title the window title
	 * @param width the window width
	 * @param height the window height
	 */
	public MyObservableGuiView(String title, int width, int height) {
		super(new Properties());
		properties.setDefaults();
		mainWindow = new MazeWindow(title, width, height , properties);
		
		////////////////////////  the selection listener that sets the behavior of - clue request - in this specific MVP  ////////////
		mainWindow.setClueListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setChanged();
				notifyObservers("clue "+mainWindow.mazeProperties.getName()+" "+properties.getSolveAlgorithm());
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// nothing
				
			}
		});
		
	////////////////////////  the selection listener that sets the behavior of - solve request - in this specific MVP  ////////////
			mainWindow.setSolveListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setChanged();
				notifyObservers("solve "+mainWindow.mazeProperties.getName()+" "+properties.getSolveAlgorithm());
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// nothing
				
			}
		});
			
	////////////////////////  the selection listener that sets the behavior of - properties update request - in this specific MVP  ////////////
			mainWindow.setPropertiesUpdateListener(new SelectionListener() {
	
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					
					setChanged();
					notifyObservers("propertiesUpdate "+mainWindow.getSelectedXMLpropertiesFile());
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// nothing
		
		
				}
			});
////////////////////////  the key listener that sets the behavior of - movements requests - in this specific MVP  ////////////
		mainWindow.setKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// nothing
				
			}
			
			@Override
			public void keyPressed(KeyEvent key) {
				switch(key.keyCode)
				{
				case SWT.ARROW_UP:
					if (properties.isDebug() == true)
						System.out.println("up key pressed");
					setChanged();
					notifyObservers("movementRequest UP " + mainWindow.mazeProperties.name);
					break;
				case SWT.ARROW_DOWN:
					if (properties.isDebug() == true)
						System.out.println("down key pressed");
					setChanged();
					notifyObservers("movementRequest DOWN "+ mainWindow.mazeProperties.name);
					break;
				case SWT.ARROW_LEFT:
					if (properties.isDebug() == true)
						System.out.println("left key pressed");
					setChanged();
					notifyObservers("movementRequest LEFT "+ mainWindow.mazeProperties.name);
					break;
				case SWT.ARROW_RIGHT:
					if (properties.isDebug() == true)
						System.out.println("right key pressed");
					setChanged();
					notifyObservers("movementRequest RIGHT "+ mainWindow.mazeProperties.name);
					break;
				case SWT.PAGE_UP:
					if (properties.isDebug() == true)
						System.out.println("lvl up key pressed");
					setChanged();
					notifyObservers("movementRequest LVLUP "+ mainWindow.mazeProperties.name);
					break;
				case SWT.PAGE_DOWN:
					if (properties.isDebug() == true)
						System.out.println("lvl down key pressed");
					setChanged();
					notifyObservers("movementRequest LVLDOWN "+ mainWindow.mazeProperties.name);
					break;
				}
				
			}
		});
		
////////////////////////  the selection listener that sets the behavior of - exit request - in this specific MVP  ////////////
		mainWindow.setExitListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				exitRequest();
				
			}
		});
////////////////////////  the selection listener that sets the behavior of - generate new maze request - in this specific MVP  ////////////		
		mainWindow.setGenerateListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Display.getDefault().syncExec(new Runnable() {
				    public void run() {
				    	setChanged();
				    	notifyObservers("generate 3d maze "+ mainWindow.mazeProperties.getName()+" "+ mainWindow.mazeProperties.getxAxis()+" "+ mainWindow.mazeProperties.getyAxis()+" "+ mainWindow.mazeProperties.getzAxis());
				    }
				});
				    
			 }
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// nothing
				
			}
		});

////////////////////////  the selection listener that sets the behavior of - maze save request - in this specific MVP  ////////////
		mainWindow.setSaveListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setChanged();
				notifyObservers("save maze "+ mainWindow.mazeProperties.getName()+" " + mainWindow.getMazePath());
				
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// nothing
				
			}
		});
////////////////////////  the selection listener that sets the behavior of - maze load request - in this specific MVP  ////////////		
		mainWindow.setLoadListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setChanged();
				notifyObservers("load maze " + mainWindow.getMazePath()+ " " +mainWindow.mazeProperties.getName());
				
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// nothing
				
			}
		});
	}

	/* (non-Javadoc)
	 * @see view.View#display(java.lang.String[])
	 */
	@Override
	public void display(String[] strings) {
		// nothing

	}

	/* (non-Javadoc)
	 * @see view.View#display(java.lang.String)
	 */
	@Override
	public void display(String string) {
		mainWindow.display(string);

	}

	/* (non-Javadoc)
	 * @see view.View#exit()
	 */
	@Override
	public void exit() {
		mainWindow.exit();

	}

	/* (non-Javadoc)
	 * @see view.View#start()
	 */
	@Override
	public void start() {
		mainWindow.run();
	}

	/* (non-Javadoc)
	 * @see view.View#exitRequest()
	 */
	@Override
	public void exitRequest() {
		setChanged();
		notifyObservers("exit");
		
	}

	/* (non-Javadoc)
	 * @see view.View#display(algorithms.search.Solution)
	 */
	@Override
	public void display(Solution<Position> solution) {
		mainWindow.setSolution(solution);
		
	}
	
	/* (non-Javadoc)
	 * @see view.View#display(algorithms.mazeGenerators.Maze3d)
	 */
	@Override
	public void display(Maze3d maze) {
		mainWindow.setMazeData(maze);
		
	}

	/* (non-Javadoc)
	 * @see view.View#display(algorithms.mazeGenerators.Position)
	 */
	@Override
	public void display(Position charPosition) {
		mainWindow.setPositionData(charPosition);
		
	}

	/* (non-Javadoc)
	 * @see view.View#displayError(java.lang.String)
	 */
	@Override
	public void displayError(String string) {
		mainWindow.displayError(string);
		
	}

	/* (non-Javadoc)
	 * @see view.View#displayCrossSectionByX(int, java.lang.String)
	 */
	@Override
	public void displayCrossSectionByX(int parseInt, String string) {
		// nothing
		
	}

	/* (non-Javadoc)
	 * @see view.View#displayCrossSectionByY(int, java.lang.String)
	 */
	@Override
	public void displayCrossSectionByY(int parseInt, String string) {
		// nothing
		
	}

	/* (non-Javadoc)
	 * @see view.View#displayCrossSectionByZ(int, java.lang.String)
	 */
	@Override
	public void displayCrossSectionByZ(int parseInt, String string) {
		// nothing
		
	}

	/* (non-Javadoc)
	 * @see view.View#setProperties(presenter.Properties)
	 */
	@Override
	public void setProperties(presenter.Properties prop) {
		if(properties.getUi()!=null)
		if (!properties.getUi().equals(prop.getUi()))
		{
			setChanged();
			notifyObservers("switchUi switch");
		}
		else
			this.properties = prop;
		
		
	}

}
