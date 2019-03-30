package com.codepath.claudia.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.claudia.socialmedia.Fragments.ComposeFragment;
import com.codepath.claudia.socialmedia.Fragments.PostFragment;
import com.codepath.claudia.socialmedia.Fragments.ProfileFragment;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private Button btnLogOut;


    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut = findViewById(R.id.btnLogOut);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        final FragmentManager fragmentManager = getSupportFragmentManager();



        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Log out button pressed");
                logOutAccount();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = new ComposeFragment();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        //TODO: swap fragment here
                        fragment = new PostFragment();
                        Log.d(TAG, "Home!");
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        Log.d(TAG, "Compose!");
                        break;
                    case R.id.action_profile:
                    default:
                        //TODO: swap fragment here
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
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

}
