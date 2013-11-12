package com.technobear.eigenharp.creator;

public class CreatorApp
{
	public static void main(String[] args)
	{
		CreatorApp app=new CreatorApp(args);
		app.execute();
	}

	static final String VERSION ="2.2.0";
	static final char[] _optionsWithParams= new char[] {'F','S','D','C'};
	private String _device;
	private CreatorConfig _creatorConfig;
	private int _argPos;
	private String[] _args;
	private String _configFile;
	private String _configCommand;
	private String _url;
	private String _scriptName;
	private String _scriptDesc;
	private String _outputFile;
	private boolean _debug;
	private boolean _unbuffered;
	private IProcessor _processor;

	public CreatorApp(String[] args)
	{
		_args=args;
		_argPos=0;
	}
	
	public void execute()
	{
		if(processOptions() && processCommandLine())
		{
			init();
			run();
		}
		else
		{
			displayUsage();
		}
	}

	private void run()
	{ 
		if(_creatorConfig!=null)
		{
			_processor.open();
			if(_scriptName!=null)
			{
				createScriptHeader();
			}
			_creatorConfig.execute();
			_processor.close();
			return;
		}
		displayUsage();
	}

	private void createScriptHeader()
	{
		_processor.process("name");
		_processor.process("    "+_scriptName);
		if(_scriptDesc!=null)
		{
			_processor.process("description");
			_processor.process(_scriptDesc);
		}
		_processor.process("");
		_processor.process("script");
	}

	private boolean processCommandLine()
	{
		// don't need args if executing single command

		if(_argPos<_args.length) 
			_device=_args[_argPos++];
		else 
			return false;

		if(_configCommand!=null) return true;

		
		if(_argPos<_args.length) 
			_configFile=_args[_argPos++];
		else 
			return false;
		
		return true;
	}

	private void init()
	{
		if(_debug)
		{
			_processor = new ScreenProcessor();
		}
		else if (_outputFile!=null)
		{
			_processor = new FileOutputProcessor(_outputFile);
		}
		else
		{
			_url="http://localhost:55553";
			_processor = new BelcantoXmlRpcProcessor(_url,!_unbuffered);
			
		}
		if(_configCommand!=null)
		{
			_creatorConfig=new CreatorConfig(_processor,_device,_configCommand,false);
		}
		else
		{
			_creatorConfig=new CreatorConfig(_processor,_device,_configFile,true);
		}
	}

	private boolean processOptions()
	{
		boolean optFinished=false;
		while(!optFinished && _argPos<_args.length)
		{
			String arg=_args[_argPos];
			if(arg.startsWith("-"))
			{
				for(int i=1;i<arg.length();i++)
				{
					boolean ret=false;
					char ch=arg.charAt(i);
					if(optionRequiresArg(ch))
					{
						ret=processOption(ch,_args[++_argPos]);
					}
					else
					{
						ret=processOption(ch);
					}
					if(!ret)
					{
						return false;
					}
				}
				_argPos++;
			}
			else
			{
				optFinished=true;
			}
		}
		return true;
	}

	private boolean optionRequiresArg(char ch)
	{
		for (int i=0;i<_optionsWithParams.length;i++ )
		{
			if(ch==_optionsWithParams[i])
			{
				return true;
			}
		}
		return false;
	}

	private boolean processOption(char ch)
	{
		switch(ch)
		{
		case 'h' : return false;
		case 'd' : _debug=true; break;
		case 'u' : _unbuffered=true; break;
		default:
			return false;
		}
		return true;
	}

	private boolean processOption(char ch, String arg)
	{
		switch(ch)
		{
		case 'F' : _outputFile = arg; break;
		case 'D' : _scriptDesc = arg; break;
		case 'S' : _scriptName = arg; break;
		case 'C' : _configCommand = arg; break;
		default:
			return false;
		}
		return true;
	}

	private void displayUsage()
	{
		System.out.println("Creator, version:"+VERSION);
		System.out.println("Usage: creator.sh [-hdu] [-S script name] [-F ouput file] [-D description] (-C 'config command' DEVICE | DEVICE CONFIG)");
		System.out.println("-h help");
		System.out.println("-d debug");
		System.out.println("-u unbuffered, execute commands individually, default false");
		System.out.println("-S create script, called name");
		System.out.println("-F output script or debug to file");
		System.out.println("-D description to be used for script file");
		System.out.println("-C config command to run, disables config file!");
		System.out.println("device = pico|tau|alpha");
		System.out.println("configuration file to use");
	}
}
