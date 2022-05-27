package application;

import java.util.ArrayList;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.util.Random;


public class Controller
{
	@FXML
	public Label size_txt;
	public Button gene_btn;
	public ComboBox<String> size_cb;
	public Label algo_txt;
	public Button solve_btn;
	public ComboBox<String> algo_cb;
	public VBox puzzle_box;
	
	public void initialize() {
		size_cb.getItems().addAll("2x2","3x3","4x4","5x5","6x6","7x7","8x8","9x9");
		algo_txt.setDisable(true);
		solve_btn.setDisable(true);
		algo_cb.setDisable(true);
    }
	public void generate_btn(ActionEvent e) {
		size = size_cb.getValue().charAt(0) - 48;
		generate();
		algo_txt.setDisable(false);
		solve_btn.setDisable(false);
		algo_cb.setDisable(false);
	}
	public void draw_btn(ActionEvent e) {
		puzzle_box.getChildren().clear();
		draw();
	}
	
	//////////////////////Algorithm Code//////////////////////
	public ArrayList<Region> Puzzle = new ArrayList<>();
	public int size = 3;
	
	public void generate() {
		//Step 0 :Clear and Create random object to get all random numbers from
		Puzzle.clear();
		Random rand = new Random();
		
		//Step 1: Ceiling Up the puzzle
		ArrayList<Region> Free;
		Free = initial_region(size);
		while(!Free.isEmpty()) {
		    int len = rand.nextInt(size+1) + 1;
		    Region current = Free.get(0);
		    Free.remove(0);
		    Region New = new Region();
		    //Ceiling Up Region
		    if(len >= current.blocks.size()) {
		    	New = new Region(current.blocks);
		    	Puzzle.add(New);//possible reference error
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
			    Puzzle.add(New);
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
		
		//Step 2: Generate Primitive Solution and fill Regions with
		int soln[][];
		soln = primitive_soln(size);
		for(int c = 0;c <10000;c++) {
		    int row1 = rand.nextInt(size);    
		    int row2 = rand.nextInt(size);
		    swap(soln,row1,row2);
			transpose(soln);
		}
		for(Region Reg : Puzzle) Reg.setVals(soln);
	    Puzzle.sort(new RegionComparator());
	}	
	public void draw() {
		double hb_w = puzzle_box.getPrefWidth();
		double hb_h = puzzle_box.getPrefHeight()/size;
		for(int i=0;i<size;i++) {
			HBox hb= new HBox();
			hb.setPrefSize(hb_w, hb_h);
			hb.setMaxSize(hb_w, hb_h);
			hb.setMinSize(hb_w, hb_h);
			puzzle_box.getChildren().add(hb);	
		}		
		for(Node i:puzzle_box.getChildren()) {
			HBox current = (HBox)i;
			double cell_w = current.getPrefWidth()/size;
			double cell_h= current.getPrefHeight();
			for(int j=0;j<size;j++) {
				BorderPane cell= new BorderPane();
				cell.setStyle("-fx-background-color: rgb(240,240,240)");
				cell.setPrefSize(cell_w, cell_h);
				cell.setMinSize(cell_w, cell_h);
				cell.setMaxSize(cell_w, cell_h);
				cell.setTop(new Rectangle(cell_w,cell_h/50,Color.BLACK));
				cell.setBottom(new Rectangle(cell_w,cell_h/50,Color.BLACK));
				cell.setLeft(new Rectangle(cell_w/50,cell_h,Color.BLACK));
				cell.setRight(new Rectangle(cell_w/50,cell_h,Color.BLACK));
				BorderPane.setAlignment(cell.getTop(), Pos.TOP_CENTER);
				BorderPane.setAlignment(cell.getBottom(), Pos.BOTTOM_CENTER);
				BorderPane.setAlignment(cell.getLeft(), Pos.CENTER_LEFT);
				BorderPane.setAlignment(cell.getRight(), Pos.CENTER_RIGHT);
				current.getChildren().add(cell);
			}	
		}
		for(Region i:Puzzle) {
			int flag = 1;
			for(int j:i.blocks) {
				int row = (j-1)/size;
				int column = (j-1)%size;
				HBox Row =(HBox)puzzle_box.getChildren().get(row);
				BorderPane Cell = (BorderPane)Row.getChildren().get(column);
				
				if(flag==1) {
					if(i.blocks.size()==1) {
						Text txt = new Text(Integer.toString(i.tot));
						txt.setFill(Color.BLUE);
						txt.setStroke(Color.WHITE);
						txt.setStrokeWidth(1);
						txt.setStrokeType(StrokeType.OUTSIDE);
						txt.setFont(Font.font ("Vintage",FontWeight.BOLD, 12+100/size));
						Cell.setCenter(txt);
						BorderPane.setAlignment(Cell.getCenter(), Pos.CENTER);
					}
					else {
						Text txt = new Text(Integer.toString(i.tot).concat("+"));
						txt.setFill(Color.DEEPSKYBLUE);
						txt.setStroke(Color.WHITE);
						txt.setStrokeWidth(1);
						txt.setStrokeType(StrokeType.OUTSIDE);
						txt.setFont(Font.font ("Vintage",FontWeight.BOLD, 10+50/size));
						Cell.setCenter(txt);
						BorderPane.setAlignment(Cell.getCenter(), Pos.TOP_LEFT);
					}
					flag = 0;
				}
				
		    	//if not first row , if its already taken, if its already valid
		    	if(j > size && i.blocks.contains(j - size)) Cell.setTop(null);
		    	//if not last row
		    	if(j <= size * size - size && i.blocks.contains(j + size)) Cell.setBottom(null);
		    	//if not first column
		    	if(j%size != 1 && i.blocks.contains(j- 1)) Cell.setLeft(null);
		    	//if not last column
		    	if(j%size != 0 && i.blocks.contains(j+ 1)) Cell.setRight(null);
			}
		}
		//if region has one element put total it in center
		//otherwise we put in center but top left align with small font
		//finally add the operation and the number
	}
	public void solve() {}
	private int[][] primitive_soln(int n){
		int[][] soln = new int[n][n];
		for(int i=1; i <= n; i++) {
			for(int j = 1;j<=n;j++) {
				int num = (j + i - 1)%n;
				soln[i-1][j-1]= (num==0)? n:num;
			}
		}
		return soln;
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
	private ArrayList<Region> initial_region(int n) {
		ArrayList<Region> Free = new ArrayList<>();
		Region R1 = new Region();
		for(int i=1;i<=n*n;i++) {
			R1.blocks.add(i);
		}
		Free.add(R1);
		return Free;
	}
}



class Region
{
	public Region() {
		blocks = new ArrayList<>();
		operator =0;
		tot = 0;
	}
	public Region(ArrayList<Integer> in) {
		blocks = new ArrayList<>();
		for(int i : in) {
			blocks.add(i);
		}
		operator = 0;
		tot = 0;
	}
	public void setOp(int op) {
		operator = op%4;
		//+ - * /
	}
	public void setVals(int puzzle[][]) {
		vals = new ArrayList<>();
		blocks.sort(null);
		if(operator >1)tot = 1;
		for(int i:blocks) {
			int row = (i-1)/puzzle[0].length;
			int column = (i-1)%puzzle[0].length;
			int num= puzzle[row][column];
			vals.add(num);
			switch(operator) {
			case 0:
				tot+=num;
				break;
			case 1:
				tot-=num;
				break;
			case 2:
				tot*=num;
				break;
			case 3:
				tot/=num;
				break;
			}
		}
	}
	public ArrayList<Integer> blocks;
	public ArrayList<Integer> vals;
	public int operator;
	public int tot;
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

