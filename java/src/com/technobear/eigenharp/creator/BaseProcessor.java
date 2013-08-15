package com.technobear.eigenharp.creator;

public abstract class BaseProcessor implements IProcessor
{

	@Override
	public boolean startBlock()
	{
		return true;
	}

	@Override
	public boolean endBlock()
	{
		return true;
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}

	public abstract boolean process(String command);
}
