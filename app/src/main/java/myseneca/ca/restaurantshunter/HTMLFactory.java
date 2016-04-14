package myseneca.ca.restaurantshunter;

import java.util.List;

/**
 * Created by OceanHsiao on 16-04-12.
 */
public class HTMLFactory {

    private List<Restaurant> mRestaurants;

    public HTMLFactory(List<Restaurant> restaurants) {
        this.mRestaurants = restaurants;
    }

    public String generateHTMLofRestaurantComments() {
        StringBuffer titleBuffer=new StringBuffer();

        titleBuffer.append("<html><head></head><body>");

        for (Restaurant r : mRestaurants){

            titleBuffer.append("<h1><p><b>Restaurant</b>:&nbsp;&nbsp;<u><i>" + r.getName() + "</i></u></p></h1>"+
                                "<p><b>Address:&nbsp;&nbsp;</b><u><i><small><font color='blue'>" +
                                        r.getAddress() + ", " + r.getCity() + ", " + r.getPostCode() + ", " + r.getCountry() +
                                "</font></small></i></u></p>"+
                                "<p><b>Rate:&nbsp;&nbsp;</b><u><i><font color='red'>"+ String.valueOf(r.getRate()) + "</font> - Star</i></u></p>"+
                                "<b><u>Comment:&nbsp;&nbsp;</u></b>" +
                                        "<p><i>&nbsp;&nbsp;&nbsp;&nbsp;"+r.getComment()+"</i></p>");

            //Since email couldn't allow <hr> tag, use a lot of &nbsp;(space) with <u> to simulate <hr>,
            titleBuffer.append("<u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u>");
            titleBuffer.append("<u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u>");
            titleBuffer.append("<u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u>");
            titleBuffer.append("<u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u>");
            titleBuffer.append("<u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u>");

            titleBuffer.append("<p></p>");
        }
        titleBuffer.append("<p></p><p>Thank You for Using Android App - <i>'Restaurants Hunter'</i></p>");
        titleBuffer.append("</body></html>");

        return titleBuffer.toString();
    }
}
