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
	private String _file;
	private IProcessor _processor;
	@SuppressWarnings("unused")
	private String _device;
	private HashMap<String, String> _varMap;

	public BelcantoTemplate(IProcessor processor, String device, String file, HashMap<String,String> varmap)
	{
		_file=file;
		_device=device;
		_processor=processor;
		_varMap=varmap;
	}

	public boolean execute()
	{
		try
		{
			StringBuffer cmd=new StringBuffer();
			String line;
			FileInputStream fis=new FileInputStream(_file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			try
			{
				while ((line = br.readLine()) != null) 
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
						cmd.append(cmdline+" ");
					}
				}
				if(!_processor.process(cmd.toString())) return false;
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
				}
				return true;
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
		return false;
	}
}
