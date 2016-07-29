package com.htc.nick.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.nick.Item.VideoItem;
import com.htc.nick.multimediaplayer.R;

import java.util.ArrayList;

/**
 * Created by nick on 7/28/16.
 */
public class PhotoGridViewAdapter extends ArrayAdapter<Bitmap> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Bitmap> data = new ArrayList<>();


    public PhotoGridViewAdapter(Context context, int resource, ArrayList<Bitmap> videoItems) {
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


        Bitmap item = data.get(position);
        holder.thumbnail.setImageBitmap(item);
        return row;

    }

    static class ViewHolder {
        TextView fileName;
        ImageView thumbnail;
    }
}
