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

	public Logger log;
	private ICC_ColorSpace ic;
	private ICC_Profile ip;
	private boolean[][] usable;
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
		usable = new boolean[rows][cols];
		for(i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++){
				if(grid.datagrid[i][j] == true)
					usable[i][j] = false;
				else
					usable[i][j] = true;
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
        		if(usable[loopr][loopc] == true){
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
	            		usable[loopr][loopc] = false;
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
		if(embedMathPuzzle(solution, rows, cols, random))
			puzzles++;
		
		solution.no_of_puzzles = puzzles;
		// TODO Auto-generated method stub
		return solution;
	}
	
	private boolean embedMathPuzzle(GridSolution solution, int rows, int cols, Random random){
		float L = (float)random.nextInt(50);
		float a = (float)random.nextInt(255) - 128.0f;
		float b = (float)random.nextInt(255) - 128.0f;
		Color tempc;
		
		float[] f = { L,a,b };
		float[] rgb = ic.toRGB(f);
		tempc = new Color( rgb[0], rgb[1], rgb[2] );
		
		//embed first number, 5
		int posx, posy;
		do{
			posx = random.nextInt(rows/2);
			posy = random.nextInt(cols/2);
		}while(checkAvailability(posx,posy, 5, rows, cols) == false);
		fillNumber(solution, posx, posy, 5, tempc);
		//embed + sign
		do{
			posx = random.nextInt(rows/3)+rows/3;
			posy = random.nextInt(cols/3)+cols/3;
		}while(checkAvailabilityPlus(posx, posy, rows, cols) == false);
		fillPlusSign(posx, posy, solution, tempc);
		//embed second number, 7
		do{
			posx = random.nextInt(rows/2)+rows/2;
			posy = random.nextInt(cols/2)+cols/2;
		}while(checkAvailability(posx,posy, 7, rows, cols) == false);
		fillNumber(solution, posx, posy, 7, tempc);
		return true;
	}
	
	//Checks if we can use enough cells starting on posx, posy for a number
	public boolean checkAvailability(int posx, int posy, int ncells, int rows, int cols){
		int cells = 0;
		for(int i = posx; i < posx+2 && cells < ncells; i++)
			for(int j = posy; j < posy+2 && cells < ncells; j++){
				if(i >= rows || j >= cols)
					return false;
				if(!usable[i][j])
					return false;
				else
					cells++;
			}
		
		return true;
	}
	//Checks if we can use enough cells centered in posx, posy for the + sign
	public boolean checkAvailabilityPlus(int posx, int posy, int rows, int cols){
		for(int j = posy-1; j < posy+2; j++){
			if(!usable[posx][j])
				return false;
		}
		for(int i = posx-1; i < posx+2; i++){
			if(!usable[i][posy])
				return false;
		}
		
		return true;
	}	
	
	//embed the + sign, centered in posx, posy
	public void fillPlusSign(int posx, int posy, GridSolution solution, Color color){
		for(int j = posy-1; j < posy+2; j++){
				solution.GridColors[posx][j] = color;
		}
		for(int i = posx-1; i < posx+2; i++){
				solution.GridColors[i][posy] = color;
		}

	}
	//embed one number starting on x, y
	public void fillNumber(GridSolution solution, int posx, int posy, int number, Color color){
		int cells = 0;
		for(int i = posx; i < posx+3 && cells < number; i++)
			for(int j = posy; j < posy+3 && cells < number; j++){
				solution.GridColors[i][j] = color;
				cells++;
			}
	}
	
}
