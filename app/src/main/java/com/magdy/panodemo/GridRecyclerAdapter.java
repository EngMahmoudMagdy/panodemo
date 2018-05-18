package com.magdy.panodemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;


public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.SimpleViewHolder> {

    private List<Place> places;
    private Context context;

    private GridInfoListener fListener;

    public GridRecyclerAdapter(Context context, List<Place> places, GridInfoListener fl) {
        this.places = places;
        this.context = context;
        fListener = fl;

    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.tour_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Place place= places.get(position);
        Picasso.with(context).load(place.getImageLink()).into(holder.headImage);
        /*Picasso.with(context).load(place.getImageLink()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.headImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/
        holder.name.setText(place.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fListener.setSelected(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        TextView name;
        SimpleViewHolder(View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.title);

        }
    }
}
