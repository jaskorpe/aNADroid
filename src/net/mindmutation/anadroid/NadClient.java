package net.mindmutation.anadroid;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;

import android.view.View;
import android.view.View.OnClickListener;

import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;

import java.io.OutputStream;
import java.io.IOException;


public class NadClient extends Activity
{
    private Button volumeUp;
    private Button volumeDown;
    private Button sourceUp;
    private Button sourceDown;

    private Spinner sourceSpinner;

    private TextView error;

    private Socket socket;
    private InetAddress addr;
    private OutputStream output;
    private byte[] command;


    private void sendCommand(String command)
    {
	try {
	    // Fix user defined host
	    addr = InetAddress.getByName("knuth.ping.uio.no");
	    socket = new Socket(addr, 1234);
	} catch (UnknownHostException e) {
	    error.setText("Unknown host: " + e.getMessage());
	    return;
	} catch (IOException e) {
	    error.setText("IO: " + e.getMessage());
	    return;
	} catch (SecurityException e) {
	    error.setText("Security: " + e.getMessage());
	    return;
	}
		
	try {
	    output = socket.getOutputStream();
	    output.write(command.getBytes());
	    socket.close();
	} catch(IOException e) {
	    error.setText("Could not write command: " + e.getMessage());
	    return;
	}
	
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	error = (TextView)this.findViewById(R.id.error);

	volumeUp = (Button)this.findViewById(R.id.volUp);
	volumeUp.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		sendCommand("\rMain.Volume+\r\0");
	    }
	});

	volumeDown = (Button)this.findViewById(R.id.volDown);
	volumeDown.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		sendCommand("\rMain.Volume-\r\0");
	    }
	});

	sourceUp = (Button)this.findViewById(R.id.srcUp);
	sourceUp.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		sendCommand("\rMain.Source+\r\0");
	    }
	});

	sourceDown = (Button)this.findViewById(R.id.srcDown);
	sourceDown.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		sendCommand("\rMain.Source-\r\0");
	    }
	});

	sourceSpinner = (Spinner)this.findViewById(R.id.sourceSpinner);
	ArrayAdapter<CharSequence> adapter =
	    ArrayAdapter.createFromResource(
					    this, R.array.srcList,
					    android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	sourceSpinner.setAdapter(adapter);
	sourceSpinner.setOnItemSelectedListener(new OnSourceSelectedListener());
    }


    public class OnSourceSelectedListener implements OnItemSelectedListener
    {
	public void onItemSelected(AdapterView<?> parent, View view,
				   int pos, long id)
	{
	    Toast.makeText(parent.getContext(), "Source: " +
		parent.getItemAtPosition(pos).toString(),
			   Toast.LENGTH_LONG).show();

	    sendCommand("\rMain.Source=" +
			parent.getItemAtPosition(pos).toString() + "\r\0");
	}

	public void onNothingSelected(AdapterView parent)
	{
	}
    }
}
