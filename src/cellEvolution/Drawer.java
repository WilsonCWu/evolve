
package cellEvolution;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;

public class Drawer extends JFrame{
    int width, height;
    int gridSize;
    Color tempColor;
    JPanel drawingPanel;

    
    public Drawer(JPanel JP){
        this.drawingPanel = JP;
        this.width = drawingPanel.getWidth();
        this.height = drawingPanel.getHeight();
    }
    
    public void startUp(){
        //preps up window for first run
        this.setSize (width,height);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBackground(Color.black);
    }
    //paints graph of herbivores vs pred depending on array data provided
    public void paintCellGraph(ArrayList <Integer> herbArray, ArrayList <Integer> predArray){
        int heightMultiplier = 0;
        Graphics brush = drawingPanel.getGraphics();
        double drawWidth = (double)width/(double)(herbArray.size()); //two (double) needed to prevent a rounding error
        int barSizeHerbOld = 800;
        int barSizePredOld = 800;
        int barSizeHerbNew, barSizePredNew;
        
        //finds highest point 
        for (int i = 0; i < herbArray.size(); i++){
            if (herbArray.get(i) > heightMultiplier){
                heightMultiplier = herbArray.get(i);
            }
            if (predArray.get(i) > heightMultiplier){
                heightMultiplier = predArray.get(i);
            } 
        }
        
        double heightConstant = (double)height/(double)(heightMultiplier*1.2);  //2 doubles needed instead of 1 at front to fix rounding error
        //drawing background
        brush.setColor(Color.black);
        brush.fillRect(0, 0, width, height);


        for(int i = 0; i < herbArray.size(); i++){
            //draws herbivore line
            brush.setColor(Color.white);
            barSizeHerbNew = (int)(800 - (double)herbArray.get(i)*heightConstant);
            brush.drawLine((int)(drawWidth*(i)), barSizeHerbOld, (int)(drawWidth*(i+1)), barSizeHerbNew);
            barSizeHerbOld = barSizeHerbNew;

            //draws predator line
            brush.setColor(Color.magenta);
            barSizePredNew = (int)(800 - (double)predArray.get(i)*heightConstant);
            brush.drawLine((int)(drawWidth*(i)), barSizePredOld, (int)(drawWidth*(i+1)), barSizePredNew);
            barSizePredOld = barSizePredNew;
            

        }
        //draws axis labels
        brush.setColor(Color.white);
        brush.drawString(String.valueOf(heightMultiplier), 10, 20);
        brush.drawString("0", 10, height - 10);
        
    }
    //used to update frame
    public void paintImage(ArrayList <Cell> cellArray, double[][] foodEnvioFill){
        //draws into jpanel
        Graphics brush = drawingPanel.getGraphics();
        Image img = createImage(cellArray, foodEnvioFill);
        brush.drawImage(img, 0, 0,drawingPanel);
    }
    //creates buffered image
    private Image createImage(ArrayList <Cell> cellArray, double[][] foodEnvioFill){
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gBrush = (Graphics2D) bufferedImage.getGraphics();
        gBrush.setColor(Color.black);
        gBrush.fillRect(0, 0, width, height);
        
        //draws food
        gridSize = (int) (width / foodEnvioFill.length);

        for (int i = 0; i < foodEnvioFill.length; i++){
            for (int j = 0; j < foodEnvioFill.length; j++){
                int colorDensity = (int)(foodEnvioFill[i][j]/0.01);
                if (colorDensity > 255){
                    colorDensity = 255;
                }
                tempColor = new Color(0,colorDensity,0,255);
                gBrush.setColor(tempColor);
                gBrush.drawRect(i*gridSize, j*gridSize, gridSize, gridSize);
            }
        }
                
        
        //draws cells
        for(int i = 0; i < cellArray.size(); i++){
            Cell tempCell = cellArray.get(i);
            if (tempCell.predator){
                gBrush.setColor(Color.MAGENTA);
                gBrush.fillOval(tempCell.xPos - (int)tempCell.cellSize, tempCell.yPos - (int)tempCell.cellSize, (int)tempCell.cellSize*2, (int)tempCell.cellSize*2);
            }
            else{
                gBrush.setColor(Color.white);
                gBrush.fillOval(tempCell.xPos - (int)tempCell.cellSize, tempCell.yPos - (int)tempCell.cellSize, (int)tempCell.cellSize*2, (int)tempCell.cellSize*2);
            }
        }

        return bufferedImage;
    }
}
