package com.example.stickyheadergridview;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;


public class StickyGridAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    public List<GridItem> list;
    private LayoutInflater mInflater;
    private GridView mGridView;
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    private Context mContext;
    public boolean isEditMode = false;
    CallBackInterface mCallBackInterface;
    String mTime =new SimpleDateFormat("yyyy年").format(System.currentTimeMillis());


    RequestOptions options = new RequestOptions()
           /* .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)*/;
    public StickyGridAdapter(Context context, List<GridItem> list,
                             GridView mGridView ,CallBackInterface callBackInterface) {
        this.list = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mGridView = mGridView;
        this.mCallBackInterface=callBackInterface;
        options.placeholder(R.drawable.pictures_load_lint)
                .error(R.drawable.pictures_load_lint);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gallery_grid_item, parent, false);
            mViewHolder.mImageView = convertView
                    .findViewById(R.id.grid_item);
            mViewHolder.mVedioPlay = convertView
                    .findViewById(R.id.vedio_play);
            mViewHolder.mVedioDuration=convertView.findViewById(R.id.vedio_duration);
            mViewHolder.mCheckBox = convertView.findViewById(R.id.checkBox);
            mViewHolder.mCover= convertView.findViewById(R.id.grid_item_cover);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        String path = list.get(position).getPath();
        if (list.get(position).isVedio()) {
            mViewHolder.mVedioPlay.setVisibility(View.VISIBLE);
            mViewHolder.mVedioDuration.setVisibility(View.VISIBLE);
            mViewHolder.mVedioDuration.setText(FileUtil.getDuration(path));
        } else {
            mViewHolder.mVedioPlay.setVisibility(View.GONE);
            mViewHolder.mVedioDuration.setVisibility(View.GONE);
        }
        if (isEditMode == true) {
            mViewHolder.mCheckBox.setVisibility(View.VISIBLE);
            if(list.get(position).isChecked()){
                mViewHolder.mCheckBox.setChecked(true);
                mViewHolder.mCover.setVisibility(View.VISIBLE);
            }else{
                mViewHolder.mCheckBox.setChecked(false);
                mViewHolder.mCover.setVisibility(View.GONE);
            }
            int paddingSize=(int)dpToPx(mContext,9)/2;
            convertView.setPadding(paddingSize,paddingSize,paddingSize,paddingSize);
        } else {
            mViewHolder.mCheckBox.setVisibility(View.GONE);
            mViewHolder.mCover.setVisibility(View.GONE);
            convertView.setPadding(0,0,0,0);
        }

        Glide.with(mContext)
                .load(path)
                .apply(options)
                .into(mViewHolder.mImageView);
        return convertView;
    }


    @Override
    public View getHeaderView(final int position, View convertView, ViewGroup parent) {
        final HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.gallery_header, parent, false);
            mHeaderHolder.mTextView = convertView
                    .findViewById(R.id.header);
            mHeaderHolder.mSelect = convertView.findViewById(R.id.select);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        mHeaderHolder.mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked=!mHeaderHolder.mSelect.getText().equals(mContext.getString(R.string.select_all));
                mHeaderHolder.mSelect.setText(isChecked ? mContext.getString(R.string.select_all):mContext.getString(R.string.select_none));
                for (int i=0 ; i<list.size() ;i++) {
                    if (list.get(i).getTime().equals(list.get(position).getTime())) {
                        list.get(i).setChecked(!isChecked);
                    }
                }
                notifyDataSetChanged();
                mCallBackInterface.callBackFunction();
            }
        });
        int i =0 ,j =0;
        if (isEditMode == true) {
            for(GridItem gridItem :list){
                if (gridItem.getTime().equals(list.get(position).getTime())){
                    i++;
                    if(gridItem.isChecked()) j++;
                }
            }
            mHeaderHolder.mSelect.setText(i==j ? mContext.getString(R.string.select_none):mContext.getString(R.string.select_all));
            mHeaderHolder.mSelect.setVisibility(View.VISIBLE);
        } else {
            mHeaderHolder.mSelect.setVisibility(View.GONE);
        }
        mHeaderHolder.mTextView.setText(list.get(position).getTime().replace(mTime,""));
        return convertView;
    }

    public static class ViewHolder {
        public ImageView mImageView;
        public ImageView mVedioPlay;
        public TextView mVedioDuration;
        public CheckBox mCheckBox;
        public ImageView mCover;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
        public TextView mSelect;
    }

    private float dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getSection();
    }
}
