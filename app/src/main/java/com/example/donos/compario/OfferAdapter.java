package com.example.donos.compario;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import models.Offer;

public class OfferAdapter extends ArrayAdapter<Offer> {

    DBManager dbManager;

    private Context mContext;
    private List<Offer> offers = new ArrayList<>();

    public OfferAdapter(@NonNull Context context, @LayoutRes ArrayList<Offer> list)
    {
        super(context,0,list);
        mContext = context;
        offers = list;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.offer_item,parent,false);
        Offer currentOffer = offers.get(position);

        ImageView image = (ImageView)  listItem.findViewById(R.id.offerImage);
        image.setImageResource(R.drawable.baseline_local_offer_black_18dp);

        TextView offerTitle = (TextView) listItem.findViewById(R.id.offerTitle);
        offerTitle.setText(currentOffer.getOfferTitle());

        TextView price = (TextView) listItem.findViewById(R.id.price);
        price.setText(currentOffer.getPrice());

        TextView storeName = (TextView) listItem.findViewById(R.id.storeName);
        storeName.setText(currentOffer.getStoreName());

        //make star icon full if it is in shopping list
        ImageView star = (ImageView) listItem.findViewById(R.id.starEmpty);
        dbManager = new DBManager(getContext());
        String[] projection = {"OfferTitle","Store","Price","Category"};
        String selection = "OfferTitle=? AND Store=? AND Price=? AND Category=?";
        String[] selectionArgs = {currentOffer.offerTitle, currentOffer.storeName, currentOffer.price, currentOffer.category};
        Cursor cursor = dbManager.query(projection,selection,selectionArgs,DBManager.ColTitle);
        if(cursor.getCount()>0)
        {
            //make star fill
            star.setImageResource(R.drawable.sharp_star_black_18dp);
        }

        return listItem;
    }
}
