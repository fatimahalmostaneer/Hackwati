package sa.ksu.swe444.hackwati.Record;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import sa.ksu.swe444.hackwati.R;


public class RecordingActivity extends AppCompatActivity implements Tab1Fragment.SecondFragmentListener{
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Tab1Fragment f1;
    private Tab2Fragment f2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_main_activity);

        //initiate frags
        f1 = new Tab1Fragment();
        f2 = new Tab2Fragment();

        // fragments array list
        ArrayList<Fragment> frags = new ArrayList<>();
        frags.add(f1);
        frags.add(f2);

        //titles array list
        ArrayList<String> titles = new ArrayList<>();
        titles.add("سجل صوتك");
        titles.add("انشر قصتك");

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TabAdapter(getSupportFragmentManager() , frags , titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

            ((IOnFocusListenable) f1).onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onNextButton() {
        viewPager.setCurrentItem(1);

    }
}