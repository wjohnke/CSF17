/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmjxb2hw6cs4750;

/**
 *
 * @author wjohnke
 */
public class Wmjxb2HW6CS4750 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args[0]==null) System.exit(0);
        /*
        Using an integer array data structure to hold the sudoku grid
        0 represents an empty or unassigned variable
        */
        
        Space grid[][]=new Space[9][9];
        PriorityQueue<Space> queue=new PriorityQueue<>();
        TotalGrid total=new TotalGrid(queue, grid);
        //Wrapping both data structures in a class to return both types in "getInitConfiguration"
        //method call
        total=getInitConfiguration(total, args[0]);
        solveProblem(total);
        
        
    }
    private static TotalGrid getInitConfiguration(TotalGrid total, String filename){
        Space [][] grid=total.grid;
        PriorityQueue<Space> queue=total.queue;
        
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
                    }
                    else{
                        grid[i][j]=new Space();
                        grid[i][j].possibilities=checkDomain(grid[i][j]);
                        queue.add(grid[i][j]);
                        
                    }
                 }
                
                
                j++;
            }
            buff.close();
            if (grid==null) throw new Exception();
            
        }catch(Exception ex){}
        total.grid=grid;
        total.queue=queue;
        
        return total;
    }
    
    private static ArrayList<Integer> checkDomain(Space space){
        space.possibilities=checkConflicts(space, 0);
        
        return space.possibilities;
    }
    private static ArrayList<Integer> checkConflicts(Space spaced, enum {NOM, NEW} ){
        for(int i=0; i< spaced.possibilities.length(); i++ ){
            
        }
        
    }
    
    
    private static void solveProblem(TotalGrid total){
        Space [][] grid=total.grid;
        PriorityQueue<Space> queue=total.queue;
        
        
        
        
        while(!queue.isEmpty()){
            
            
            
            
        }
        
       
        return grid;
    }
}
