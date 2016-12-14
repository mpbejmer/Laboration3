package orig2011.v6;

import java.beans.PropertyChangeListener;

//Kolla OH Föreläsning 10 för info om Observer-klassen
public interface IObservable {
	
	
	/**
	 * 
	 * @param observer
	 */
	void addObserver(PropertyChangeListener observer);
	
	
	/**
	 * 
	 * @param observer
	 */
	void removeObserver(PropertyChangeListener observer);


}
