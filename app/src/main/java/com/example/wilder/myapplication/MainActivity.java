package com.example.wilder.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    Button getData;
    TextView serverDataReceived;
    TextView showParsedJSON;
    EditText userInput;
    BufferedReader br;
    String line, output;
    String restURL="http://www.androidexample.com/media/webservice/JsonReturn.php";
    StringBuilder sb;
    URL url;
    JSONObject jsonResponse;
    JSONArray jsonArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverDataReceived = (TextView) findViewById(R.id.server_data_received);
        showParsedJSON =(TextView) findViewById(R.id.show_parsed_JSON_data);
        userInput =(EditText) findViewById(R.id.user_input);

        getData = (Button) findViewById(R.id.get_service_data);
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RestOperation().execute(restURL);
            }
        });

    }
    private class RestOperation extends AsyncTask<String, Void, Void> {

        String content;
        String error;
        String data;
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle(getString(R.string.PlsWait));

            try {
                data = "&"+ URLEncoder.encode("data","UTF-8")+"-"+userInput.getText();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params) {


            try {
                url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWriter.write(data);
                outputStreamWriter.flush();

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();

                while ((line = br.readLine())!=null){
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                content = sb.toString();

            } catch (MalformedURLException e) {
                error = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                error = e.getMessage();
                e.printStackTrace();
            }finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(error!=null){
                serverDataReceived.setText(getString(R.string.error)+error);
            }else{
                serverDataReceived.setText(content);

                try {
                    jsonResponse = new JSONObject(content);
                    jsonArray =jsonResponse.optJSONArray("Android");

                    for(int i = 0; i<jsonArray.length();i++){
                        JSONObject child = jsonArray.getJSONObject(i);

                        String name = child.getString("name");
                        String number = child.getString("number");
                        String time = child.getString("date_added");

                        output = "name : "+name+ System.lineSeparator()
                                +"number : "+number+ System.lineSeparator()
                                +"time : "+ time + System.lineSeparator();
                        output+= System.lineSeparator();
                    }

                    showParsedJSON.setText(output);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
