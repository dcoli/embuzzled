package embuzzled.g4;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ColorSpace.*;
import java.io.IOException;
import java.util.Random;

import embuzzled.g4.Logger.LogLevel;
import embuzzled.ui.*;



public class TestPlayer implements Player{

	private enum state{FREE,BLOCKED, USED, RESERVED};
	public Logger log;
	private ICC_ColorSpace ic;
	private ICC_Profile ip;
	private state[][] usable;
	private int puzzles;
	
	@Override
	public GridSolution move(Grid grid) {
		
		log = new Logger(LogLevel.DEBUG,this.getClass());
		
//		float[] f = new float[3]; //{ 50f, 30f, 40f };
		long seed = 13;
		Random random = new Random(seed);
		
		int i=0;
		try {
			ip = ICC_Profile.getInstance("embuzzled/g4/lab.icm");
		} catch(IOException e) {
			log.debug("index "+(i++) + ": " + e.toString());
		}
		ic = new ICC_ColorSpace(ip);
		
		log.debug("colorspace: "+ip.getColorSpaceType());
		
		int rows = grid.rows;
		int cols = grid.cols;
		usable = new state[rows][cols];
		for(i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++){
				if(grid.datagrid[i][j] == true)
					usable[i][j] = state.BLOCKED;
				else
					usable[i][j] = state.FREE;
			}

		puzzles = 0;
		String solutionKey = "The number of columns before the black one follows an arithmetic succession with common difference = 1";
		GridSolution solution = new GridSolution(rows, cols, 1, solutionKey);
		
		int whiteCols = 1;
		int whitePainted = 0;
		Color tempc;
		for(int loopc=0;loopc<cols;loopc++)
        {
    		float L = (float)random.nextInt(50) + 50f;
    		float a = (float)random.nextInt(255) - 128.0f;
    		float b = (float)random.nextInt(255) - 128.0f;
    		
            for(int loopr=0;loopr<rows;loopr++)
            {
            	//Check if we can use the cell
        		if(usable[loopr][loopc] == state.FREE){
	            	if(whitePainted < whiteCols){
	
	            		// 0 <= L* <= 100
	            		// -128 <= a*,b* <= 127
	            		float[] f = { L,a,b };
	            		float[] rgb = ic.toRGB(f);
	            		tempc = new Color( rgb[0], rgb[1], rgb[2] );
	            		for (float fi : rgb) {
	            			log.debug(fi);
	            		}
	                    solution.GridColors[loopr][loopc] = tempc;
	            	}
	            	else{
	            		tempc = new Color(0,0,0);
	            		//We're not marking the cell as USED because it's possible to overlap puzzles with this one
	                    solution.GridColors[loopr][loopc] = tempc;
	            	}
        		}

            }
            whitePainted++;
            if(whitePainted > whiteCols){
            	whitePainted = 0;
            	whiteCols++;
            }

        }
		puzzles++;
		
		//embed two numbers and the + sign
		if(embedMathPuzzle(solution, rows, cols, random, 5, 7))
			puzzles++;
		if(embedMathPuzzle(solution, rows, cols, random, 2, 8))
			puzzles++;
		
		solution.setNo_of_puzzles(puzzles);
		// TODO Auto-generated method stub
		return solution;
	}
	
	/*
	 * Embed two numbers and mathematical signs to hint the user to add them
	 * @param solution GridSolution object where we embed the puzzle
	 * @param rows Number of rows in solution
	 * @param cols Number of cols in solution
	 * @param random Random number generator used by our Player
	 * @return True if the puzzle was embedded. False otherwise.
	 */
	private boolean embedMathPuzzle(GridSolution solution, int rows, int cols, Random random, int first, int second){
		//Decide color for this puzzle
		float L = (float)random.nextInt(50);
		float a = (float)random.nextInt(255) - 128.0f;
		float b = (float)random.nextInt(255) - 128.0f;
		Color tempc;
		
		float[] f = { L,a,b };
		float[] rgb = ic.toRGB(f);
		tempc = new Color( rgb[0], rgb[1], rgb[2] );
		
		int posx, posy, posx2, posy2, posx3, posy3, posx4, posy4, tries;
		
		//Find a place for the first number
		tries = 0;
		do{
			posx = random.nextInt(rows/2);
			posy = random.nextInt(cols/2);
			tries++;
		}while(checkAvailability(posx,posy, first, rows, cols) == false && tries < 50);
		if(tries == 50){ //There is no space for it, free reserved cells and return false
			freeReserved(rows, cols);
			return false;
		}

		//Try to fit the + sign
		tries = 0;
		do{
			posx2 = random.nextInt(rows/3)+rows/3;
			posy2 = random.nextInt(cols/3)+cols/3;
			tries++;
		}while(checkAvailabilityPlus(posx2, posy2, rows, cols) == false && tries < 50);
		if(tries == 50){
			freeReserved(rows, cols);
			return false;
		}

		//Try to fit the second number
		tries = 0;
		do{
			posx3 = random.nextInt(rows/2)+rows/2;
			posy3 = random.nextInt(cols/2)+cols/2;
			tries++;
		}while(checkAvailability(posx3,posy3, second, rows, cols) == false && tries < 50);
		if(tries == 50){
			freeReserved(rows, cols);
			return false;
		}
		
		//Try to fit the = sign
		tries = 0;
		do{
			posx4 = random.nextInt(rows/2)+rows/2;
			posy4 = random.nextInt(cols/2)+cols/2;
			tries++;
		}while(checkAvailabilityEquals(posx4, posy4, rows, cols) == false && tries < 50);
		if(tries == 50){
			freeReserved(rows, cols);
			return false;
		}
		
		//Embed all parts of the puzzle
		fillNumber(solution, posx, posy, first, tempc);
		fillPlusSign(solution, posx2, posy2, tempc);		
		fillNumber(solution, posx3, posy3, second, tempc);
		fillEqualsSign(solution, posx4, posy4, tempc);
		return true;
	}
	
