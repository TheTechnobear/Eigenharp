package com.technobear.eigenharp.creator;

public interface IProcessor
{
	boolean open();
	boolean process(String command);
	boolean close();
	boolean startBlock();
	boolean endBlock();
}
