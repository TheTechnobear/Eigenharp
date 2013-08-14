package com.technobear.eigenharp.creator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class BelcantoXmlRpcProcessor implements IProcessor
{
    XmlRpcClient _client;
    
    public BelcantoXmlRpcProcessor(String url)
	{
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try
		{
			config.setServerURL(new URL(url+"/RPC2"));
	        _client = new XmlRpcClient();
	        _client.setConfig(config);
		} catch (MalformedURLException e)
		{
			System.err.println("cannot create client to connect to eigenD:"+e.getMessage());
		}
 	}

	@Override
	public boolean process(String command)
	{
	     Vector<String> params = new Vector<String>();
	     params.addElement(command);

	    Object result;
		try
		{
			System.out.println("executing:"+command);
			result =  _client.execute("execBelcanto", params);
			System.out.println("exec belcanto returned:"+result);
			
			return ((Boolean)result).booleanValue();
		} catch (XmlRpcException e)
		{
			System.err.println("execBelcanto failed:"+e.getMessage());
			return false;
		}
	}

}
