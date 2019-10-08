package sa.ksu.swe444.hackwati;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

import sa.ksu.swe444.hackwati.Recording.RecordingActivity;
import sa.ksu.swe444.hackwati.storyActivity.StoryActivity;


public class MainActivity extends AppCompatActivity   {

    private RecyclerView recyclerView;
    private storyAdapter adapter;
    private List<Item> itemList;
    private static CircularImageView channelimage;
    private RelativeLayout relativeLayout;
    private Toolbar toolbarMain;
    BottomNavigationView navView;
    public View item;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        toolbarMain = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_explore, R.id.navigation_record, R.id.navigation_subscription)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        itemList = new ArrayList<>();
        adapter = new storyAdapter(this, itemList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        prepareItems();


      /*  channelimage = findViewById(R.id.channelimage);

        relativeLayout.bringToFront();

       channelimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserProfile.class); // from where? and to the distanation
                startActivity(intent); // to start another activity
            }
        });*/

        // MENU::::::
        installButton110to250();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.navigation_record:
                        startActivity(new Intent(MainActivity.this, RecordingActivity.class));
                        break;

                    case R.id.navigation_subscription:
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        break;

                    case R.id.navigation_explore:
                        startActivity(new Intent(MainActivity.this, StoryActivity.class));
                        break;

                }// end of switch
             return true;
            }
        });

    }// end of setOnNavigationItemSelectedListener()


    ////MENU\\\\


    private void installButton110to250() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_110_250);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.animal_elp, R.drawable.ic_power_settings_new_black_24dp,R.drawable.animal_elp, R.drawable.ic_search_black_24dp};
        int[] color = {R.color.colorAccent, R.color.colorAccent, R.color.colorAccent,R.color.colorAccent };
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 7);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }

    private void setListener(AllAngleExpandableButton button) {
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                switch (index){
                    case 1:
                        FirebaseAuth.getInstance().signOut();
                        startActivity( new Intent(MainActivity.this, SplashActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, UserProfile.class));
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onExpand() { }
            @Override
            public void onCollapse() { }  }); }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }//end dpToPx

    private void prepareItems() {
        int[] covers = new int[]{
                R.drawable.gray,

        };


        int[] pictures = new int[]{

                R.drawable.animal_elp,
                R.drawable.animal_p,
                R.drawable.animal_r,
                R.drawable.animal_z,
                R.drawable.animal_t,


        };

        int views = R.drawable.ic_play_circle_outline_black_24dp;


        Item a1 = new Item("أخي زيد", covers[0], pictures[0], "12 ألف ", "غيداء العجاجي", views);
        itemList.add(a1);

        Item a2 = new Item("ليس بعد !", covers[0], pictures[1], "10 آلاف ", "فاطمة القحطاني", views);
        itemList.add(a2);

        Item a3 = new Item("جود ودراجتها الجديدة", covers[0], pictures[2], "2 ألف ", "دار سلوى للنشر", views);
        itemList.add(a3);

        Item a4 = new Item("انتبهي يا جود", covers[0], pictures[3], "40 ألف ", "دار الوطن", views);
        itemList.add(a4);

        Item a5 = new Item("أخي زيد", covers[0], pictures[4], "12 ألف ", "غيداء العجاجي", views);
        itemList.add(a5);

        Item a6 = new Item("ليس بعد !", covers[0], pictures[1], "10 آلاف ", "فاطمة القحطاني", views);
        itemList.add(a6);

        Item a7 = new Item("جود ودراجتها الجديدة", covers[0], pictures[2], "2 ألف ", "دار سلوى للنشر", views);
        itemList.add(a7);

        Item a8 = new Item("انتبهي يا جود", covers[0], pictures[0], "40 ألف ", "دار الوطن", views);
        itemList.add(a8);


    }


    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom

            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }//end inner class GridSpacingItemDecoration

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class); // from where? and to the distanation
        startActivity(intent); // to start another activity
    }


}

