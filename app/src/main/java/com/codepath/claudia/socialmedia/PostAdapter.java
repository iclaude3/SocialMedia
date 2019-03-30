package com.codepath.claudia.socialmedia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private static final String TAG = "PostAdapter";

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //parent is the root that we are going to inflate the view into
        //attachtoroot is false b/c recycler view is responsible for attaching and detaching it
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bing(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHandle;
        private ImageView ivImage;
        private TextView tvDescription;
        private ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }

        public void bing(Post post) {
            //TODO: bing the view elements to the post
            tvHandle.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            ParseFile profilePhoto = post.getUser().getParseFile("profilePhoto");

            if(image != null) {
                Log.d(TAG, "Image exists");
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            if(profilePhoto != null) {
                Log.d(TAG, "Profile photo exists");
                Glide.with(context).load(profilePhoto.getUrl()).into(ivProfileImage);
            }
            tvDescription.setText(post.getDescription());
        }

    }
}
