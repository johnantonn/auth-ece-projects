package utilities;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import online.Controller;

/**
 * @description REST application class
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

@ApplicationPath("/api")
public class JAXRSPublisher extends Application{

	public JAXRSPublisher(){}
	
	@Override
    public Set<Class<?>> getClasses(){
        HashSet<Class<?>> SetOfClasses = new HashSet<Class<?>>();
        SetOfClasses.add(Controller.class);
        SetOfClasses.add(CORSResponseFilter.class);
        return SetOfClasses;
    }

    @Override
    public Set<Object> getSingletons(){
        return new HashSet<Object>();
    }
}
