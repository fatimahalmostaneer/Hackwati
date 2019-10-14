package sa.ksu.swe444.hackwati;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sa.ksu.swe444.hackwati.Recording.RecordingActivity;
import sa.ksu.swe444.hackwati.storyActivity.StoryActivity;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private storyAdapter adapter;
    public List<Item> itemList;
    private static CircularImageView channelimage;
    private RelativeLayout relativeLayout;
    private Toolbar toolbarMain;
    BottomNavigationView navView;
    public View item;
    public FirebaseAuth mAuth;
    String userUid;
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public DocumentReference documentReference;
    public String[] subscribedUsers;
    private static final String TAG = "MainActivity";
    public static List<String> list;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        toolbarMain = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();




      /*  MySharedPreference.clearValue(MainActivity.this,Constants.Keys.STORY_COVER);
        MySharedPreference.clearValue(MainActivity.this,Constants.Keys.STORY_AUDIO);
*/

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
        retriveSubscribedUsers();
        recyclerView.setAdapter(adapter);




        // MENU::::::
        installButton110to250();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

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


    }// end of OnCreate()


    ////MENU\\\\





    private void installButton110to250() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_110_250);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.animal_elp, R.drawable.ic_power_settings_new_black_24dp, R.drawable.animal_elp, R.drawable.ic_search_black_24dp};
        int[] color = {R.color.colorAccent, R.color.colorAccent, R.color.colorAccent, R.color.colorAccent};
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

    private void setListener(final AllAngleExpandableButton button) {
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                switch (index) {
                    case 1:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, UserProfile.class));
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onExpand() {
                Toast.makeText(MainActivity.this, "jjjj", Toast.LENGTH_SHORT).show();
            /*    int[] drawable = {R.drawable.animal_r};

                final List<ButtonData> buttonDatas = new ArrayList<>();
                ButtonData buttonData;
                buttonData = ButtonData.buildIconButton(MainActivity.this, drawable[0], 7);
                buttonDatas.add(buttonData);*/

            }

            @Override
            public void onCollapse() {
            }
        });
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }//end dpToPx

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


    public void retriveSubscribedUsers() {

        //firebaseFirestore.collection("users").document(userUid);

        DocumentReference docRef = firebaseFirestore.collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List<String> list = (List<String>) document.get("subscribedUsers");
                        if (list != null)
                            for (String user : list) {
                                Log.d("TAG", "soso " + user);
                            }
                        retriveStories(list);
                        // String str = document.getString("subscribedUsers").toString();
                        //   Log.d(TAG, "DocumentSnapshot data: " + str);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    public void retriveStories(List<String> list) {

        if (list == null)
            return;

        for (String story : list) {
            Query subscribedStories = firebaseFirestore.collection("stories").whereEqualTo("userId", story);
            subscribedStories.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult())         {
                            document.getData();
                            String description = (String) document.get("description");
                            String pic = (String) document.get("pic");
                            String rate = (String) document.get("rate");
                            String sound = (String) document.get("sound");
                            String title = (String) document.get("title");
                            String userId = (String) document.get("userId");

                            Log.d(TAG, "asomy " + description);
                            Log.d(TAG, "asomy " + pic);
                            Log.d(TAG, "asomy " + title);

                            Item item = new Item(title,pic,userId);
                            itemList.add(item);


                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });


        }// end for loop


    }
}

