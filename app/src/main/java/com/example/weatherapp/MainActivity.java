package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button search;
    TextView information;
    EditText cityName;
    Bitmap weatherSymbolMap;
    ImageView weatherSymbol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search=(Button)findViewById(R.id.bottom);
        information=(TextView)findViewById(R.id.information);
        cityName=(EditText)findViewById(R.id.city_name);
        weatherSymbol=(ImageView) findViewById(R.id.imageView);
        Log.i("BREAK", "BREAK");

    }
    public void SearchClick(View view){
        DownloadText downloadText=new DownloadText();
        String cName= cityName.getText().toString();
        String cityURL1="http://api.weatherstack.com/current?access_key=3264a9c5037c634d0258b4dc8a759be5&query=";
        String cityURL=cityURL1+cName;
        Log.i("URL: ",cityURL);
        downloadText.execute(cityURL);
    }

    public class DownloadText extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... URLs) {
            String result="";
            URL url;
            HttpURLConnection URLConnection=null;
            try{
                //exception if for example it doesn't have https://
                url=new URL(URLs[0]);
                URLConnection=(HttpURLConnection) url.openConnection();

                //hold input of data as it comes in
                InputStream in=URLConnection.getInputStream();

                //read data
                InputStreamReader reader=new InputStreamReader(in);
                //get it one character a a time
                int data=reader.read();
                while(data != -1){
                    char current=(char)data;
                    result=result+current;
                    data=reader.read();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                JSONObject jsonObject=new JSONObject(result);

                //get temperature
                JSONObject weather= (JSONObject) jsonObject.get("current");
                String temp=weather.getString("temperature");
                JSONArray wIconArr=weather.getJSONArray("weather_icons");
                String weatherIcon=wIconArr.getString(0);
                JSONArray wDescArr=weather.getJSONArray("weather_descriptions");
                String weatherDescription=wDescArr.getString(0);
                String time=weather.getString("observation_time");

                //get place
                JSONObject location=(JSONObject) jsonObject.get("location");
                String nameCity=location.getString("name");
                String nameCountry=location.getString("country");

                information.setText(nameCity+", "+nameCountry+" "+time+"\n"+weatherDescription+"\n"+temp+" degree Celsius");


                DownloadImage task=new DownloadImage();
                try{
                    weatherSymbolMap= task.execute(weatherIcon).get();
                    weatherSymbol.setImageBitmap(weatherSymbolMap);
                }catch(Exception e){
                    e.printStackTrace();
                }


            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }

        }
    }
    //AsyncTask is a way of running code on a different thread
    //First String is the type of variable that we send to instruct what to do (URL)
    //Void is name of the method used to show progress
    //Third String is the variable returned
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        //protected is like public, but it can be access anywhere in the package(APP)
        //String.. is like an "Array"-
        protected Bitmap doInBackground(String... URLs){
            URL url;
            HttpURLConnection connection=null;
            try{
                //exception if for example it doesn't have https://
                url=new URL(URLs[0]);
                connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);

                return myBitmap;


            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }
    }
}


