package myseneca.ca.restaurantshunter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by OceanHsiao on 16-04-03.
 */
public class Restaurant implements Serializable{
    public static final String TABLE = "Restaurants";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_name = "Name";
    public static final String KEY_city = "City";
    public static final String KEY_address = "Address";
    public static final String KEY_postcode = "PostCode";
    public static final String KEY_zipcode = "ZipCode";
    public static final String KEY_latitude = "Latitude";
    public static final String KEY_longitude = "Longitude";
    public static final String KEY_rate = "Rate";
    public static final String KEY_comment = "Comment";
    public static final String KEY_imgs = "ImageNames";
    public static final String KEY_state = "State";
    public static final String KEY_country = "Country";

    private int _id;
    private String _name;
    private String _address;
    private String _city;
    private String _postCode;
    private String _zipCode;
    private float _latitude;
    private float _longitude;
    private int _rate;
    private String _comment;
    private String _images;
    private String _state;
    private String _country;

    public static final String SPLIT_REX = ","; //For splitting images string

    public Restaurant() {
        this._id = 0;
        this._name = "";
        this._address = "";
        this._city = "";
        this._postCode = "";
        this._zipCode = "";
        this._latitude = 0f;
        this._longitude = 0f;
        this._rate = 0;
        this._comment = "";
        this._images = "";
        this._state = "";
        this._country = "";
    }

    public Restaurant(int id, String name, String addr, String city, String postCode, String zipCode,
                      float lat, float lon, int rate, String comm, String imgs, String state, String country) {
        this._id = id;
        setName(name);
        setAddress(addr);
        setCity(city);
        setPostCode(postCode);
        setZipCode(zipCode);
        setLatitude(lat);
        setLongitude(lon);
        setRate(rate);
        setComment(comm);
        setImages(imgs);
        setState(state);
        setCountry(country);
    }

    public int getId() {
        return _id;
    }
    public void setId(int id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }
    public void setName(String name) {
        try {
            if (name.isEmpty())
                throw new InputFormatException("Name of Restaurant is empty.");
            else
                this._name = name;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public String getAddress() {
        return _address;
    }
    public void setAddress(String addr) {
        try {
            if (addr.isEmpty())
                throw new InputFormatException("Address of Restaurant is empty.");
            else
                this._address = addr;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public String getCity() {
        return _city;
    }
    public void setCity(String city) {
        try {
            if (city.isEmpty())
                throw new InputFormatException("City of Restaurant is empty.");
            else
                this._city = city;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public String getZipCode() {
        return _zipCode;
    }
    public void setZipCode(String zipCode) {
        try {
            if (zipCode.isEmpty())
                throw new InputFormatException("Zip code of Restaurant is empty.");
            else
                this._zipCode = zipCode;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public String getPostCode() {
        return _postCode;
    }
    public void setPostCode(String postCode) {
        try {
            if (postCode.isEmpty())
                throw new InputFormatException("Post code of Restaurant is empty.");
            else
                this._postCode = postCode;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public String getFullAddress() {
        String fullAddr = _address;
        if (_zipCode.equals(""))
            fullAddr += ", " + _city + ", " + _postCode;

        return fullAddr;
    }

    public float getLatitude() {
        return _latitude;
    }
    public void setLatitude(float latitude) {
        try {
            if (latitude < -90.f || latitude > 90.f)
                throw new InputFormatException("The range of Latitude is invalid.");
            else
                this._latitude = latitude;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }
    public void setLatitude(String latitude) {
        try {
            if (latitude.isEmpty())
                throw new InputFormatException("Latitude is empty.");
            else
                this._latitude = Float.parseFloat(latitude);
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public float getLongitude() {
        return _longitude;
    }
    public void setLongitude(float longitude)
    {
        try {
            if (longitude < -180.f || longitude > 180.f)
                throw new InputFormatException("The range of Longtitude is invalid.");
            else
                this._longitude = longitude;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }
    public void setLongitude(String longitude) {
        try {
            if (longitude.isEmpty())
                throw new InputFormatException("Longitude is empty.");
            else
                this._longitude = Float.parseFloat(longitude);
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public void setLocation( String latitude, String longitude ) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public int getRate() {
        return _rate;
    }
    public void setRate(String strRate) {
        int rate = Integer.parseInt(strRate);
        setRate(rate);
    }
    public void setRate(int rate) {
        try {
            if (rate < 0 || rate > 5)
                throw new InputFormatException("The range of rate is invalid.");
            else
                _rate = rate;
        }catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    public String getComment() {
        return _comment;
    }
    public void setComment(String comment) {
        _comment = comment;
    }

    public void setImages(String imgNames) {
        _images = imgNames;
    }

    public void setImages(String[] imgNames) {
        _images = "";
        addImages(imgNames);
    }

    public void addImages(String[] imgNames) {
        if (imgNames.length > 0) {
            for (String img : imgNames) {
                _images += (img + SPLIT_REX);
            }
        }
    }

    public static ArrayList<String> transToImageStringArray(String photos) {

        ArrayList<String> photoStringArray;
        String[] photoStrings = photos.split(SPLIT_REX);
        photoStringArray = new ArrayList<String>(Arrays.asList(photoStrings));

        return photoStringArray;
    }

    public static String convertImageArrayToString(ArrayList<String> photoStringArray) {
        String res = "";

        for (String s : photoStringArray) {
            if (!s.equals(""))
                res += (s + SPLIT_REX);
        }
        return res;
    }

    public String getImages() {
        return _images;
    }

    public String[] getImageNames() {
        return _images.split(SPLIT_REX);
    }

    public String getState() {
        return _state;
    }
    public void setState(String state) {
        _state = state;
    }

    public String getCountry() {
        return _country;
    }
    public void setCountry(String country) {
        _country = country;
    }

    public Boolean isCommented() {
        if (getRate() == 0 && getComment().isEmpty() && getImages().isEmpty())
            return false;
        else
            return true;
    }

    //Custom exception for validating input data
    class InputFormatException extends Exception
    {
        //Parameterless Constructor
        public InputFormatException() {}

        //Constructor that accepts a message
        public InputFormatException(String message)
        {
            super(message);
        }
    }
}
