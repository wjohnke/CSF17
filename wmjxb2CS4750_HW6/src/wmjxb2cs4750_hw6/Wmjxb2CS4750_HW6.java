/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmjxb2cs4750_hw6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author wjohnke
 */
public class Wmjxb2CS4750_HW6 {

    
    public static void main(String[] args) {
        if(args==null || args[0]==null) System.exit(0);
        /*
        Using an integer array data structure to hold the sudoku grid
        0 represents an empty or unassigned variable
        */
        
        Space grid[][]=new Space[9][9];
        PriorityQueue queue= new PriorityQueue(10, new Comparator<Space>(){
            @Override
            public int compare(Space space1, Space space2) {
                if(space1.neighbors.size() < space2.neighbors.size()) return -1;
                if(space1.neighbors.size() < space2.neighbors.size()) return -1;
                return 0;
            }
        });
        TotalGrid total=new TotalGrid(queue, grid);
        //Wrapping both data structures in a class to return both types in "getInitConfiguration"
        //method call
        total=getInitConfiguration(grid, queue, args[0]);
        //solveProblem(total);
        
    }
    private static TotalGrid getInitConfiguration(Space [][] emptyGrid, PriorityQueue<Space> queue, String filename){
        Space [][] grid=emptyGrid;
        TotalGrid total;
        String input;
        char [] temp;
        int position=0;
        try{
            FileReader in =new FileReader(filename);
            BufferedReader buff=new BufferedReader(in);
            int j=0;
            while((input=buff.readLine())!=null){
                temp=input.toCharArray();
                for(int i=0; i<9; i++){
                    if(temp[i]!='0'){
                        //Final value for space
                        grid[i][j]=new Space(Character.getNumericValue(temp[i]));
                        grid[i][j].xCoord=i;
                        grid[i][j].yCoord=j;
                    }
                    else{
                        grid[i][j]=new Space();
                        queue.offer(grid[i][j]);
                        grid[i][j].xCoord=i;
                        grid[i][j].yCoord=j;
                    }
                }
                
 
                j++;
            }
            buff.close();
            if (grid==null) throw new Exception();
            
        }catch(Exception ex){}
        grid=initializeNeighbors(grid);
        grid=setConstraints(grid);
        total=new TotalGrid(queue, grid);
        return total;
    }
    private static Space [][] initializeNeighbors(Space [][] grid){
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(i>0) grid[i][j].neighbors.add(grid[i-1][j]);
                if(j>0) grid[i][j].neighbors.add(grid[i][j-1]);
                if(i<8) grid[i][j].neighbors.add(grid[i+1][j]);
                if(j<8) grid[i][j].neighbors.add(grid[i][j+1]);
                if(i>0 && j>0) grid[i][j].neighbors.add(grid[i-1][j-1]);
                if(i>0 && j<8) grid[i][j].neighbors.add(grid[i-1][j+1]);
                if(i<8 && j>0) grid[i][j].neighbors.add(grid[i+1][j-1]);
                if(i<8 && j<8) grid[i][j].neighbors.add(grid[i+1][j+1]);
                
            }
        }
        return grid;
    }
    
    private static Space[][] setConstraints(Space [][] grid){
        grid=allDiffSectionWise(grid);
        grid=allDiffHorizontal(grid);
        grid=allDiffVertical(grid);
        
        return grid;
    }
    
    private static Space [][] allDiffVertical(Space [][] grid){
        ArrayList<Integer> usedValues=new ArrayList();
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(grid[i][j].value!=0 && grid[i][j].possibilities==null){
                    usedValues.add(grid[i][j].value);
                }
            }
            for(int j=0; j<9; j++){
                if(grid[i][j].value==0){
                    int length=0;
                    while(length<usedValues.size()){
                        grid[i][j].possibilities.remove(usedValues.get(length++));
                    }
                }
            }
            usedValues.clear();
        }
        return grid;
    }
    
    private static Space [][] allDiffHorizontal(Space [][] grid){
        ArrayList<Integer> usedValues=new ArrayList();
        for(int j=0; j<9; j++){
            for(int i=0; i<9; i++){
                if(grid[i][j].value!=0 && grid[i][j].possibilities==null){
                    usedValues.add(grid[i][j].value);
                }
            }
            for(int i=0; i<9; i++){
                if(grid[i][j].value==0){
                    int length=0;
                    while(length<usedValues.size()){
                        grid[i][j].possibilities.remove(usedValues.get(length++));
                    }
                }
            }
            usedValues.clear();
        }
        return grid;
    }
    private static Space [][] allDiffSectionWise(Space [][] grid){
        grid=removeConflictsSection(grid, 3, 3);
        grid=removeConflictsSection(grid, 3, 6);
        grid=removeConflictsSection(grid, 3, 9);
        grid=removeConflictsSection(grid, 6, 3);        
        grid=removeConflictsSection(grid, 6, 6);
        grid=removeConflictsSection(grid, 6, 9);
        grid=removeConflictsSection(grid, 9, 3);
        grid=removeConflictsSection(grid, 9, 6);
        grid=removeConflictsSection(grid, 9, 9);        
        return grid;
    }
    private static Space [][] removeConflictsSection(Space [][] grid, int sectionLimitX, int sectionLimitY){
        ArrayList<Integer> usedValues=new ArrayList();
        for(int i=sectionLimitX-3; i<sectionLimitX; i++){
            for(int j=sectionLimitY-3; j<sectionLimitY; j++){
                if(grid[i][j].value!=0 && grid[i][j].possibilities==null){
                    usedValues.add(grid[i][j].value);
                }
            }
        }
        for(int i=sectionLimitX-3; i<sectionLimitX; i++){
            for(int j=sectionLimitY-3; j<sectionLimitY; j++){
                if(grid[i][j].value==0){
                    int length=0;
                    while(length<usedValues.size()){
                        grid[i][j].possibilities.remove(usedValues.get(length++));
                    }
                }
                
            }
        }
        return grid;
    }
    
    /*
    
    private static ArrayList<Integer> checkDomain(Space space, int xCoord, int yCoord, Space [][] grid){
        ArrayList<Integer> possibilities=space.possibilities;
        possibilities=checkConflicts(space,xCoord, yCoord, 0, grid);
        
        return possibilities;
    }
    private static ArrayList<Integer> checkConflicts(Space space,int xCoord, int yCoord, int direction, Space [][] grid){
        switch(direction){
            case 0: //Check grid -> at most 8 options
                if(xCoord!=0)   makeConsistent(grid, xCoord, yCoord, xCoord-1, yCoord);
                if(yCoord!=0)   makeConsistent(grid, xCoord, yCoord, xCoord , yCoord-1);
                if(xCoord!=0 && yCoord!=0)  makeConsistent(grid, xCoord, yCoord, xCoord-1, yCoord-1);
                if(xCoord<9)    makeConsistent(grid, xCoord, yCoord, xCoord+1, yCoord);
                if(yCoord<9)    makeConsistent(grid, xCoord, yCoord, xCoord , yCoord+1);
                if(xCoord<9 && yCoord<9)    makeConsistent(grid, xCoord, yCoord, xCoord+1 , yCoord+11);
                if(xCoord<9 && yCoord!=0)   makeConsistent(grid, xCoord, yCoord, xCoord+1 , yCoord-1);
                if(xCoord!=0 && yCoord<9)   makeConsistent(grid, xCoord, yCoord, xCoord-1 , yCoord+1);
                
                
        
        
            for(int i=0; i< spaced.possibilities.size(); i++ ){
            
            }
        
        }
        
    }
    */
    
    
    private static void solveProblem(TotalGrid total){
        Space [][] grid=total.grid;
        PriorityQueue<Space> queue=total.queue;
        Space current;
        int xCoord, yCoord;
        Random rand=new Random();
        
        current=queue.remove();
        
        
        while(!queue.isEmpty() ){
            current=queue.remove();
            xCoord=current.xCoord;
            yCoord=current.yCoord;
            try{
                
                //Choose random value from variable's domain
                int value=current.possibilities.get(
                        rand.nextInt()%(current.possibilities.size()));
                current.value=value;
                for (Space neighbor : current.neighbors) {
                    neighbor.possibilities.remove(current.value);
                }
                
                grid[xCoord][yCoord].value=value;
                current=exploreNode(current, grid);
                while(current.neighbors!=null){
                    
                }
            }catch(Exception ex){}
        }
        
        while(!queue.isEmpty()){   
            
        }
        
    }
    
    private static Space exploreNode(Space space, Space [][] grid){
        if(space.possibilities.isEmpty()) return null;
        int index=0;
        Space current=space;
        Random rand=new Random();
        //Pick a new value for current variable
        int value=current.possibilities.get(
                        rand.nextInt()%(current.possibilities.size()) );
        current.value=value;
        current.possibilities.clear();
        /*Update domain for neighboring variables*/
        for(Space neighbor : current.neighbors) {
            neighbor.possibilities.remove(current.value);
        }
        grid=updateHorizontal(space, grid);
        grid=updateVertical(space, grid);
        
        
        
        return space;
    }
    
    private static Space [][] updateHorizontal(Space space, Space [][] grid){
        for(int i=0; i<9; i++){
            if(i==space.xCoord) continue;
            else if(grid[i][space.yCoord].finalized) continue;
            else{
                grid[i][space.yCoord].possibilities.remove(space.value);
            }
        }
        return grid;
    }
    private static Space [][] updateVertical(Space space, Space [][] grid){
        for(int i=0; i<9; i++){
            if(i==space.xCoord) continue;
            else if(grid[space.xCoord][i].finalized) continue;
            else{
                grid[space.xCoord][i].possibilities.remove(space.value);
            }
        }
        return grid;
    }
    
    
    
    
    
    
}
