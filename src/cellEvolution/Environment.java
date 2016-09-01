
package cellEvolution;


public class Environment {
    
    int blockNum;
    double[][] envioFill;
    public Environment (int BN){
        blockNum = BN;
        envioFill = new double [blockNum][blockNum];
    }
    
    
    
    
    
    public void setup(){
        //grid item quantity
        for (int i = 0; i < envioFill.length; i++){
            for (int j = 0; j < envioFill.length; j++){
                envioFill[i][j] = 0;
            }
        }
    }
    
    
}
