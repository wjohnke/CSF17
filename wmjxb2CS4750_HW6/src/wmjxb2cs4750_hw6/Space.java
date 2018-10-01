/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmjxb2cs4750_hw6;

import java.util.ArrayList;

/**
 *
 * @author wjohnke
 */
public class Space {
    /*
    Space is a data structure to contain the value of each given space on the grid,
    as well as an ArrayList of possibilities, which holds the domain of possible values
    for each given space/node. 
    */
    
    ArrayList<Integer> possibilities;
    int value;
    ArrayList<Space> neighbors;
    int xCoord, yCoord;
    boolean finalized=false;
    
    
    public Space(){
        /*
        Empty construct, possibilities for space are 1-9 on initialization,
        updated after neighbors are checked
        */
        possibilities=new ArrayList();
        this.value=0;
        for(int i=0; i<9; i++){
            possibilities.add(i, i+1);
        }
        neighbors=new ArrayList();
    }
    public Space(int value){
        this.value=value;
        possibilities=null;
        neighbors=new ArrayList();
        finalized=true;
    }
    
    
    
    
    
    
    
}
