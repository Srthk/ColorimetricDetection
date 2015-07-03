package com.example.glucodetector;


//import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.*;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
//import android.widget.TableLayout;
//import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.util.Log;
import android.widget.ArrayAdapter;


public class ViewResults extends ListActivity{
	
	//private ArrayList<String> results = new ArrayList<String>();
	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.result_page);
		openAndQueryDatabase();
		displayResultList();
		//int i=0,j=0;
	}
		/*TextView t1=(TextView)findViewById(R.id.tview);
		t1.append("Results: (Total Count=" + resultSet.getCount() + ")\n");
						
			do
			{ 	int sno=++j;
				t1.append("" + sno);
				for(i=0; i<resultSet.getColumnCount(); i++)
				{
					String temp=resultSet.getString(i);
					t1.append("\t\t");
					t1.append(temp);
				}
				t1.append("\n");
			}while(resultSet.moveToNext());*/
			
						
		/*TableLayout l1 = (TableLayout) findViewById(R.id.table2);
		l1.setStretchAllColumns(true);
		l1.bringToFront();
	       	
	        	do
	        	{	TableRow tr =  new TableRow(this);
	        		TableRow.LayoutParams tlparams = new TableRow.LayoutParams( 
	        			TableRow.LayoutParams.WRAP_CONTENT, 
	        			TableRow.LayoutParams.WRAP_CONTENT); 
	        			TextView t1 = new TextView(this);
	        			t1.setLayoutParams(tlparams); 
	        			TextView t2 = new TextView(this);
	        			t2.setLayoutParams(tlparams);
	        			TextView t3 = new TextView(this);
	        			t3.setLayoutParams(tlparams);
	        			TextView t4 = new TextView(this);
	        			t4.setLayoutParams(tlparams);
	        			TextView t5 = new TextView(this);
	        			t5.setLayoutParams(tlparams);
				       // t1.setBackgroundResource(R.drawable.border);
						t1.setText(resultSet.getString(0));
						t2.setText(resultSet.getString(1));
						t3.setText(resultSet.getString(2));
						t4.setText(resultSet.getString(3));
						t5.setText(resultSet.getString(5));
						
						tr.addView(t1);
						tr.addView(t2);
						tr.addView(t3);
						tr.addView(t4);
						tr.addView(t5);
					
	        		l1.addView(tr);
	        	}while(resultSet.moveToNext()); */
			
		private void displayResultList() {
			TextView tView = new TextView(this);
	        //tView.setText("Timestamp" + "\t" + "Gluco1" + "\t" + "Gluco2" + "\t" + "Gluco3" + "\t" + "Gluco4");
			tView.setText("GLUCOSE LEVELS in mg/dL");
	        getListView().addHeaderView(tView);
	        
	       /* setListAdapter(new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_1, results));
	        getListView().setTextFilterEnabled(true);*/
			
			//ListView list = (ListView) findViewById(R.id.SCHEDULE);
			SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.row,
		            new String[] {"Time", "g1", "g2", "g3", "g4"}, new int[] {R.id.TIME_CELL, R.id.g1, R.id.g2, R.id.g3, R.id.g4});
			setListAdapter(mSchedule);
			//list.setAdapter(mSchedule); 
			
			
		}
		private void openAndQueryDatabase() {
			try {
				SQLiteDatabase Glucodb = openOrCreateDatabase("glucoDB",MODE_PRIVATE,null);
				Cursor c = Glucodb.rawQuery("Select * from Results",null);
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("Time", "Date & Time");
				map.put("g1", "Using HSV (Range)");
				map.put("g2", "Using HSV (Gradient)");
				map.put("g3", "Using LAB (Range)");
				map.put("g4", "Using LAB (Gradient)");
				mylist.add(map);
				if (c != null ) {
		    		if  (c.moveToFirst()) {
		    			do {
		    				map = new HashMap<String, String>();
		    				String s1=c.getString(0);
							String s2=c.getString(1);
							String s3=c.getString(2);
							String s4=c.getString(3);
							String s5=c.getString(4);
		    				//results.add(s1 + "\t " +  s2 + "\t " +  s3 + "\t " +  s4 + "\t " +  s5);
							
							map.put("Time", s1);
							map.put("g1", s2);
							map.put("g2", s3);
							map.put("g3", s4);
							map.put("g4", s5);
							mylist.add(map);
		    			}while (c.moveToNext());
		    		} 
		    	}			
			} catch (SQLiteException se ) {
	        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
	        } 

		}
		
} 
		
		

		

