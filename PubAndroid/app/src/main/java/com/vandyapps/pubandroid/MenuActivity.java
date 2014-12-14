package com.vandyapps.pubandroid;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MenuActivity extends Activity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.entrees) private ListView entreeList;
    @InjectView(R.id.sides)   private ListView sideList;
    @InjectView(R.id.sweets)  private ListView sweetList;

    private TabHost myTabHost;
    private ArrayAdapter<String> entreeAdapter, sideAdapter, sweetAdapter;
    private String[] entreeNames, sideNames, sweetNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ButterKnife.inject(this);

        adapterSetup();
        entreeList.setAdapter(entreeAdapter);
        sideList.setAdapter(sideAdapter);
        sweetList.setAdapter(sweetAdapter);
        entreeList.setOnItemClickListener(this);
        sideList.setOnItemClickListener(this);
        sweetList.setOnItemClickListener(this);
        tabHostSetup();
    }

    private void adapterSetup() {
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

        entreeAdapter = new CustomFontArrayAdapter(MenuActivity.this, R.layout.menu_list_item, entreeNames, "chalk.ttf");
        sideAdapter = new CustomFontArrayAdapter(MenuActivity.this, R.layout.menu_list_item, sideNames, "chalk.ttf");
        sweetAdapter = new CustomFontArrayAdapter(MenuActivity.this, R.layout.menu_list_item, sweetNames, "chalk.ttf");
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
            Toast.makeText(MenuActivity.this, "No description for this item", Toast.LENGTH_LONG).show();
        } else {
            final Dialog dialog = new Dialog(MenuActivity.this);
            dialog.setContentView(R.layout.food_description);
            dialog.setTitle(name);

            TextView text = (TextView) dialog.findViewById(R.id.tvDescription);
            text.setText(description);
            dialog.show();
        }

    }

    public enum Entrees {
        HARVEST_SALAD(0, "Harvest Salad",
                "Mixed greens with granny smith apple, blue cheese crumbles, " +
                        "chopped walnuts and dried cranberries with fat free balsamic vinaigrette"),
        PUB_SALAD(1, "Pub Salad",
                "Garden greens with cherry tomatoes, cheddar cheese, cucumber, red onion, " +
                        "croutons and choice of dressing"),
        QUESADILLAS(2, "Quesadillas",
                "Three types: \n 1. Buffalo Chicken \n 2. Corn, Black Beans, and Roasted Poblano " +
                        "Peppers \n 3. Jack and Cheese (Chicken optional) \n (Quesadillas served " +
                        "with Chipotle Lime dipping sauce and Salsa)"),
        PUB_BURGER(3, "Pub Burger",
                "1/3 lb. burger with choice of American, Swiss, Cheddar or Provolone Cheese"),
        DR_PRAEGGERS_VEGAN_BURGER(4, "Dr. Praeggers Vegan Burger",
                "Toasted wheat bun with lettuce, tomato, pickle and red onion"),
        SOUTHERN_CHICKEN_WRAP(5, "Southern Chicken Wrap",
                "Fried chicken tenders wrapped in a flour tortilla with lettuce, cheddar cheese " +
                        "and ranch dressing"),
        NASHVILLE_HOT_CHICKEN(6, "Nashville Hot Chicken",
                "Open faced sandwich on texas toast with dill pickles"),
        BUFFALO_HOT_WINGS(7, "Buffalo Hot Wings",
                "Served with ranch dressing and celery"),
        SPICED_GRILLED_CHICKEN(8, "Spiced Grilled Chicken",
                "Toasted bun with lettuce, tomato and red onion with choice of American, Swiss, " +
                        "Cheddar, or Provolone Cheese"),
        CHICKEN_TENDERS(9, "Chicken Tenders",
                ""),
        PUB_TURKEY_CLUB(10, "Pub Turkey Club",
                "Turkey, smoked bacon and provolone cheese on asiago ciabatta with lettuce, " +
                        "tomato and red onion"),
        POPCORN_SHRIMP(11, "Popcorn Shrimp and Fries Basket",
                "Deep fried and served with cocktail sauce"),
        SHRIMP_PO_BOY(12, "Shrimp Po Boy",
                "French baguette, popcorn shrimp, lettuce, tomato and cajun remoulade");

        public final int orderNum;
        public final String name;
        public final String description;

        private Entrees(int num, String foodName, String foodDescription){
            this.name = foodName;
            this.description = foodDescription;
            this.orderNum = num;
        }

        public String giveDescription() {
            return description;
        }
    }

    public enum Sides {
        PUB_FRIES("Pub Fries", ""),
        KETTLE_CHIPS("Kettle Chips", ""),
        TORTILLA_CHIPS("Tortilla Chips", ""),
        CUT_FRUIT("Cut Fruit", ""),
        GREEN_SALAD("Green Salad", ""),
        CHIPS_AND_SALSA("Chips and Salsa", ""),
        CHIPS_AND_QUESO("Chips and Queso", ""),
        GUACAMOLE("Guacamole", "");

        private final String name;
        private final String description;

        private Sides(String foodName, String foodDescription){
            name = foodName;
            description = foodDescription;
        }

        public String giveDescription(){
            return description;
        }
    }

    public enum Sweets {
        PUB_CHOCOLATE_CHIP_COOKIE(0, "Pub Chocolate Chip Cookie", ""),
        GHIRARDELLI_BROWNIE(1, "Ghirardelli Brownie", ""),
        MILKSHAKES(2, "Milkshakes",
                "Three flavors: \n 1. Strawberry \n 2. Vanilla \n " +
                        "3. Chocolate"),
        ABITA_ROOT_BEER_FLOAT(3, "Abita Root Beer Float", "");

        private final int orderNum;
        private final String name;
        private final String description;

        private Sweets(int num, String foodName, String foodDescription){
            this.name = foodName;
            this.description = foodDescription;
            this.orderNum = num;
        }

        public String giveDescription(){
            return description;
        }
    }
}
