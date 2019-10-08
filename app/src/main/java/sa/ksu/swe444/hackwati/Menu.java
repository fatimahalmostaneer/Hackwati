package sa.ksu.swe444.hackwati;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;

import java.util.ArrayList;
import java.util.List;

import sa.ksu.swe444.hackwati.ui.profileActivity.ProfileActivity;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        installButton110to250();

    }



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
                showToast("clicked index:" + index);
                switch (index){
                    case 1:
                        startActivity(new Intent(Menu.this, Login.class));
                        break;
                    case 2:
                        startActivity(new Intent(Menu.this, UserProfile.class));
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onExpand() {
//                showToast("onExpand");

            }

            @Override
            public void onCollapse() {
//                showToast("onCollapse");
            }
        });
    }

    private void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}