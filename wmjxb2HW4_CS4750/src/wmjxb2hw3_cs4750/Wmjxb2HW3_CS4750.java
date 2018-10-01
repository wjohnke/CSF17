/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmjxb2hw3_cs4750;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author wjohnke
 */
public class Wmjxb2HW3_CS4750 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /*
        Initialize empty grid of coordinates. Coordinates contain
        integer values for X and Y locations on the grid
        */
        
        
        int beginnerCounter=0, advancedCounter=0;
        System.out.println("Beginner Game, play against the computer. Beginner is X, Advanced is O");
        try{
        for(int i=0; i<50; i++){    
        int[][] emptyGrid=new int[5][6];
        for(int k=0; k<5; k++){
            for(int j=0; j<6; j++){
                emptyGrid[k][j]=0;
            }
        }
            
        int [][] grid=copyArray(emptyGrid);
        Coordinate decision, checkWin;
        State currentState=new State(grid);
            
        while(true){
            decision=masterPlayer(currentState);
            grid[decision.xCoord][decision.yCoord]=Status.X.ordinal();
            if(decision.end){
                if(decision.win){
                    //System.out.println("Beginner has won. Final Move at: " + decision.xCoord + ":" + decision.yCoord);
                    beginnerCounter++;
                    break;
                    //System.exit(0);
                }
                else{                    
                    //System.out.println("The game has ended in a tie");
                    break;
                    //System.exit(0);
                }
            }
            //System.out.println("Beginner moved at: " + decision.xCoord + ":" + decision.yCoord);
            long currentTime=System.currentTimeMillis();
            /*
            ********************************************************************************************
            The following 3 lines are the alternating players, human, advanced player, and master player
            Depending on which player is to be utilized, exactly one line should be not commented out
            ********************************************************************************************
            */
            /*
            ********************************************/
            //decision=humanMove(currentState);
            //decision=advancedPlayer(currentState);
            decision=advancedPlayer(currentState);
            /*******************************************
            */
            //System.out.println("CPU Execution time:" + (System.currentTimeMillis()-currentTime) );
            grid[decision.xCoord][decision.yCoord]=Status.O.ordinal();
            checkWin=checkType(Status.O.ordinal(), currentState);
            if(checkWin.end){
                if(checkWin.win==false){
                    //System.out.println("Advanced has won. Final Move at: " + decision.xCoord + ":" + decision.yCoord);
                    advancedCounter++;
                    break;
                    //System.exit(0);
                }
            }
            //System.out.println("Advanced moved at: " + decision.xCoord + ":" + decision.yCoord);
            
        }
        
        
        }
        System.out.println("Beginner: " + beginnerCounter);
        System.out.println("Advanced: " + advancedCounter);
        }
        catch(Exception ex){ex.printStackTrace(System.out);}
   }
    
    public static Coordinate humanMove(State currentState){
        int [][] grid=currentState.grid;
        Coordinate decision=new Coordinate(0,0);
        int playerXCoord=-1, playerYCoord=-1;
        Scanner sc = new Scanner(System.in);
        
        while(playerXCoord==-1){
                System.out.println("Enter X Coordinate for next move: ");
                playerXCoord=sc.nextInt();
                System.out.println("Enter Y Coordinate for next move: ");
                playerYCoord=sc.nextInt();
                if(playerXCoord<0 || playerXCoord>4 || playerYCoord<0 || playerYCoord>5){
                    playerXCoord=-1;
                    System.out.println("Invalid input, try again");
                }
                else if(grid[playerXCoord][playerYCoord]!=Status.EMPTY.ordinal()){
                    playerXCoord=-1;
                    System.out.println("Not an empty tile, try again");
                }
        }
        decision.xCoord=playerXCoord;
        decision.yCoord=playerYCoord;
        return decision;
    }
    
    public static Coordinate beginnerPlayer(State currentState){
        /*
        Beginner will play with only one given state, only
        ever looking at the current state of the tic-tac-toe grid
        and making limited moves based on what's avaialble
        */
        return checkBeginnerMove(currentState);
    }
    
    public static Coordinate checkBeginnerMove(State currentState){
        Coordinate decision;
        
        decision=checkType(Status.O.ordinal(), currentState);
        if(decision.end) return decision;
        decision=checkType(Status.X.ordinal(), currentState);
        if(decision.end) return decision;
        
        else{                        //Choose random position for next move
            Random rand=new Random();
            int randOffset=0, randXCoord, randYCoord;
            while(true){
                randXCoord=Math.abs((rand.nextInt()+randOffset)%5 );
                randYCoord=Math.abs((rand.nextInt()+randOffset++)%6 );
                if(currentState.grid[randXCoord][randYCoord]==Status.EMPTY.ordinal()) break;
            }
            decision.xCoord=randXCoord;
            decision.yCoord=randYCoord;
        }
        return decision;
    }
    
    private static Coordinate checkType(int num, State currentState){
        /*
        Based on the problem description, this method simply checks
        if there is a current "open 3-in-a-row" on the board for the given
        type. It does not check for possible intermediate moves that would still
        result in a 4-in-a-row
        */
        Boolean draw=true;
        Coordinate decision=new Coordinate(0,0);
        for(int i=0; i<5; i++){
            for (int j=0; j<6; j++){
                if(currentState.grid[i][j]==Status.EMPTY.ordinal()) draw=false;
                if(!( ((j>3) && ((i==3) || (i==4))) || ((i>2) && ((j==4) || (j==5))  ) )){
                if(currentState.grid[i][j]==(num)){
                    /*Check diagonals*/
                    if( ((i<3) && (j<4)) && (currentState.grid[i+1][j+1]==(num))  
                          &&  currentState.grid[i+2][j+2]==(num) ){
                        if(i+3<5)   decision.xCoord=(i+3);
                        else        decision.xCoord=i-1;
                        if(j+3<6)   decision.yCoord=(j+3);
                        else        decision.yCoord=(j+3)%6;        
                        if(num==Status.X.ordinal()) decision.win=true;
                        if(currentState.grid[decision.xCoord][decision.yCoord]!=Status.EMPTY.ordinal()) break;
                        decision.end=true;
                        return decision;
                    }
                    /*Check vertical*/
                    if(( (j<4)) && (currentState.grid[i][j+1]==(num))
                          && currentState.grid[i][j+2]==(num) ){
                        decision.xCoord=i;
                        if(j+3<6)   decision.yCoord=(j+3);
                        else        decision.yCoord=j-1;
                        if(num==Status.X.ordinal()) decision.win=true;
                        if(currentState.grid[decision.xCoord][decision.yCoord]!=Status.EMPTY.ordinal()) break;
                        decision.end=true;
                        return decision;
                    }
                    /*Check horizontal*/
                    if( (i<3) && (currentState.grid[i+1][j]==(num))
                          && currentState.grid[i+2][j]==(num) ){
                        if(i+3<5)   decision.xCoord=(i+3);
                        else        decision.xCoord=(i-1);
                        decision.yCoord=j;
                        if(num==Status.X.ordinal()) decision.win=true;
                        if(currentState.grid[decision.xCoord][decision.yCoord]!=Status.EMPTY.ordinal()) break;
                        decision.end=true;
                        return decision;
                    }
                }
                }
            }
        }
        if(draw){ decision.win=false; decision.end=true; return decision;}
        decision.end=false;
        return decision;
    }
    
    private static Coordinate advancedPlayer(State currentState){
        int index=0, totalNodes=0;
        Coordinate decision;
        State root=generateMoves(currentState, 1, 2, totalNodes);
        System.out.println("Total nodes expanded: "+ root.totalNode*root.children.get(0).totalNode);
        index=chooseAdvancedMove(root);
        decision=getDecision(root.children.get(index).grid, currentState.grid);
        currentState.totalNode=0;
        return decision;
    }
    
    private static Coordinate masterPlayer(State currentState){
        int index=0, totalNodes=0;
        Coordinate decision;
        State root=generateMoves(currentState, 1, 4, totalNodes);
        
        index=chooseAdvancedMove(root);
        decision=getDecision(root.children.get(index).grid, currentState.grid);
        currentState.totalNode=0;
        return decision;
    }
    
    private static Coordinate getDecision(int [][] newGrid, int [][] oldGrid){
        Coordinate decision=new Coordinate(0,0);
        for(int i=0; i<6; i++){
            for(int j=0; j<6; j++){
                if(!(newGrid[i][j]==(oldGrid[i][j])) ){
                    decision.xCoord=i;
                    decision.yCoord=j;
                    return decision;
                }
            }
        }
        return decision;
    }
    
    private static int chooseAdvancedMove(State root){
        State head=root;
        int minimax=0, decision=0, offset=0;
        Random coinFlip=new Random();
        while((offset<root.children.size()) ){
            head=root.children.get(offset);
            if(minimax<head.heuristic) {
                minimax=head.heuristic;
                decision=offset;
            }
            else if(minimax==head.heuristic){ //Choose random
                if(coinFlip.nextBoolean()){
                    minimax=head.heuristic;
                    decision=offset;
                }
                //Otherwise keep same value for minimax direction
            }
            offset++;
        }
        
        if(minimax==0){ decision=Math.abs(coinFlip.nextInt()%(root.children.size()));}
        
        return decision;
    }
    
    
    private static State generateMoves(State currentState, int level, int finalLevel, int totalNodes){
        /*
        This function takes the current state, and generates all possible
        moves the Advanced player can make (using O). It then calculates
        the heuristic for each new state and adds it to the tree
        */
        int [][] grid=currentState.grid;
        int [][] gridCopy=null;
        State root=currentState, newState=null;
        root.children=new ArrayList();
        State head;
        int offset=0, minimax=0;
        
        
        for(int i=0; i<5; i++){
            for(int j=0; j<6; j++){
                if(grid[i][j]!=Status.O.ordinal() && grid[i][j]!=Status.X.ordinal()){
                    gridCopy=copyArray(grid);
                    gridCopy[i][j]=level%2==0? Status.X.ordinal():Status.O.ordinal();
                    newState=new State(gridCopy);
                    totalNodes++;
                    newState.parent=root;
                    if(level<finalLevel){ //Generate new level with new states
                        
                        newState=generateMoves(newState, level+1, finalLevel, ++root.totalNode);
                        while((offset<newState.children.size()) ){
                            head=newState.children.get(offset++);
                            if(minimax<head.heuristic) minimax=head.heuristic;
                        }
                        newState.heuristic=minimax;
                    }
                    else{
                        newState.heuristic=generateHeuristic(newState.grid);
                    }
                    
                    root.children.add(newState);
                    newState=null;
                    gridCopy=null;
                }
            }
        }
        
        /*
        Returns back original root, with added children
        */
        if(totalNodes>root.totalNode) root.totalNode=totalNodes;
        return root;
    }
    
    private static int[][] copyArray(int [][] grid){
        int[][] gridCopy=new int[5][6];
        for(int i=0; i<5; i++){
            System.arraycopy(grid[i], 0, gridCopy[i], 0, 6);
        }
        return gridCopy;
    }
    
    
    private static int generateHeuristic(int [][] grid){
        /*
        Calculates the heuristic of a given state based on 
        the configuration of each player's choices
        Draw returns 0
        Loss returns large negative value
        Win returns large positive value
        Non-terminal/cutoff nodes return calculated value
        */
        
        Coordinate checkFinality;
        checkFinality=calculate3InARow(grid, Status.O);
        
        if (checkFinality.win=true){
            if(checkFinality.end==null){
                //Win
                return checkFinality.xCoord;
            }
            else if(checkFinality.end==true){
                //Draw
                return 0;
            }
            else{
                //Loss
                return checkFinality.xCoord;
            }
        }
        //Else calculate actual heuristic
        return  3*checkFinality.xCoord
                - 3* calculate3InARow(grid, Status.X).xCoord
                + calculate2InARow(grid, Status.O)
                - calculate2InARow(grid, Status.X);
    }
    
    private static Coordinate calculate3InARow(int [][] grid, Status stat){
        /*
        Function will return a very large positive or negative value
        for terminal nodes (4 in a row nodes), or the total number
        of 3-in-a-rows
        */
        Coordinate checkDraw=new Coordinate(0,1);
        Boolean draw=true;
        /*
        Using Coordinate data structure to hold values for draw/win/loss
        xCoord value = heuristic value
        end = true is a draw
        !end = false is a loss
        win = true is a win, with end being null
        !win = false is default, further moves are possible
        */
        checkDraw.win=false;
        int result=0;
        for(int i=0; i<3; i++){
            for(int j=0; j<4; j++){
                if(grid[i][j]==Status.EMPTY.ordinal()) draw=false;
                if(grid[i][j]==stat.ordinal()){
                    if(grid[i+1][j+1]==stat.ordinal()
                            && grid[i+2][j+2]==stat.ordinal()){ //Diagonal
                        if( (i<2) && (j<3) && grid[i+3][j+3]==stat.ordinal()){//Terminal node
                            checkDraw.xCoord=stat.ordinal()==Status.O.ordinal()? 100000 : -100000;
                            checkDraw.win=true;
                            checkDraw.end=stat.ordinal()==Status.O.ordinal()? null : false ;
                            return checkDraw;
                        }
                        result++;
                    }
                    if(grid[i+1][j]==stat.ordinal()
                            && grid[i+2][j]==stat.ordinal()){  //Horizontal
                        if((i<2) && grid[i+3][j]==stat.ordinal()){//Terminal node
                            checkDraw.xCoord=stat.ordinal()==Status.O.ordinal()? 100000 : -100000;
                            checkDraw.win=true;
                            checkDraw.end=stat.ordinal()==Status.O.ordinal()? null : false ;
                            return checkDraw;
                        }
                        result++;
                    }
                    if(grid[i][j+1]==stat.ordinal()
                            && grid[i][j+2]==stat.ordinal()){ //Vertical
                        if((j<3) && grid[i][j+3]==stat.ordinal()){//Terminal node
                            checkDraw.xCoord=stat.ordinal()==Status.O.ordinal()? 100000 : -100000;
                            checkDraw.win=true;
                            checkDraw.end=stat.ordinal()==Status.O.ordinal()? null : false ;
                            return checkDraw;
                        }
                        result++;
                    }
                }   
            }
        }
        if(draw){ //Traverse rest of board to check if result is a draw
            for(int i=3; i<5; i++){
                for(int j=4; j<6; j++){
                    if(grid[i][j]==Status.EMPTY.ordinal()) draw=false;
                }
            }
        }
        if (draw){ checkDraw.end=true; checkDraw.win=true; }
        checkDraw.xCoord=result;
        return checkDraw;
    }
    private static int calculate2InARow(int [][] grid, Status stat){
        int result=0;
        for(int i=0; i<4; i++){
            for(int j=0; j<5; j++){
                if(grid[i][j]==stat.ordinal()){
                    if(grid[i+1][j+1]==stat.ordinal()){ //Diagonal
                        result++;
                    }
                    if(grid[i+1][j]==stat.ordinal()){  //Horizontal
                        result++;
                    }
                    if(grid[i][j+1]==stat.ordinal()){ //Vertical
                        result++;
                    }
                }   
            }
        }
        return result;
    }
    
    
    
}
