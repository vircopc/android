package com.bbg.textalbum;

import android.content.ContentResolver;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawableLoadProvider;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter;

import java.io.File;
import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 2017/10/19.
 */

public class RVAdapter extends SectionedRecyclerViewAdapter<RVAdapter.MyHeadView,RVAdapter.MyViewHolder,RVAdapter.MyFootView>{
    public List<Photo> photos;
    Context context;
    public List<String> photoDates;
    public static int sectionCount;
    String[][] path=new String[1000][1000];

    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;
    public RVAdapter(List<Photo> photos, List<String> photoDates,Context context) {
        this.photos = photos;
        this.context = context;
        this.photoDates = photoDates;
        sectionCount=photoDates.size();
        System.out.println("zhiqian");
       // System.out.println(sectionCount);
        getPath();
        System.out.println(photos.size()+"@@@"+photoDates.size());
    }

    @Override
    protected MyFootView onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected MyHeadView onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
       View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.headitem,parent,false);

        return new MyHeadView(view);
    }

    @Override
    protected MyViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        //  System.out.println("photoSize: "+photos.size()+"\n"+"photoDates: "+photoDates.size());
        return new MyViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(MyViewHolder holder, final int section, int position) {
        final MyViewHolder viewHolder=(MyViewHolder)holder;
//       String path=getPath(section)[section][position];
//        System.out.println(path);
//            Ttest();
       Glide.with(context).load(new File(path[section][position])).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.img);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,viewHolder.getAdapterPosition()-section-1);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener itemClick){
        this.onItemClickListener=itemClick;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClick){
        this.onItemLongClickListener=itemLongClick;
    }
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }


    @Override
    protected void onBindSectionFooterViewHolder(MyFootView holder, int section) {

    }

    @Override
    protected void onBindSectionHeaderViewHolder(MyHeadView holder, int section) {
            MyHeadView viewHolder=(MyHeadView)holder;
            viewHolder.dateTV.setText(photoDates.get(section));

    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    @Override
    public int getSectionCount() {
        //return photoDates.size();
       // System.out.println(MainActivity.photoDates.size()+"");
        return MainActivity.photoDates.size();
    }

    @Override
    public int getItemCount() {
        return photos.size()+photoDates.size();
    }

    @Override
    protected int getItemCountForSection(int section) {
        return getCountInsection(section);
    }


    public int  getCountInsection(int section){
        List<Photo> sectionPhotos=new ArrayList<>();
        for (int i=0;i<MainActivity.photos.size();i++){
            if (MainActivity.photoDates.get(section).equals(MainActivity.photos.get(i).getDate())){
                sectionPhotos.add(MainActivity.photos.get(i));
            }
        }

        return sectionPhotos.size();
    }

    public void getPath(){

        int count=0;
        for (int i=0;i<photoDates.size();i++){
            for (int j=0;j<getCountInsection(i);j++){
                path[i][j]=photos.get(count).getPath();
                count++;
            }
        }

        System.out.println(photos.get(0).getPath());
    }



    class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        SquareImg img;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView=(CardView)itemView.findViewById(R.id.cardView);
            img=(SquareImg)itemView.findViewById(R.id.img);
        }
    }
    class MyHeadView extends RecyclerView.ViewHolder{
        TextView dateTV;

        public MyHeadView(View itemView) {
            super(itemView);
            dateTV=(TextView) itemView.findViewById(R.id.headTV);
        }
    }
    class MyFootView extends RecyclerView.ViewHolder{
        public MyFootView(View itemView) {
            super(itemView);
        }
    }
}
