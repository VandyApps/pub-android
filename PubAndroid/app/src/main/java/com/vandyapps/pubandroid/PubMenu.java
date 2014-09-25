package com.vandyapps.pubandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.InjectView;

public class PubMenu extends Activity {


    @InjectView(R.id.entrees)ListView entreeList;
    @InjectView(R.id.sides)ListView sideList;
    private ArrayAdapter<String> entreeAdapter;
    private String[] foodNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu);
        adapterSetup();
        entreeList.setAdapter(entreeAdapter);
    }

    private void adapterSetup(){
        foodNames = new String[Entrees.values().length];
        for(int ii = 0; ii < Entrees.values().length; ii++)
        {
            foodNames[ii] = Entrees.values()[ii].name;
        }

        entreeAdapter = new ArrayAdapter<String>(PubMenu.this, android.R.layout.simple_expandable_list_item_1, foodNames);
        //sideAdapter = new ArrayAdapter<String>(PubMenu.this, android.R.layout.simple_expandable_list_item_1, );
    }

    public enum Entrees {
        HARVEST_SALAD("Harvest Salad"), PUB_SALAD("Pub Salad"), QUESADILLAS("Quesadillas"), NASHVILLE_HOT_CHICKEN("Nashville Hot Chicken"),
        SOUTHERN_CHICKEN_WRAP("Southern Chicken Wrap"), BUFFALO_HOT_WINGS("Buffalo Hot Wings"), DR_PRAEGGERS_VEGAN_BURGER("Dr. Praeggers Vegan Burger"),
        PUB_TURKEY_CLUB("Pub Turkey Club"), SPICED_GRILLED_CHICKEN("Spiced Grilled Chicken"), CHICKEN_TENDERS("Chicken Tenders"),
        POPCORN_SHRIMP("Popcorn Shrim and Fries Basket"),PUB_BURGER("Pub Burger"), SHRIMP_PO_BOY("Shrimp Po Boy");
        private String name;
        private String description = "";

        private Entrees( String name){
            this.name = name;
        }

        private String giveDescription(){
            switch(this){
                case HARVEST_SALAD:
                    description = "Mixed greens with granny smith apple, blue cheese crumbles, " +
                            "chopped walnuts and dried cranberries with fat free balsamic vinaigrette";
                case PUB_SALAD:
                    description = "Garden greens with cherry tomatoes, cheddar cheese, cucumber, red onion,croutons and choice of dressing";
                case QUESADILLAS:
                    description = "Three types: \n 1. Buffalo Chicken \n 2. Corn, Black Beans, and 3. Roasted Poblano Peppers \n " +
                            "Jack and Cheese (Chicken optional) \n (Quesadillas served with Chipotle Lime dipping sauce and Salsa.";

            }
            return description;
        }




    }

}
