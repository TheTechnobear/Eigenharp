package com.technobear.eigenharp.creator;

public class ScreenProcessor implements IProcessor
{

	@Override
	public boolean process(String command)
	{
		System.out.println(command);
		return true;
	}

}
