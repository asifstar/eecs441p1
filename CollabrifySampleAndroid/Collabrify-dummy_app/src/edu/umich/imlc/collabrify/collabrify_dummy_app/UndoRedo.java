package edu.umich.imlc.collabrify.collabrify_dummy_app;
/*
 * THIS CLASS IS PROVIDED TO THE PUBLIC DOMAIN FOR FREE WITHOUT ANY
 * RESTRICTIONS OR ANY WARRANTY.
 */

import java.util.LinkedList;
import java.util.Stack;

//import edu.umich.imlc.collabrify.collabrify_dummy_app.TextViewUndoRedo.EditHistory;
//import edu.umich.imlc.collabrify.collabrify_dummy_app.TextViewUndoRedo.EditTextChangeListener;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A generic undo/redo implementation for TextViews.
 */


public class UndoRedo {
	protected EditText textArea;
	protected Stack<String> undoStack;
	protected Stack<String> redoStack;
	
    public UndoRedo() {
    	undoStack = new Stack<String>();
    	redoStack = new Stack<String>();
}
			
}
 