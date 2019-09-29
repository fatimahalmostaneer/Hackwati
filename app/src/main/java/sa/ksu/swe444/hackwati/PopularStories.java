package sa.ksu.swe444.hackwati;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PopularStories extends Fragment {


    private RecyclerView recyclerView;
    private storyAdapter adapter;
    private List<Item> itemList;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_popular_stories, container, false);
        final FragmentActivity fragmentBelongActivity = getActivity();

    recyclerView = view.findViewById(R.id.recycler_view);
    itemList = new ArrayList<>();
    adapter = new storyAdapter(fragmentBelongActivity,itemList);
    mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setAdapter(adapter);
    itemList();

        return view;
    }

    private void itemList() {
        int[] covers = new int[]{
                R.drawable.gray,
                R.drawable.gray,
        };
        Item a =new Item("انبتهي يا جود",covers[0]);
        itemList.add(a);
        a =new Item("انبتهي يا ساره",covers[1]);
        itemList.add(a);
        a =new Item("انبتهي يا هدى",covers[0]);
        itemList.add(a);
        a =new Item("انبتهي يا حمديه",covers[1]);
        itemList.add(a);
    }



}
