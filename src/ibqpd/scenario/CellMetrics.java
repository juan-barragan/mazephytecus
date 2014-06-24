package ibqpd.scenario;

// Ideal candidate for a singleton: this should be a global constant once created 
public final class CellMetrics {
	public static void setXOffxet(int xOffset) { xOffset_ = xOffset; }
	public static void setYOffxet(int yOffset) { yOffset_ = yOffset; }
	public static void setTileSize(int tileSize) { tileSize_ = tileSize; }
	public static void setCellSize(int cellSize) { cellSize_ = cellSize; }
	
	public static CellMetrics instance() 
	{ 
		if (instance_ == null)
			instance_ = new CellMetrics();
		return instance_; 
	}
	public int getXOffset() { return xOffset_; }
	public int getYOffset() { return yOffset_; }
	public int getTileSize() { return tileSize_; }
	public int getCellSize() { return cellSize_; }
	public int getCellInternalSize() { return cellSize_ - tileSize_; }

	private CellMetrics() {}
	
	private static CellMetrics instance_ = null;
	private static int xOffset_;
	private static int yOffset_;
	private static int tileSize_ = 9;
	private static int cellSize_;
}
