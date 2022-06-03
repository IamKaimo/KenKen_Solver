package application;

import java.util.ArrayList;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
	public ListView<String> log;
	public ArrayList<String> log_usr = new ArrayList<>();
	public ArrayList<String> log_dev = new ArrayList<>();
	public Button test_btn;
	public ComboBox<Integer> cases_no;
	public ComboBox<String> test_sizes;
	public ToggleButton dev;
	public Label iter_txt;

	public void initialize() {
		size_cb.getItems().addAll("3x3","4x4","5x5","6x6","7x7","8x8","9x9");
		algo_cb.getItems().addAll("Backtracking");
		cases_no.getItems().addAll(10,20,50,100);
		test_sizes.getItems().addAll("Mix","3x3","4x4","5x5","6x6","7x7","8x8","9x9");
		size_cb.setValue("3x3");
		algo_cb.setValue("Backtracking");
		cases_no.setValue(10);
		test_sizes.setValue("Mix");
		algo_txt.setDisable(true);
		solve_btn.setDisable(true);
		algo_cb.setDisable(true);
    }
	public void generate_btn(ActionEvent e) {
		size = size_cb.getValue().charAt(0) - 48;
		algo_txt.setDisable(false);
		solve_btn.setDisable(false);
		algo_cb.setDisable(false);
		solve_btn.setText("Solve");
		generate();
		draw(Puzzle);
		setDomain(Puzzle);
	}
	public void solve_btn(ActionEvent e) {	
		if(solve_btn.getText().equals("Solve")) {
			solve_btn.setText("Unsolve");
			algo_cb.setDisable(true);
			long t1 = System.currentTimeMillis();
			long t2;
			switch(algo_cb.getValue()) {
			case("Backtracking"):
				Solu = solve_backtracking(Puzzle);
				t2 = System.currentTimeMillis();
				log_usr.add("Algorithm:		Backtracking");
				log_usr.add("Size:				"+size_cb.getValue());
				log_usr.add("Time Taken:		" + Long.toString(t2-t1) +"ms");
				log_usr.add("Checks No.:		" + Integer.toString(checks));
				log_usr.add("--------------------------------------------------------");
				checks =0; 
				break;
			case("Backtracking (FC)"):
				break;
			case("Backtracking (AC)"):
				break;
			}
			log.setItems(FXCollections.observableList(log_usr));
			log.scrollTo(log.getItems().size()-1);
			draw(Solu);
		}
		else {
			solve_btn.setText("Solve");
			algo_cb.setDisable(false);
			draw(Puzzle);
		}
	}
	public void test_btn(ActionEvent e) {
		log_dev.add("---	Iterations:"+Integer.toString(cases_no.getValue())+"	---		Size:"+test_sizes.getValue()+"	---");
		Random rand = new Random();
		long t1,t2;
		long c1=0;
		long ti1= 0;
		Solu = new ArrayList<>();
		for(int i=0;i<cases_no.getValue();i++) {
			if(test_sizes.getValue().charAt(0)=='M') size = rand.nextInt(7)+3;
			else size = test_sizes.getValue().charAt(0) - 48;
			generate();
			setDomain(Puzzle);
			t1 = System.currentTimeMillis();
			solve_backtracking(Puzzle);
			c1+=checks;
			checks = 0;
			t2 = System.currentTimeMillis();
			ti1+=t2-t1;
		}
		log_dev.add("**First Algorithm:		Backtracking");
		log_dev.add("Total Time Taken:		" + Long.toString(ti1) +"ms");
		log_dev.add("Total Checks No.:		" + Long.toString(c1));
		log_dev.add("--------------------------------------------------------");
		log.setItems(FXCollections.observableList(log_dev));
		log.scrollTo(log.getItems().size()-1);
	}
	public void dev_btn(ActionEvent e) {
		if(dev.isSelected()) {
			log.setItems(FXCollections.observableList(log_dev));
			cases_no.setVisible(true);
			test_sizes.setVisible(true);
			test_btn.setVisible(true);
			iter_txt.setVisible(true);

			
			size_cb.setVisible(false);
			algo_cb.setVisible(false);
			gene_btn.setVisible(false);
			solve_btn.setVisible(false);
			algo_txt.setVisible(false);
			
		}
		else {
			log.setItems(FXCollections.observableList(log_usr));
			cases_no.setVisible(false);
			test_sizes.setVisible(false);
			test_btn.setVisible(false);
			iter_txt.setVisible(false);
			
			size_cb.setVisible(true);
			algo_cb.setVisible(true);
			gene_btn.setVisible(true);
			solve_btn.setVisible(true);
			algo_txt.setVisible(true);
		}
	}
	public void clr_btn(ActionEvent e) {
		if(dev.isSelected()) {
			log_dev.clear();
			log.setItems(FXCollections.observableList(log_dev));
		}
		else {
			log_usr.clear();
			log.setItems(FXCollections.observableList(log_usr));
		}
		draw(null);
	}
	
	//////////////////////Algorithm Code//////////////////////
	public ArrayList<Region> Puzzle = new ArrayList<>();
	public ArrayList<Region> Solu = new ArrayList<>();
	public int size = 3;
	int checks = 0;

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
	private void draw(ArrayList<Region> Puzzle) {
		puzzle_box.getChildren().clear();
		if(Puzzle==null)return;
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
				double cell_w = Row.getPrefWidth()/size;
				double cell_h= Row.getPrefHeight();
				BorderPane Cell = (BorderPane)Row.getChildren().get(column);
				if(flag==1) {
					if(i.blocks.size()==1) {
						Text txt = new Text(Integer.toString((int)i.tot));
						txt.setFill(Color.BLUE);
						txt.setStroke(Color.WHITE);
						txt.setStrokeWidth(1);
						txt.setStrokeType(StrokeType.OUTSIDE);
						txt.setFont(Font.font ("Vintage",FontWeight.BOLD, 12+100/size));
						Cell.setCenter(txt);
						BorderPane.setAlignment(Cell.getCenter(), Pos.CENTER);
					}
					else {
						Text txt;
						if(i.tot-(int)i.tot>0)	
						{
							String nu = Double.toString(i.tot);
							if(nu.indexOf(".")+4<nu.length())nu=nu.substring(0, nu.indexOf(".")+4);
							txt = new Text(nu.concat(i.getOp()));
						}
						else
							txt = new Text(Integer.toString((int)i.tot).concat(i.getOp()));
						
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
		    	if(j > size && i.blocks.contains(j - size)) {
		    		Cell.setTop(new Rectangle(0.9*cell_w,0.5*cell_h/50,Color.LIGHTGRAY));
		    		BorderPane.setAlignment(Cell.getTop(), Pos.TOP_CENTER);
		    	}
		    	//if not last row
		    	if(j <= size * size - size && i.blocks.contains(j + size)) {
		    		Cell.setBottom(new Rectangle(0.9*cell_w,0.5*cell_h/50,Color.LIGHTGRAY));
					BorderPane.setAlignment(Cell.getBottom(), Pos.BOTTOM_CENTER);
		    	}
		    	//if not first column
		    	if(j%size != 1 && i.blocks.contains(j- 1)) {
		    		Cell.setLeft(new Rectangle(0.5*cell_w/50,0.9*cell_h,Color.LIGHTGRAY));
					BorderPane.setAlignment(Cell.getLeft(), Pos.CENTER_LEFT);
		    	}
		    	//if not last column
		    	if(j%size != 0 && i.blocks.contains(j+ 1)) {
		    		Cell.setRight(new Rectangle(0.5*cell_w/50,0.9*cell_h,Color.LIGHTGRAY));
					BorderPane.setAlignment(Cell.getRight(), Pos.CENTER_RIGHT);
		    	}
			}
		}
	}
	private ArrayList<Region> solve_backtracking(ArrayList<Region> puzz) {
		int reg_index = -1;
		for(int i = 0;i<puzz.size();i++) {
			if(puzz.get(i).blocks.size()>1) {
				if(reg_index == -1 || puzz.get(i).domains.size() < puzz.get(reg_index).domains.size()) {
					reg_index = i;
				}
			}
		}
		if(reg_index == -1)return puzz;
		for(int dom_index=0;dom_index<puzz.get(reg_index).domains.size();dom_index++) {
			checks++;
			//Copy The Array
			ArrayList<Region> cpy = new ArrayList<Region>();
			for(Region x:puzz) cpy.add(new Region(x));	
			
			try_domain(cpy,reg_index,dom_index);
			if(validate(cpy)==false) continue;
			cpy = solve_backtracking(cpy);
			if(cpy !=null) return cpy;
		}
		return null;	
	}	private void setDomain(ArrayList<Region> puzz) {
		//Loop at each Region not completed
		for(int i = 0;i<puzz.size();i++) {
			//////////////////////////////////////////////////////////////
			Region current = puzz.get(i);
			if(current.blocks.size()==1)continue;
			ArrayList<ArrayList<Integer>> domain = new ArrayList<>();
			//Generate Domain Full Pattern for each cell
			for(int j = 0;j<current.blocks.size();j++) {
				domain.add(new ArrayList<Integer>());
				for(int k = 1;k<=size;k++)domain.get(j).add(k);
			}
			//Reduce Domain Pattern for each cell
			for(int j = 0;j<puzz.size();j++) {
				if(puzz.get(j).blocks.size()!=1)continue;
				int index = puzz.get(j).blocks.get(0)-1;
				int value = (int)puzz.get(j).tot;
				for(int k =0;k<current.blocks.size();k++) {
					int myindex = current.blocks.get(k)-1;
					if((int)index/size == (int)myindex/size)
					{
						if(domain.get(k).contains(value))
							domain.get(k).remove(domain.get(k).indexOf(value));
					}
					else if((int)index%size == (int)myindex%size)
					{
						if(domain.get(k).contains(value))
							domain.get(k).remove(domain.get(k).indexOf(value));
					}
				}
			}
			//All Possible Domains in region
			int max = 1;
			for(ArrayList<Integer> x : domain) max*=x.size();
			current.domains = new ArrayList<>();
			for(int j = 0;j<max;j++) {
				int div = 1;
				current.domains.add(new ArrayList<Integer>());
				for(int k = 0;k<domain.size();k++) {
					ArrayList<Integer> x = domain.get(k);
					div*=x.size();
					int num =x.get((j/(max/div))%x.size());
					current.domains.get(j).add(num);
				}
			}
			//////////////////////////////////////////////////////////////
			//Inner region constraints
			for(int c=0;c<current.domains.size();c++) {
				int bf = 0;
				ArrayList<Integer> dom = current.domains.get(c);
				for(int j=0;j<dom.size();j++) {
					if(bf == 1)break;
					int index = current.blocks.get(j);
					int value = dom.get(j);
					for(int k=j+1;k<dom.size();k++) {
						int nindex = current.blocks.get(k);
						int nvalue = dom.get(k);
						if((index-1)/size == (nindex-1)/size && value==nvalue) {
							current.domains.remove(c);
							c--;
							bf = 1;
							break;
						}
						else if((index-1)%size == (nindex-1)%size && value==nvalue) {
							current.domains.remove(c);
							c--;
							bf = 1;
							break;
						}
					}
				}
			}
			//////////////////////////////////////////////////////////////
			//Operator in region constraints
			double tot = 0;
			switch(current.getOp()) {
			case("+"):
				for(int j=0;j<current.domains.size();j++) {
					ArrayList<Integer> dom = current.domains.get(j);
					tot = 0;
					for(int k : dom) {
						tot+=k;
					}
					if(tot !=current.tot) {
						current.domains.remove(j);
						j--;
					}
				}
				break;
			case("*"):
				for(int j=0;j<current.domains.size();j++) {
					ArrayList<Integer> dom = current.domains.get(j);
					tot =1;
					for(int k : dom) {
						tot*=k;
					}
					if(tot !=current.tot) {
						current.domains.remove(j);
						j--;
					}
				}
				break;
			case("-"):
				for(int j=0;j<current.domains.size();j++) {
					ArrayList<Integer> dom = current.domains.get(j);
					tot = 0;
					for(int k : dom) {
						if(tot==0)tot=k;
						else tot-=k;
					}
					if(tot !=current.tot) {
						current.domains.remove(j);
						j--;
					}
				}
				break;
			case("/"):
				for(int j=0;j<current.domains.size();j++) {
					ArrayList<Integer> dom = current.domains.get(j);
					tot = 0;
					for(int k : dom) {
						if(tot==0)tot=k;
						else tot/=k;
					}
					if(tot !=current.tot) {
						current.domains.remove(j);
						j--;
					}
				}
				break;
			}
			//////////////////////////////////////////////////////////////
		}
	}
	private void try_domain(ArrayList<Region> puzz,int reg_index,int dom_index) {
		Region current = puzz.get(reg_index);
		Region ret = new Region(current.blocks);
		ret.domains = new ArrayList<>();
		ret.domains.add(new ArrayList<Integer>());
		ArrayList<Integer> vals = current.domains.get(dom_index);
		for(int i = 0;i<vals.size();i++) {
			ret.domains.get(0).add(vals.get(i));
			Region New = new Region(current.blocks.get(i),vals.get(i));
			puzz.add(New);
		}
		puzz.remove(reg_index);
		puzz.sort(new RegionComparator());
	}
	private boolean validate(ArrayList<Region> puzz) {
		for(int i = 0;i<puzz.size();i++) {
			if(puzz.get(i).blocks.size()!=1)continue;
			int index = puzz.get(i).blocks.get(0)-1;
			int value = (int)puzz.get(i).tot;
			for(int j =i+1;j<puzz.size();j++) {
				if(puzz.get(j).blocks.size()!=1)continue;
				int sindex = puzz.get(j).blocks.get(0)-1;
				int svalue = (int)puzz.get(j).tot;
				if((int)index/size == (int)sindex/size && value == svalue) return false;
				else if(index%size == sindex%size && value == svalue) return false;
			}
		}
		return true;
	}
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
	public Region(int index,int val) {
		blocks = new ArrayList<>();
		blocks.add(index);
		operator =0;
		tot = val;
	}
	public Region(Region x) {
		blocks = new ArrayList<>();
		for(int i : x.blocks) {
			blocks.add(i);
		}
		operator =x.operator;
		tot = x.tot;
		domains = new ArrayList<>();
		if(x.domains!=null) {
			for(ArrayList<Integer> i : x.domains) {
				ArrayList<Integer> neo = new ArrayList<>(i);
				domains.add(neo);
			}
		}
		
		
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
		if(blocks.size()==2) {
			if(op <= 5)operator = 3;
			else operator = op%2;
		}
		else operator = op%2;
	}
	public String getOp() {
		switch(operator) {
		case 0:
			return "+";
		case 1:
			return "*";
		case 2:
			return "-";
		case 3:
			return "/";
		}
		return null;
	}
	public void setVals(int puzzle[][]) {
		blocks.sort(null);
		if(operator ==1)tot = 1;
		if(getOp()=="/") {
			int i = blocks.get(0);
			int row = (i-1)/puzzle[0].length;
			int column = (i-1)%puzzle[0].length;
			int num= puzzle[row][column];
			i = blocks.get(1);
			row = (i-1)/puzzle[0].length;
			column = (i-1)%puzzle[0].length;
			int num2= puzzle[row][column];
			if((double)num/num2 - num/num2 > 0) {
				operator = 2;
			}
			else {
				tot = num/num2;
				return;
			}
		}
		
		for(int i:blocks) {
			int row = (i-1)/puzzle[0].length;
			int column = (i-1)%puzzle[0].length;
			int num= puzzle[row][column];
			switch(getOp()) {
			case "+":
				tot+=num;
				break;
			case "*":
				tot*=num;
				break;
			case "-":
				if(tot==0)tot=num;
				else tot-=num;
				break;
			}
		}
	}
	public ArrayList<Integer> blocks;
	public ArrayList<ArrayList<Integer>> domains;    
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

