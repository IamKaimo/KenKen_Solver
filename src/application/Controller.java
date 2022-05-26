package application;

import java.util.ArrayList;
import java.util.Comparator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.util.Random;


class Region
{
	public Region() {
		blocks = new ArrayList<>();
		//operator =0;
	}
	public Region(ArrayList<Integer> in) {
		blocks = new ArrayList<>();
		for(int i : in) {
			blocks.add(i);
		}
		//operator = 0;
	}
	
	/*public void setVals(int puzzle[][]) {
		for(int i:blocks) {
			int row = (i-1)/puzzle[0].length;
			int column = (i-1)%puzzle[0].length;
			vals.add(puzzle[row][column]);
		}
	}*/
	
	public ArrayList<Integer> blocks;
	//public ArrayList<Integer> vals;
	//public int operator;
}

class RegionComparator implements Comparator<Region> {
    public int compare(Region s1, Region s2)
    {
        if (s1.blocks.size() < s2.blocks.size())
            return 1;
        else if (s1.blocks.size() == s2.blocks.size())
        	return 0;
        else
            return -1;
    }
}

public class Controller
{
	public ArrayList<Region> Free = new ArrayList<>();
    public ArrayList<Region> Occu = new ArrayList<>();
    public int puzzle[][];
 	public int size = 3;
    
	@FXML
	public void generate(ActionEvent e) {
		//Step 1: Generate Primitive Solution
		Random rand = new Random();
		primitive_soln(size);
		for(int c = 0;c <10000;c++) {
		    int row1 = rand.nextInt(size);    
		    int row2 = rand.nextInt(size);
		    swap(puzzle,row1,row2);
			transpose(puzzle);
		}
		//Step 2: Ceiling Up the cell
		initial_region(size);
		while(!Free.isEmpty()) {
		    int len = rand.nextInt(size) + 1;
		    Region current = Free.get(0);
		    Free.remove(0);
		    Region New = new Region();
		    //Ceiling Up Region
		    if(len >= current.blocks.size()) {
		    	New = new Region(current.blocks);
		    	Occu.add(New);//possible reference error
		    	current.blocks.clear();
		    }
		    else {
			    ArrayList<Integer> valid = new ArrayList<>();
			    ArrayList<Integer> recycle = new ArrayList<>();
		    	int current_block;
			    while(len > 0) {
			    	int index = rand.nextInt(current.blocks.size());
			    	current_block = current.blocks.get(index);
			    	current.blocks.remove(index);
		    		if(New.blocks.isEmpty() && valid.isEmpty())valid.add(current_block);
			    	if(valid.contains(current_block))
			    	{
			    		valid.remove(valid.indexOf(current_block));
				    	New.blocks.add(current_block);
				    	//if not first row , if its already taken, if its already valid
				    	if(current_block > size && !New.blocks.contains(current_block - size) && !valid.contains(current_block - size)) valid.add(current_block - size);
				    	//if not last row
				    	if(current_block <= size * size - size && !New.blocks.contains(current_block + size) &&!valid.contains(current_block + size)) valid.add(current_block + size);
				    	//if not first column
				    	if((current_block)%size != 1 && !New.blocks.contains(current_block- 1) &&!valid.contains(current_block- 1)) valid.add(current_block- 1);
				    	//if not last column
				    	if((current_block)%size != 0 && !New.blocks.contains(current_block+ 1) &&!valid.contains(current_block+ 1)) valid.add(current_block+ 1);
				    	for(int i : recycle)current.blocks.add(i); 
				    	recycle.clear();
				    	len--;
			    	}
			    	else recycle.add(current_block);
			    }
			    Occu.add(New);
		    }	
		    //Reallocate Regions
		    while(!current.blocks.isEmpty()) {
			    ArrayList<Integer> valid = new ArrayList<>();
			    New = new Region();			    
		    	for(int i=0;i<current.blocks.size();i++) {
		    		int current_block = current.blocks.get(i);
		    		if(New.blocks.isEmpty() && valid.isEmpty())valid.add(current_block);
		    		if(valid.contains(current_block)) {
		    			current.blocks.remove(i);
		    			New.blocks.add(current_block);
		    			valid.remove(valid.indexOf(current_block));
				    	//if not first row , if its already taken, if its already valid
				    	if(current_block > size && !New.blocks.contains(current_block - size) && !valid.contains(current_block - size)) valid.add(current_block - size);
				    	//if not last row
				    	if(current_block <= size * size - size && !New.blocks.contains(current_block + size) &&!valid.contains(current_block + size)) valid.add(current_block + size);
				    	//if not first column
				    	if((current_block)%size != 1 && !New.blocks.contains(current_block- 1) &&!valid.contains(current_block- 1)) valid.add(current_block- 1);
				    	//if not last column
				    	if((current_block)%size != 0 && !New.blocks.contains(current_block+ 1) &&!valid.contains(current_block+ 1)) valid.add(current_block+ 1);
				    	i = -1;
		    		}
		    	}
		    	Free.add(New);
		    	valid.clear();
		    }
		    Free.sort(new RegionComparator());
		}
		//Step 3: Add Operators
		
		
		int z = 1+1;
	}
	
	
	private void primitive_soln(int n){
		puzzle = new int[n][n];
		for(int i=1; i <= n; i++) {
			for(int j = 1;j<=n;j++) {
				int num = (j + i - 1)%n;
				puzzle[i-1][j-1]= (num==0)? n:num;
			}
		}	
	}
	private void transpose(int [][]m)
	{
		int [][]temp = new int[m[0].length][m.length];
		for(int i = 0;i < m.length;i++)
		{
			for(int j = 0;j < m[0].length;j++)
			{
				temp[j][i] = m[i][j];
			}
		}
		for(int i=0;i<m.length;i++)
			m[i] = temp[i].clone();
	}
	private void swap(int [][]m,int row1,int row2)
	{
		int [] temp = m[row1];
		m[row1] = m[row2];
		m[row2] = temp;
	}
	private void initial_region(int n) {
		Region R1 = new Region();
		for(int i=1;i<=n*n;i++) {
			R1.blocks.add(i);
		}
		Free.add(R1);
	}

}


