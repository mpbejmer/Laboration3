package orig2011.v5;

import java.beans.PropertyChangeListener;

//Kolla OH F�rel�sning 10 f�r info om Observer-klassen
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
