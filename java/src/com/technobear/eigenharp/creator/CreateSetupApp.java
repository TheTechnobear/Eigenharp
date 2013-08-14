package com.technobear.eigenharp.creator;

public class CreateSetupApp
{
	public static void main(String[] args)
	{
		CreateSetupApp app=new CreateSetupApp(args);
		app.execute();
	}

	static final char[] _optionsWithParams= new char[] {'F'};
	private String _device;
	private CreatorConfig _creatorConfig;
	private int _argPos;
	private String[] _args;
	private String _file;
	private String _url;
	

	public CreateSetupApp(String[] args)
	{
		_args=args;
		_argPos=0;
	}
	
	public void execute()
	{
		if(processOptions() && processCommandLine())
		{
			run();
		}
	}

	private void run()
	{ 
		if(_creatorConfig!=null)
		{
			_creatorConfig.execute();
		}
	}

	private boolean processCommandLine()
	{
		while(_argPos<_args.length)
		{
			_device=_args[_argPos++];
			_file=_args[_argPos++];
			_url="http://localhost:55553";
			IProcessor p = new BelcantoXmlRpcProcessor(_url);
//			IProcessor p = new ScreenProcessor();
			_creatorConfig=new CreatorConfig(p,_device,_file);
	
		}
		return true;
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
						displayUsage();
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
		System.out.println("option:" + ch);
		
		return true;
	}

	private boolean processOption(char ch, String arg)
	{
		System.out.println("option:" + ch + " with "+arg);
		
		return true;
	}

	private void displayUsage()
	{
		System.out.println("usage:");
	}
}
