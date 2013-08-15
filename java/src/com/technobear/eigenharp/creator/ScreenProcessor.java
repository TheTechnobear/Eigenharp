package com.technobear.eigenharp.creator;

public class ScreenProcessor extends BaseProcessor
{

	@Override
	public boolean process(String command)
	{
		System.out.println(command);
		return true;
	}

}
