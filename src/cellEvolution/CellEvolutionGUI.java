/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cellEvolution;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Wilson
 */
public class CellEvolutionGUI extends javax.swing.JFrame {
    int numInitCells;
    int blockNum;
    double cellDecay;
    double growthFactor;
    int initCellSize;
    int initSplitSize;
    boolean programRunning = false;
    boolean programStarted = false;
    boolean paintScreen = false;
    Random dice = new Random();
    Food myFood;
    Drawer myCanvas;
    int blockSize;
    int leftBound, rightBound, topBound, bottomBound;
    double foodTotal;
    
    //stores up cells
    ArrayList <Cell> oldCellArray = new ArrayList(0);
    ArrayList <Cell> newCellArray = new ArrayList(0);
    ArrayList <Cell> deadCellArray = new ArrayList(0);
    
    //variables to store for graphing after simulation
    ArrayList <Integer> herbPopArray = new ArrayList(0);
    ArrayList <Integer> predPopArray = new ArrayList(0);
    ArrayList <Integer> herbMassArray = new ArrayList(0);
    ArrayList <Integer> predMassArray = new ArrayList(0);
    int herbTot, predTot, herbMassTot, predMassTot;
    
    //initiates variables for simulation
    public void setSimulation(){
        //grabs values from input boxes
        numInitCells = Integer.parseInt(numInitCellsInput.getText());
        blockNum = Integer.parseInt(blockNumInput.getText());
        cellDecay = Double.parseDouble(cellDecayInput.getText());
        growthFactor = Double.parseDouble(growthFactorInput.getText());
        initCellSize = Integer.parseInt(initCellSizeInput.getText());
        initSplitSize = Integer.parseInt(initSplitSizeInput.getText());
        
        myCanvas = new Drawer (drawPanel);
        
        //creates initial cells
        for (int i = 0; i < numInitCells; i++){
            newCellArray.add(i,new Cell(initCellSize, initSplitSize));  
        }
        //sets up food
        blockSize = myCanvas.width/blockNum;
        myFood = new Food(blockNum, growthFactor);
        myFood.setup();
    }
 
