package appewtc.masterung.myrestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {

    //Explicit
    private UserTABLE objUserTABLE;
    private FoodTABLE objFoodTABLE;
    private EditText userEditText, passEditText;
    private String userString, passString, nameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connected Database
        connectedDatabase();

        //Test Add Value
        //testAddValue();

        //Delete All Data
        deleteAllData();

        //Synchronize JSON to SQLite
        synJSONtoSQLite();

    }   // onCreate

    public void clickLogin(View view) {

        //Bind Widget
        userEditText = (EditText) findViewById(R.id.edtUser);
        passEditText = (EditText) findViewById(R.id.edtPassword);

        userString = userEditText.getText().toString().trim();
        passString = passEditText.getText().toString().trim();

        if (userString.equals("") || passString.equals("")) {

            showMyDialog("Have Space", "Please Fill All Blank");

        } else {

            //Check User
            checkUser(userString);

        }

    }   // clickLogin

    private void checkUser(String userString) {

        try {

            String myResult[] = objUserTABLE.searchUser(userString);
            nameString = myResult[3];

            //Check Password
            checkPassword(myResult[2]);

        } catch (Exception e) {
            showMyDialog("No This User", "No " + userString + " in my Database");
        }

    }   // checkUser

    private void checkPassword(String strTruePassword) {

        if (passString.equals(strTruePassword)) {

            Log.d("rest", "Welcome ==> " + nameString);

            //Intent to Order
            Intent objIntent = new Intent(MainActivity.this, OrderActivity.class);
            objIntent.putExtra("Officer", nameString);
            startActivity(objIntent);
            finish();

        } else {
            showMyDialog("Password False", "Please Try Again Password False");
        }

    }   //checkPassword

    private void showMyDialog(String strTitle, String strMessage) {

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.danger);
        objBuilder.setTitle(strTitle);
        objBuilder.setMessage(strMessage);
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        objBuilder.show();

    }   // showMyDialog

    private void deleteAllData() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase("Restaurant.db", MODE_PRIVATE, null);
        objSqLiteDatabase.delete("userTABLE", null, null);
        objSqLiteDatabase.delete("foodTABLE", null, null);

    }   // deleteAllData

    private void synJSONtoSQLite() {

        //Change Policy
        StrictMode.ThreadPolicy objPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(objPolicy);

        int intTime = 0;

        while (intTime <= 1) {

            //1. Create InputStream
            InputStream objInputStream = null;
            String strJSON = "";
            String strUserUrl = "http://swiftcodingthai.com/rest/php_get_data_master.php";
            String strFoodUrl = "http://swiftcodingthai.com/rest/php_get_data_food.php";
            HttpPost objHttpPost;

            try {

                HttpClient objHttpClient = new DefaultHttpClient();

                if (intTime != 1) {
                    objHttpPost = new HttpPost(strUserUrl);
                } else {
                    objHttpPost = new HttpPost(strFoodUrl);
                }


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

                while ((strLine = objBufferedReader.readLine()) != null) {

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

                    if (intTime != 1) {

                        String strUser11 = myJsonObject.getString("User");
                        String strPassword11 = myJsonObject.getString("Password");
                        String strName11 = myJsonObject.getString("Name");

                        //Update UserTABLE
                        objUserTABLE.addValueUser(strUser11, strPassword11, strName11);

                    } else {

                        String strFood = myJsonObject.getString("Food");
                        String strPrice = myJsonObject.getString("Price");

                        //Update FoodTABLE
                        objFoodTABLE.addFood(strFood, strPrice);

                    }

                }//for

            } catch (Exception e) {
                Log.d("rest", "SQLite update ==> " + e.toString());
            }

            // Increase intTime
            intTime += 1;

        }   //while


    }   // synJSONtoSQLite

    private void testAddValue() {

        objUserTABLE.addValueUser("testUser", "testPass", "testName");
        objFoodTABLE.addFood("ข้าวผัด", "60");

    }   // testAddValue

    private void connectedDatabase() {
        objUserTABLE = new UserTABLE(this);
        objFoodTABLE = new FoodTABLE(this);
    }   //connectedDatabase

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
