package com.vandyapps.pubandroid;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.vandyapps.pubandroid.view.EnumAdapter;
import com.vandyapps.pubandroid.view.ViewDecorator;

public class MenuActivity extends Activity {

    @InjectView(R.id.tabhost) TabHost myTabHost;
    @InjectView(R.id.entrees) ListView entreeList;
    @InjectView(R.id.sides)   ListView sideList;
    @InjectView(R.id.sweets)  ListView sweetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ButterKnife.inject(this);

        entreeList.setAdapter(createEnumAdapter(Entrees.class));
        entreeList.setOnItemClickListener(new MenuDetailPopup<>(Entrees.class));

        sideList.setAdapter(createEnumAdapter(Sides.class));
        sideList.setOnItemClickListener(new MenuDetailPopup<>(Sides.class));

        sweetList.setAdapter(createEnumAdapter(Sweets.class));
        sweetList.setOnItemClickListener(new MenuDetailPopup<>(Sweets.class));

        tabHostSetup();
    }

    private void tabHostSetup(){
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

    private BaseAdapter createEnumAdapter(Class<? extends Enum> enumClass) {
        EnumAdapter adapter = new EnumAdapter<>(this, R.layout.menu_list_item, enumClass);
        adapter.addDecorator(new ChalkDecorator());
        return adapter;
    }

    private static class ChalkDecorator implements ViewDecorator {
        private static Typeface tf;

        @Override public void decorate(View v) {
            TextView tv = (TextView) v;
            tv.setTextColor(Color.WHITE);
            if (tf == null) {
                tf = Typeface.createFromAsset(v.getContext().getAssets(), "chalk.ttf");
            }
            tv.setTypeface(tf);
        }
    }

    private static class MenuDetailPopup<E extends Enum & MenuEntry>
            implements AdapterView.OnItemClickListener {

        private final E[] enumItem;

        MenuDetailPopup(Class<E> enumClass) {
            enumItem = enumClass.getEnumConstants();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String name = enumItem[position].getName();
            String description = enumItem[position].giveDescription();
            if(description.equals("")){
                Toast.makeText(parent.getContext(), "No description for this item", Toast.LENGTH_LONG).show();
            } else {
                final Dialog dialog = new Dialog(parent.getContext());
                dialog.setContentView(R.layout.food_description);
                dialog.setTitle(name);

                TextView text = (TextView) dialog.findViewById(R.id.tvDescription);
                text.setText(description);
                dialog.show();
            }
        }
    }

    private static interface MenuEntry {
        String getName();
        String giveDescription();
    }

    public enum Entrees implements MenuEntry {
        HARVEST_SALAD("Harvest Salad",
                "Mixed greens with granny smith apple, blue cheese crumbles, " +
                        "chopped walnuts and dried cranberries with fat free balsamic vinaigrette"),
        PUB_SALAD("Pub Salad",
                "Garden greens with cherry tomatoes, cheddar cheese, cucumber, red onion, " +
                        "croutons and choice of dressing"),
        QUESADILLAS("Quesadillas",
                "Three types: \n 1. Buffalo Chicken \n 2. Corn, Black Beans, and Roasted Poblano " +
                        "Peppers \n 3. Jack and Cheese (Chicken optional) \n (Quesadillas served " +
                        "with Chipotle Lime dipping sauce and Salsa)"),
        PUB_BURGER("Pub Burger",
                "1/3 lb. burger with choice of American, Swiss, Cheddar or Provolone Cheese"),
        DR_PRAEGGERS_VEGAN_BURGER("Dr. Praeggers Vegan Burger",
                "Toasted wheat bun with lettuce, tomato, pickle and red onion"),
        SOUTHERN_CHICKEN_WRAP("Southern Chicken Wrap",
                "Fried chicken tenders wrapped in a flour tortilla with lettuce, cheddar cheese " +
                        "and ranch dressing"),
        NASHVILLE_HOT_CHICKEN("Nashville Hot Chicken",
                "Open faced sandwich on texas toast with dill pickles"),
        BUFFALO_HOT_WINGS("Buffalo Hot Wings",
                "Served with ranch dressing and celery"),
        SPICED_GRILLED_CHICKEN("Spiced Grilled Chicken",
                "Toasted bun with lettuce, tomato and red onion with choice of American, Swiss, " +
                        "Cheddar, or Provolone Cheese"),
        CHICKEN_TENDERS("Chicken Tenders",
                ""),
        PUB_TURKEY_CLUB("Pub Turkey Club",
                "Turkey, smoked bacon and provolone cheese on asiago ciabatta with lettuce, " +
                        "tomato and red onion"),
        POPCORN_SHRIMP("Popcorn Shrimp and Fries Basket",
                "Deep fried and served with cocktail sauce"),
        SHRIMP_PO_BOY("Shrimp Po Boy",
                "French baguette, popcorn shrimp, lettuce, tomato and cajun remoulade");

        private final String name;
        private final String description;

        private Entrees(String foodName, String foodDescription){
            this.name = foodName;
            this.description = foodDescription;
        }

        public String giveDescription() {
            return description;
        }

        public String getName() { return name; }

        @Override public String toString() { return name; }
    }

    public enum Sides implements MenuEntry {
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

        public String giveDescription() {
            return description;
        }

        public String getName() { return name; }

        @Override public String toString() { return name; }
    }

    public enum Sweets implements MenuEntry {
        PUB_CHOCOLATE_CHIP_COOKIE("Pub Chocolate Chip Cookie", ""),
        GHIRARDELLI_BROWNIE("Ghirardelli Brownie", ""),
        MILKSHAKES("Milkshakes",
                "Three flavors: \n 1. Strawberry \n 2. Vanilla \n " +
                        "3. Chocolate"),
        ABITA_ROOT_BEER_FLOAT("Abita Root Beer Float", "");

        private final String name;
        private final String description;

        private Sweets(String foodName, String foodDescription){
            this.name = foodName;
            this.description = foodDescription;
        }

        public String giveDescription() {
            return description;
        }

        public String getName() {
            return name;
        }

        @Override public String toString() { return name; }
    }

}
