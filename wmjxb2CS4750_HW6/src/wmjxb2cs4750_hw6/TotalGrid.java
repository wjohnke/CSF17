/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmjxb2cs4750_hw6;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author wjohnke
 */
public class TotalGrid {
    PriorityQueue<Space> queue;
    Space [][] grid;
    
    public TotalGrid(PriorityQueue<Space> queue, Space[][] grid){
        this.queue=queue;
        this.grid=grid;
    }
}
