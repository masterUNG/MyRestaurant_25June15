package appewtc.masterung.myrestaurant;

import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OrderActivity extends AppCompatActivity {

    //Explicit
    private TextView officerTextView;
    private EditText deskEditText;
    private ListView foodListView;
    private String officerString, deskString, foodString, itemString;
    private FoodTABLE objFoodTABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Connected Database
        objFoodTABLE = new FoodTABLE(this);

        bindWidget();

        //Show Officer
        showOfficer();

        //Synchronize JSON to foodTABLE
        synJSONtoFood();

    }   // onCreate

    private void synJSONtoFood() {

        //Change Policy
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy objPolicy = new StrictMode.
                    ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(objPolicy);
        }



        //1. Create InputStream
        InputStream objInputStream = null;
        String strJSON = "";

        try {

            HttpClient objHttpClient = new DefaultHttpClient();
            HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/rest/php_get_data_food.php");
            HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
            HttpEntity objHttpEntity = objHttpResponse.getEntity();
            objInputStream = objHttpEntity.getContent();

        } catch (Exception e) {
            Log.d("rest", "InputStream ==> " + e.toString());
        }


        //2. Create JSON String
        try {

            BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
            StringBuilder objStringBuilder = new StringBuilder();
            String strLine = null;

            while ((strLine = objBufferedReader.readLine()) != null ) {

                objStringBuilder.append(strLine);

            }   //while

            objInputStream.close();
            strJSON = objStringBuilder.toString();



        } catch (Exception e) {
            Log.d("rest", "JSON ==> " + e.toString());
        }



        //3. Update SQLite

        try {

            final JSONArray objJsonArray = new JSONArray(strJSON);

            for (int i = 0; i < objJsonArray.length(); i++) {

                JSONObject myJsonObject = objJsonArray.getJSONObject(i);

                String strFood = myJsonObject.getString("Food");
                String strPrice = myJsonObject.getString("Price");

                objFoodTABLE.addFood(strFood, strPrice);


            }//for

        } catch (Exception e) {
            Log.d("rest", "SQLite update ==> " + e.toString());
        }





    }   //synJSONtoFood

    private void showOfficer() {
        officerString = getIntent().getExtras().getString("Officer");
        officerTextView.setText(officerString);
    }

    private void bindWidget() {
        officerTextView = (TextView) findViewById(R.id.txtShowOfficer);
        deskEditText = (EditText) findViewById(R.id.edtDesk);
        foodListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}   // Main Class
