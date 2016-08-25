package com.htc.nick.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.htc.nick.Item.PhotoItem;
import com.htc.nick.Page.PhotoViewer.FullScreenViewActivity_;
import com.htc.nick.multimediaplayer.R;

import java.util.ArrayList;

/**
 * Created by nick on 7/28/16.
 */
public class PhotoGridViewAdapter extends ArrayAdapter<PhotoItem> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<PhotoItem> data = new ArrayList<>();


    public PhotoGridViewAdapter(Context context, int resource, ArrayList<PhotoItem> videoItems) {
        super(context, resource, videoItems);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = videoItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.fileName = (TextView) row.findViewById(R.id.text);
            holder.thumbnail = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        Glide.with(context)
                .load(data.get(position).getThumbnailUri())
                .thumbnail(0.1f)
                .into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new OnImageClickListener(position));
        return row;

    }


    class OnImageClickListener implements View.OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(getContext(), FullScreenViewActivity_.class);
            i.putExtra("position", _postion);
            getContext().startActivity(i);
        }

    }
    static class ViewHolder {
        TextView fileName;
        ImageView thumbnail;
    }
}