	//Checks if we can use enough cells starting on posx, posy for a number
	public boolean checkAvailability(int posx, int posy, int ncells, int rows, int cols){
		int cells = 0;
		for(int i = posx; i < posx+2 && cells < ncells; i++)
			for(int j = posy; j < posy+2 && cells < ncells; j++){
				if(i >= rows || j >= cols)
					return false;
				if(usable[i][j] != state.FREE)
					return false;
				else{
					cells++;
					usable[i][j] = state.RESERVED;
				}
			}
		
		return true;
	}
	//Checks if we can use enough cells centered in posx, posy for the + sign
	public boolean checkAvailabilityPlus(int posx, int posy, int rows, int cols){
		for(int j = posy-1; j < posy+2; j+=2){
			if(j >= cols)
				return false;
			if(usable[posx][j] != state.FREE)
				return false;
			else
				usable[posx][j] = state.RESERVED;
		}
		for(int i = posx-1; i < posx+2; i++){
			if(i >= rows)
				return false;
			if(usable[i][posy] != state.FREE)
				return false;
			else
				usable[i][posy] = state.RESERVED;
		}
		
		return true;
	}	
	
	//Checks if we can use enough cells (in posx, posx+2 rows, from posy-1 to posy+1) for the = sign
	public boolean checkAvailabilityEquals(int posx, int posy, int rows, int cols){		
		for(int j = posy-1; j < posy+2; j++){
			if(j >= cols)
				return false;
			if(usable[posx][j] != state.FREE)
				return false;
			else
				usable[posx][j] = state.RESERVED;
		}
		if(posx+2 >= rows)
			return false;
		for(int j = posy-1; j < posy+2; j++){
			if(j >= cols)
				return false;
			if(usable[posx+2][j] != state.FREE)
				return false;
			else
				usable[posx+2][j] = state.RESERVED;
		}
		
		return true;
	}	
	
	//embed the + sign, centered in posx, posy
	public void fillPlusSign(GridSolution solution, int posx, int posy, Color color){
		for(int j = posy-1; j < posy+2; j++){
				solution.GridColors[posx][j] = color;
				usable[posx][j] = state.USED;
		}
		for(int i = posx-1; i < posx+2; i++){
				solution.GridColors[i][posy] = color;
				usable[i][posy] = state.USED;
		}

	}
	
	//embed = sign
	public boolean fillEqualsSign(GridSolution solution, int posx, int posy, Color tempc){		
		for(int j = posy-1; j < posy+2; j++){
			solution.GridColors[posx][j] = tempc;
			usable[posx][j] = state.USED;
		}
		for(int j = posy-1; j < posy+2; j++){
			solution.GridColors[posx+2][j] = tempc;
			usable[posx+2][j] = state.USED;
		}
		
		return true;
	}
		
	//embed one number starting on x, y
	public void fillNumber(GridSolution solution, int posx, int posy, int number, Color color){
		int cells = 0;
		for(int i = posx; i < posx+3 && cells < number; i++)
			for(int j = posy; j < posy+3 && cells < number; j++){
				solution.GridColors[i][j] = color;
				usable[i][j] = state.USED;
				cells++;
			}
	}
	
	//Free reserved cells (called when one puzzle couldn't complete its embedding)
	private void freeReserved(int rows, int cols){
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				if(usable[i][j] == state.RESERVED)
					usable[i][j] = state.FREE;
	}
	
}
