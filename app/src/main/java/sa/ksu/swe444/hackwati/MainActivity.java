package sa.ksu.swe444.hackwati;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import sa.ksu.swe444.hackwati.Recording.RecordingActivity;
import sa.ksu.swe444.hackwati.explor.ExploreActivity;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private storyAdapter adapter;
    private List<Item> itemList = new ArrayList<>();
    private Toolbar toolbarMain;
    public BottomNavigationView navView;
    public View item;
    private TextView emptyStories;
    private String userUid;
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String TAG = "MainActivity";
    ArrayList<User> arrayList = new ArrayList<User>();


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyStories = findViewById(R.id.emptyStories);
        navView = findViewById(R.id.nav_view);
        toolbarMain = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_subscription,  R.id.navigation_explore, R.id.navigation_record)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initRecyclerView();
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
                        startActivity(new Intent(MainActivity.this, ExploreActivity.class));
                        break;

                }// end of switch
                return true;
            }
        });


    }// end of OnCreate()

    private void initRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new storyAdapter(this, itemList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        retrieveSubscribedUsers();


    }

    private void installButton110to250() {


        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_110_250);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.defult_thumbnail, R.drawable.ic_power_settings_new_black_24dp, R.drawable.defult_thumbnail, R.drawable.ic_search_black_24dp, R.drawable.ic_error_outline_black_24dp};// gray is some thing else
        int[] color = {R.color.colorAccent, R.color.colorAccent, R.color.colorAccent, R.color.colorAccent, R.color.colorAccent};
        for (int i = 0; i < 5; i++) {
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


                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("هل أنت متأكد من أنك تريد تسجيل الخروج؟")
                                .setCancelable(false)
                                .setPositiveButton("أنا متأكد", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                                    }

                                });
                        builder.setNeutralButton("إلغاء", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { }});

                        AlertDialog alert = builder.create();
                        alert.show();


                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, UserProfile.class));
                        break;
                    case 3:
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, ConcatUsActivity.class));
                        break;
                }
            }

            @Override
            public void onExpand() {
                Toast.makeText(MainActivity.this, "jjjj", Toast.LENGTH_SHORT).show();


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

    public void retrieveSubscribedUsers() {

        DocumentReference docRef = firebaseFirestore.collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        final List<String> list = (List<String>) document.get("subscribedUsers");
                        if (list == null) {
                            emptyStories.setText("لا يوجد قصص تابع واستمتع!");
                        } else { // has SubscribedUsers

                            retriveUserData(list, new MyCallback() {
                                @Override
                                public void onCallback(ArrayList<User> usersDataList) {
                                    retriveSubscribedUserStories(list, usersDataList);
                                }
                            });


                        }// end for loop

                        for (int i = 0; i <= itemList.size(); i++) {
                            if (itemList.size() == 0) {
                                Log.d("TAG", "itemlest is empty");
                                return;
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    public void retriveSubscribedUserStories(List<String> list, final ArrayList<User> usersDataList) {


        for (int i = 0; i < list.size(); i++) {
            Log.d("TAG", "list: " + list.get(i));


            firebaseFirestore.collection("publishedStories")
                    .whereEqualTo("userId", list.get(i) + "")// <-- This line
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {


                                Log.d(TAG, " 1 => test");

                                for (DocumentSnapshot document : task.getResult()) {



                                    document.getData();

                                    String title = (String) document.get("title");
                                    String userId = (String) document.get("userId");
                                    String storyId = (String) document.getId();
                                    String userName = "";
                                    String pic = (String) document.get("pic");
                                    String sound = (String) document.get("sound");

                                    String thumbnail = "";


                                    //retrive user data
                                    for (int i = 0; i < usersDataList.size(); i++) {
                                        if (usersDataList.get(i).getEmail().equals(userId)) {
                                            userName = (String) usersDataList.get(i).getUsername();
                                            thumbnail = (String) usersDataList.get(i).getImg();
                                            break;
                                        }
                                    }

                                    Item item = new Item(true,storyId, title, pic,sound, userId, userName, thumbnail);
                                    itemList.add(item);
                                    adapter.notifyDataSetChanged();

                                }

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }

    }

    public void retriveUserData(final List<String> subscribedUsers, final MyCallback myCallback) {


        for (int i = 0; i < subscribedUsers.size(); i++) {
            DocumentReference docRef2 = firebaseFirestore.collection("users").document(subscribedUsers.get(i));
            docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        int c = 0;
                        DocumentSnapshot userDocument = task.getResult();
                        if (userDocument.exists()) {
                            String userName = userDocument.get("username").toString();
                            String userID = userDocument.getId().toString();
                            String thumbnail = userDocument.get("thumbnail").toString();
                            arrayList.add(new User(userID, userName, thumbnail));

                            if (c < subscribedUsers.size())
                                myCallback.onCallback(arrayList);

                            c++;

                        }

                    }
                }
            });
        }// end retriveUserData


    }

    public interface MyCallback {
        void onCallback(ArrayList<User> arrayList);
    }

    private void showDialogWithOkButton(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}