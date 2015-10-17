package boot;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


 
 /**
  * BasicWindow represents what every window with event driven GUI should implement.
  *
  */
public abstract class BasicWindow implements Runnable{
	
	protected Display display;
	protected Shell shell;

	
 	public BasicWindow( String title, int width,int height) {		//CTOR
 		display=new Display();
 		shell  = new Shell(display);
 		shell.setSize(width,height);
 		shell.setText(title);
 		
	}

 	/**
 	 * Initializing each window's widgets.
 	 */
 	abstract void initWidgets();			//every window will implement its own widgets.

	@Override
	public void run() {
		initWidgets();
		shell.open();
		// main event loop
		 while(!shell.isDisposed()){ // while window isn't closed

		    // 1. read events, put then in a queue.
		    // 2. dispatch the assigned listener
		    if(!display.readAndDispatch()){ 	// if the queue is empty
		       display.sleep(); 			// sleep until an event occurs 
		    }

		 } // shell is disposed

		 display.dispose(); // dispose OS components
		 
	}
	
	/**
	 * Used to dispose the window manually. 
	 */
	public void exit()
	{
		shell.dispose(); 
	}
	
}
