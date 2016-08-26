package com.htc.nick.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.htc.nick.CustomView.TouchImageView;
import com.htc.nick.Item.PhotoItem;
import com.htc.nick.multimediaplayer.R;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private ArrayList<PhotoItem> photoItems;
	private LayoutInflater inflater;

	// constructor
	public FullScreenImageAdapter(Activity activity,
			ArrayList<PhotoItem> photoItems) {
		this._activity = activity;
		this.photoItems = photoItems;
	}

	@Override
	public int getCount() {
		return this.photoItems.size();
	}

	@Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
	
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        Button btnClose;
        ImageButton btnMoreInfo;
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
		final PhotoItem photoItem = photoItems.get(position);
        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
		btnMoreInfo = (ImageButton) viewLayout.findViewById(R.id.moreInfo);
		Glide.with(_activity)
				.load(photoItems.get(position).getPath())
				.crossFade()
				.into(imgDisplay);
		      // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				_activity.finish();
			}
		});
		btnMoreInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Log.d("Photo",photoItem.getName()+photoItem.getPath()+
						","+photoItem.getContentType()+","+ photoItem.getSize()+","+
						photoItem.getDate()+","+photoItem.getResolution());

				SweetAlertDialog sweetAlertDialog =new SweetAlertDialog(_activity);
				sweetAlertDialog.setCanceledOnTouchOutside(true);
				sweetAlertDialog.setTitleText("Detail")
						.setContentText("Name:"+photoItem.getName()+'\n'+"Path:"+photoItem.getPath()
						+'\n'+"ContentType:"+photoItem.getContentType()
						+'\n'+"Size:"+photoItem.getSize()+"kb"
						+'\n'+"Resolution:"+photoItem.getResolution())
						.show();

			}
		});

         container.addView(viewLayout);
 
        return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
         container.removeView((RelativeLayout) object);
 
    }

}
