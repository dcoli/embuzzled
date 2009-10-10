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

            		// 0 <= L* <= 100
            		// -128 <= a*,b* <= 127

            		float L = (float)random.nextInt(50) + 50f;
            		float a = (float)random.nextInt(255) - 128.0f;
            		float b = (float)random.nextInt(255) - 128.0f;
            		
            		float[] f = { L,a,b };
            		float[] rgb = ic.toRGB(f);
            		tempc = new Color( rgb[0], rgb[1], rgb[2] );
            		for (float fi : rgb) {
            			log.debug(fi);
            		}
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
