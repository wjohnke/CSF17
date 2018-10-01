/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmjxb2hw3_cs4750;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author wjohnke
 */
public class State {
    public int [][] grid;
    public State parent;
    public ArrayList<State> children;
    public int heuristic=0, totalNode;
   
    public State(int [][] grid){
        this.grid=grid;
    }
}
