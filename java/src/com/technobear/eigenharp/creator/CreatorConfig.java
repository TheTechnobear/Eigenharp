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

	private String _configFile;
	private IProcessor _processor;
	private String _device;
	private String _configCommand;
	private static final String TEMPLATE_SUFFIX = ".bc";
	private static final String TEMPLATES_DIR="templates";
	private static final String LAYOUT_MODULE = "KeygroupLayout";
	private static final String ITERATOR_MODULE = "Iterate";
	

	public CreatorConfig(IProcessor processor, String device,String fileOrCommand, boolean isFile)
	{
		if(isFile)
			_configFile=fileOrCommand;
		else
			_configCommand=fileOrCommand;
		_device=device;
		_processor=processor;
	}

	public boolean execute()
	{
		if(_configCommand!=null)
		{
			return executeLine(_configCommand);
		}
		else
		{
			try
			{
				String line;
				FileInputStream fis=new FileInputStream(_configFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				try
				{
					while ((line = br.readLine()) != null) 
					{
						if(!executeLine(line)) return false;
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
				System.err.println("unable process config file:"+e.getMessage());
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
		if(buf.length() == 0 || buf.charAt(0)=='#')
		{
		}
		else
		{
			int i=1;
			HashMap<String, String> args=new HashMap<String, String>();
			String[] segs=line.split(":");
			String rmodule=segs[0];
			while(i<segs.length)
			{
				String var="VAR"+i;
				String arg=segs[i++];
				args.put(var,arg);
			}

			String module=rmodule;
			
			return executeModule(module, args);
		}
		return true;
	}

	private boolean iterateModule(HashMap<String, String> args)
	{
		String varName;
		int varOffset=1;
		String var=args.get("VAR1");
		if(var.contains("="))
		{
			String[] v=var.split("=");
			varName=v[0];
			varOffset=Integer.parseInt(v[1]);
		}
		else 
		{
			varName=var;
			varOffset=1;
		}
		String[] varList=args.get("VAR2").split(",");
		String module = args.get("VAR3");

		for(int i=0;i<varList.length;i++)
		{
			HashMap<String, String> newArgs=new HashMap<String, String>();
			for (int z=0;z<args.size()-3;z++)
			{
				String v=args.get("VAR"+(z+4));
				if(v.equalsIgnoreCase(varName))
				{
					v=varList[i];
				}
				else if(v.equalsIgnoreCase("%IDX%"))
				{
					v=Integer.toString(i+1);
				}
				else if(v.equalsIgnoreCase("%IDX-OFFSET%"))
				{
					v=Integer.toString(i+varOffset);
				}
				String vn="VAR"+(z+1);
				newArgs.put(vn,v);
			}
			boolean r=executeModule(module, newArgs);
			if(!r) return false;
		}
			
		return true;
	}

	private boolean executeModule(String module, HashMap<String, String> args)
	{
		if(module.endsWith(TEMPLATE_SUFFIX))
		{
			String dir=TEMPLATES_DIR;
			String file=module;
			
			// config always use unix directory separators, convert to windows if applicable
			int idx=module.lastIndexOf("/");
			if(idx>=0)
			{
				String rdir=module.substring(0,idx);
				file=module.substring(idx+1,module.length());
				dir=dir+File.separator+rdir.replace("/", File.separator);
				module=dir+File.separator+file;
			}
			
			File f;
			f=new File(dir+File.separator+file);
			if(f.exists())
			{
				BelcantoTemplate t=new BelcantoTemplate(_processor,_device,f.getPath(),true,args);
				if(!t.execute()) return false;
			}
			f=new File(dir+File.separator+_device+File.separator+file);
			if(f.exists())
			{
				BelcantoTemplate t=new BelcantoTemplate(_processor,_device,f.getPath(),true,args);
				if(!t.execute()) return false;
			}
		}
		else if(module.equalsIgnoreCase(LAYOUT_MODULE))
		{
			KeygroupLayout t=new KeygroupLayout(_processor,_device, args);
			if(!t.execute()) return false;
		}
		else if (module.equalsIgnoreCase(ITERATOR_MODULE))
		{
			return iterateModule(args);
		}
		
		return true;
	}
}
