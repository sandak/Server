package boot;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import model.MazeDB;

public class HibernateTest {

	public static void main(String[] args) {
		
		
		    //creating configuration object  
		    Configuration cfg=new Configuration();  
		    cfg.configure("hibernate.cfg.xml");//populates the data of the configuration file  
		      
		    //creating seession factory object  
		    SessionFactory factory=cfg.buildSessionFactory();  
		      
		    //creating session object  
		    Session session=factory.openSession();  
		      
		    //creating transaction object  
		    Transaction t=session.beginTransaction();  
		          
		    MazeDB mazedb=new MazeDB();  
		    mazedb.setMaze(new Maze3d(11, 11, 11));
		    mazedb.setName("test");
		    mazedb.setSolution(new Solution<Position>());
		    session.persist(mazedb);//persisting the object  
		      
		    t.commit();//transaction is commited  
		    session.close();  
		      
		    System.out.println("successfully saved");  
		      
		
		
//		
//		AnnotationConfiguration config;
//		config = new AnnotationConfiguration();
//		config.addAnnotatedClass(MazeDB.class);
//		config.configure();
//		new SchemaExport(config).create(true, true);
//		
//		
//		
//		 Configuration configuration=new Configuration();  
//		  configuration.configure();  
//		  ServiceRegistry sr= new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();  
//		  SessionFactory sf=configuration.buildSessionFactory(sr);  

		
		
		
//		SessionFactory factory = new Configuration().configure().buildSessionFactory();
//		Session session = factory.openSession();
//		session.beginTransaction();
		
//		
////		Configuration cfg = new Configuration();
////		 
////		 SessionFactory sf;
////		 sf = cfg.configure("hibernate.cfg.xml").buildSessionFactory();
////
//	//	 Session session = sf.openSession();
//	        // begin transaction
//		// session.beginTransaction();
//		 session.saveOrUpdate(prop);

	}

}
