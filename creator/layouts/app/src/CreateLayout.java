
public class CreateLayout
{
	private static String NL=System.getProperty("line.separator");
	private static String INDENT="      ";
	private String[] _args;
	private String _keygroup;
	private int _kgCols;
	private int _kgRows;
	private float _courseOffset;
	private boolean _isReverse;
	private int _increment;
	private StringBuffer _buf;
	private int _blockRows;
	private int _blockCols;
	private boolean _isLinear;
	private boolean _setPhysical;
	private int _kgStart;
	private int _kgEnd;

	public CreateLayout(String[] args) {
		_args=args;
	}

	public static void main(String [] args)
	{
		CreateLayout app = new CreateLayout(args);
		if(app.init())
		{
			app.generate();
		}
	}
	
	public boolean init()
	{
		if(_args.length < 8) 
		{
			System.out.println("CreateLayout usage:");
			System.out.println("CreateLayout keygroup kgstartrow kgendrow kgrow kgcol blockrow blockcol courseoffset increment linear physical");
			System.out.println("CreateLayout \"keygroup 1\" 1 23 22 5 1 0 4.0 1 true");
			System.out.println(_args[2]);
			return false;
		}

		int i=0;

		_keygroup = _args[i++];
		_kgStart = Integer.valueOf(_args[i++]);
		_kgEnd = Integer.valueOf(_args[i++]);
		_kgRows = Integer.valueOf(_args[i++]);
		_kgCols = Integer.valueOf(_args[i++]);
		_blockRows = Integer.valueOf(_args[i++]);
		_blockCols = Integer.valueOf(_args[i++]);
		_courseOffset = Float.valueOf(_args[i++]);
		_increment = Integer.valueOf(_args[i++]);
		if(_increment < 0 )
		{
			_isReverse = true;
			_increment *= -1;
		}
		_isLinear = Boolean.valueOf(_args[i++]);
		_setPhysical = Boolean.valueOf(_args[i++]);
		_buf=new StringBuffer();
		return true;
	}

	private boolean generate()
	{
		if(!generateOffsets()) return false;
		if(_setPhysical && !generatePhysicalMapping()) return false;
		if(!generateMusicalMapping()) return false;
		System.out.println(_buf.toString());
		return true;
	}
	
	private boolean generatePhysicalMapping() 
	{
		createHey();
		_buf.append(" physical mapping to [").append(NL);
		if (_isLinear)
		{
			int rowOffset = _kgStart - 1;
			int numKeys =  _kgEnd  * _kgCols;
			for (int keyNum=0 ;keyNum<numKeys;keyNum++)
			{
				int col=(keyNum / _kgEnd)+1;
				int row=(keyNum % _kgEnd)+1 - rowOffset;
				if(row > 0 & row<=_kgRows && col<=_kgCols)
				{
					generateRowCol(1,keyNum+1,col,row);
				}
			}
		}
		else
		{
			int rowOffset = _kgStart - 1;
			for (int c=1;c<=_kgCols;c++)
			{
				for (int r=1;r<=_kgEnd;r++)
				{
					int row=r-rowOffset;
					if(row > 0 & row<=_kgRows)
					{
						generateRowCol(c,r,c,row);
					}
				}
			}
		}
		_buf.delete(_buf.length()-2, _buf.length());
		_buf.append("] set").append(NL);
		
		return true;
	}
	
	private boolean generateMusicalMapping() 
	{
		createHey();
		_buf.append(" musical mapping to [").append(NL);
		if (_isLinear)
		{
			int rowOffset = _kgStart - 1;
			int numKeys =  _kgEnd  * _kgCols;
			for (int keyNum=0 ;keyNum<numKeys;keyNum++)
			{
				int col=(keyNum / _kgEnd)+1;
				int row=(keyNum % _kgEnd)+1 - rowOffset;
				if(row > 0 & row<=_kgRows && col<=_kgCols)
				{
					generateRowCol(1,keyNum+1,col,row);
				}
			}
		}
		else
		{
			int rowOffset = _kgStart - 1;
			for (int c=1;c<=_kgCols;c++)
			{
				for (int r=1;r<=_kgEnd;r++)
				{
					int row=r-rowOffset;
					if(row > 0 & row<=_kgRows)
					{
						generateRowCol(c,r,c,row);
					}
				}
			}
		}
		_buf.delete(_buf.length()-2, _buf.length());
		_buf.append("] set").append(NL);
		
		return true;
	}
	
	private boolean generateRowCol(int icol, int irow, int col, int row)
	{
		int blockSize = _blockCols * _blockRows;
		int course = 0;
		int note = 0;
		if(blockSize > 0 )
		{
			//block modes
			int blockNum = (row-1) / _blockRows ;
			int blockPos = (row-1) % _blockRows + ( col - 1 ) * _blockRows + 1; 
			course = blockNum +1 ;
			note = blockPos * _increment;
		}
		else if (_blockRows > 0)
		{
			// vertical
			course = col;
			note = (! _isReverse ? row-1 : _kgRows - row)  * _increment + 1; 
		}
		else
		{
			//horizonal
			course = (! _isReverse ? row-1 : _kgRows - row ) + 1;
			note =  (col-1)  * _increment + 1; 
		}
		_buf.append(INDENT).append("[[");
		_buf.append(Integer.toString(icol));
		_buf.append(",");
		_buf.append(Integer.toString(irow));
		_buf.append("],[");
		_buf.append(Integer.toString(course)).append(",").append(Integer.toString(note));
		_buf.append("]],").append(NL);
		return true;
	}
	
	private void createHey()
	{
		_buf.append(_keygroup+" hey ");
	}
	
	boolean generateOffsets()
	{
		int numCourses = 0;
		if( _blockRows > 0 && _blockCols > 0 )
		{
			numCourses = (_kgRows/_blockRows) + 1;
		}
		else
		{
			numCourses = ( _blockRows >0 ? _kgCols : _kgRows);
		}
		createHey();
		_buf.append(" course offset to [0");
		for (int i=1;i<numCourses;i++)
		{
			_buf.append(",");
			_buf.append(Float.toString(_courseOffset));
		}
		_buf.append("] set").append(NL);
		return true;
	}
}
