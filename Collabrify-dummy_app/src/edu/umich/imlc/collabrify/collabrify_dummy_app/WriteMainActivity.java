package edu.umich.imlc.collabrify.collabrify_dummy_app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import com.google.protobuf.InvalidProtocolBufferException;

import edu.umich.imlc.android.common.Utils;
import edu.umich.imlc.collabrify.client.CollabrifyAdapter;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WriteMainActivity extends Activity {

	// Privates
	private EditText writePad;
	private TextView label;
	private TextView label2;
	private EditText userName;
	private String userNameStr;
	
	
	private Button undoBtn;
	private Button redoBtn;
	
	
	private ByteArrayInputStream baseFileBuffer;
	private ByteArrayOutputStream baseFileReceiveBuffer;
	private long sessionId;
	private String sessionName;
	private CollabrifyClient myClient;
	private ArrayList<String> tags = new ArrayList<String>();
	
	
	private AlertDialog.Builder msgWindow;
	
	
	private static final Level LOGGING_LEVEL = Level.ALL;
	
	private static String TAG = "writify";
	
	private CollabrifyListener collabrifyListener;
	
	
	private LinkedList<TextProperties> undoStack = new LinkedList<TextProperties>();
	private LinkedList<TextProperties> redoStack = new LinkedList<TextProperties>();
	
	
	
	
	public void CreateCollabrify(){
		
	}
	
	
	public void CreateDefaultSession(){
		Random rand = new Random();
        sessionName = "WeWrite " + rand.nextInt(Integer.MAX_VALUE);
        
        try{
        
        	myClient.createSession(sessionName, tags, null, 0);
        	Log.i("writify", "Created Session: " + sessionName);
        	
        }catch(CollabrifyException e){
        	Log.e(TAG, "error", e);
        }
		
		
		
	}
	
	
	public void initControls(){
		writePad = (EditText) findViewById(R.id.writeText);
		label = (TextView) findViewById(R.id.textView1);
		label2 = (TextView) findViewById(R.id.textView2);
		
		undoBtn = (Button) findViewById(R.id.undoBtn);
		redoBtn = (Button) findViewById(R.id.redoBtn);
		
		userName = (EditText) findViewById(R.id.userName);
		
		msgWindow = new AlertDialog.Builder(this);
		
		
	}
	
	
	public void undoEvent(){
		undoBtn.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){
	    		if (undoStack.size() > 0){
	    			TextProperties tp = undoStack.pop();
	    			TextProperties tp2 = new TextProperties();
	    			tp2.typedText = writePad.getText().toString();
	    			writePad.setText(tp.typedText);
	    			redoStack.push(tp2);
	    			
	    		}else{
	    			label.setText("Nothing to undo!");
	    			
	    		}
	    	}
	    });
	    
		
	}
	
	public void redoEvent(){
		redoBtn.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){
	    		if (redoStack.size() > 0){
	    			TextProperties tp = redoStack.pop();
	    			TextProperties tp2 = new TextProperties();
	    			tp2.typedText = writePad.getText().toString();
	    			writePad.setText(tp.typedText);
	    			undoStack.push(tp2);
	    		}else{
	    			label.setText("Nothing to redo!");
	    			
	    		}
	    	}
	    });
	    
		
	}
	

	public String prevText;
	public String nextText;
	public Boolean catchUndoRedoEvent;
	
	public void writePadListener(){
		 writePad.addTextChangedListener(new TextWatcher(){
		    	@Override
		    	public void afterTextChanged(Editable s) {
		    	   // This is triggered after change	

		    		if (writePad.hasFocus()){
		    			TextProperties tp = new TextProperties();
			    		tp.typedText = prevText;
		    			undoStack.push(tp);
		    			for (int i=0; i<undoStack.size(); i++){
		    				System.out.println("=>" + undoStack.get(i));
		    			}
		    		}
		    	}
		    	 
		    	@Override
		    	   public void beforeTextChanged(CharSequence s, int start, 
		    	     int count, int after) {
		    			label.setText("Previous : " + s.toString());
		    			//TextProperties tp = new TextProperties();
		    			if (writePad.hasFocus()){
		    				prevText = s.toString();
		    			}
		    			
		    		// Before Change
		    		//label.setText("OnTyping: " + s + " NumChar: " + count + " StartPos: " + start + " Before: " + before);
		    	   }
		    	 
		    	@Override
		    	   public void onTextChanged(CharSequence s, int start, 
		    			     int before, int count) {
		    		
		    		//label2.setText("OnTyping: " + s + " NumChar: " + count + " StartPos: " + start + " Before: " + before);
		    		//1label2.setText(writePad.getText().)
		    		
		    		if( myClient != null && myClient.inSession() )
		            {
		              try
		              {
		            	InputProp.Properties ip = InputProp.Properties.newBuilder()
		            			.setTypedText(writePad.getText().toString())
		            			.setCursorPosition(writePad.getSelectionEnd())
		            			.setMode(InputProp.Properties.EditMode.INSERT)
		            			.setUserName(userNameStr).build();
		            	
		            	
		                myClient.broadcast(ip.toByteArray(),
		                    "lol");
		                
		                
		                Log.i(TAG, "myClient Info=> " + myClient.displayName() + " " + myClient.currentSessionOwner().getId() + " " + myClient.currentSessionOrderId() + " " + myClient.currentSessionParticipantCount() + " " + myClient.accountGmail()  + " " + myClient.currentSessionParticipants().size());
		              }
		              catch( CollabrifyException e )
		              {
		                Log.e(TAG, "error", e);
		              }
		            }
		    		
		    		
		    	}
		    	
		    	
		    	

		    });
		    
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_main);
		
		
		
		// init controls()
		initControls();
		
		// init on change
		writePadListener();
		
		//
		undoEvent();
		
		redoEvent();
		
		/** Collabrify Listener **/
		collabrifyListener = new CollabrifyAdapter()
	    {

	      @Override
	      public void onDisconnect()
	      {
	        Log.i(TAG, "disconnected");
	        runOnUiThread(new Runnable()
	        {

	          @Override
	          public void run()
	          {
	            //connectButton.setText("CreateSession");
	          }
	        });
	      }

	      @Override
	      public void onReceiveEvent(final long orderId, int subId,
	          String eventType, final byte[] data)
	      {
	        Utils.printMethodName(TAG);
	        Log.d(TAG, "RECEIVED SUB ID:" + subId);
	        runOnUiThread(new Runnable()
	        {
	          @Override
	          public void run()
	          {
	            Utils.printMethodName(TAG);
	            
	            InputProp.Properties ip;
				try {
					ip = InputProp.Properties.parseFrom(data);
					
					if (!ip.getUserName().equals(userNameStr)){
						writePad.setText(ip.getTypedText().toCharArray(), ip.getCursorPosition(), ip.getTypedText().length());
					}
					
					//label.setText(ip.getTypedText());
				} catch (InvalidProtocolBufferException e) {
					// TODO Auto-generated catch block
					Log.e("writify", "Failed to parseFrom");
				}
	            //String message = new String(data);
	            //writePad.setText(message);
	            
	          }
	        });
	      }

	      @Override
	      public void onReceiveSessionList(final List<CollabrifySession> sessionList)
	      {
	        if( sessionList.isEmpty() )
	        {
	          Log.i(TAG, "No session available");
	          return;
	        }
	        List<String> sessionNames = new ArrayList<String>();
	        for( CollabrifySession s : sessionList )
	        {
	          sessionNames.add(s.name());
	        }
	        final AlertDialog.Builder builder = new AlertDialog.Builder(
	            WriteMainActivity.this);
	        builder.setTitle("Choose Session").setItems(
	            sessionNames.toArray(new String[sessionList.size()]),
	            new DialogInterface.OnClickListener()
	            {
	              @Override
	              public void onClick(DialogInterface dialog, int which)
	              {
	                try
	                {
	                  sessionId = sessionList.get(which).id();
	                  sessionName = sessionList.get(which).name();
	                  myClient.joinSession(sessionId, null);
	                }
	                catch( CollabrifyException e )
	                {
	                  Log.e(TAG, "error", e);
	                }
	              }
	            });

	        runOnUiThread(new Runnable()
	        {

	          @Override
	          public void run()
	          {
	            builder.show();
	          }
	        });
	      }

	      @Override
	      public void onSessionCreated(long id)
	      {
	        Log.i(TAG, "Session created, id: " + id);
	        sessionId = id;
	        runOnUiThread(new Runnable()
	        {

	          @Override
	          public void run()
	          {
	            //connectButton.setText(sessionName);
	          }
	        });
	      }

	      @Override
	      public void onError(CollabrifyException e)
	      {
	        Log.e(TAG, "error", e);
	      }

	      @Override
	      public void onSessionJoined(long maxOrderId, long baseFileSize)
	      {
	        Log.i(TAG, "Session Joined");
	        if( baseFileSize > 0 )
	        {
	          //initialize buffer to receive base file
	          baseFileReceiveBuffer = new ByteArrayOutputStream((int) baseFileSize);
	        }
	        runOnUiThread(new Runnable()
	        {

	          @Override
	          public void run()
	          {
	            //connectButton.setText(sessionName);
	          }
	        });
	      }

	      /*
	       * (non-Javadoc)
	       * 
	       * @see
	       * edu.umich.imlc.collabrify.client.CollabrifyAdapter#onBaseFileChunkRequested
	       * (long)
	       */
	      @Override
	      public byte[] onBaseFileChunkRequested(long currentBaseFileSize)
	      {
	        // read up to max chunk size at a time
	        byte[] temp = new byte[CollabrifyClient.MAX_BASE_FILE_CHUNK_SIZE];
	        int read = 0;
	        try
	        {
	          read = baseFileBuffer.read(temp);
	        }
	        catch( IOException e )
	        {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	        }
	        if( read == -1 )
	        {
	          return null;
	        }
	        if( read < CollabrifyClient.MAX_BASE_FILE_CHUNK_SIZE )
	        {
	          // Trim garbage data
	          ByteArrayOutputStream bos = new ByteArrayOutputStream();
	          bos.write(temp, 0, read);
	          temp = bos.toByteArray();
	        }
	        return temp;
	      }

	      /*
	       * (non-Javadoc)
	       * 
	       * @see
	       * edu.umich.imlc.collabrify.client.CollabrifyAdapter#onBaseFileChunkReceived
	       * (byte[])
	       */
	      @Override
	      public void onBaseFileChunkReceived(byte[] baseFileChunk)
	      {
	        try
	        {
	          if( baseFileChunk != null )
	          {
	            baseFileReceiveBuffer.write(baseFileChunk);
	          }
	          else
	          {
	            runOnUiThread(new Runnable()
	            {
	              @Override
	              public void run()
	              {
	                writePad.setText(baseFileReceiveBuffer.toString());
	              }
	            });
	            baseFileReceiveBuffer.close();
	          }
	        }
	        catch( IOException e )
	        {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	        }
	      }

	      /*
	       * (non-Javadoc)
	       * 
	       * @see
	       * edu.umich.imlc.collabrify.client.CollabrifyAdapter#onBaseFileUploadComplete
	       * (long)
	       */
	      @Override
	      public void onBaseFileUploadComplete(long baseFileSize)
	      {
	        runOnUiThread(new Runnable()
	        {

	          @Override
	          public void run()
	          {
	            writePad.setText(sessionName);
	          }
	        });
	        try
	        {
	          baseFileBuffer.close();
	        }
	        catch( IOException e )
	        {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	        }
	      }
	    };

	    boolean getLatestEvent = false;

	    // Instantiate client object
	    try
	    {
	    	
	    	Random rand = new Random();
	        userNameStr = "wewrite" + rand.nextInt(Integer.MAX_VALUE);
	    	
	      myClient = new CollabrifyClient(this, "asifaziz@umich.edu", userNameStr,
	          "441fall2013@umich.edu", "XY3721425NoScOpE", getLatestEvent,
	          collabrifyListener);
	    }
	    catch( CollabrifyException e )
	    {
	      e.printStackTrace();
	    }


	    tags.add("sample");
	    
	    

		//CreateDefaultSession();
	    try {
			myClient.requestSessionList(tags);
		} catch (CollabrifyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.write_main, menu);
		return true;
	}

}
