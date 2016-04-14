package myseneca.ca.restaurantshunter;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OceanHsiao on 16-04-02.
 */
public class XmlParser {

    public enum Cities {
        LONDON, YONKERS, ROCHESTER, ALBANY, SYRACUSE
    }
    private boolean startRecord;
    private String Name = "restaurant_name";
    private String address = "restaurant_address";
    private String desc = "restaurant_description";
    private String city = "restaurant_city";
    private String state = "restaurant_location_state";
    private String latitude = "restaurant_location_latitude";
    private String longitude = "restaurant_location_longitude";
    private String type = "business_type";
    private String postCode = "restaurant_postcode";
    private String zipCode = "restaurant_zipcode";

    private XmlPullParserFactory xmlFactoryObject;
    private List<Restaurant> restaurants;
    private boolean loadFinish;
    private int num;

    public XmlParser() {
        restaurants = new ArrayList();
        num = 0;
    }

    public boolean parseLondonXML(XmlPullParser myParser){
        int event;
        String text = null;
        loadFinish = false;
        startRecord = false;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if(name.equals("EstablishmentDetail")) {
                            startRecord = true;
                            address = "";
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("BusinessName")) {
                            Name = text;
                        } else if (name.equals("BusinessType")) {
                            type = text;
                        } else if (name.equals("AddressLine1")) {
                            address += text;
                        } else if (name.equals("AddressLine2")) {
                            address += ", " + text;
                        } else if (name.equals("AddressLine3")) {
                            address += ", " + text;
                        } else if (name.equals("PostCode")) {
                            postCode = text;
                        } else if (name.equals("Longitude")) {
                            longitude = text;
                        } else if (name.equals("Latitude")) {
                            latitude = text;
                        }
                        else if (name.equals("EstablishmentDetail")) {
                            if (isRestaurantForLondon(type))
                            {//Insert Data to Database
                                Restaurant rst = new Restaurant(num, Name, address, "London", postCode, zipCode,
                                                                    Float.parseFloat(latitude), Float.parseFloat(longitude),
                                                                    0, "", "", "", "UK");
                                restaurants.add(rst);
                                num++;
                            }
                            startRecord = false;
                        }
                        break;
                }event = myParser.next();
            }
            loadFinish = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return loadFinish;
    }

    public boolean parseNYXML(XmlPullParser myParser, Cities eCity) {
        int event;
        String text = null;
        loadFinish = false;
        startRecord = false;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if(name.equals("facility")) {
                            startRecord = true;
                            address = "";
                        }
                        else if(name.equals("location1")) {
                            latitude = myParser.getAttributeValue(null,"latitude");
                            longitude = myParser.getAttributeValue(null,"longitude");

                            switch (eCity) {
                                case YONKERS: {
                                    if (city.equalsIgnoreCase("YONKERS")) {
                                        addRestaurantToList();
                                    }
                                }break;
                                case ROCHESTER: {
                                    if (city.equalsIgnoreCase("ROCHESTER")) {
                                        addRestaurantToList();
                                    }
                                }break;
                                case ALBANY: {
                                    if (city.equalsIgnoreCase("ALBANY")) {
                                        addRestaurantToList();
                                    }
                                }break;
                                case SYRACUSE: {
                                    if (city.equalsIgnoreCase("SYRACUSE")) {
                                        addRestaurantToList();
                                    }
                                }break;
                            }
                            startRecord = false;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("facility")) {
                            Name = text;
                        }
                        else if(name.equals("address")) {
                            address += text;
                        }
                        else if(name.equals("city")) {
                            city = text;
                            address += ", " + city;
                        }
                        else if(name.equals("zip_code")) {
                            zipCode = text;
                        }
                        else if(name.equals("description")) {
                            desc = text;
                            boolean isR = isRestaurantForNY(desc);
                        }
                        else if(name.equals("food_service_facility_state")){
                            state = text;
                            address += ", " + state + ", " + zipCode;
                        }
                        break;
                }
                event = myParser.next();
            }
            loadFinish = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return loadFinish;
    }

    public boolean parseFromFileStream(InputStream fileStream, Cities city) {
        boolean parseRes = false;
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(true);
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setInput(fileStream, null);

            switch (city)
            {
                case LONDON:
                    parseRes = parseLondonXML(myparser);
                    break;
                case YONKERS:
                    parseRes = parseNYXML(myparser, city);
                    break;
                case ROCHESTER:
                    parseRes = parseNYXML(myparser, city);
                    break;
                case ALBANY:
                    parseRes = parseNYXML(myparser, city);
                    break;
                case SYRACUSE:
                    parseRes = parseNYXML(myparser, city);
                    break;
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return parseRes;
    }

    public boolean isRestaurantForLondon(String desc) {
        boolean res = false;

        String[] splitString = desc.split("/");
        for(int i = 0; i < splitString.length; i++) {
            if (splitString[i].trim().equalsIgnoreCase("restaurant")) {
                res = true;
                break;
            }
        }
        return res;
    }

    public boolean isRestaurantForNY(String desc) {
        boolean res = false;

        String[] splitString = desc.split(" - ");
        if (splitString[1].trim().equalsIgnoreCase("restaurant"))
            res = true;

        return res;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurants;
    }

    private void addRestaurantToList() {
        Restaurant rst = new Restaurant(num, Name, address, city, postCode, zipCode,
                Float.parseFloat(latitude), Float.parseFloat(longitude),
                0, "", "", state, "USA");
        restaurants.add(rst);
        num++;
    }

}
