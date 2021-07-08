package com.example.httpapi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    String BASE_URL = "http://192.168.43.237:5000/api/v1/example/";
    EditText nameEditText;
    TextView msgTxt;
    boolean sqlClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = (EditText) findViewById(R.id.editText);
        msgTxt = findViewById(R.id.textView);
        Button getBtn = (Button) findViewById(R.id.get);
        Button postBtn = (Button) findViewById(R.id.post);
        Button sqlBtn = (Button) findViewById(R.id.sql);
        sqlClicked = false;
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,
                        "Thread running", Toast.LENGTH_SHORT).show();
                //Your code goes here
                String name = getTextFromEditText(nameEditText);
                String link = BASE_URL + "get?name=" + name;
                sqlClicked = false;
                Task task = new Task();
                task.execute(link);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "POST button Clicked", Toast.LENGTH_SHORT).show();
                //Your code goes here
                String name = getTextFromEditText(nameEditText);
                String link = BASE_URL + "post";

                Task2 task2 = new Task2();
                task2.execute(link, name);
            }
        });

        sqlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,
                        "Thread running", Toast.LENGTH_SHORT).show();
                //Your code goes here
                String name = getTextFromEditText(nameEditText);
                String link = BASE_URL + "sql";
                sqlClicked = true;
                Task task = new Task();
                task.execute(link);
            }
        });
    }

    public String contactAPI(String link) {
        String jsonResponse = "";
        URL url = QueryUtils.createUrl(link);
        try {
            return jsonResponse = QueryUtils.makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String postToAPI(String link, String name) {
        String jsonResponse = "";
        URL url = QueryUtils.createUrl(link);
        return jsonResponse = QueryUtils.makeHttpPostRequest(url, name);
    }

    public String getTextFromEditText(EditText editText) {
        return editText.getText().toString();
    }

    private class Task extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.i("MainActivity.this", "Bg= " + strings[0]);
            return contactAPI(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String msg = "";
            ArrayList<String> namesArray = new ArrayList<>();
            if (sqlClicked) {
                namesArray = QueryUtils.extractJsonFromUserDb(s);
                sqlClicked = false;

                StringBuilder listString = new StringBuilder();

                for (String str : namesArray) {
                    listString.append(str).append(" ");
                }
                msgTxt.setText("All names= " + listString.toString());

            } else {
                msg = QueryUtils.extractJson(s);
                msgTxt.setText("msg= " + msg);
                Toast.makeText(MainActivity.this,
                        "GET request= " + s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Task2 extends AsyncTask<String, String, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            Log.i("MainActivity.this", "Bg= " + strings[0]);
            return postToAPI(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String msg = QueryUtils.extractJson(s);
            msgTxt.setText("msg= " + msg);
            Toast.makeText(MainActivity.this,
                    "POST request= " + s, Toast.LENGTH_SHORT).show();
        }
    }
}