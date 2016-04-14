package myseneca.ca.restaurantshunter;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;

import java.io.InputStream;

//Refs: http://stackoverflow.com/questions/5486789/how-do-i-make-a-splash-screen
//      https://www.youtube.com/watch?v=BAJVM5QBZZk
//      http://www.41post.com/4588/programming/android-coding-a-loading-screen-part-1
public class SplashActivity extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    private XmlParser xmlParser;
    private DataBaseHelper _db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Set no title bar
        setContentView(R.layout.activity_splash);

        _db = new DataBaseHelper(this);
        xmlParser = new XmlParser();
        new LoadViewTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Close database
        _db.close();
    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Restaurants Hunter");
            progressDialog.setMessage("Loading data, please wait...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
            //Display the progress dialog
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            try
            {
                //Get the current thread's token
                synchronized (this)
                {
                    if(_db.isEmpty()) {
                        this.wait(850);
                        //UK, London
                        InputStream in_london = getResources().openRawResource(R.raw.london);
                        xmlParser.parseFromFileStream(in_london, XmlParser.Cities.LONDON);

                        //USA, NY state
                        InputStream in_NY = getResources().openRawResource(R.raw.nystate);
                        xmlParser.parseFromFileStream(in_NY, XmlParser.Cities.YONKERS);

                        //Load data to Database
                        for (Restaurant r : xmlParser.getAllRestaurants()) {
                            _db.insert(r);
                        }
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            //close the progress dialog
            progressDialog.dismiss();
            //initialize the View

            /* New Handler to start the Menu-Activity
            * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
}
