package orig2011.v7;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import orig2011.v7.ReversiModel.Turn;

public class ReversiScoreView implements PropertyChangeListener{

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int whiteScore;
		int blackScore;
		boolean isWhiteTurn=((ReversiModel) evt.getSource()).getTurnColor()==Turn.WHITE;
		String turn;
		if(evt.getSource().getClass() == ReversiModel.class){
			if("play".equals(evt.getPropertyName())){
				whiteScore=((ReversiModel) evt.getSource()).getWhiteScore();
				blackScore=((ReversiModel) evt.getSource()).getBlackScore();
				
				turn=(isWhiteTurn?"White":"Black");
				System.out.println("White Score: "+ whiteScore+" Black Score: "+blackScore+" It is "+turn+"'s turn.");
			}
			
		}
		
		
		
		
	}
	
	

}
