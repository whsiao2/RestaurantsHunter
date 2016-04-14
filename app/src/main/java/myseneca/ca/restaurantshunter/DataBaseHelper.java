package myseneca.ca.restaurantshunter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OceanHsiao on 16-04-03.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String LOGCAT = null;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "restaurants.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        String CREATE_TABLE_RESTAURANT = "CREATE TABLE " + Restaurant.TABLE  + "("
                + Restaurant.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Restaurant.KEY_name + " TEXT, "
                + Restaurant.KEY_address + " TEXT, "
                + Restaurant.KEY_city + " TEXT, "
                + Restaurant.KEY_postcode + " TEXT, "
                + Restaurant.KEY_zipcode + " TEXT, "
                + Restaurant.KEY_latitude + " REAL UNIQUE ON CONFLICT REPLACE, "
                + Restaurant.KEY_longitude + " REAL UNIQUE ON CONFLICT REPLACE, "
                + Restaurant.KEY_rate + " INTEGER, "
                + Restaurant.KEY_comment + " TEXT, "
                + Restaurant.KEY_imgs + " TEXT, "
                + Restaurant.KEY_state + " TEXT, "
                + Restaurant.KEY_country + " TEXT )";

        db.execSQL(CREATE_TABLE_RESTAURANT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Restaurant.TABLE);

        // Create tables again
        onCreate(db);
    }

    public long insert(Restaurant restaurant) {
        //Open connection to write data
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Restaurant.KEY_ID, restaurant.getId());
        values.put(Restaurant.KEY_name, restaurant.getName());
        values.put(Restaurant.KEY_address, restaurant.getAddress());
        values.put(Restaurant.KEY_city, restaurant.getCity());
        values.put(Restaurant.KEY_postcode, restaurant.getPostCode());
        values.put(Restaurant.KEY_zipcode, restaurant.getZipCode());
        values.put(Restaurant.KEY_latitude, restaurant.getLatitude());
        values.put(Restaurant.KEY_longitude, restaurant.getLongitude());
        values.put(Restaurant.KEY_rate, restaurant.getRate());
        values.put(Restaurant.KEY_comment, restaurant.getComment());
        values.put(Restaurant.KEY_imgs, restaurant.getImages());
        values.put(Restaurant.KEY_state, restaurant.getState());
        values.put(Restaurant.KEY_country, restaurant.getCountry());

        // Inserting Row
        long restaurant_Id = db.insert(restaurant.TABLE, null, values);

        return restaurant_Id;
    }

    public boolean isEmpty(){
        boolean res = false;
        String selectQuery = "SELECT  * FROM " + Restaurant.TABLE;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
            res = false;
        else
            res = true;
        cursor.close();

        return res;
    }

    public int getRestaurantCount() {
        String countQuery = "SELECT  * FROM " + Restaurant.TABLE;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public List<Restaurant> getSpecificRestaurants(String queryWhere) {
        List<Restaurant> restaurantList = new ArrayList<Restaurant>();

        String selectQuery = "SELECT  * FROM " + Restaurant.TABLE;

        //If quereWhere is empty, it means query all data from Database
        if (!queryWhere.equals("")) {
            selectQuery += " WHERE ( " + queryWhere +" )";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(Integer.parseInt(cursor.getString(0)));
                restaurant.setName(cursor.getString(1));
                restaurant.setAddress(cursor.getString(2));
                restaurant.setCity(cursor.getString(3));
                restaurant.setPostCode(cursor.getString(4));
                restaurant.setZipCode(cursor.getString(5));
                restaurant.setLatitude(cursor.getString(6));
                restaurant.setLongitude(cursor.getString(7));
                restaurant.setRate(cursor.getString(8));
                restaurant.setComment(cursor.getString(9));
                restaurant.setImages(cursor.getString(10));
                restaurant.setState(cursor.getString(11));
                restaurant.setCountry(cursor.getString(12));
                // Adding contact to list
                restaurantList.add(restaurant);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return restaurantList;
    }

    public boolean updateRestaurantComment(Restaurant res) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(res.KEY_rate, res.getRate());
        args.put(Restaurant.KEY_comment, res.getComment());
        args.put(Restaurant.KEY_imgs, res.getImages());

        return db.update(Restaurant.TABLE, args, "id=?", new String[] {String.valueOf(res.getId())}) > 0;
    }
}
