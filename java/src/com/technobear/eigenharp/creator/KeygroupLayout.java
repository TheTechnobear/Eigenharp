package com.technobear.eigenharp.creator;

import java.util.HashMap;

public class KeygroupLayout
{
	private static final String INDENT = "    ";
	private HashMap<String, String> _args;
	private IProcessor _processor;

	private String _keygroup;
	private int _kgStart;
	private int _kgEnd;
	private int _kgCols;
	private int _kgRows;
	private int _blockRows;
	private int _blockCols;
	private float _courseOffset;
	private int _inc_note;
	private boolean _isRevRow;
	private boolean _isRevCol;
	private StringBuffer _buf;
	private String _device;
	private boolean _isLinear;
	private boolean _setPhysical;
	private boolean _isDiag;
	
	final static String DEVICE_TAU="tau";
	final static String DEVICE_PICO="pico";
	final static String DEVICE_ALPHA="alpha";
	

	public KeygroupLayout(IProcessor processor, String device, HashMap<String, String> args)
	{
		_processor=processor;
		_args=args;
		_device=device;
	}
	
	public boolean execute()
	{
		if(!init()) return false;
		return generate();
	}

	public boolean init()
	{
		if(_args.keySet().size() < 14) 
		{
			System.err.println("KeygroupLayout usage:");
			System.err.println("KeygroupLayout:keygroup:kgstartrow:kgendrow:kgrow:kgcol:blockrow:blockcol:courseoffset:inc_note:rev_row:rev_col:diag:linear:physical:");
			System.err.println("KeygroupLayout:keygroup 1:1:23:22:5:1:0:4.0:1:false:false:true:false:false");
			return false;
		}
		
		_keygroup = _args.get("VAR1");
		_kgStart = Integer.valueOf(_args.get("VAR2"));
		_kgEnd = Integer.valueOf(_args.get("VAR3"));
		_kgRows = Integer.valueOf(_args.get("VAR4"));
		_kgCols = Integer.valueOf(_args.get("VAR5"));
		_blockRows = Integer.valueOf(_args.get("VAR6"));
		_blockCols = Integer.valueOf(_args.get("VAR7"));
		_courseOffset = Float.valueOf(_args.get("VAR8"));
		_inc_note = Integer.valueOf(_args.get("VAR9"));
		_isRevRow = Boolean.valueOf(_args.get("VAR10"));
		_isRevCol = Boolean.valueOf(_args.get("VAR11"));
		_isDiag = Boolean.valueOf(_args.get("VAR12"));
		_isLinear = Boolean.valueOf(_args.get("VAR13"));
		_setPhysical = Boolean.valueOf(_args.get("VAR14"));
		_buf=new StringBuffer();
		return true;
	}

	public boolean generate()
	{
		if(!generateOffsets()) return false;
		if(_setPhysical && !generatePhysicalMapping()) return false;
		if(!generateMusicalMapping()) return false;
		return true;
	}
	
	private boolean generatePhysicalMapping() 
	{
		if(!_processor.startBlock()) return false;
		createHey();
		_buf.append(" physical mapping to [");
		{
			int rowOffset = _kgStart - 1;
			for (int c=1;c<=_kgCols;c++)
			{
				for (int r=1;r<=_kgEnd;r++)
				{
					int row=r-rowOffset;
					if(row > 0 & row<=_kgRows)
					{
						_buf.append(INDENT).append("[[");
						_buf.append(Integer.toString(c));
						_buf.append(",");
						_buf.append(Integer.toString(r));
						_buf.append("],[");
						_buf.append(Integer.toString(c)).append(",").append(Integer.toString(row));
						_buf.append("]],");
					}
				}
			}
		}
		_buf.delete(_buf.length()-1, _buf.length());
		_buf.append("] set");

		if(!_processor.process(_buf.toString()));
		_buf.setLength(0);
		return _processor.endBlock();
	}
	
	private boolean generateMusicalMapping() 
	{
		if(!_processor.startBlock()) return false;
		createHey();
		_buf.append(" musical mapping to [");
		if (_isLinear)
		{
			boolean isTau = _device.equalsIgnoreCase(DEVICE_TAU);
			int rowOffset = _kgStart - 1;
			for (int c=1;c<=_kgCols;c++)
			{
				int numRows=_kgEnd;
				if(isTau)
				{
					if(c<3 && _kgEnd>16) numRows=16;
				}
				for (int r=1;r<=numRows;r++)
				{
					int row=r-rowOffset;
					int keyNum = (c-1) * _kgEnd + r;
					if (isTau && _kgEnd>16) 
					{
						if (c>=2) keyNum-=4;
						if (c>2) keyNum-=4;
					}
					if(row > 0 & row<=_kgRows)
					{
						generateRowCol(1,keyNum,c,row);
					}
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
		_buf.delete(_buf.length()-1, _buf.length());
		_buf.append("] set");
		
		if(!_processor.process(_buf.toString()));
		_buf.setLength(0);
		return _processor.endBlock();
	}
	
	private boolean generateRowCol(int icol, int irow, int col, int row)
	{
		int blockSize = _blockCols * _blockRows;
		int course = 0;
		int note = 0;
		if (_isDiag)
		{
			note =  (! _isRevCol ? col-1 : _kgCols - col)  * _inc_note + 1; 
			course = (! _isRevRow ? row-1 : _kgRows - row ) + (_kgCols-note) + 1;
		}
		else if(blockSize > 0 )
		{
			//block modes
			int c = ! _isRevCol ? col-1 : _kgCols - col ;
			int blockNum = (row-1) / _blockRows ;
			int blockPos = (row-1) % _blockRows + c * _blockRows + 1; 
			course = blockNum +1 ;
			note = blockPos * _inc_note;
		}
		else if (_blockRows > 0)
		{
			// vertical
			course = ! _isRevCol ? col : _kgCols - ( col - 1 ) ;
			note = (! _isRevRow ? row-1 : _kgRows - row)  * _inc_note + 1; 
		}
		else
		{
			//horizonal
			course = (! _isRevRow ? row-1 : _kgRows - row ) + 1;
			note =  (! _isRevCol ? col-1 : _kgCols - col)  * _inc_note + 1; 
		}
		_buf.append(INDENT).append("[[");
		_buf.append(Integer.toString(icol));
		_buf.append(",");
		_buf.append(Integer.toString(irow));
		_buf.append("],[");
		_buf.append(Integer.toString(course)).append(",").append(Integer.toString(note));
		_buf.append("]],");
		return true;
	}
	
	private void createHey()
	{
		_buf.append(_keygroup+" hey ");
	}
	
	boolean generateOffsets()
	{
		if(!_processor.startBlock()) return false;
		int numCourses = 0;
		if( _blockRows > 0 && _blockCols > 0 )
		{
			numCourses = (_kgRows/_blockRows);
		}
		else
		{
			numCourses = ( _blockRows >0 ? _kgCols : _kgRows);
		}
		createHey();
		_buf.append(" course offset to [0.0");
		for (int i=1;i<numCourses;i++)
		{
			_buf.append(",");
			_buf.append(Float.toString(_courseOffset));
		}
		_buf.append("] set");
		if(!_processor.process(_buf.toString()));
		_buf.setLength(0);
		return _processor.endBlock();
	}
}
