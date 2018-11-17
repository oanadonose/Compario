package com.example.donos.compario;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> implements
        View.OnClickListener{
    private ArrayList<DataModel> dataSet;
    Context context;

    //View lookup cache
    private static class ViewHolder{
        TextView shopName;
        TextView shopCompany;
        TextView shopDistance;
        TextView shopOffers;
        ImageView shopImage;
    }
    public CustomAdapter(ArrayList<DataModel> data, Context context){
        super(context,R.layout.activity_listview,data);
        this.dataSet = data;
        this.context = context;
    }
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {
            case R.id.root:
                Toast.makeText(getContext(),"This is "+dataModel.getName(),Toast.LENGTH_LONG).show();
                break;
        }
    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_listview, parent, false);
            viewHolder.shopName = (TextView) convertView.findViewById(R.id.shopName);
            viewHolder.shopCompany = (TextView) convertView.findViewById(R.id.shopCompany);
            viewHolder.shopDistance = (TextView) convertView.findViewById(R.id.shopDistance);
            viewHolder.shopOffers = (TextView) convertView.findViewById(R.id.shopOffers);
            viewHolder.shopImage = (ImageView) convertView.findViewById(R.id.shopImage);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.shopName.setText(dataModel.getName());
        viewHolder.shopCompany.setText(dataModel.getCompany());
        viewHolder.shopDistance.setText(dataModel.getDistance());
        viewHolder.shopImage.setOnClickListener(this);
        viewHolder.shopImage.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
