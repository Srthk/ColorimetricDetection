package com.example.glucodetector;

import java.io.File;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.graphics.Canvas;
import android.graphics.Color;
import android.database.SQLException;
import android.database.sqlite.*;
/*import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;*/

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public class MainActivity extends Activity implements OnClickListener {
	ImageView imVCature_pic;
	Button btnCapture;
	Button viewRes, instruct;
	int L,A,B;
	int gvalue=0,gvalue2=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeControls();
		
	}
	
	public class Lab
	{	double L,A,B;
		public Lab(double L, double A, double B)
		{	this.L=L;
			this.A=A;
			this.B=B;
		}
		/*public void setValue(double L, double A, double B)
		{	
			this.L=L;
			this.A=A;
			this.B=B;
		}*/
	}	

	private void initializeControls() {
		imVCature_pic=(ImageView)findViewById(R.id.imVCature_pic);
		btnCapture=(Button)findViewById(R.id.btnCapture);
		viewRes=(Button)findViewById(R.id.viewRes);
		instruct=(Button)findViewById(R.id.instruct);
		btnCapture.setOnClickListener(this);
		viewRes.setOnClickListener(this);
		instruct.setOnClickListener(this);
				
	}
	@Override
	public void onClick(View v) {
		/* create an instance of intent
		 * pass action android.media.action.IMAGE_CAPTURE 
		 * as argument to launch camera
		 */
		if(v==btnCapture)
		{
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		
			/*create instance of File with name img.jpg*/
			File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
			/*put uri as extra in intent object*/
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			/*start activity for result pass intent as argument and request code */
			startActivityForResult(intent, 1);
		}
		if(v==viewRes) {
			Intent i= new Intent(this, ViewResults.class);
	    	startActivity(i);
		}
		if(v==instruct) {
			Intent i2= new Intent(this, Instructions.class);
	    	startActivity(i2);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//if request code is same we pass as argument in startActivityForResult
		if(requestCode==1){
			//create instance of File with same name we created before to get image from storage
			File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
			//Crop the captured image using an other intent
			try {
				/*the user's device may not support cropping*/
				cropCapturedImage(Uri.fromFile(file));
			}
			catch(ActivityNotFoundException aNFE){
				//display an error message if user device doesn't support
				String errorMessage = "Sorry - your device doesn't support the crop action!";
				Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if(requestCode==2){
			//Create an instance of bundle and get the returned data
			Bundle extras = data.getExtras();
			//get the cropped bitmap from extras
			Bitmap thePic = extras.getParcelable("data");
			//set image bitmap to image view
			imVCature_pic.setImageBitmap(thePic);
			//startActivityForResult(cropIntent, 3);
		}
		if(requestCode==3){
			 // get the returned data
		    Bundle extras = data.getExtras();
		    // get the cropped bitmap
		    Bitmap thePic = extras.getParcelable("data");
		    // retrieve a reference to the ImageView
		    ImageView picView = (ImageView) findViewById(R.id.imVCature_pic);
		    // display the returned cropped image
		    
		    GraphicsUtil graphicUtil = new GraphicsUtil();
		     //picView.setImageBitmap(graphicUtil.getRoundedShape(thePic));
		    picView.setImageBitmap(graphicUtil.getCircleBitmap(thePic, 16));
		    try{
		    fetchRGB(thePic);
		    }catch(Exception e)
		    { System.out.println("Exception Caught: " + e);
		    }
		  
		}

	}
	//create helping method cropCapturedImage(Uri picUri)
	public void cropCapturedImage(Uri picUri){
		//call the standard crop action intent 
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		//indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		//retrieve data on return
		cropIntent.putExtra("return-data", true);
		//start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, 3);
		
	}
	public void fetchRGB(Bitmap bitmap)throws Exception{
		int redColors = 0;
	    int greenColors = 0;
	    int blueColors = 0;
	    int pixelCount = 0;
	    float[] glucose=new float[2];
	    SQLiteDatabase glucoDB = openOrCreateDatabase("glucoDB",MODE_PRIVATE,null);
	    for (int y = 0; y < bitmap.getHeight(); y++)
	    {
	        for (int x = 0; x < bitmap.getWidth(); x++)
	        {
	            int c = bitmap.getPixel(x, y);
	            pixelCount++;
	            redColors += Color.red(c);
	            greenColors += Color.green(c);
	            blueColors += Color.blue(c);
	        }
	    }
	    // calculate average of bitmap r,g,b values
	    int red = (redColors/pixelCount);
	    int green = (greenColors/pixelCount);
	    int blue = (blueColors/pixelCount);
	    String str="NA";
	    TextView t = (TextView)findViewById(R.id.myImageViewText);
	    float[] hsv = new float[3];
	    Color.RGBToHSV(red,green,blue,hsv);
	    rgbToLab(red, green, blue);
	    int LabRes=result();	 
	    double LabRes2=makeGradient(gvalue, gvalue2);
	    LabRes2=Math.round(LabRes2*100.0)/100.0;
	    int largeg=0;
	    if(gvalue2>LabRes)
	    {	largeg=gvalue2;
	    	gvalue2=LabRes;
	    }
	    else
	    largeg=LabRes;
	    glucose=calcGlucose(hsv[0]);
	    glucose[1]=Math.round(glucose[1]*100)/100;
	    if(glucose[0]==0)
	    { str="0 or Out of Range";
	    }
	    else if(glucose[0]==30)
	    {str="0-30";
	    }
	    else if(glucose[0]==80)
	    {str="30-80";
	    }
	    else if(glucose[0]==120)
	    {str="80-120";
	    }
	    else if(glucose[0]==160)
	    {str="120-160";
	    }
	    else if(glucose[0]==230)
	    {str="160-230";
	    }
	    else if(glucose[0]==300)
	    {str="230-300";
	    }
	    else if(glucose[0]==375)
	    {str="300-375";
	    }
	    else if(glucose[0]==450)
	    {str="375-450";
	    }
	    else if(glucose[0]==500)
	    {str=">450";
	    }
	    t.setBackgroundColor(getResources().getColor(R.color.grey));
	    t.setText("Glucose Level (Using HSV range): " + str + " mg/dL " + "\nGlucose Level (Using HSV gradient): " + Float.toString(glucose[1]) + " mg/dL " +  
	    "\nUsing LAB, Glucose Value is between: " + gvalue2 + " and " + largeg + "mg/dL (Closer to " + LabRes + ")" + "\n Precise Value (Using LAB): " + LabRes2 + "mg/dL" );
	    //"\nRed: " + Integer.toString(red) + " Green: " + Integer.toString(green) + " Blue: " + Integer.toString(blue) + "\n Hue: " + Float.toString(hsv[0]) + " Sat: " + Float.toString(hsv[1]) + " Val: " + Float.toString(hsv[2])
	    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

	    try{
	    	    glucoDB.execSQL("CREATE TABLE IF NOT EXISTS Results(Timestamp VARCHAR, Level_using_hsv_range VARCHAR,Level_using_hsv_gradient VARCHAR,Level_using_lab_range VARCHAR,Level_using_lab_gradient VARCHAR);");
	    	    String query=new String("INSERT INTO Results VALUES ('" + currentDateTimeString + "', '" + str + " mg/dL', '" + glucose[1] + " mg/dL', '" + gvalue + "-" + largeg + " mg/dL', '" + LabRes2 + " mg/dL');");
	    	    glucoDB.execSQL(query);
	    }catch(SQLException e){
	    	t.append("Error in SQL query" + e);
	    }

	}
	public void rgbToLab(double ri, double gi, double bi)
	{	L=0; A=0; B=0;
		double L1=0, A1=0, B1=0;
		double r = ri / 255.0;
		double g = gi / 255.0;
		double b = bi / 255.0;

		// D65 standard referent
		double X = 0.950470, Y = 1.0, Z = 1.088830;

		// second, map sRGB to CIE XYZ
		r = r <= 0.04045 ? r/12.92 : Math.pow((r+0.055)/1.055, 2.4);
		g = g <= 0.04045 ? g/12.92 : Math.pow((g+0.055)/1.055, 2.4);
		b = b <= 0.04045 ? b/12.92 : Math.pow((b+0.055)/1.055, 2.4);
		double x = (0.4124564*r + 0.3575761*g + 0.1804375*b) / X,
			   y = (0.2126729*r + 0.7151522*g + 0.0721750*b) / Y,
			   z = (0.0193339*r + 0.1191920*g + 0.9503041*b) / Z;

		// third, map CIE XYZ to CIE L*a*b* and return
		x = x > 0.008856 ? Math.pow(x, 1.0/3) : 7.787037*x + 4.0/29;
		y = y > 0.008856 ? Math.pow(y, 1.0/3) : 7.787037*y + 4.0/29;
		z = z > 0.008856 ? Math.pow(z, 1.0/3) : 7.787037*z + 4.0/29;

				L1 = 116*y - 16;
			    A1 = 500*(x-y);
			    B1 = 200*(y-z);
				L1=L1+0.5;A1=A1+0.5;B1=B1+0.5;
				L=(int)L1;
				A=(int)A1;
				B=(int)B1;
	}
	public int result()
	{		Lab l1=new Lab(L,A,B);
			double min=1000;	//smallest value
			//int gvalue=0;
			//int gvalue2=0;
			double min2=0; //second smallest value
			double Delta_e=compare(l1,new Lab(78,-10,71));
			if(Delta_e < min || Delta_e <min2)
				{	
					if(Delta_e<min)
					{	min2=min;
						min=Delta_e;
						gvalue=0;
					}
					else
					{
						min2=Delta_e;
						gvalue2=0;
					}
				
				}
			Delta_e=compare(l1,new Lab(73,-14,44));
			if(Delta_e < min || Delta_e < min2)
				{	if(Delta_e<min)
						{	min2=min;
							min=Delta_e;
							gvalue=30;
						}	
					else
					{
						min2=Delta_e;
						gvalue2=30;
					}
				}
			Delta_e=compare(l1,new Lab(71,-19,53));
			if(Delta_e < min || Delta_e <min2)
				{	if(Delta_e<min)
						{	min2=min;
							min=Delta_e;
							gvalue=80;
						}	
					else
					{
						min2=Delta_e;
						gvalue2=80;
					}
				}
			Delta_e=compare(l1,new Lab(67,-18,31));
			if(Delta_e < min || Delta_e <min2)
				{	if(Delta_e<min)
						{	min2=min;
							min=Delta_e;
							gvalue=160;
						}	
					else
					{
						min2=Delta_e;
						gvalue2=160;
					}
				}
			Delta_e=compare(l1,new Lab(69,-21,29));
			if(Delta_e < min || Delta_e <min2)
				{	if(Delta_e<min)
						{	min2=min;
							min=Delta_e;
							gvalue=300;
						}	
					else
					{
						min2=Delta_e;
						gvalue2=300;
					}
				}
			Delta_e=compare(l1,new Lab(61,-14,3));
			if(Delta_e < min || Delta_e <min2)
				{	if(Delta_e<min)
						{	min2=min;
							min=Delta_e;
							gvalue=450;
						}	
					else
					{
						min2=Delta_e;
						gvalue2=450;
					}
				}
			return gvalue;
	}
	public double makeGradient(double n1, double n2) throws Exception {
		int n=100;
		double glucValue=0;
		Lab gradient = new Lab(0,0,0);
		Set<String> hashSet = new LinkedHashSet<String>();
	   // int[] arr={0, 30, 80, 160, 300, 450 };
	   if(n1>n2)
	   { double temp=n2;
		 n2=n1;
		 n1=temp;
	   }
	   Lab c1=returnColor(n1);
	   Lab c2=returnColor(n2);
		for (int i = 0; i < n; i++) {
			double alpha = (double)i / (n-1);  // 0.0 <= alpha <= 1.0 
			double L = (1-alpha) * c1.L + alpha * c2.L;
			double a = (1-alpha) * c1.A + alpha * c2.A;
			double b = (1-alpha) * c1.B + alpha * c2.B;
			gradient = new Lab(L, a, b);
			String s=new String("L=" + gradient.L + " A=" + gradient.A + " B=" + gradient.B);
			hashSet.add(s);
		}
		Iterator<String> it = hashSet.iterator();
		double count=0;
		String LAB,Lc,Ac,Bc;
		//String parts[];
		int start,end;
		double Delta_e;
		double min=1000;
		double save=0;
		while (it.hasNext()) 
		{
			count++;
			LAB=new String(it.next());
			//System.out.println(LAB);
			start=LAB.indexOf("=")+1;
			end=LAB.indexOf(" ");
			Lc=new String(LAB.substring(start,end));
		
			start=LAB.indexOf("=", start+1)+1;
			end=LAB.indexOf(" ", end+1);
			Ac=new String(LAB.substring(start,end));
			start=LAB.indexOf("=", start+1)+1;
			Bc=new String(LAB.substring(start));
			Lab l1=new Lab(L,A,B);
			Lab l2=new Lab(Double.parseDouble(Lc),Double.parseDouble(Ac),Double.parseDouble(Bc));
			Delta_e=compare(l1, l2);
			//System.out.println(Delta_e);
			if(Delta_e<min)
				{	min=Delta_e;
					save=count+1;
				}
		}
		glucValue=n1+(((n2-n1)/count)*save);
		System.out.println(min + " " + save + " " + count);
		return glucValue;
	}
	public Lab returnColor(double x) {
		Lab color=new Lab(0,0,0);
		
		if(x==0)
			color=new Lab(78, -10, 71);
		else if(x==30)
			color=new Lab(73, -14, 44);
		else if(x==80)
			color=new Lab(71, -19, 53);
		else if(x==160)
			color=new Lab(67, -18, 31);
		else if(x==300)
			color=new Lab(69, -21, 29);
		else if(x==450)
			color=new Lab(61, -14, 3);
		return color;
	}
	public double compare(Lab lab1, Lab lab2)
    {
        //Set weighting factors to 1
        double k_L = 1.0d;
        double k_C = 1.0d;
        double k_H = 1.0d;
		
        //Calculate Cprime1, Cprime2, Cabbar
        double c_star_1_ab = Math.sqrt(lab1.A * lab1.A + lab1.B * lab1.B);
        double c_star_2_ab = Math.sqrt(lab2.A * lab2.A + lab2.B * lab2.B);
        double c_star_average_ab = (c_star_1_ab + c_star_2_ab) / 2;

        double c_star_average_ab_pot7 = c_star_average_ab * c_star_average_ab * c_star_average_ab;
        c_star_average_ab_pot7 *= c_star_average_ab_pot7 * c_star_average_ab;
		double num=6103515625l;
        double G = 0.5d * (1 - Math.sqrt(c_star_average_ab_pot7 / (c_star_average_ab_pot7 + num))); //25^7
        double a1_prime = (1 + G) * lab1.A;
        double a2_prime = (1 + G) * lab2.A;

        double C_prime_1 = Math.sqrt(a1_prime * a1_prime + lab1.B * lab1.B);
        double C_prime_2 = Math.sqrt(a2_prime * a2_prime + lab2.B * lab2.B);
        //Angles in Degree.
        double h_prime_1 = ((Math.atan2(lab1.B, a1_prime) * 180d / Math.PI) + 360) % 360d;
        double h_prime_2 = ((Math.atan2(lab2.B, a2_prime) * 180d / Math.PI) + 360) % 360d;

        double delta_L_prime = lab2.L - lab1.L;
        double delta_C_prime = C_prime_2 - C_prime_1;

        double h_bar = Math.abs(h_prime_1 - h_prime_2);
        double delta_h_prime;
        if (C_prime_1 * C_prime_2 == 0) delta_h_prime = 0;
        else
        {
            if (h_bar <= 180d)
            {
                delta_h_prime = h_prime_2 - h_prime_1;
            }
            else if (h_bar > 180d && h_prime_2 <= h_prime_1)
            {
                delta_h_prime = h_prime_2 - h_prime_1 + 360.0;
            }
            else
            {
                delta_h_prime = h_prime_2 - h_prime_1 - 360.0;
            }
        }
        double delta_H_prime = 2 * Math.sqrt(C_prime_1 * C_prime_2) * Math.sin(delta_h_prime * Math.PI / 360d);

        // Calculate CIEDE2000
        double L_prime_average = (lab1.L + lab2.L) / 2d;
        double C_prime_average = (C_prime_1 + C_prime_2) / 2d;

        //Calculate h_prime_average

        double h_prime_average;
        if (C_prime_1 * C_prime_2 == 0) h_prime_average = 0;
        else
        {
            if (h_bar <= 180d)
            {
                h_prime_average = (h_prime_1 + h_prime_2) / 2;
            }
            else if (h_bar > 180d && (h_prime_1 + h_prime_2) < 360d)
            {
                h_prime_average = (h_prime_1 + h_prime_2 + 360d) / 2;
            }
            else
            {
                h_prime_average = (h_prime_1 + h_prime_2 - 360d) / 2;
            }
        }
        double L_prime_average_minus_50_square = (L_prime_average - 50);
        L_prime_average_minus_50_square *= L_prime_average_minus_50_square;

        double S_L = 1 + ((.015d * L_prime_average_minus_50_square) / Math.sqrt(20 + L_prime_average_minus_50_square));
        double S_C = 1 + .045d * C_prime_average;
        double T = 1
            - .17 * Math.cos(DegToRad(h_prime_average - 30))
            + .24 * Math.cos(DegToRad(h_prime_average * 2))
            + .32 * Math.cos(DegToRad(h_prime_average * 3 + 6))
            - .2 * Math.cos(DegToRad(h_prime_average * 4 - 63));
        double S_H = 1 + .015 * T * C_prime_average;
        double h_prime_average_minus_275_div_25_square = (h_prime_average - 275) / (25);
        h_prime_average_minus_275_div_25_square *= h_prime_average_minus_275_div_25_square;
        double delta_theta = 30 * Math.exp(-h_prime_average_minus_275_div_25_square);

        double C_prime_average_pot_7 = C_prime_average * C_prime_average * C_prime_average;
        C_prime_average_pot_7 *= C_prime_average_pot_7 * C_prime_average;
		
        double R_C = 2 * Math.sqrt(C_prime_average_pot_7 / (C_prime_average_pot_7 + num));

        double R_T = -Math.sin(DegToRad(2 * delta_theta)) * R_C;

        double delta_L_prime_div_k_L_S_L = delta_L_prime / (S_L * k_L);
        double delta_C_prime_div_k_C_S_C = delta_C_prime / (S_C * k_C);
        double delta_H_prime_div_k_H_S_H = delta_H_prime / (S_H * k_H);

        double CIEDE2000 = Math.sqrt(
            delta_L_prime_div_k_L_S_L * delta_L_prime_div_k_L_S_L
            + delta_C_prime_div_k_C_S_C * delta_C_prime_div_k_C_S_C
            + delta_H_prime_div_k_H_S_H * delta_H_prime_div_k_H_S_H
            + R_T * delta_C_prime_div_k_C_S_C * delta_H_prime_div_k_H_S_H
            );

        return CIEDE2000;
    }
    private double DegToRad(double degrees)
    {
        return degrees * Math.PI / 180;
    }
	public float[] calcGlucose(float h){
		float[] f=new float[2];
		f[0]=0;
		f[1]=0;
		if(h>=50 && h<=60){
			f[0]=0;
		}
		else if(h>60 && h<=66){
			f[0]=30;
			f[1]=calcGradient(60,66,0,30,h);
		}
		else if(h>66 && h<=70){
			f[0]=80;
			f[1]=calcGradient(66,70,30,80,h);
		}
		else if(h>70 && h<79){
			f[0]=120;
			f[1]=calcGradient(70,79,80,120,h);
		}
		else if(h>=79 && h<=83){
			f[0]=160;
			f[1]=calcGradient(79,83,120,160,h);
		}
		else if(h>83 && h<88){
			f[0]=230;
			f[1]=calcGradient(83,88,160,230,h);
		}
		else if(h>=88 && h<=93){
			f[0]=300;
			f[1]=calcGradient(88,93,230,300,h);
		}
		else if(h>93 && h<152){
			f[0]=375;
			f[1]=calcGradient(93,152,300,375,h);
		}
		else if(h>=152 && h<=156){
			f[0]=450;
			f[1]=calcGradient(152,156,375,450,h);
		}
		else if(h>=157 && h<=180){
			f[0]=500;
			f[1]=0;
		}
		return f;
	}
	public float calcGradient(float h1, float h2, float g1, float g2, float hue){
		float steps=100;
		//h1=h1/360;
		//h2=h2/360;
		//hue=hue/360;
		float d=h2-h1;
		float g=g2-g1;
		float ginc=g/steps;
		float p=0,p2=0;
		float hi=0,hi2=0;
		float gret=g1;
		float diff1=0,diff2=0;
		for(int i=0;i<steps;i++){
			p=i/steps;
			p2=(i+1)/steps;
			hi=h1+(d*p);
			hi2=h1+(d*p2);
			/*if(hi<0) 
				hi=hi+1;
			else if(hi>1)
				hi=hi-1;
			if(hi2<0) 
				hi2=hi2+1;
			else if(hi2>1)
				hi2=hi2-1;*/
			diff1=Math.abs(hue-hi);
			diff2=Math.abs(hue-hi2);
			//diff=Math.abs(hi-hi2);
			if(hue>hi && hue>hi2){
				gret=gret+ginc;
				continue;
			}
			else if(diff2>diff1){
				return gret;
			}
			else if(diff1>=diff2){
				gret=gret+ginc;
				return gret;
			}
			//gret=gret+ginc;
		}
		return 0;
	}
}
