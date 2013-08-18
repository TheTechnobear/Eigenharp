package com.technobear.eigenharp.creator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class BelcantoXmlRpcProcessor extends BaseProcessor
{
    private XmlRpcClient _client;
	private String _url;
	private StringBuffer _buffer;
	private boolean _buffered;

    
    public BelcantoXmlRpcProcessor(String url)
    {
    	this(url,true);
    }
    public BelcantoXmlRpcProcessor(String url,boolean buffered)
	{
    	_url=url;
    	_buffer = new StringBuffer(1024);
    	_buffered = buffered;
 	}

	@Override
	public boolean open()
	{
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try
		{
			config.setServerURL(new URL(_url+"/RPC2"));
	        _client = new XmlRpcClient();
	        _client.setConfig(config);
		} catch (MalformedURLException e)
		{
			System.err.println("cannot create client to connect to eigenD:"+e.getMessage());
			return false;
		}
        return true;
	}

	@Override
	public boolean process(String command)
	{
		_buffer.append(command);
		_buffer.append(' ');
		if(_buffered) return true;
		return endBlock();
	}

	@Override
	public boolean startBlock()
	{
		if (_client==null) return false;
		
		if(_buffer.length()>0)
		{
			if(!endBlock()) return false;
		}
		_buffer.setLength(0);
		return true;
	}

	@Override
	public boolean endBlock()
	{
		if(_client== null) return false;
		if(_buffer.length()==0) return true;
		
	    Vector<String> params = new Vector<String>();
	    params.addElement(_buffer.toString());

	    Object result;
		try
		{
			result =  _client.execute("execBelcanto", params);
			System.out.println("executed:"+_buffer.toString()+" "+result);
			_buffer.setLength(0);

			return ((Boolean)result).booleanValue();
		} catch (XmlRpcException e)
		{
			System.err.println("execBelcanto failed:"+e.getMessage());
			return false;
		}
	}
}
