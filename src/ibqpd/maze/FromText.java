package ibqpd.maze;


public class FromText implements MazeBuilder {
	public FromText()
	{
	}
	
	public void setString(String m)
	{
		m_= m;
	}
	
	public void buildMaze()
	{
		// Format is rows,columns:cellStates; rows are first
		String[] results = m_.split(":");
		String[] size = results[0].split(",");
		beingBuild_ = new Maze(Integer.parseInt(size[0]), Integer.parseInt(size[1]));	
		beingBuild_.board_ = new int[beingBuild_.rows_][beingBuild_.columns_];
		for(int i=0; i<beingBuild_.rows_; ++i)
			for(int j=0; j<beingBuild_.columns_; ++j) {
				beingBuild_.board_[i][j] = (int) results[1].charAt(i*beingBuild_.columns_ + j);
			}
	}
	
	public Maze getMaze()
	{
		return beingBuild_;
	}
	
	private String m_;
	private Maze beingBuild_;
}

