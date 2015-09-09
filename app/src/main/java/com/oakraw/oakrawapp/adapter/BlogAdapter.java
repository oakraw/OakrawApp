package com.oakraw.oakrawapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.oakraw.oakrawapp.AppController;
import com.oakraw.oakrawapp.R;
import com.oakraw.oakrawapp.model.BlogList;

import java.util.ArrayList;

/**
 * Created by Rawipol on 2/21/15 AD.
 */
public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {


    public static class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView category;
        protected TextView title;
        protected TextView date;
        protected ImageView bg;
        public OnItemClickListener onItemClickListener;

        public BlogViewHolder(View v, OnItemClickListener listener) {
            super(v);
            bg =  (ImageView) v.findViewById(R.id.bg);
            title =  (TextView) v.findViewById(R.id.title);
            date =  (TextView) v.findViewById(R.id.date);
            category =  (TextView) v.findViewById(R.id.category);
            onItemClickListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onBlogSelect(v.getId());
        }

        public interface OnItemClickListener {
            void onBlogSelect(int blogIndex);
        }
    }

    private ArrayList<BlogList> blogList;
    private Context mContext;

    public BlogAdapter(Context context, ArrayList<BlogList> blogList, OnBlogClickListener listener) {
        this.mContext = context;
        this.blogList = blogList;
        onBlogClickListener = listener;
    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list, viewGroup, false);

        return new BlogViewHolder(view, new BlogViewHolder.OnItemClickListener() {
            @Override
            public void onBlogSelect(int blogIndex) {
                onBlogClickListener.onClick(blogIndex);
            }
        });
    }

    @Override
    public void onBindViewHolder(BlogViewHolder blogViewHolder, int i) {

        Glide.with(mContext).load("http://oakraw.com"+blogList.get(i).feature_img).into(blogViewHolder.bg);
        //blogViewHolder.bg.setImageResource(R.mipmap.promo);

        blogViewHolder.title.setText(blogList.get(i).title);
        blogViewHolder.date.setText(blogList.get(i).date);
        blogViewHolder.category.setText(blogList.get(i).categories);
        GradientDrawable drawable = (GradientDrawable) blogViewHolder.category.getBackground();
        if(AppController.catColor.size() == 0){
            drawable.setColor(mContext.getResources().getColor(R.color.text_bg));
        }else {
            drawable.setColor(Color.parseColor(AppController.catColor.get(blogList.get(i).categories)));
        }
        /*blogViewHolder.title.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "SukhumvitSet.ttc"), Typeface.BOLD);
        blogViewHolder.date.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "SukhumvitSet.ttc"), Typeface.NORMAL);
        blogViewHolder.category.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "SukhumvitSet.ttc"), Typeface.NORMAL);*/
        /*if(AppController.catColor.size() == 0){
            blogViewHolder.category.setBackgroundColor(mContext.getResources().getColor(R.color.title_bar));
        }else {
            blogViewHolder.category.setBackgroundColor(Color.parseColor("#98"+AppController.catColor.get(blogList.get(i).categories).substring(1)));
        }*/
        ((View) blogViewHolder.bg.getParent()).setId(blogList.get(i).id);

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public OnBlogClickListener onBlogClickListener;

    public interface OnBlogClickListener {
        public void onClick(int blog);
    }

}
