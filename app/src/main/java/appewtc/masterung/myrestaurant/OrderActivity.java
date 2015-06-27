package appewtc.masterung.myrestaurant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

        //Bind Widget
        bindWidget();

        //Show Officer
        showOfficer();

        //Create ListView
        createListView();


    }   // onCreate

    private void createListView() {

        //Get All Data
        String strListFood[] = objFoodTABLE.readAllFood();
        String strListPrice[] = objFoodTABLE.readAllPrice();
        int intImageFood[] = {R.drawable.food1, R.drawable.food2, R.drawable.food3, R.drawable.food4,
                R.drawable.food5, R.drawable.food6, R.drawable.food7, R.drawable.food8, R.drawable.food9,
                R.drawable.food10, R.drawable.food11, R.drawable.food12, R.drawable.food13, R.drawable.food14,
                R.drawable.food15, R.drawable.food16, R.drawable.food17, R.drawable.food18, R.drawable.food19,
                R.drawable.food20, R.drawable.food21, R.drawable.food22, R.drawable.food23, R.drawable.food24,
                R.drawable.food25, R.drawable.food26, R.drawable.food27, R.drawable.food28, R.drawable.food29,
                R.drawable.food30, R.drawable.food31, R.drawable.food32, R.drawable.food33, R.drawable.food34,
                R.drawable.food35, R.drawable.food36, R.drawable.food37, R.drawable.food38, R.drawable.food39,
                R.drawable.food40, R.drawable.food41, R.drawable.food42, R.drawable.food43, R.drawable.food44,
                R.drawable.food45, R.drawable.food46, R.drawable.food47, R.drawable.food48, R.drawable.food29,
                R.drawable.food50};
        MyAdapter objMyAdapter = new MyAdapter(OrderActivity.this, strListFood, strListPrice, intImageFood);
        foodListView.setAdapter(objMyAdapter);

    }   // createListView


    private void showOfficer() {
        officerString = getIntent().getExtras().getString("Officer");
        officerTextView.setText(officerString);
    }   // showOfficer

    private void bindWidget() {
        officerTextView = (TextView) findViewById(R.id.txtShowOfficer);
        deskEditText = (EditText) findViewById(R.id.edtDesk);
        foodListView = (ListView) findViewById(R.id.listView);
    }   // bindWidget

}   // Main Class
