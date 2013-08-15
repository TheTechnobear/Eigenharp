package com.technobear.eigenharp.creator;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOutputProcessor extends BaseProcessor
{
	BufferedOutputStream _os;
	private String _file;
	final static String NL = System.getProperty("line.separator");
	
	public FileOutputProcessor(String file)
	{
		_file=file;
	}
	
	@Override
	public boolean open()
	{
		try
		{
			_os=new BufferedOutputStream(new FileOutputStream(_file));
		} catch (FileNotFoundException e)
		{
			System.err.println("unable create output stream: "+e.getMessage());
		}
		
		return true;
	}

	@Override
	public boolean close()
	{
		try
		{
			_os.close();
		} catch (IOException e)
		{
			System.err.println("unable create output stream: "+e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean process(String command)
	{
		if(_os==null) return false;
		try
		{
			_os.write(command.getBytes());
			_os.write(NL.getBytes());
		} catch (IOException e)
		{
			System.err.println("unable to process command: "+e.getMessage());
			return false;
		}
		return true;
	}

}
