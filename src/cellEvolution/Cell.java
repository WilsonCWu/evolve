
package cellEvolution;

import java.util.Random;


public class Cell {
    int xPos, yPos;
    int xSpeed, ySpeed;
    double cellSize;
    double splitSize;
    boolean predator;
    boolean cellAlive;
    
    
    Random rand = new Random();
    
    //create new random cell
    public Cell (int ICS, int ISS){
        this.xPos = rand.nextInt(800);
        this.yPos = rand.nextInt(800);
        this.xSpeed = rand.nextInt(5) - 2;
        this.ySpeed = rand.nextInt(5) - 2;
        this.cellSize = rand.nextInt(5) + ICS;
        this.splitSize = rand.nextInt(15) + ISS;
        predator = rand.nextInt(5) == 0;
        this.cellAlive = true;
    }
    
    //create cell with specific traits
    public Cell (int XP, int YP, int XS, int YS, double CS, double SS, boolean P){
        this.xPos = XP;
        this.yPos = YP;
        this.xSpeed = XS;
        this.ySpeed = YS;
        this.cellSize = CS;
        this.splitSize = SS;
        this.predator = P;
        this.cellAlive = true;
    }
    
    //creates new cell from parent cell
    public Cell (Cell other){
        this.xPos = other.xPos;
        this.yPos = other.yPos;
        this.xSpeed = other.xSpeed + rand.nextInt(5) - 2;
        this.ySpeed = other.ySpeed + rand.nextInt(5) - 2;
        this.cellSize = other.cellSize/2.5;
        this.splitSize = other.splitSize + rand.nextInt(5) - 2;
        if (splitSize < 3){ //keep until food is introduced 
            splitSize = 3;
        }
        if (rand.nextInt(20) != 0){ //shorten if time
            this.predator = other.predator;
        }
        else{
            this.predator = !other.predator;
        }
        this.cellAlive = true;
    }
    //distance of 2 points
    public static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }
    //checks if cell is in range to encompass another cell
    public boolean edible(Cell other){
        return (this.cellSize > other.cellSize && distance(this.xPos, this.yPos, other.xPos, other.yPos) < this.cellSize);   
    }
    public void updateCellLocation (){

        //updates location, bouncing off of walls
        if (xPos - cellSize < 0){
            xPos = (int)cellSize;
            xSpeed = - xSpeed;
        }
        if (xPos + cellSize > 800){
            xPos = 800 - (int)cellSize;
            xSpeed = - xSpeed;
        }
        if (yPos - cellSize < 0){
            yPos = (int)cellSize;
            ySpeed = - ySpeed;
        }
        if (yPos + cellSize > 800){
            yPos = 800 - (int)cellSize;
            ySpeed = - ySpeed;
        }
        
        //updates location
        xPos += xSpeed;
        yPos -= ySpeed;
        
    }
    
    @Override
    public String toString() {
        String output = "Location:\t\t(" + xPos + "," + yPos + ")\nDirection:\t\t(" + xSpeed + "," + ySpeed + ")\nSize:\t\t\t" + cellSize + "\nSplit Size:\t\t" + splitSize + "\nPredator or Prey:\t";
        if (predator){
            output += "Predator";
        }
        else{
            output += "Prey";
        }
        output += "\nCell Alive:\t\t" + cellAlive;
        return (output);
    }
    
}
