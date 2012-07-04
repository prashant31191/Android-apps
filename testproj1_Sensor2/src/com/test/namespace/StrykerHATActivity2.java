package com.test.namespace;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StrykerHATActivity2 extends Activity {
	EditText textOut;
	TextView textIn;
	 public boolean isServer = true;
	 private  ServerSocket mmServerSocket;
	 Socket socket = null; // USed by client to connect to server
	 Socket clientsocket = null; // Used by Server for reading buffer
	 DataOutputStream dataOutputStream = null;
	 DataInputStream dataInputStream = null;
	 private ServerThread mSecureAcceptThread;
	 private ReceiveThread mrecThread = null;
	 public static PrintWriter pw = null;
	 
	 Button buttonSend;
	 Button setServer;
	 Button connect;
	 Button getIP;
	 Button showOrientation;
	 
	 static public StrykerHATActivity2 temp = null;
	 static public int Reference = 0; // 0 for X, 1 for Y, 2 for Z
	 public static  boolean misServer = false;
	 private ConnectThread mConnectThread;
	 /** Called when the activity is first created. */
	 
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     temp = this;
	     setContentView(R.layout.main);
	  
	     textOut = (EditText)findViewById(R.id.textout);
	     buttonSend = (Button)findViewById(R.id.send);
	     textIn = (TextView)findViewById(R.id.textin);
	     getIP = (Button)findViewById(R.id.get_info);
	     
	     
	     getIP.setOnClickListener(buttonGetIPOnClickListener);
	     
	     buttonSend.setOnClickListener(buttonSendOnClickListener);
	     
	     setServer = (Button)findViewById(R.id.set_server);
	     setServer.setOnClickListener(buttonSetServerClickListener);
	     
	     connect = (Button)findViewById(R.id.connect_server);
	     connect.setOnClickListener(buttonConnectServerClickListener);
	     
	     
	     showOrientation = (Button)findViewById(R.id.show_orientation);
	     showOrientation.setOnClickListener(buttonShowOrientationClickListener);
	     
	 }
	 
	 
	    @Override
	    public synchronized void onPause() {
	        super.onPause();
	      
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	       
	    }
	    public void OnDestroy(){
	    	super.onDestroy();
	    	if(mmServerSocket != null)
		       {
		    	   try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	   mmServerSocket = null;
		       }
		       try {
		    	   synchronized(this)
		    	   {
		    		   wait();
		    	   }
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		       if(socket != null)
		       {
		    	   try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       }
	    }
	    
	    
	 public static void sendMessage(String msg)
	 {
		 
		 if(pw != null){
			 pw.println(msg);
		 	pw.flush();
		 }
		 //pw.close();
		 
	    System.out.println(msg);
	 }
	 // The Handler that gets information back from the BluetoothChatService
	    private final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	           
	            case 1:
	                EditText msg1 = (EditText)findViewById(R.id.textout);
	                //Toast.makeText(getApplicationContext(), "Text Recieved, Hurray", 100).show();
	             
	                Bundle bundle = msg.getData();
	                String msg11 = (String) bundle.get("Message");
	                EditText tex = (EditText)findViewById(R.id.textout);
	                tex.setText(msg11);
	                break;
	            }
	        }
	    };
	 Button.OnClickListener buttonSendOnClickListener
	 = new Button.OnClickListener(){

	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
		//Send the textout text to Server
		if(socket.isConnected())
		{
			sendMessage(textOut.getText().toString());
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Not connected to Server", 100).show();
		}
	}};
	
	 Button.OnClickListener buttonSetServerClickListener
	 = new Button.OnClickListener(){

	

	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
		 mSecureAcceptThread = new ServerThread();
		  mSecureAcceptThread.start();
		  misServer = true;
	}};
	
	Button.OnClickListener buttonShowOrientationClickListener
	 = new Button.OnClickListener(){

	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
		Intent intent = new Intent(getApplicationContext(), HATActivity.class);
		startActivity(intent);
	}};
	
	Button.OnClickListener buttonConnectServerClickListener
	 = new Button.OnClickListener(){

	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
		 mConnectThread = new ConnectThread();
		 mConnectThread.start();
	}};
	 Button.OnClickListener buttonGetIPOnClickListener
	 = new Button.OnClickListener(){

	public void onClick(View arg0) {
	 // TODO Auto-generated method stub
	 

	 try {
			 WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			 List<WifiConfiguration> networks = wifiManager.getConfiguredNetworks();
			 int count = networks.size();
			 
			 
			 WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			 int ipAddress = wifiInfo.getIpAddress();
			 WifiInfo info;
			 String ip_str1 = String.format("%d.%d.%d.%d",
					 (ipAddress & 0xff),
					 (ipAddress >> 8 & 0xff),
					 (ipAddress >> 16 & 0xff),
					 (ipAddress >> 24 & 0xff));
			 TextView ip_get = (TextView)findViewById(R.id.textin);
			 ip_get.setText(ip_str1);
		  
		  
	 }
	 finally{
		  if (socket != null){
		   try {
		    socket.close();
		   } catch (IOException e) {
		    // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		  }
	
		 }
	}
	
	 };
	public static String getLocalIpAddressString() {
		 try {
		     for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		         NetworkInterface intf = en.nextElement();
		         for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		             InetAddress inetAddress = enumIpAddr.nextElement();
		             if (!inetAddress.isLoopbackAddress()) {
		                 return inetAddress.getHostAddress().toString();
		             }
		         }
		     }
		 } catch (SocketException ex) {
		     Log.e("IPADDRESS", ex.toString());
		 }
		 return null;
}
	
	
	
	  /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class ServerThread extends Thread {
        // The local server socket
        
        public boolean cancel = false;

        public ServerThread() {
        	 EditText port = (EditText)findViewById(R.id.port);
        
        	try {
				mmServerSocket = new ServerSocket(5555);
				int i = 0;
				i++;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        public void run() {
          
           
        	int i = 0;
            // Listen to the server socket if we're not connected
            while (i < 1) {
            	++i;
	            	try {
	                    // This is a blocking call and will only return on a
	                    // successful connection or an exception
	                    clientsocket = mmServerSocket.accept();
	                } catch (IOException e) {
	                	//Toast.makeText(getApplicationContext(), "Server accept exception", 100).show();
	                }
                
                // If a connection was accepted
                if (clientsocket != null) {
                	String clientMessage = null;
                   //Toast.makeText(getApplicationContext(), "Connection accepted", 100).show();
                	//Start receiev thread here
	                
	                		//mrecThread = new ReceiveThread();
	                		//mrecThread.start();
	                		
	                		BufferedReader inFromClient = null;
							try {
								inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

	                        DataOutputStream outToClient = null;
							try {
								outToClient = new DataOutputStream(clientsocket.getOutputStream());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							while(!cancel)
							{
								if(mmServerSocket != null)
								{
									cancel = false;
								}
								else
								{
									cancel = true;
								}
		                        try {
									clientMessage = inFromClient.readLine();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	
		                        System.out.println("Received: " + clientMessage);
		                        mHandler.obtainMessage(1,1,1,clientMessage).sendToTarget();
		                        Message msg = mHandler.obtainMessage(1);
		                        Bundle bundle = new Bundle();
		                        bundle.putString("Message", clientMessage);
		                        msg.setData(bundle);
		                        mHandler.sendMessage(msg);
		                        
		                        //Aslo send the message to sensor
		                        String delims = ",";
		                        String[] tokens = clientMessage.split(delims);
		                        HATActivity.mLastX_remote = Float.parseFloat(tokens[0]);
		                        HATActivity.mLastY_remote = Float.parseFloat(tokens[1]);
	                        	HATActivity.mLastZ_remote = Float.parseFloat(tokens[2]);
		                        
		                        
		                        
		                        
		                        
		                        try {
									outToClient.writeBytes("I received this: "+ clientMessage +"\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
	                        try {
								inFromClient.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                        try {
								outToClient.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                        
                    }
                }
        this.notifyAll();    
        }
            
        
        }
    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class ConnectThread extends Thread {
        // The local server socket
        
        public boolean cancel = false;

        public ConnectThread() {
        	 EditText port = (EditText)findViewById(R.id.port);
        
        	try {
        		EditText ip = (EditText)findViewById(R.id.textout);
        		socket = new Socket(ip.getText().toString(),5555);
        		Toast.makeText(getApplicationContext(), "Client socket created", 100).show();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		 
        	try {
				pw = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }

        public void run() {
          
         if(socket!= null)
         {
        	 try {
        		 socket.connect(socket.getRemoteSocketAddress());
				Toast.makeText(getApplicationContext(), "Client socket connected to server", 100).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
        }
            

        }
    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class ReceiveThread extends Thread {
        // The local server socket
       
        public ReceiveThread() {
        	
        }

        public void run() {
        	try
        	{
    			BufferedReader fromClient = new BufferedReader(
            	new InputStreamReader(clientsocket.getInputStream()));
            	PrintWriter toClient = 
    			new PrintWriter(clientsocket.getOutputStream(), true);

        		while (true) 
        		{
        			int num = fromClient.read();
//        			Integer numb =  new Integer(fromClient.readLine());
//        			System.out.println("received: " + numb.intValue());
//        			toClient.println(numb.intValue()+1);
        			 mHandler.obtainMessage(1,1,1,num).sendToTarget();
        		}
        	}
        	catch(IOException ex)
        	{
        		System.err.println(ex);
        	}
         
        }
            

        }
    
}