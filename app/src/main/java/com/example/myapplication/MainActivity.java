package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    String[] data;
    InputStream inputStream;
    //loss=0 main=1 gain=2
    //male=0 female=1
    //any=0 veg=1
    Map<Integer,ArrayList<Integer>>clusters=new HashMap<Integer,ArrayList<Integer>>();
    private EditText name,email,pass,age,weghit,height;
    private Spinner foodtype,activ,plan;
    private RadioButton male,female;
    private Button sign;
    database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize Cluster
        int clusterID=0;
        for (int i=0;i<3;i++){ //plan loss=0 main=1 gain=2

            for (int k=0;k<2;k++){ //type any=0 veg=1
                ArrayList<Integer> values = new ArrayList<Integer>(Arrays.asList(i,k));
                clusters.put(clusterID,values);
                clusterID++;
            }

        }
        for(int i=0;i<6;i++){
            ArrayList<Integer> list=clusters.get(i);
            Log.v("Main","key="+i + "     Values"+list);
        }

        //==========================================================================================

        //load Food Data
        List<String[]> mylist=new ArrayList<>();
        inputStream=getResources().openRawResource(R.raw.www);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try{
            String csvLine;
            while((csvLine = reader.readLine()) !=null)
            {
                data=csvLine.split(",");
                mylist.add(data);
                try{
                    //Log.e("data",""+data[4]+data[5]);

                }catch (Exception e)
                {
                    Log.e("problem",e.toString());
                }
            }

        }catch (IOException ex)
        {
            throw new  RuntimeException("Error"+ex);
        }

        //==========================================================================================

        //add food to Cluster
        int planval=-1;
        int typeval=-1;
        for (int i=1;i<mylist.size();i++){
            String plan=mylist.get(i)[4]; //loss=0 man=1 gain=2
            String veg=mylist.get(i)[5];  //any=0 //veg=1

            if(veg.equals("0")){
                typeval=0;
            }
            else if(veg.equals("1")){
                typeval=1;
            }
            for(int j=0;j<plan.length();j++){
                char Temp=plan.charAt(j);
                if (Temp=='L')
                {
                    planval=0;
                    Log.v("Main","cat "+findcluster(planval,typeval));
                }
              else  if (Temp=='G')
                {
                    planval=2;
                    Log.v("Main","cat "+findcluster(planval,typeval));
                }
            }

            Log.v("Main","cat "+findcluster(1,typeval));

        }



        name=(EditText)findViewById(R.id.editText);
        email=(EditText)findViewById(R.id.editText2);
        pass=(EditText)findViewById(R.id.editText6);
        age=(EditText)findViewById(R.id.editText3);
        weghit=(EditText)findViewById(R.id.editText4);
        height=(EditText)findViewById(R.id.editText5);

        foodtype=(Spinner)findViewById(R.id.spinner3);
        activ=(Spinner)findViewById(R.id.spinner);
        plan=(Spinner)findViewById(R.id.spinner2);

        male=(RadioButton)findViewById(R.id.radioButton2);
        female=(RadioButton)findViewById(R.id.radioButton);

        sign=(Button)findViewById(R.id.button);

        db=new database(this);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int planvalue=-1,gendervalue=-1,foodtypevalue=-1;
                String plantxt=plan.getSelectedItem().toString();
                if (plantxt.equals("Loss Weight")){
                    planvalue=0;
                }
                else if (plantxt.equals("Maintain")){
                    planvalue=1;
                }
                else if (plantxt.equals("Gain Weight")){
                    planvalue=2;
                }


                String foodtypetxt=foodtype.getSelectedItem().toString();
                if (foodtypetxt.equals("Anything")){
                    foodtypevalue=0;
                }
                else if (foodtypetxt.equals("Vegetarian")){
                    foodtypevalue=1;
                }


                if (male.isChecked()){
                    gendervalue=0;
                }
                else if (female.isChecked()){
                    gendervalue=1;
                }

                String activityleveltxt=activ.getSelectedItem().toString();
                int activitylevelvalue=-1;
                if (activityleveltxt.equals("Lightly Active")){
                    activitylevelvalue=0;
                }
                else if (activityleveltxt.equals("Moderately Active")){
                    activitylevelvalue=1;
                }
                else if (activityleveltxt.equals("Very Active")){
                    activitylevelvalue=2;
                }


                int clusterid=findcluster(planvalue,foodtypevalue);
                //double Calories=calcCalories(gendervalue,planvalue,activitylevelvalue,Integer.parseInt(weghit.getText().toString()),Integer.parseInt(height.getText().toString()),Integer.parseInt(age.getText().toString()));
                //db.insert("yy",1,"fdsda",2,2,0,String.valueOf(y),String.valueOf(x),"Dsadas",String.valueOf(z),clusterid);
                Toast.makeText(getApplicationContext(),String.valueOf(clusterid),Toast.LENGTH_LONG).show();
            }
        });
    }
    public int findcluster(int planvalue,int foodtypevalue){
        int min=-1;
        for(int i=0;i<clusters.size();i++){

            ArrayList<Integer> list=clusters.get(i);
            int plan= list.get(0);
            int type=list.get(1);

            int add= (int) Math.pow((planvalue-plan),2)+(int)Math.pow((foodtypevalue-type),2);
            int res=(int)Math.sqrt(add);
            if (res==0){
                min=i;
                break;
            }
        }

        return min;
    }
    public double calcCalories(int gender,int plan,int activitylevel,int weight,int height,int age){

        double factor=-1;
        double cal=-1;

        if (activitylevel==0){
            factor=1.375;
        }
        else if(activitylevel==1){
            factor=1.55;
        }
        else if(activitylevel==2){
            factor=1.725;
        }
        if (gender==0){
            cal=((10*weight)+(6.25*height)-(5*age)+5)*factor;
        }
        else if(gender==1){
            cal=((10*weight)+(6.25*height)-(5*age)-161)*factor;
        }
        if (plan==0){
            cal-=500;
        }
        else if(plan==2){
            cal+=500;
        }
        return cal;
    }
}



