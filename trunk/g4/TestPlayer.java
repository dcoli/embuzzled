package embuzzled.g4;

import java.awt.Color;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ColorSpace.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Random;

import embuzzled.g4.Logger.LogLevel;
import embuzzled.ui.*;

/******************************************
 * To change random seed go to line 130
 *
 *****************************************/


public class TestPlayer implements Player{

        private enum state{FREE,BLOCKED, USED, RESERVED};
        public Logger log;
        private ICC_ColorSpace ic;
        private ICC_Profile ip;
        private state[][] usable;
        private int puzzles;
        
        private class ReturnValue{
        	public Point startPoint;
        	public String shape;
        	public String sol;
        	
        	public void ReturnValue(){
        		startPoint = new Point(-1, -1);
        		shape = "";
        		sol = "";
        	}
        }

        private state[][] eye = {
                { state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.USED, state.USED, state.FREE, state.FREE, state.FREE, state.FREE },
                { state.FREE, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.USED, state.FREE, state.FREE },
                { state.FREE, state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.FREE, state.USED, state.FREE },
                { state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.USED },
                { state.FREE, state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.FREE, state.USED, state.FREE },
                { state.FREE, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.USED, state.FREE, state.FREE },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.USED, state.USED, state.FREE, state.FREE, state.FREE, state.FREE },
        };

        private state[][] tieFighter = {
                        { state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED },
                        { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE },
                        { state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.USED },
                        { state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.FREE, state.USED },
                        { state.USED, state.USED, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.USED, state.USED },
                        { state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.FREE, state.USED },
                        { state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.USED },
                        { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE },
                        { state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED },
        };
        
        private state[][] pi = {
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.USED, },
                { state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.USED, },
                { state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.USED, },
                { state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.USED, },
                { state.USED, state.FREE, state.USED, state.USED, state.USED, state.USED, state.USED, },
        };

        private state[][] vaderFighter = {
                        { state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE },
                        { state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.FREE },
                        { state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.USED },
                        { state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.FREE, state.USED },
                        { state.USED, state.USED, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.USED, state.USED },
                        { state.USED, state.FREE, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.FREE, state.USED },
                        { state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.USED },
                        { state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.FREE },
                        { state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE },
        };
        
        private state[][] xWingFighter = {
                { state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.USED },
                { state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED, state.USED, state.FREE, state.USED, state.USED, state.FREE },
                { state.FREE, state.FREE, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED, state.FREE, state.FREE, state.FREE },
                { state.FREE, state.FREE, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED, state.FREE, state.FREE, state.FREE },
                { state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED, state.USED, state.FREE, state.USED, state.USED, state.FREE },
                { state.USED, state.FREE, state.FREE, state.FREE, state.USED, state.USED, state.USED, state.FREE, state.FREE, state.FREE, state.USED },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE },
                { state.FREE, state.FREE, state.FREE, state.FREE, state.FREE, state.USED, state.FREE, state.FREE, state.FREE, state.FREE, state.FREE },
};
        
        private state[][] sos = {
                { state.USED, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED,state. FREE, state.USED, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.USED },
                { state.USED, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED,state. FREE, state.USED, state.USED, state.FREE, state.USED, state.FREE, state.USED, state.FREE, state.USED }
        };
        
        private state[][] fibo = {
        		{ state.USED, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED, state.USED, state.FREE, state.USED, state.USED,state. USED, state.USED, state.USED, },
                { state.USED, state.FREE, state.USED, state.USED, state.FREE, state.USED, state.USED, state.USED, state.FREE, state.USED, state.USED,state. USED, state.USED, state.USED, }
        };

        private state[][] transposePuzzle ( state[][] puzzle ) {
                state[][] temp = new state[puzzle[0].length][puzzle.length];
                for ( int i=0; i<puzzle.length; i++ ) {
                        for ( int j=0; j<puzzle[0].length; j++ ) {
                                temp[j][i] = puzzle[i][j];
                        }
                }
                return temp;
        }
        
        @Override
        public GridSolution move(Grid grid) {
                
                log = new Logger(LogLevel.WARN,this.getClass());
                
//              float[] f = new float[3]; //{ 50f, 30f, 40f };
                
        /********************************************************************/
                long seed = 13;
        /********************************************************************/
                Random random = new Random(seed);
                
                ReturnValue ret;
                int i=0;
                try {
                        //ip = ICC_Profile.getInstance("embuzzled/g4/lab.icm");
                        ip = ICC_Profile.getInstance("embuzzled/g4/AppleRGB.icc");
                } catch(IOException e) {
                        log.debug("index "+(i++) + ": " + e.toString());
                }
                ic = new ICC_ColorSpace(ip);
                
                //log.debug("colorspace: "+ip.getColorSpaceType());
                
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
                String solutionKey = "";
                GridSolution solution = new GridSolution(rows, cols, 1, "");
                
//              float[] labLines = getLABColor(20,70,random);
//              float[] rgbLines = ic.toRGB(labLines);
                
                if ( rows < 80 || cols < 80 ) {
                	for ( i=0; i < sos[1].length; i++) {
                		sos[1][i] = state.FREE;
                	}
                	for ( i=0; i < fibo[1].length; i++) {
                		fibo[1][i] = state.FREE;
                	}
                }
                
                
                embedArithmeticLines( solution, random, rows, cols );
                puzzles++;
                
                //embed two numbers and the + sign
                ret = embedMathPuzzle(solution, rows, cols, random, 5, 7, solutionKey);
                if(ret != null){ 
                	puzzles++;
                	solutionKey += ret.sol;
                	solutionKey += "\n";
                }
                else 
                	log.warn("math 1 can't fit");
                ret = embedMathPuzzle(solution, rows, cols, random, 3, 6, solutionKey); 
                if(ret != null){
                	puzzles++;
                	solutionKey += ret.sol;
                	solutionKey += "\n";
                }
                else 
                	log.warn("math 2 can't fit");
                
                ret = embedPuzzle( solution, tieFighter, rows, cols, random );
                if(ret.startPoint.x != -1){
                	puzzles++;
                	solutionKey+="There is a Tie Fighter (from Star Wars) starting in "+ret.startPoint.x+","+ret.startPoint.y+"\n";
                	solutionKey+= ret.shape;
                	solutionKey += "\n";
                }
                else 
                	log.warn("tiefighter can't fit");
                ret = embedPuzzle( solution, vaderFighter, rows, cols, random );
                if(ret.startPoint.x != -1){
                	puzzles++;
                	solutionKey+="There is a Darth Vader Tie Fighter (from Star Wars) starting in "+ret.startPoint.x+","+ret.startPoint.y+"\n";
                	solutionKey+= ret.shape;
                	solutionKey += "\n";
                }
                else 
                	log.warn("vaderfighter can't fit");
                ret = embedPuzzle( solution, xWingFighter, rows, cols, random );
                if(ret.startPoint.x != -1){
                	puzzles++;
                	solutionKey+="There is an X-Wing (from Star Wars) starting in "+ret.startPoint.x+","+ret.startPoint.y+"\n";
                	solutionKey+= ret.shape;
                	solutionKey += "\n";
                }
                else 
                	log.warn("xWingfighter can't fit");
                ret = embedPuzzle( solution, eye, rows, cols, random );
                if(ret.startPoint.x != -1){
                	puzzles++;
                	solutionKey+="There is an eye starting in "+ret.startPoint.x+","+ret.startPoint.y+"\n";
                	solutionKey+= ret.shape;
                	solutionKey += "\n";
                }
                else 
                	log.warn("eye can't fit");
                ret = embedPuzzle( solution, sos, rows, cols, random );
                if(ret.startPoint.x != -1){
                	puzzles++;
                	solutionKey+="There is a SOS code starting in "+ret.startPoint.x+","+ret.startPoint.y+"\n";
                	solutionKey+= ret.shape;
                	solutionKey += "\n";
                }
                else 
                	log.warn("sos can't fit");
                ret = embedPuzzle( solution, fibo, rows, cols, random );
                if(ret.startPoint.x != -1){
                	puzzles++;
                	solutionKey+="There is a Fibonacci sequence starting in "+ret.startPoint.x+","+ret.startPoint.y+"\n";
                	solutionKey+= ret.shape;
                	solutionKey += "\n";
                }
                else 
                	log.warn("fibo can't fit");
                ret = embedPuzzle( solution, pi, rows, cols, random );
                if(ret.startPoint.x != -1){
                	puzzles++;
                	solutionKey+="There is a pi representation starting in "+ret.startPoint.x+","+ret.startPoint.y+"\n";
                	solutionKey+= ret.shape;
                	solutionKey += "\n";
                }
                else 
                	log.warn("pi can't fit");
                
                solution.setSolutionKey(solutionKey);
                solution.setNo_of_puzzles(puzzles);
                return solution;
        }
        
        private void embedArithmeticLines( GridSolution solution, Random random, int rows, int cols ) {
                float[] rgbLines = getRGB( random );
                //the below can be used to determine if bordering colors differ enough in lab quantities.
        //float[] labLines = rgbToLab( rgbLines );
        int whiteCols = 1;
                int whitePainted = 0;
                Color tempc;
                for(int loopc=0;loopc<cols;loopc++)
        {

                for(int loopr=0;loopr<rows;loopr++)
            {
                        //float[] lab = getLABColor( 100,0,random );
                        float[] rgb = getRGB( random );//ic.toRGB(lab);
                        //float[] labLines = rgbToLab( rgbLines );
                        tempc = new Color(rgb[0],rgb[1],rgb[2]);
                
                //Check if we can use the cell
                        if(usable[loopr][loopc] == state.FREE){
	                        if(whitePainted < whiteCols){
	        
	                                // 0 <= L* <= 100
	                                // -128 <= a*,b* <= 127
	                            solution.GridColors[loopr][loopc] = tempc;
	                        }
	                        else{
	                                tempc = new Color(rgbLines[0],rgbLines[1],rgbLines[2]);
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
        }

        private ReturnValue embedPuzzle( GridSolution solution, state[][] puzzle, int rows, int cols,
                        Random random) {
        	ReturnValue ret = new ReturnValue();
    		ret.startPoint = foundSpaceForPuzzle( puzzle, rows, cols, random );
            if ( ret.startPoint.x != -1 ) {
                    float[] rgb = getRGB( random );//ic.toRGB(lab);         
                    Color tempc = new Color(rgb[0],rgb[1],rgb[2]);
                    ret.shape = setPuzzle( solution, ret.startPoint, puzzle, tempc );
                    fixColors( solution, ret.startPoint, puzzle, tempc, rows, cols, random );
            }
            return ret;
        }

        private void fixColors(GridSolution solution, Point start,
                        state[][] puzzle, Color tempc, int rows, int cols, Random random) {
                int leftSide, topSide, rightSide, bottomSide;
                for ( int g = 0; g < puzzle.length; g++ ) {
                        for ( int h = 0; h < puzzle[0].length; h++ ) {
                                if ( puzzle[g][h] == state.USED ) {
                                //if ( g == 0 ) {
                                        if ( start.x == 0 && g == 0)
                                                leftSide = g;
                                        else
                                                leftSide = start.x + g - 1;
                                        
                                //} else leftSide = start.x - 1;
                                //if ( h == 0 ) {
                                        if ( start.y == 0 && h == 0) 
                                                topSide = h;
                                        else 
                                                topSide = start.y + h - 1;
                                        
                                //} else topSide = start.y + h - 1;
                                        if ( start.x + g < rows - 1 ) 
                                        	rightSide = start.x + g + 1;
                                        else 
                                        	rightSide = start.x + g;
                                        if ( start.y + h < cols - 1 ) 
                                        	bottomSide = start.y + h + 1;
                                        else 
                                        	bottomSide = start.y + h;
                                        // the "sides" have all been adjusted to absolute coordinates within the solution
                                        for ( int i = leftSide; i <= rightSide; i++ ) {
                                                for ( int j = topSide; j <= bottomSide; j++ ) {
                                                        if ( usable[i][j] == state.FREE) {
                                                                Color testColor = solution.GridColors[i][j];
//                                                              log.debug("position:"+i+","+j);
//                                                              log.debug( "testColor blue value: "+testColor.getBlue() );
                                                                while ( colorsAreSimilar( testColor, tempc ) ) {
                                                                        float[] rgb = getRGB( random );
                                                                        testColor = new Color( rgb[0], rgb[1], rgb[2] );
                                                                }
                                                                solution.GridColors[i][j] = testColor;
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }

        private boolean colorsAreSimilar(Color testColor, Color puzzleColor) {
                //log.debug( "testColor blue value: "+testColor.getBlue() );
                float[] testf = {
		                (float)testColor.getRed() / 255.0f,
		                (float)testColor.getGreen() / 255.0f,
		                (float)testColor.getBlue() / 255.0f
                };
                log.debug("testf "+testf[0]+","+testf[1]+","+testf[2]);
                float[] puzzlef = {
                		(float)puzzleColor.getRed() / 255.0f,
                		(float)puzzleColor.getGreen() / 255.0f,
                		(float)puzzleColor.getBlue() / 255.0f
                };
                log.debug("puzzlef "+puzzlef[0]+","+puzzlef[1]+","+puzzlef[2]);
                float[] testLAB = rgbToLab(testf);
                float[] puzzleLAB = rgbToLab(puzzlef);
//                log.debug("testLAB[0] "+testLAB[0]);
//                log.debug("Math.abs( testLAB[0] - puzzleLAB[0] ) "+Math.abs( testLAB[0] - puzzleLAB[0] ) );
//                log.debug("Math.abs( testLAB[1] - puzzleLAB[1] ) "+Math.abs( testLAB[1] - puzzleLAB[1] ) );
//                log.debug("Math.abs( testLAB[2] - puzzleLAB[2] ) "+Math.abs( testLAB[2] - puzzleLAB[2] ) );
                return Math.abs( testLAB[0] - puzzleLAB[0] ) < 30 
                        && Math.abs( testLAB[1] - puzzleLAB[1] ) < 20 
                        && Math.abs( testLAB[2] - puzzleLAB[2] ) < 20;
        }

        private String setPuzzle( GridSolution solution, Point start, state[][] puzzle, Color color ) {
        	String p = new String();
        	for ( int i = 0; i < puzzle.length; i++ ) {
                        for ( int j = 0; j < puzzle[0].length; j++ ) {
                			p += puzzle[i][j]==state.USED? " o ":"   ";
                                if ( puzzle[i][j] == state.USED ) {
                                        solution.GridColors[start.x + i][start.y + j] = color;
                                        //solution.GridColors[start.x + i][start.y + j] = color;
                                        usable[start.x + i][start.y + j] = state.USED;
                                }
                        }
                		p+="\n";
                }
        	p+="\n";
        	return p;
        }

        private Point foundSpaceForPuzzle(state[][] puzzle, int rows, int cols,
                        Random random) {
                Point start = new Point();
                boolean found = false;
                int tries = 0;
                while (!found && tries<200) {
                	tries++;
                    start.x = random.nextInt( rows-1 );
                    start.y = random.nextInt( cols-1 );
                    if ( start.x + puzzle.length < rows-1 && start.y + puzzle[0].length < cols-1 ) {
                        found = true;
                        for (int i = 0; i < puzzle.length; i++) {
                            for (int j = 0; j < puzzle[0].length; j++) {
                                if ( puzzle[i][j] == state.USED && usable[start.x + i][start.y + j] != state.FREE) {
                                        found = false;
                                }
                            }
                        }
                    }
                }
                if ( tries == 200 ) {
                        start.x = -1;
                        start.y = -1;
                } 
                return start;
        }

        private float[] rgbToLab(float[] rgbLines) {
                
                float var_R = rgbLines[0];
                float var_G = rgbLines[1];
                float var_B = rgbLines[2];

                if ( var_R > 0.04045 ) var_R = (float) java.lang.Math.pow( ( ( var_R + 0.055 ) / 1.055 ), 2.4 );
                else                   var_R = var_R / 12.92f;
                if ( var_G > 0.04045 ) var_G = (float) java.lang.Math.pow( ( ( var_G + 0.055 ) / 1.055 ) , 2.4 );
                else                   var_G = var_G / 12.92f;
                if ( var_B > 0.04045 ) var_B = (float) java.lang.Math.pow( ( ( var_B + 0.055 ) / 1.055 ) , 2.4 );
                else                   var_B = var_B / 12.92f;

                var_R = var_R * 100;
                var_G = var_G * 100;
                var_B = var_B * 100;

                //Observer. = 2Á, Illuminant = D65
                float X = var_R * 0.4124f + var_G * 0.3576f + var_B * 0.1805f;
                float Y = var_R * 0.2126f + var_G * 0.7152f + var_B * 0.0722f;
                float Z = var_R * 0.0193f + var_G * 0.1192f + var_B * 0.9505f;
                
                float L = (float) (10 * java.lang.Math.pow( Y,0.5f ));
                float a = (float) (17.5 * ( ( ( 1.02 * X ) - Y ) / java.lang.Math.pow( Y,.5f ) ));
                float b = (float) (7 * ( ( Y - ( 0.847 * Z ) ) / java.lang.Math.pow( Y,.5f ) ));
                float[] f = { L,a,b }; 
                return f;
        }

        private float[] getRGB(Random random) {
                float[] rgb = { 0,0,0 };
        rgb[0] = random.nextFloat() * .5f + .45f;
        rgb[1] = random.nextFloat() * .5f + .45f;
        rgb[2] = random.nextFloat() * .5f + .45f;
        //log.debug(rgb[0]+','+rgb[1]+','+rgb[2]);
                return rgb;
        }

        private float[] getLABColor(int i, int j, Random random) {
        float L = (float)random.nextInt(i) + j;
        float a = (float)random.nextInt(255) - 128.0f;
        float b = (float)random.nextInt(255) - 128.0f;
//              float[] rgb = ic.toRGB(f);
                float[] f = { L,a,b };

                return f;
        }

        /*
         * Embed two numbers and mathematical signs to hint the user to add them
         * @param solution GridSolution object where we embed the puzzle
         * @param rows Number of rows in solution
         * @param cols Number of cols in solution
         * @param random Random number generator used by our Player
         * @return True if the puzzle was embedded. False otherwise.
         */
        private ReturnValue embedMathPuzzle(GridSolution solution, int rows, int cols, Random random, int first, int second, String key){
                //Decide color for this puzzle
//                float L = (float)random.nextInt(20)+30;
//                float a = (float)random.nextInt(255) - 128.0f;
//                float b = (float)random.nextInt(255) - 128.0f;
                Color tempc;
//                
//                float[] f = { L,a,b };
//                float[] rgb = ic.toRGB(f);
        		float[] rgb = getRGB( random );
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
                        return null;
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
                        return null;
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
                        return null;
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
                        return null;
                }
                
                ReturnValue ret = new ReturnValue();
                //Embed all parts of the puzzle
                fillNumber(solution, posx, posy, first, tempc);
                fillPlusSign(solution, posx2, posy2, tempc);            
                fillNumber(solution, posx3, posy3, second, tempc);
                fillEqualsSign(solution, posx4, posy4, tempc);
                ret.sol = "";
                ret.sol+=("There is a mathematic puzzle with:\n");
                ret.sol+=("Two numbers in "+posx+","+posy+" and "+posx3+","+posy3+"\n");
                ret.sol+=("One + sign in "+posx2+","+posy2+"\n");
                ret.sol+=("And one = sign in "+posx4+","+posy4+"\n");
                ret.sol+=("The solution is "+(first+second)+" (after adding the number of blocks in each number)\n");
                return ret;
        }
        
        //Checks if we can use enough cells starting on posx, posy for a number
        public boolean checkAvailability(int posx, int posy, int ncells, int rows, int cols){
                int cells = 0;
                for(int i = posx; i < posx+3 && cells < ncells; i++)
                        for(int j = posy; j < posy+3 && cells < ncells; j++){
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