package boot;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import controller.Properties;

public class HibernateTest {

	public static void main(String[] args) {
		Properties prop = new Properties();
		prop.setDefaults();
		
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		Session session = factory.openSession();
		session.beginTransaction();
		
//		
//		Configuration cfg = new Configuration();
//		 
//		 SessionFactory sf;
//		 sf = cfg.configure("hibernate.cfg.xml").buildSessionFactory();
//
	//	 Session session = sf.openSession();
	        // begin transaction
		// session.beginTransaction();
		 session.saveOrUpdate(prop);

	}

}
