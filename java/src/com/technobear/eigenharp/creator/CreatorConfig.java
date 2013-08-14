package com.technobear.eigenharp.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

// represets the 'old' config files which were colon separated one per line
public class CreatorConfig
{

	private String _file;
	private IProcessor _processor;
	private String _device;
	private static String MODULES_DIR="modules";

	public CreatorConfig(IProcessor processor, String device,String file)
	{
		_file=file;
		_device=device;
		_processor=processor;
	}

	public boolean execute()
	{
		try
		{
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
					if(buf.charAt(0)=='#')
					{
					}
					else
					{
						int i=1;
						HashMap<String, String> args=new HashMap<String, String>();
						String[] segs=line.split(":");
//						String[] segs=buf.toString().split(":");
						String template=segs[0];
						while(i<segs.length)
						{
							String var="%VAR"+i+"%";
							String arg=segs[i++];
							args.put(var,arg);
						}
						File f;
						f=new File(MODULES_DIR+File.separator+template);
						if(f.exists())
						{
							BelcantoTemplate t=new BelcantoTemplate(_processor,_device,f.getPath(),args);
							if(!t.execute()) return false;
						}
						f=new File(MODULES_DIR+File.separator+_device+File.separator+template);
						if(f.exists())
						{
							BelcantoTemplate t=new BelcantoTemplate(_processor,_device,f.getPath(),args);
							if(!t.execute()) return false;
						}
					}
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
			System.err.println("unable to open config file:"+e.getMessage());
		} catch (IOException e)
		{
			System.err.println("unable process onfig file:"+e.getMessage());
		}
		return false;
	}
}
