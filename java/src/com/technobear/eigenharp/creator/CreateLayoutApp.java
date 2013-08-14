package com.technobear.eigenharp.creator;

public class CreateLayoutApp {
	public static void main(String [] args)
	{
		CreateLayout app = new CreateLayout(args);
		if(app.init())
		{
			app.generate();
		}
	}
}
