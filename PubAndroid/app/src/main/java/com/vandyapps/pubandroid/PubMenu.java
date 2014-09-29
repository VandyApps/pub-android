package com.vandyapps.pubandroid;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TabActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.InjectView;

public class PubMenu extends Activity implements AdapterView.OnItemClickListener{

    private TabHost myTabHost;
    private ListView entreeList, sideList, sweetList;
    private ArrayAdapter<String> entreeAdapter, sideAdapter, sweetAdapter;
    private String[] entreeNames, sideNames, sweetNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu);
        adapterSetup();
        entreeList = (ListView) findViewById(R.id.entrees);
        sideList = (ListView) findViewById(R.id.sides);
        sweetList = (ListView) findViewById(R.id.sweets);
        entreeList.setAdapter(entreeAdapter);
        sideList.setAdapter(sideAdapter);
        sweetList.setAdapter(sweetAdapter);
        entreeList.setOnItemClickListener(this);
        sideList.setOnItemClickListener(this);
        sweetList.setOnItemClickListener(this);
        tabHostSetup();
    }

    private void adapterSetup(){
        entreeNames = new String[Entrees.values().length];
        sideNames = new String[Sides.values().length];
        sweetNames = new String[Sweets.values().length];
        for(int ii = 0; ii < Entrees.values().length; ii++)
        {
            entreeNames[ii] = Entrees.values()[ii].name;
        }
        for(int jj = 0; jj < Sides.values().length; jj++)
        {
            sideNames[jj] = Sides.values()[jj].name;
        }
        for(int jj = 0; jj < Sweets.values().length; jj++)
        {
            sweetNames[jj] = Sweets.values()[jj].name;
        }

        entreeAdapter = new ArrayAdapter<String>(PubMenu.this, R.layout.menu_list, entreeNames);
        sideAdapter = new ArrayAdapter<String>(PubMenu.this, R.layout.menu_list, sideNames); //android.R.layout.simple_selectable_list_item
        sweetAdapter = new ArrayAdapter<String>(PubMenu.this, R.layout.menu_list, sweetNames);
    }

    private void tabHostSetup(){
        myTabHost = (TabHost)findViewById(R.id.tabhost);
        myTabHost.setup();

        TabHost.TabSpec spec1 = myTabHost.newTabSpec("entree_tab");
        spec1.setIndicator("Entrees");
        spec1.setContent(R.id.Entrees);
        myTabHost.addTab(spec1);

        spec1 = myTabHost.newTabSpec("side_tab");
        spec1.setIndicator("Sides");
        spec1.setContent(R.id.Sides);
        myTabHost.addTab(spec1);

        spec1 = myTabHost.newTabSpec("sweet_tab");
        spec1.setIndicator("Sweets");
        spec1.setContent(R.id.Sweets);
        myTabHost.addTab(spec1);
    }


    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String description = "", name = "";
        switch(parent.getId()){
            case R.id.entrees:
                name = Entrees.values()[position].name;
                description = Entrees.values()[position].giveDescription();
                break;

            case R.id.sides:
                name = Sides.values()[position].name;
                description = Sides.values()[position].giveDescription();
                break;

            case R.id.sweets:
                name = Sweets.values()[position].name;
                description = Sweets.values()[position].giveDescription();
        }
        if(description.equals("")){
            Toast.makeText(PubMenu.this, "No description for this item", Toast.LENGTH_LONG).show();
        } else {
            final Dialog dialog = new Dialog(PubMenu.this);
            dialog.setContentView(R.layout.food_description);
            dialog.setTitle(name);

            TextView text = (TextView) dialog.findViewById(R.id.tvDescription);
            text.setText(description);
            dialog.show();
        }

    }

    public enum Entrees {
        HARVEST_SALAD("Harvest Salad", "", 0), PUB_SALAD("Pub Salad", "", 1), QUESADILLAS("Quesadillas", "", 2),PUB_BURGER("Pub Burger", "", 3),
        DR_PRAEGGERS_VEGAN_BURGER("Dr. Praeggers Vegan Burger", "", 4), SOUTHERN_CHICKEN_WRAP("Southern Chicken Wrap", "", 5), NASHVILLE_HOT_CHICKEN("Nashville Hot Chicken", "", 6),
        BUFFALO_HOT_WINGS("Buffalo Hot Wings", "", 7),SPICED_GRILLED_CHICKEN("Spiced Grilled Chicken", "", 8), CHICKEN_TENDERS("Chicken Tenders", "", 9),
        PUB_TURKEY_CLUB("Pub Turkey Club", "", 10),POPCORN_SHRIMP("Popcorn Shrimp and Fries Basket", "", 11), SHRIMP_PO_BOY("Shrimp Po Boy", "", 12);
        public String name, description;
        public int orderNum;

        private Entrees( String foodName, String foodDescription, int num){
            this.name = foodName;
            this.description = foodDescription;
            this.orderNum = num;
        }

        public String giveDescription(){
            switch(this.orderNum){
                case 0:
                    description = "Mixed greens with granny smith apple, blue cheese crumbles, " +
                            "chopped walnuts and dried cranberries with fat free balsamic vinaigrette";
                    break;
                case 1:
                    description = "Garden greens with cherry tomatoes, cheddar cheese, cucumber, red onion, croutons and choice of dressing";
                    break;
                case 2:
                    description = "Three types: \n 1. Buffalo Chicken \n 2. Corn, Black Beans, and Roasted Poblano Peppers \n " +
                            "3. Jack and Cheese (Chicken optional) \n (Quesadillas served with Chipotle Lime dipping sauce and Salsa)";
                    break;
                case 3:
                    description = "1/3 lb. burger with choice of American, Swiss, Cheddar or Provolone Cheese";
                    break;
                case 4:
                    description = "Toasted wheat bun with lettuce, tomato, pickle and red onion";
                    break;
                case 6:
                    description = "Open faced sandwich on texas toast with dill pickles";
                    break;
                case 5:
                     description = "Fried chicken tenders wrapped in a flour tortilla with lettuce, cheddar cheese and ranch dressing";
                    break;
                case 7:
                    description = "Served with ranch dressing and celery";
                    break;
                case 8:
                    description = "Toasted bun with lettuce, tomato and red onion with choice of American, Swiss, Cheddar, or Provolone Cheese";
                    break;
                case 10:
                    description = "Turkey, smoked bacon and provolone cheese on asiago ciabatta with lettuce, tomato and red onion";
                    break;
                case 11:
                    description = "Deep fried and served with cocktail sauce";
                    break;
                case 12:
                    description = "French baguette, popcorn shrimp, lettuce, tomato and cajun remoulade";
                    break;
            }
            return description;
        }
    }

    public enum Sides {
        PUB_FRIES("Pub Fries", ""), KETTLE_CHIPS("Kettle Chips", ""), TORTILLA_CHIPS("Tortilla Chips", ""), CUT_FRUIT("Cut Fruit", ""),
        GREEN_SALAD("Green Salad", ""), CHIPS_AND_SALSA("Chips and Salsa", ""), CHIPS_AND_QUESO("Chips and Queso", ""), GUACAMOLE("Guacmole", "");
        private String name, description;

        private Sides(String foodName, String foodDescription){
            name = foodName;
            description = foodDescription;
        }

        public String giveDescription(){
            return description;
        }
    }

    public enum Sweets {
        PUB_CHOCOLATE_CHIP_COOKIE("Pub Chocolate Chip Cookie", "", 0), GHIRARDELLI_BROWNIE("Ghirardelli Brownie", "", 1), MILKSHAKES("Milkshakes", "", 2),
        ABITA_ROOT_BEER_FLOAT("Abita Root Beer Float", "", 3);
        private String name;
        private String description = "";
        private int orderNum;

        private Sweets(String foodName, String foodDescription, int num){
            this.name = foodName;
            this.description = foodDescription;
            this.orderNum = num;
        }

        public String giveDescription(){
            switch(this.orderNum){
                case 2:
                    description = "Three flavors: \n 1. Strawberry \n 2. Vanilla \n " +
                            "3. Chocolate";
                    break;
                }
            return description;
        }
    }
}
