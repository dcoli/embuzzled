package embuzzled.g4;

import java.awt.Color;

import embuzzled.ui.Grid;
import embuzzled.ui.GridSolution;
import embuzzled.ui.Player;

public class TestPlayer implements Player{

	@Override
	public GridSolution move(Grid grid) {
		int rows = grid.rows;
		int cols = grid.cols;
		String solutionKey = "The number of white columns follows an arithmetic succession with common difference = 1";
		GridSolution solution = new GridSolution(rows, cols, 1, solutionKey);
		
		int whiteCols = 1;
		int whitePainted = 0;
		Color tempc;
		for(int loopc=0;loopc<cols;loopc++)
        {
            for(int loopr=0;loopr<rows;loopr++)
            {
            	
            	if(whitePainted < whiteCols){
            		tempc = new Color(255,255,255);
            	}
            	else{
            		tempc = new Color(0,0,0);
            	}
                solution.GridColors[loopr][loopc] = tempc;

            }
            whitePainted++;
            if(whitePainted > whiteCols){
            	whitePainted = 0;
            	whiteCols++;
            }

        }
		// TODO Auto-generated method stub
		return solution;
	}

}
