/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cellEvolution;

import java.util.Random;

/**
 *
 * @author Wilson
 */
public class Food extends Environment{
    double growthFactor;
    double[][] foodGrowth;
    Random dice = new Random();
    public Food(int BN, double GF){
        super(BN);
        foodGrowth = new double [BN][BN];
        growthFactor = GF;
    }
    
    public void setup(){
        super.setup();
        //grid item increasing speed
        for (int i = 0; i < foodGrowth.length; i++){
            for (int j = 0; j < foodGrowth.length; j++){
                foodGrowth[i][j] = dice.nextInt(5) * growthFactor;
            }
        }

    }
    //updates array with its growth rate
    public void update(){
        for (int i = 0; i < envioFill.length; i++){
            for (int j = 0; j < envioFill.length; j++){
                envioFill[i][j] += foodGrowth[i][j];
            }
        }
    }
    
}
