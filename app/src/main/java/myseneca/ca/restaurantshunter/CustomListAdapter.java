package myseneca.ca.restaurantshunter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OceanHsiao on 16-04-10.
 */
// Ref: http://www.androidhive.info/2014/07/android-custom-listview-with-image-and-text-using-volley/
public class CustomListAdapter extends BaseAdapter {
    private Activity mListActivity;
    private LayoutInflater inflater;
    private List<Restaurant> mRestaurantsItems;
    private Restaurant mChosenRestaurant;
    private List<Restaurant> mCheckedRestaurants;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Restaurant> restaurantList) {
        this.mListActivity = activity;
        this.mRestaurantsItems = restaurantList;
        mCheckedRestaurants = new ArrayList<Restaurant>();
    }

    @Override
    public int getCount() {
        return mRestaurantsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return mRestaurantsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        mChosenRestaurant = mRestaurantsItems.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) mListActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_restaurants_content, null);

        if(!mChosenRestaurant.getImages().equals("")) {
            Bitmap bmp = CommentActivity.decodeSampledBitmapFromFile(CommentActivity.mPhotosDir + "/"
                    + Restaurant.transToImageStringArray(mChosenRestaurant.getImages()).get(0) + ".jpg");
            ImageView resImage = (ImageView) convertView.findViewById(R.id.defPhoto);
            resImage.setImageBitmap(bmp);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.ResName);
        tvName.setText(mChosenRestaurant.getName());
        TextView tvAddr = (TextView) convertView.findViewById(R.id.ResAddr);
        tvAddr.setText(mChosenRestaurant.getAddress());
        TextView tvPost = (TextView) convertView.findViewById(R.id.ResPostCode);
        tvPost.setText(mChosenRestaurant.getPostCode());
        TextView tvCountry = (TextView) convertView.findViewById(R.id.ResCountry);
        tvCountry.setText(mChosenRestaurant.getCountry());

        //Set-up custom Ratingbar
        //Ref: http://kozyr.zydako.net/2010/05/23/pretty-ratingbar/
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rates);
        ratingBar.setRating(mChosenRestaurant.getRate());

        Button comment_btn = (Button) convertView.findViewById(R.id.btnComment);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parentRow = (View) view.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                mChosenRestaurant = mRestaurantsItems.get(position);

                Intent commentIntent = new Intent(parentRow.getContext(), CommentActivity.class);
                commentIntent.putExtra("RequestFrom", MainActivity.COMM_FROM_LIST);
                commentIntent.putExtra("RestaurantObj", mChosenRestaurant);
                mListActivity.startActivity(commentIntent);
                mListActivity.finish();
            }
        });

        final CheckBox check = (CheckBox)convertView.findViewById(R.id.checkBox);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //do stuff
                mChosenRestaurant = mRestaurantsItems.get(position);
                if (check.isChecked())
                    mCheckedRestaurants.add(mChosenRestaurant);
                else {
                    int i = 0;
                    for (Restaurant r : mCheckedRestaurants) {
                        if (r.getId() == mChosenRestaurant.getId() ) {
                            mCheckedRestaurants.remove(i);
                            break;
                        }
                        i++;
                    }
                }
            }
        });

        return convertView;
    }

    public List<Restaurant> getCheckedRestaurants() {
        return mCheckedRestaurants;
    }
}
