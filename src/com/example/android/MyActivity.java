package com.example.android;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.json_examlpe.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class MyActivity extends Activity implements View.OnClickListener{

    Button b1;
    TextView t1;
    TextView t2;
    TextView t3;
    EditText et;
    EditText et2;

    /**
     * Called when the activity is first created.
     */
    @Override

    //1.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        b1 = (Button) findViewById(R.id.btn1);
        t1 = (TextView) findViewById(R.id.Connected);
        t2 = (TextView) findViewById(R.id.name);
        t3 = (TextView) findViewById(R.id.Country);
        et = (EditText) findViewById(R.id.etCountry);
        et2 = (EditText) findViewById(R.id.etName);

        if(isConnected())
        {
            t1.setText("you are connected");
        }
        else
            t1.setText("you are not connexted");
        b1.setOnClickListener(MyActivity.this);



    }
    //2.
    public boolean isConnected()
    {
        //Class that answers queries about the state of network connectivity.
        ConnectivityManager connmgr =
                (ConnectivityManager) getSystemService(MyActivity.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connmgr.getActiveNetworkInfo();
        if(netInfo!=null && netInfo.isConnected())
        {
            return true;
        }
        else
            return false;

    }
    //3
    public static String POST(String url, Person person) throws IOException {
        InputStream inputstream= null;
        String result = "";
        try
        {
            //1. create http client
            HttpClient httpClient = new DefaultHttpClient();
            //2. make post request to the given url
            HttpPost httppost = new HttpPost(url);

            String json = "";

            //3.create json object
            JSONObject job = new JSONObject();
            //4. get details
            job.accumulate("name",person.getName());
            job.accumulate("country",person.getCountry());

            //5. convert to string
            json = job.toString();

            //6. set json to stringentity
            StringEntity se = new StringEntity(json);

            //7. set http post entity
            httppost.setEntity(se);

            //8. set header to inform the server about hte type of content
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            //9. receive post request to the given url
            HttpResponse htpR = httpClient.execute(httppost);

            //10. recieve response as input stream
            inputstream = htpR.getEntity().getContent();

            if(inputstream!=null)
            {
                result= convertInputStreamToString(inputstream);
            }
            else
                result = "did not work";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }



//4
    public static String convertInputStreamToString(InputStream inputstream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputstream) );
        String line = "";
        String result = "";
        while((line = br.readLine()) != null)

           result+= line;
        inputstream.close();
        return result;



    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.btn1:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data", Toast.LENGTH_LONG).show();

                    new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservelet");
                break;

        }
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String >{


        @Override
        protected String doInBackground(String... urls) {
            try {
                Person person = new Person();
                person.setName(et.getText().toString());
                person.setCountry(et2.getText().toString());

                return POST(urls[0], person);
            }catch (IOException e) {
                e.printStackTrace();
            } ;

           return null;
        }
        protected  void onPostExecute(String result)
        {
            Toast.makeText(getBaseContext(),"data sent!", Toast.LENGTH_LONG);
        }
    }
    private boolean validate()
    {
        if(et.getText().toString().trim().equals(""))
            return false;
        if(et2.getText().toString().trim().equals(""))
            return false;
            else
            return true;
    }


}
