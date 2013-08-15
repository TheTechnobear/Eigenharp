package com.technobear.eigenharp.creator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

public class BelcantoTemplate
{
	private String _fileOrCommand;
	private IProcessor _processor;
	@SuppressWarnings("unused")
	private String _device;
	private HashMap<String, String> _varMap;
	private boolean _isFile;

	public BelcantoTemplate(IProcessor processor, String device, String fileOrCommand, boolean isFile, HashMap<String,String> varmap)
	{
		_fileOrCommand=fileOrCommand;
		_device=device;
		_processor=processor;
		_varMap=varmap;
		_isFile=isFile;
	}

	public boolean execute()
	{
		if(!_isFile)
		{
			// this allows a line from a template file to be substituted individual i.e. a belcanto phrase with vars in it
			if(!_processor.startBlock()) return false;
			if(!executeLine(_fileOrCommand)) return false;
			return _processor.endBlock();
		}
		else
		{
			try
			{
				String line;
				FileInputStream fis=new FileInputStream(_fileOrCommand);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				try
				{
					if(!_processor.startBlock()) return false;
					while ((line = br.readLine()) != null) 
					{
						if(!executeLine(line)) return false;
					}
					return _processor.endBlock();
				}
				finally
				{
					br.close();
					br = null;
					fis = null;
				}
			} catch (FileNotFoundException e)
			{
				System.err.println("unable to open template file:"+e.getMessage());
			} catch (IOException e)
			{
				System.err.println("unable process template file:"+e.getMessage());
			}
		}
		return false;
	}
	
	private boolean executeLine(String line)
	{
		StringBuffer buf=new StringBuffer(line);
		// remove leading whitespace.
		while(buf.length()>0 && Character.isWhitespace(buf.charAt(0)))
		{
			buf.deleteCharAt(0);
		}
		// ignore comments
		if(buf.length()==0 || buf.charAt(0)=='#')
		{
		}
		else
		{
			String cmdline = buf.toString();
			Iterator<String> iter=_varMap.keySet().iterator();
			while(iter.hasNext())
			{
				String var=iter.next();
				String value=_varMap.get(var);
				if(!var.isEmpty() && value!=null)
				{
					cmdline=cmdline.replace(var, value);
				}
			}
			if(!cmdline.isEmpty())
			{
				if(!_processor.process(cmdline.toString())) return false;
			}
		}
		return true;
	}
}
