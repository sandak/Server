package boot;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import controller.Properties;

public class PropertiesXMLCreator {

	public static void main(String[] args) {
		Properties prop = new Properties();
		prop.setDefaults();
		
		try {
			FileOutputStream in = new FileOutputStream("properties.xml");		//tries to read the properties.xml default pathname.
			XMLEncoder decoder = new XMLEncoder(in);
					//decoding the xml file.
			decoder.writeObject(prop);
					decoder.flush();
					decoder.close();
		} catch (FileNotFoundException e) {				//if no properties.xml was found in directory, generating default properties.
			System.out.println("file not found, default properties will be loaded");
			prop = new Properties();
			prop.setDefaults();
		}

	}

}