    public void updateImage(){

        myCanvas.paintImage(newCellArray, myFood.envioFill);
        
    }
    //updates each element in the array of cells, and adds data to a graph variables
    public void updateCells(){
        
        herbTot = 0;
        predTot = 0;
        herbMassTot = 0;
        predMassTot = 0;

        oldCellArray.clear();
        oldCellArray.addAll(newCellArray);
        newCellArray.clear();
        //prints all cell data
        /*
        for (int i = 0; i < oldCellArray.size(); i++){
            System.out.println("\nCell number:\t\t" + i);
            System.out.println(oldCellArray.get(i));
        }
        * */
        
        
        //cellDecay and killing off any cells that die from it
        for (int i = 0; i < oldCellArray.size(); i++){
            //shrinks cell 
            //oldCellArray.get(i).cellSize += cellDecay;
            
            
            //for making predator decay faster
            if (oldCellArray.get(i).predator){
                oldCellArray.get(i).cellSize += cellDecay*1.5;
            }
            else{
                oldCellArray.get(i).cellSize += cellDecay;
            }
            
            //kills off cells that are too small
            if (oldCellArray.get(i).cellSize < 0){
                //gives predator a chance to turn to herbivore if too small
                if (oldCellArray.get(i).predator && dice.nextInt(50) == 0){
                    oldCellArray.get(i).cellSize = 1;
                    oldCellArray.get(i).predator = false;

                }
                //rids of dead herbivore
                else{
                    oldCellArray.get(i).cellAlive = false;
                }
            }
        }
        
        //updates cells eating either food or other cells
        for (int i = 0; i < oldCellArray.size(); i++){
            //predator eating cells
            if (oldCellArray.get(i).cellAlive && oldCellArray.get(i).predator){
                for (int j = 0; j < oldCellArray.size(); j++){
                    if (j != i && !oldCellArray.get(j).predator && oldCellArray.get(i).edible(oldCellArray.get(j))){
                        oldCellArray.get(i).cellSize = Math.sqrt(Math.pow(oldCellArray.get(i).cellSize,2) + Math.pow(oldCellArray.get(j).cellSize/0.8,2)) ;
                        oldCellArray.get(j).cellAlive = false;
                    }
                }
            }
            //herbivore eating food
            else if (oldCellArray.get(i).cellAlive && !oldCellArray.get(i).predator){
                foodTotal = 0;
                //for ranges of foodarray that cell can reach
                leftBound = (int)Math.floor((oldCellArray.get(i).xPos - oldCellArray.get(i).cellSize)/blockSize);
                rightBound = (int)Math.ceil((oldCellArray.get(i).xPos + oldCellArray.get(i).cellSize)/blockSize);
                topBound = (int)Math.floor((oldCellArray.get(i).yPos - oldCellArray.get(i).cellSize)/blockSize);
                bottomBound = (int)Math.ceil((oldCellArray.get(i).yPos + oldCellArray.get(i).cellSize)/blockSize);
                
                if (leftBound < 0){
                    leftBound = 0;
                }
                if (rightBound >= blockNum){
                    rightBound = blockNum-1;
                }
                if (topBound < 0){
                    topBound = 0;
                }
                if (bottomBound >= blockNum){
                    bottomBound = blockNum-1;
                }

                for (int row = leftBound; row <= rightBound; row++){
                    for (int column = topBound; column <= bottomBound; column++){
                        foodTotal += myFood.envioFill[row][column];
                        myFood.envioFill[row][column] = 0;
                    }
                }
                foodTotal = Math.sqrt(foodTotal);
                oldCellArray.get(i).cellSize = Math.sqrt(Math.pow(oldCellArray.get(i).cellSize,2) + Math.pow(foodTotal,2)) ;



            }
        }


        //updates cell splitting, stores graph data, and updates cell arraylist
        for (int i = 0; i < oldCellArray.size(); i++){
            //if cell is alive
            if (oldCellArray.get(i).cellAlive){ 
                
                //gets population data for graphing
                if (!oldCellArray.get(i).predator){
                    herbTot++;
                    herbMassTot += oldCellArray.get(i).cellSize;
                }
                else{
                    predTot++;
                    predMassTot += oldCellArray.get(i).cellSize;
                }
                
                oldCellArray.get(i).updateCellLocation();

                //if cell big enough to split
                if (oldCellArray.get(i).cellSize > oldCellArray.get(i).splitSize){ 
                    deadCellArray.add(deadCellArray.size(), oldCellArray.get(i));
                    for (int j = 0; j < 2; j++){
                        newCellArray.add(newCellArray.size(), new Cell(oldCellArray.get(i)));
                    }
                }
                //if cell not big enough to split
                else{ 
                    newCellArray.add(newCellArray.size(), oldCellArray.get(i));
                }
            }
            //cell is dead
            else{
                deadCellArray.add(deadCellArray.size(), oldCellArray.get(i));
            }
        }
        //adds data for graph
        if (herbTot != 0 || predTot != 0){
            herbPopArray.add(herbTot);
            predPopArray.add(predTot);
            herbMassArray.add(herbMassTot);
            predMassArray.add(predMassTot);
        }
        
        
        //sets up display data
        herbLabel.setText("Herbivores:   " + herbTot);
        predLabel.setText("Predators:      " + predTot);
        allCellsLabel.setText("All Cells:         " + String.valueOf(deadCellArray.size() + oldCellArray.size()));
        /*
        //adds rate of change to an array for later graph
        //System.out.println(deadHerb +"\t"+ deadPred);
        deadHerbROC.add(deadHerb);
        deadPredROC.add(deadPred);
        */
        
    }

    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } 
        catch (Exception e) {}
    }
    /**
     * Creates new form CellEvolutionGUI
     */
    public CellEvolutionGUI() {

        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        drawPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        fastForwardButton = new javax.swing.JButton();
        speedSlider = new javax.swing.JSlider();
        numInitCellsLabel = new javax.swing.JLabel();
        numInitCellsInput = new javax.swing.JTextField();
        cellDecayLabel = new javax.swing.JLabel();
        cellDecayInput = new javax.swing.JTextField();
        initCellSizeLabel = new javax.swing.JLabel();
        initCellSizeInput = new javax.swing.JTextField();
        growthFactorLabel = new javax.swing.JLabel();
        growthFactorInput = new javax.swing.JTextField();
        initSplitSizeLabel = new javax.swing.JLabel();
        initSplitSizeInput = new javax.swing.JTextField();
        blockNumInput = new javax.swing.JTextField();
        blockNumLabel = new javax.swing.JLabel();
        speedLabel = new javax.swing.JLabel();
        graphBox = new javax.swing.JComboBox();
        herbLabel = new javax.swing.JLabel();
        predLabel = new javax.swing.JLabel();
        allCellsLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        drawPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                drawPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                drawPanelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout drawPanelLayout = new javax.swing.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        drawPanelLayout.setVerticalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );

        startButton.setText("START");
        startButton.setToolTipText("Press once to start simulation, twice to end simulation, 3 times to graph");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        pauseButton.setText("PAUSE");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        fastForwardButton.setText("FAST FORWARD");
        fastForwardButton.setToolTipText("Warning: Stops animation");
        fastForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastForwardButtonActionPerformed(evt);
            }
        });

        speedSlider.setMinimum(50);
        speedSlider.setToolTipText("");
        speedSlider.setValue(90);

        numInitCellsLabel.setText("Initial Cells");

        numInitCellsInput.setText("10");
        numInitCellsInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numInitCellsInputActionPerformed(evt);
            }
        });

        cellDecayLabel.setText("Cell Decay Rate");

        cellDecayInput.setText("-0.006");

        initCellSizeLabel.setText("Initial Cell Size");

        initCellSizeInput.setText("5");

        growthFactorLabel.setText("Food Growth Rate");

        growthFactorInput.setText("0.003");

        initSplitSizeLabel.setText("Initial Split Size");

        initSplitSizeInput.setText("20");

        blockNumInput.setText("80");

        blockNumLabel.setText("Food Density");

        speedLabel.setText("Speed");

        graphBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-------------", "Mass Graph", "Population Graph" }));
        graphBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphBoxActionPerformed(evt);
            }
        });

        herbLabel.setText("Herbivores:");

        predLabel.setText("Predators: ");

        allCellsLabel.setText("All Cells:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(drawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(numInitCellsLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(blockNumInput)
                                .addComponent(growthFactorInput)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(blockNumLabel)
                                        .addComponent(growthFactorLabel))))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cellDecayInput)
                                .addComponent(initCellSizeInput)
                                .addComponent(initSplitSizeInput)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cellDecayLabel)
                                        .addComponent(initCellSizeLabel)
                                        .addComponent(initSplitSizeLabel))
                                    .addGap(12, 12, 12))
                                .addComponent(numInitCellsInput))
                            .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pauseButton)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(startButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(graphBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(speedLabel)
                                .addComponent(fastForwardButton))
                            .addComponent(herbLabel)
                            .addComponent(predLabel)
                            .addComponent(allCellsLabel))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(drawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(numInitCellsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numInitCellsInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cellDecayLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cellDecayInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(initCellSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(initCellSizeInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(initSplitSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(initSplitSizeInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(blockNumLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(blockNumInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(growthFactorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(growthFactorInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(fastForwardButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pauseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(startButton)
                            .addComponent(graphBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(speedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(herbLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predLabel)
                        .addGap(18, 18, 18)
                        .addComponent(allCellsLabel)))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //initiates and uninitiates variables that control if game is running, and later becomes the graphing button
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        //initiates game control variables
        if (startButton.getText().equals("START")){
            programStarted = true;
            programRunning = true;
            paintScreen = true;
            startButton.setText("END");
        }
        //uninitiates game control variables
        else if (startButton.getText().equals("END")){
            programStarted = false;
            programRunning = false;
            paintScreen = false;
            fastForwardButton.setText("----------");
            pauseButton.setText("----------");
            startButton.setText("GRAPH");
            graphBox.setSelectedIndex(1);
        }
        //calls for graph
        else if (startButton.getText().equals("GRAPH")){
            if (graphBox.getSelectedItem().equals("Population Graph")){
                myCanvas.paintCellGraph(herbPopArray, predPopArray);
            }
            else if (graphBox.getSelectedItem().equals("Mass Graph")){
                myCanvas.paintCellGraph(herbMassArray, predMassArray);
            }
                
        }
        
            

    }//GEN-LAST:event_startButtonActionPerformed

    private void drawPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawPanelMouseEntered

    }//GEN-LAST:event_drawPanelMouseEntered

    private void drawPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawPanelMouseExited

    }//GEN-LAST:event_drawPanelMouseExited
//pauses program
    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        if (programStarted){
            if (programRunning){
                programRunning = false;
                pauseButton.setText("PLAY");
            }
            else{
                programRunning = true;
                pauseButton.setText("PAUSE");
            }
        }
    }//GEN-LAST:event_pauseButtonActionPerformed
//turns off drawing to speed up program
    private void fastForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastForwardButtonActionPerformed
        if (programStarted){
            if (paintScreen){
                paintScreen = false;
                fastForwardButton.setText("REGULAR SPEED");
            }
            else{
                paintScreen = true;
                fastForwardButton.setText("FAST FORWARD");
            }
        }
    }//GEN-LAST:event_fastForwardButtonActionPerformed

    private void numInitCellsInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numInitCellsInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_numInitCellsInputActionPerformed

    private void graphBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_graphBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CellEvolutionGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CellEvolutionGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CellEvolutionGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CellEvolutionGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        CellEvolutionGUI CE = new CellEvolutionGUI();
        /* Create and display the form */
        CE.setVisible(true);
        //pauses until game starts
        while(!CE.programStarted){
            sleep(100);
        }
        
        CE.setSimulation();
        
        //runs simulation
        while(CE.programStarted){ 
            if (CE.programRunning){
                CE.updateCells();
                CE.myFood.update();

            }
            if (CE.paintScreen){
                CE.updateImage();
                sleep(100-CE.speedSlider.getValue());
            }
            
            //System.out.println(CE.deadCellArray.size());
        }
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel allCellsLabel;
    private javax.swing.JTextField blockNumInput;
    private javax.swing.JLabel blockNumLabel;
    private javax.swing.JTextField cellDecayInput;
    private javax.swing.JLabel cellDecayLabel;
    private javax.swing.JPanel drawPanel;
    private javax.swing.JButton fastForwardButton;
    private javax.swing.JComboBox graphBox;
    private javax.swing.JTextField growthFactorInput;
    private javax.swing.JLabel growthFactorLabel;
    private javax.swing.JLabel herbLabel;
    private javax.swing.JTextField initCellSizeInput;
    private javax.swing.JLabel initCellSizeLabel;
    private javax.swing.JTextField initSplitSizeInput;
    private javax.swing.JLabel initSplitSizeLabel;
    private javax.swing.JTextField numInitCellsInput;
    private javax.swing.JLabel numInitCellsLabel;
    private javax.swing.JButton pauseButton;
    private javax.swing.JLabel predLabel;
    private javax.swing.JLabel speedLabel;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
