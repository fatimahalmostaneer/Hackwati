package sa.ksu.swe444.hackwati.storyActivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleImageArrayAdapter extends ArrayAdapter<Integer> {
    private Integer[] images;
    private String[] name;

    public SimpleImageArrayAdapter(Context context, Integer[] images,String[] name) {
        super(context, android.R.layout.simple_spinner_item, images);
        this.name=name;
        this.images = images;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position);
    }

    private View getImageForPosition(int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(images[position]);
        imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return imageView;
    }

    private View getStringForPosition(int position) {
        TextView textView = new TextView(getContext());
        textView.setBackgroundResource(Integer.parseInt(name[position]));
        textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }
}