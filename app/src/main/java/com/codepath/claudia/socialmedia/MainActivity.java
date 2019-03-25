package com.codepath.claudia.socialmedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private EditText etDescription;
    private ImageButton btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private Button btnLogOut;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription = findViewById(R.id.etDescription);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        ivPostImage = findViewById(R.id.ivPostImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogOut = findViewById(R.id.btnLogOut);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

//        queryPosts();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                ParseUser user = ParseUser.getCurrentUser();
                Log.d(TAG, "Submit button clicked");
                if(photoFile == null || ivPostImage.getDrawable() == null) {
                    Log.e(TAG, "No photo to submit");
                    Toast.makeText(MainActivity.this, "There is no photo!", Toast.LENGTH_LONG).show();
                    return;
                }
                savePost(description, user, photoFile);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Log out button pressed");
                logOutAccount();
            }
        });
    }

    private void logOutAccount() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    //TODO: report to user that they have entered wrong login information
                    Toast.makeText(MainActivity.this, "Unable to logout", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Issue logging out");
                    e.printStackTrace();
                    return;
                }
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        Log.d(TAG, "Navigating to Login Activity");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void savePost(String description, ParseUser user, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setImage(new ParseFile(photoFile));
        Log.d(TAG, "Going to save in background now");
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "In done method");
                if(e != null) {
                    Log.d(TAG, "Error while saving");
                    return;
                }
                Log.d(TAG, "Successfully saved post!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        //want to do in background because network calls are expensive
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "ERROR WITH QUERY");
                    e.printStackTrace();
                    return;
                }
                for(int i = 0; i < posts.size(); i++) {
                    Log.d(TAG, "Post: " + posts.get(i).getDescription() + " username:" + posts.get(i).getUser().getUsername());
                }
            }
        });
    }
}
