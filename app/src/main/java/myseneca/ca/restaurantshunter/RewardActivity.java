package myseneca.ca.restaurantshunter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class RewardActivity extends AppCompatActivity {

    private int mRewardLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRewardLevel = getIntent().getIntExtra("RewardLevel", -1);

        ImageView img1= (ImageView) findViewById(R.id.reward1);
        ImageView img2= (ImageView) findViewById(R.id.reward2);
        ImageView img3= (ImageView) findViewById(R.id.reward3);
        ImageView img4= (ImageView) findViewById(R.id.reward4);
        ImageView img5= (ImageView) findViewById(R.id.reward5);
        ImageView img6= (ImageView) findViewById(R.id.reward6);
        ImageView img7= (ImageView) findViewById(R.id.reward7);
        ImageView img8= (ImageView) findViewById(R.id.reward8);
        ImageView img9= (ImageView) findViewById(R.id.reward9);
        ImageView img10= (ImageView) findViewById(R.id.reward10);

        if (mRewardLevel != -1) {
            switch (mRewardLevel) {
                case 1: {
                    img1.setImageResource(R.drawable.img_reward1);
                    break;
                }
                case 2: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    break;
                }
                case 3: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    break;
                }
                case 4: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    img4.setImageResource(R.drawable.img_reward4);
                    break;
                }
                case 5: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    img4.setImageResource(R.drawable.img_reward4);
                    img5.setImageResource(R.drawable.img_reward5);
                    break;
                }
                case 6: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    img4.setImageResource(R.drawable.img_reward4);
                    img5.setImageResource(R.drawable.img_reward5);
                    img6.setImageResource(R.drawable.img_reward6);
                    break;
                }
                case 7: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    img4.setImageResource(R.drawable.img_reward4);
                    img5.setImageResource(R.drawable.img_reward5);
                    img6.setImageResource(R.drawable.img_reward6);
                    img7.setImageResource(R.drawable.img_reward7);
                    break;
                }
                case 8: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    img4.setImageResource(R.drawable.img_reward4);
                    img5.setImageResource(R.drawable.img_reward5);
                    img6.setImageResource(R.drawable.img_reward6);
                    img7.setImageResource(R.drawable.img_reward7);
                    img8.setImageResource(R.drawable.img_reward8);
                    break;
                }
                case 9: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    img4.setImageResource(R.drawable.img_reward4);
                    img5.setImageResource(R.drawable.img_reward5);
                    img6.setImageResource(R.drawable.img_reward6);
                    img7.setImageResource(R.drawable.img_reward7);
                    img8.setImageResource(R.drawable.img_reward8);
                    img9.setImageResource(R.drawable.img_reward9);
                    break;
                }
                case 10: {
                    img1.setImageResource(R.drawable.img_reward1);
                    img2.setImageResource(R.drawable.img_reward2);
                    img3.setImageResource(R.drawable.img_reward3);
                    img4.setImageResource(R.drawable.img_reward4);
                    img5.setImageResource(R.drawable.img_reward5);
                    img6.setImageResource(R.drawable.img_reward6);
                    img7.setImageResource(R.drawable.img_reward7);
                    img8.setImageResource(R.drawable.img_reward8);
                    img9.setImageResource(R.drawable.img_reward9);
                    img10.setImageResource(R.drawable.img_reward10);
                    break;
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
