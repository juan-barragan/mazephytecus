package ibqpd.maze;

public class CellPoint {
	public CellPoint(int row, int column) {
		row_ = row;
		column_ = column;
		h_ = 1000*row_ + column_;
	}
	public CellPoint clone()
	{
		return new CellPoint(row_, column_);
	}

	@Override 
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		if (!(o instanceof CellPoint))
			return false;
		CellPoint t = (CellPoint)o;
		return (row_ == t.row_ && column_ == t.column_ );
	}
	
	@Override 
	public int hashCode()
	{
		return h_;
	}	
	
	public int row_;
	public int column_;
	private int h_;

}

