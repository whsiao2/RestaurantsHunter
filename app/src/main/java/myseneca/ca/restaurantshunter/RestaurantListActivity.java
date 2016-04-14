package myseneca.ca.restaurantshunter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {

    private List<Restaurant> mRestaurantList;
    private ListView mListView;
    private CustomListAdapter mAdapter;

    private String[] mRewards;
    private int mRewardLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        mRewards = getResources().getStringArray(R.array.rewards_array);

        //Ref: http://sourcey.com/android-custom-centered-actionbar-with-material-design/
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRestaurantList = MainActivity.mDb.getSpecificRestaurants(Restaurant.KEY_rate + " > 0 OR " +
                                                                  Restaurant.KEY_comment + " != '' OR " +
                                                                  Restaurant.KEY_imgs + " != ''");
        String rankName = checkLevel(mRestaurantList.size());
        if (!rankName.equals("")) {
            int level = 1;
            for (String s : mRewards) {
                if (rankName.equals(s)) {
                    break;
                }
                level++;
            }
            mRewardLevel = Integer.parseInt(ReadRewardFile());
            if (level > mRewardLevel) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantListActivity.this);
                alertDialogBuilder.setTitle("Level UP!");
                alertDialogBuilder.setMessage("Congradulations!!! You are a " + rankName + " of Restaurant-Hunter!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                WriteRewardFile(level);
                mRewardLevel = level;
            }
        }

        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new CustomListAdapter(this, mRestaurantList);
        mListView.setAdapter(mAdapter);
        //Ref:http://stackoverflow.com/questions/15949839/android-set-empty-view-to-a-list-view
        mListView.setEmptyView(findViewById(android.R.id.empty));

        Button btnEmail = (Button) findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Restaurant> checkedRestaurants = mAdapter.getCheckedRestaurants();
                HTMLFactory htmlFactory = new HTMLFactory(checkedRestaurants);
                String htmlBody = htmlFactory.generateHTMLofRestaurantComments();

                if (checkedRestaurants.size() > 0) {
                    Intent mailItent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                    mailItent.setType("text/html");
                    mailItent.putExtra(Intent.EXTRA_EMAIL, "");
                    mailItent.putExtra(Intent.EXTRA_SUBJECT, "The recommendation of my visited Restaurants");
                    //mailItent.putExtra(Intent.EXTRA_TEXT, "Thank You for Using Adroid App - 'Restaurants Hunter'");
                    mailItent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlBody));
                    startActivity(mailItent);
                    startActivity(Intent.createChooser(mailItent, "Choose a mail app to share your comments"));

                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantListActivity.this);
                    alertDialogBuilder.setTitle("Please Select Restaurants");
                    alertDialogBuilder.setMessage("You MUST select At Least ONE restaurant to share via E-mail.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        Button btnReward = (Button) findViewById(R.id.btn_rewards);
        btnReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rewardIntent = new Intent(RestaurantListActivity.this, RewardActivity.class);
                rewardIntent.putExtra("RewardLevel", mRewardLevel);
                startActivity(rewardIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public String ReadRewardFile() {
        File sdCardDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/" + MainActivity.RH_PHOTO_FOLDER);
        File myFile = new File(sdCardDir.getAbsolutePath(), MainActivity.RH_REWARD_FILE);
        if (!myFile.exists()) //If
            WriteRewardFile(0);
        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String line = null;
            while ((line = myReader.readLine()) != null) {
                String[] strLine = line.split("=");
                if(strLine[0].equals("level"))
                    sb.append(strLine[1]);
            }
            myReader.close();
            fIn.close();

        } catch (Exception e) {
            return "";
        }
        return sb.toString();
    }

    public void WriteRewardFile(int level) {
        File sdCardDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/" + MainActivity.RH_PHOTO_FOLDER);
        if (!sdCardDir.exists()) {
            sdCardDir.mkdirs();
        }
        File myFile = new File(sdCardDir, MainActivity.RH_REWARD_FILE);
        if (!myFile.exists())
            Toast.makeText(getBaseContext(), "file is not existed!", Toast.LENGTH_SHORT).show();

        try {
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("level=" + String.valueOf(level));
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String checkLevel(int numHuntedRestaurants) {
        String strLevel = "";

        if (numHuntedRestaurants >= 5 && numHuntedRestaurants < 10) {
            strLevel = mRewards[0];
        } else if (numHuntedRestaurants >= 10 && numHuntedRestaurants < 15) {
            strLevel = mRewards[1];
        } else if (numHuntedRestaurants >= 15 && numHuntedRestaurants < 20) {
            strLevel = mRewards[2];
        } else if (numHuntedRestaurants >= 20 && numHuntedRestaurants < 30) {
            strLevel = mRewards[3];
        } else if (numHuntedRestaurants >= 30 && numHuntedRestaurants < 45) {
            strLevel = mRewards[4];
        } else if (numHuntedRestaurants >= 45 && numHuntedRestaurants < 60) {
            strLevel = mRewards[5];
        } else if (numHuntedRestaurants >= 60 && numHuntedRestaurants < 75) {
            strLevel = mRewards[6];
        } else if (numHuntedRestaurants >= 75 && numHuntedRestaurants < 95) {
            strLevel = mRewards[7];
        } else if (numHuntedRestaurants >= 95 && numHuntedRestaurants < 150) {
            strLevel = mRewards[8];
        } else if (numHuntedRestaurants >= 150) {
            strLevel = mRewards[9];
        }

        return strLevel;
    }
}
