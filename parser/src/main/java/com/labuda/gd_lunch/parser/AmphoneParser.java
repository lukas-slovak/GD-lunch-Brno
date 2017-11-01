package com.labuda.gd_lunch.parser;

import com.labuda.gd_lunch.DailyMenu;
import com.labuda.gd_lunch.MenuItem;
import com.labuda.gd_lunch.WeeklyMenu;
import com.labuda.gd_lunch.tools.WebAddressesConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Parses weekly menu from Amphone hotel
 */
public class AmphoneParser extends AbstractRestaurantWebParser implements WeeklyParser {

    /**
     * Logger
     */
    private final static Logger log = LoggerFactory.getLogger(AmphoneParser.class);

    /**
     * Constructor
     */
    public AmphoneParser() {
        super(WebAddressesConfig.getInstance().getString("amphone"));
    }

    @Override
    public WeeklyMenu parse() {
        WeeklyMenu result = new WeeklyMenu();

        try {
            Document document = Jsoup.connect(webAddress).get();
            Elements elements = document.select(".uk-grid li");
            Elements soups = elements.select("p em");
            Elements mainCourses = elements.select("p ~ ol");

            for (int i = 0; i < mainCourses.size(); i++) {
                DailyMenu dailyMenu = new DailyMenu();
                dailyMenu.setDate(LocalDate.now());
                dailyMenu.getMenu().add(new MenuItem(soups.get(i).text(), 0.0f));

                Elements courses = mainCourses.get(i).select("li");
                for (Element course : courses) {
                    String item = course.text();
                    dailyMenu.getMenu().add(new MenuItem(item.substring(0, item.length() - 5), parsePrice(course.text())));
                }

                result.addMenuDay(dailyMenu);
            }
        } catch (IOException e) {
            log.error("Parsing failed", e);
        }

        return result;
    }

    /**
     * Helper method to parse price from item
     * @param item item on the menu
     * @return price as float
     */
    private float parsePrice(String item) {
        item = item.trim();
        String cropped = item.substring(item.length() - 5, item.length() - 2);
        return Float.parseFloat(cropped.trim());
    }
}
