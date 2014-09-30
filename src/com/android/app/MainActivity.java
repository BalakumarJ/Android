package com.android.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

	private Button b1;
	private EditText ed1;
	private TextView tx1; 
	private String msg;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		//Here, I have associated the b1 with the ID button1, ed1 with the ID typebox, 
		// and tx1 with the ID textView2.  Note that textView1 (see xml file) is used to displaya  fixed message.
		
		b1 = (Button) findViewById(R.id.button1);
		ed1 = (EditText) findViewById(R.id.typebox);
		tx1 = (TextView) findViewById(R.id.textView2);
		
		//Here is the Button functionality.  Note how the entire functionality is within the listener.
		b1.setOnClickListener(new View.OnClickListener() 
	    {
			public void onClick(View v) 
	        {
	        System.out.println("Inside Button\n");
	        
	      //Here is the Edit text functionality.
	        msg = ed1.getText().toString();
	        
	      //Toast can be used to display on screen for a short duration
	        Context context = getApplicationContext(); 
	        int duration = Toast.LENGTH_SHORT;
	        Toast toast = Toast.makeText(context, msg, duration); //Toasting message entered
	        toast.show();
	        
	      //Also Display stuff on the Textview
			tx1.setText(msg);
	        ed1.setText(null);
	        }
	    });
			
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }	

	
}



