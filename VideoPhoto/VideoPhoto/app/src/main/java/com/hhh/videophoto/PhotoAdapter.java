package com.hhh.videophoto;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mList;

    private static final int DATE = 0;
    private static final int PHOTO = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == DATE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item,parent,false);
            return new DateHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,parent,false);
            return new PhotoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHolder) {
            ((DateHolder) holder).textView.setText((String)mList.get(position));
        } else if (holder instanceof PhotoHolder) {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource((String)mList.get(position));// videoPath 本地视频的路径
            Bitmap bitmap  = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC );
            ((PhotoHolder) holder).imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position) instanceof String && !((String) mList.get(position)).contains(".mp4") ) {
            return DATE;
        } else {
            return PHOTO;
        }
    }

    public void setData(List<Object> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    class DateHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public DateHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.date);
        }
    }

    class PhotoHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == DATE
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

}
