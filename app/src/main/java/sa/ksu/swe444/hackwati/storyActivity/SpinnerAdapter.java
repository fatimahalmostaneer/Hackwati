package sa.ksu.swe444.hackwati.storyActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import sa.ksu.swe444.hackwati.R;


public class SpinnerAdapter extends ArrayAdapter<String> {

    private Context ctx;
    private Integer[] imageArray;

    public SpinnerAdapter(Context context, int resource, String[] objects, Integer[] imageArray) {
        super(context, R.layout.spinner_value_layout,objects);
        this.ctx = context;
        this.imageArray = imageArray;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_value_layout, parent, false);
        ImageView imageView = row.findViewById(R.id.spinnerImages);
        imageView.setImageResource(imageArray[position]);

        return row;
    }
}

