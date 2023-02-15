package com.example.newsaggregator;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {
    TopHeadline[] headlines;
    Date publishedAt;

    ViewPager2Adapter(TopHeadline[] headlines) {
        this.headlines = headlines;
    }

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");// input date 2022-11-20T14:01:52Z
    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy HH:mm");// output date MMM dd, yyyy HH:mm

    @NonNull
    @Override
    public ViewPager2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPager2Adapter.ViewHolder holder, int position) {
        // This will set the images in imageview
        //display all info in viewholder
        holder.tvHeadline.setText(headlines[position].title);
        holder.tvHeadline.setOnClickListener(v -> openNews(v, position));
        try {
            publishedAt = format.parse(headlines[position].publishedAt);
            assert publishedAt != null;
            holder.tvNewsArticleDate.setText(format2.format(publishedAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvNewsArticleAuthor.setText(headlines[position].author);
        imageOf(holder.imageView, headlines[position].urlToImage);
        holder.imageView.setOnClickListener(v -> openNews(v, position));
        holder.tvDescription.setText(headlines[position].description);
        holder.tvDescription.setOnClickListener(v -> openNews(v, position));
    }

    private void openNews(View v, int position) {
        startActivity(v.getContext(), new Intent(Intent.ACTION_VIEW, Uri.parse(headlines[position].url)), null);
    }

    private void imageOf(ImageView imgRep, String url) {
        if (url.length() > 0) {
            Glide.with(imgRep.getContext())
                    .load(url)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.brokenimage)
                    .into(imgRep);
        } else {
            imgRep.setBackgroundResource(R.drawable.noimage);
        }

    }


    @Override
    public int getItemCount() {
        return headlines.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeadline;
        TextView tvNewsArticleDate;
        TextView tvNewsArticleAuthor;
        ImageView imageView;
        TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeadline = itemView.findViewById(R.id.tvHeadline);
            tvNewsArticleDate = itemView.findViewById(R.id.tvNewsArticleDate);
            tvNewsArticleAuthor = itemView.findViewById(R.id.tvNewsArticleAuthor);
            imageView = itemView.findViewById(R.id.imgNewsArticle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
