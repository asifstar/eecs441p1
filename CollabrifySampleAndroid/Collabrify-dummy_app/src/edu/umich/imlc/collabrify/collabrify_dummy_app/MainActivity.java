package edu.umich.imlc.collabrify.collabrify_dummy_app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.lang.Math;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

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
  private Button changeScreenButton;
  private Button homeButton;
  private EditText editText1;
  private TextView editText2;
  private Button undoButton;
  private Button redoButton;
  private String collabString = "";
  private Timer collabTimer = new Timer();

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
    changeScreenButton = (Button) findViewById(R.id.button1);
    homeButton = (Button) findViewById(R.id.mainMenuButton);
    //editText1 = (EditText) findViewById(R.id.editText1);
    //editText2 = (TextView) findViewById(R.id.editText2);
    //undoButton = (Button) findViewById(R.id.undoButton);
    //redoButton = (Button) findViewById(R.id.redoButton);
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
          }
          catch( CollabrifyException e )
          {
            Log.e(TAG, "error", e);
          }
        }
      }
    });
    
    changeScreenButton.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        setContentView(R.layout.text_area);
  	  	//editText1.setText("Hello World");
        editText();
        
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
          Log.i(TAG, "Session name is " + sessionName);
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
  
  public void editText()
  {
      editText1 = (EditText) findViewById(R.id.editText1);
      undoButton = (Button) findViewById(R.id.undoButton);
      redoButton = (Button) findViewById(R.id.redoButton);
      homeButton = (Button) findViewById(R.id.mainMenuButton);
      collabTimer.scheduleAtFixedRate(
    		  new TimerTask()
    		  {
    			  public void run()
    			  {
    				  updateMaster();
    			  }
    		  },
    		  0,
    		  10000);

      class StackInfo
      {
    	  public int cursorLocation;
    	  public String text;
      };
      
      final Stack<StackInfo> undoStack = new Stack<StackInfo>();
      final Stack<StackInfo> redoStack = new Stack<StackInfo>();
      StackInfo undoInfo = new StackInfo();
      undoInfo.cursorLocation = 0;
      undoInfo.text = editText1.getText().toString();
      undoStack.push(undoInfo);	 
      
	  
      editText1.addTextChangedListener(new TextWatcher(){
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
        	  collabString = s.toString();
        	  if (s.charAt(s.length() - 1) == ' ')
        	  {
        		  //TODO: update the server
        		  collabString = "";
        	  }
        	  StackInfo info = new StackInfo();
        	  info.text = editText1.getText().toString();
        	  info.cursorLocation = count + start;
        	  if (!undoStack.isEmpty())
        	  {
	        	  StackInfo previousInfo = new StackInfo();
	        	  previousInfo = undoStack.pop();
	        	  if (previousInfo.cursorLocation - info.cursorLocation > 1 || info.cursorLocation - previousInfo.cursorLocation > 1)
	        	  {
	        		  //TODO: update the server
	        		  collabString = "";
	        	  }
	        	  if (previousInfo.text.equals(info.text))
	        	  {
	        		  undoStack.push(previousInfo);
	        	  }
	        	  else
	        	  {
	        		  undoStack.push(previousInfo);
	        		  undoStack.push(info);
	        	  }
        	  }
          }

		@Override
		public void afterTextChanged(Editable s) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
      });
     
	  undoButton.setOnClickListener(new OnClickListener()
	  {
	    @Override
	    public void onClick(View v)
	    {
	        undoButton = (Button) findViewById(R.id.undoButton);
	        StackInfo info = new StackInfo();
	        if (!undoStack.isEmpty())
	        {
		        info = undoStack.pop();        
		        redoStack.push(info);
		        if (!undoStack.isEmpty()) 
		        {
			        info = undoStack.pop();
			        editText1.setText(info.text);
		        	editText1.setSelection(info.cursorLocation);
			        if (undoStack.isEmpty())
			        {
			        	undoStack.push(info);
			        }
		        }
		        else
		        {
		        	info = redoStack.pop();
		        	undoStack.push(info);
		        }
	        }
	    }
	  });
	  
	  redoButton.setOnClickListener(new OnClickListener()
	  {
	    @Override
	    public void onClick(View v)
	    {
	    	redoButton = (Button) findViewById(R.id.redoButton);
	    	if (!redoStack.isEmpty())
	    	{
	    		StackInfo info = new StackInfo();
		    	info = redoStack.pop();
		    	editText1.setText(info.text);
	    		editText1.setSelection(info.cursorLocation);
	    	}
	    }
	  });
	  
	    homeButton.setOnClickListener(new OnClickListener()
	    {
	      @Override
	      public void onClick(View v)
	      {
	        setContentView(R.layout.activity_main);
	      }
	    });
  }
  public void updateMaster()
  {
	//TODO: push the collabString to the server
	  collabString = "";
  };
}

