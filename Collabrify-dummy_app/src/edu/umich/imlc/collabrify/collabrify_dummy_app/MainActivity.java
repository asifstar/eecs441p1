package edu.umich.imlc.collabrify.collabrify_dummy_app;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;

public class MainActivity extends Activity
{

  /**
   * Logging level for HTTP requests/responses.
   * <p>
   * To turn on, set to {@link Level#CONFIG} or {@link Level#ALL} and run this
   * from command line: {@code adb shell setprop log.tag.HttpTransport DEBUG}.
   * </p>
   */
  private static final Level LOGGING_LEVEL = Level.ALL;

  private static String TAG = "dummy";

  private CollabrifyClient myClient;
  private TextView broadcastedText;
  private EditText broadcastText;
  private Button broadcastButton;
  private Button connectButton;
  private Button getSessionButton;
  private Button leaveSessionButton;
  private CheckBox withBaseFile;
  private CollabrifyListener collabrifyListener;
  private ArrayList<String> tags = new ArrayList<String>();
  private long sessionId;
  private String sessionName;
  private ByteArrayInputStream baseFileBuffer;
  private ByteArrayOutputStream baseFileReceiveBuffer;
  private Button signInBtn;
  private EditText editText;
  private EditText editText2;
  private EditText FriendsID;
 
  private class EditTextProp {
	  public String inputtext;
	  public int start;
	  public int count;
	  public int before;
  }

  
  public void createWritifyTextInput(){
	  editText = (EditText) findViewById(R.id.editText1);
	  editText2 = (EditText) findViewById(R.id.editText2);
	    
	    editText.addTextChangedListener(new TextWatcher(){
	    	@Override
	    	public void afterTextChanged(Editable s) {
	    		Log.i(TAG, "After: " + s);
	    	   }
	    	 
	    	@Override
	    	   public void beforeTextChanged(CharSequence s, int start, 
	    	     int count, int after) {
	    			
	    		//Log.e(TAG, "Message: " + s);
	    	   }
	    	 
	    	@Override
	    	   public void onTextChanged(CharSequence s, int start, 
	    			     int before, int count) {
	    		
	    		if (editText.hasFocus()) {
		    		Log.i(TAG, "OnTyping: " + s + " Start: " + start + " " + " Count: " + count + " Before: " + before);
		    		editText2.setSelection(start);
		    		editText2.setText(editText.getText());
	    	   }
	    	
	    	}

	    });
	    
	    editText2.addTextChangedListener(new TextWatcher(){
	    	
	    	@Override
	    	public void afterTextChanged(Editable s) {
	    		Log.i(TAG, "After: " + s);
	    	   }
	    	 
	    	@Override
	    	   public void beforeTextChanged(CharSequence s, int start, 
	    	     int count, int after) {
	    			
	    		//Log.e(TAG, "Message: " + s);
	    	   }
	    	 
	    	@Override
	    	   public void onTextChanged(CharSequence s, int start, 
	    			     int before, int count) {
	    		
	    		Log.i(TAG, "OnTyping: " + s + " Start: " + start + " " + " Count: " + count + " Before: " + before);
	    		
	    		if (editText2.hasFocus()) {
		    		editText.setSelection(start);
		    		editText.setText(editText2.getText());
	    		}
	    	   }

	    });
	  
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    withBaseFile = (CheckBox) findViewById(R.id.withBaseFileCheckBox);
    broadcastText = (EditText) findViewById(R.id.BroadcastText);
    broadcastedText = (TextView) findViewById(R.id.BroadcastedText);
    broadcastButton = (Button) findViewById(R.id.BroadcastButton);
    connectButton = (Button) findViewById(R.id.ConnectButton);
    getSessionButton = (Button) findViewById(R.id.getSessionButton);
    leaveSessionButton = (Button) findViewById(R.id.LeaveSessionButton);
    signInBtn = (Button) findViewById(R.id.button1);
    FriendsID = (EditText) findViewById(R.id.FriendsID);
    
    
    
    signInBtn.setOnClickListener(new OnClickListener(){
    	@Override
    	public void onClick(View v){
    		
    		//setContentView(R.layout.linear_main);
    		//createWritifyTextInput();
    		//Intent intent = new Intent(MainActivity.this, MainActivity.class);
    		//startActivity(intent);
    		//overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    		
    		/** NOTE: Always check getText().toString().isEmpty() before
    		 * passing the text
    		 */
    		if( broadcastText.getText().toString().isEmpty() ){
    			Log.e("empty", "empty text");
    		}else{
    			Intent myIntent = new Intent(MainActivity.this, WriteMainActivity.class);
        		myIntent.putExtra("username", broadcastText.getText().toString()); //Optional parameters
        		if (!FriendsID.getText().toString().isEmpty()){
        			myIntent.putExtra("fusername",FriendsID.getText().toString()); //Optional parameters
        		}
        		
        		Log.i("MainActivity", broadcastText.getText().toString());
        		MainActivity.this.startActivity(myIntent);
        		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    		}
    		
    		
    		
    		
    	}
    });
    
    // enable logging
    Logger.getLogger("edu.umich.imlc.collabrify.client")
        .setLevel(LOGGING_LEVEL);
    

    broadcastButton.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if( broadcastText.getText().toString().isEmpty() )
          return;
        if( myClient != null && myClient.inSession() )
        {
          try
          {
            myClient.broadcast(broadcastText.getText().toString().getBytes(),
                "lol");
            broadcastText.getText().clear();
            
            Log.i(TAG, "myClient Info=> " + myClient.displayName() + " " + myClient.currentSessionOwner().getId() + " " + myClient.currentSessionOrderId() + " " + myClient.currentSessionParticipantCount() + " " + myClient.accountGmail()  + " " + myClient.currentSessionParticipants().size());
          }
          catch( CollabrifyException e )
          {
            Log.e(TAG, "error", e);
          }
        }
      }
    });

    connectButton.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        try
        {
          Random rand = new Random();
          sessionName = "Test " + rand.nextInt(Integer.MAX_VALUE);

          if( withBaseFile.isChecked() )
          {
            // initialize basefile data for this example we will use the session
            // name as the data
            baseFileBuffer = new ByteArrayInputStream(sessionName.getBytes());

            myClient.createSessionWithBase(sessionName, tags, null, 0);
          }
          else
          {
            myClient.createSession(sessionName, tags, null, 0);
          }
          
        }
        catch( CollabrifyException e )
        {
          Log.e(TAG, "error", e);
        }
      }
    });

    getSessionButton.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        try
        {
          myClient.requestSessionList(tags);
        }
        catch( Exception e )
        {
          Log.e(TAG, "error", e);
        }
      }
    });

    leaveSessionButton.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        try
        {
          if( myClient.inSession() )
            myClient.leaveSession(false);
        }
        catch( CollabrifyException e )
        {
          Log.e(TAG, "error", e);
        }
      }
    });

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
            connectButton.setText("CreateSession");
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
            String message = new String(data);
            broadcastedText.setText(message);
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
            MainActivity.this);
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
            connectButton.setText(sessionName);
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
            connectButton.setText(sessionName);
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
                broadcastedText.setText(baseFileReceiveBuffer.toString());
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
            broadcastedText.setText(sessionName);
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
      myClient = new CollabrifyClient(this, "user email", "user display name",
          "441fall2013@umich.edu", "XY3721425NoScOpE", getLatestEvent,
          collabrifyListener);
    }
    catch( CollabrifyException e )
    {
      e.printStackTrace();
    }


    tags.add("sample");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }
}
