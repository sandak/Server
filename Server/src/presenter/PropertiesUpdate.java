package presenter;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Defines what the Command PropertiesUpdate should do.
 * @author Guy Golan & Amit Sandak.
 *
 */
public class PropertiesUpdate extends CommonCommand {

	public PropertiesUpdate(Presenter presenter) {
		super(presenter);
		
	}

		@Override
		public void doCommand(String param) {
			String s[] = param.split(" ");
			if(s.length > 0)
			{
				Properties prop ;
				try {
					FileInputStream in = new FileInputStream(s[0]);						//trying to decode an xml file for properties.
					XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
					prop = (Properties)decoder.readObject();
					decoder.close();
			
				} catch (FileNotFoundException e) {
					if(presenter.getProperties().isDebug())
						System.out.println("file not found, default properties will be loaded");
					prop = new Properties();
					prop.setDefaults();
				}
				presenter.setProperties(prop);
			
			}
			else
			{
				presenter.getView().displayError("Missing parameters.");
			}
			
		}
}


	