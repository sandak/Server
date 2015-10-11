package GuiView;


import presenter.Properties;
import view.ObservableCommonView;

/**
 * This abstract class that represents a generic GUI view in the MVP.
 * 
 *  @author Guy Golan && Amit Sandak.
 */
public abstract class ObservableCommonGuiView extends ObservableCommonView {
	
	/** The properties of the game. */
	protected Properties properties;

	/**
	 * Instantiates a new observable common GUI view.
	 *
	 * @param properties the game properties
	 */
	public ObservableCommonGuiView(Properties properties) {
		super();
		this.properties = properties;
	}
	
	/**
	 * Sets the debug mode.
	 *
	 * @param debug the new debug mode
	 */
	public void setDebugMode(boolean debug) {
		properties.setDebug(debug);
		
	}
	
	


}
