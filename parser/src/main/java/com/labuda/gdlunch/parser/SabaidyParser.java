package com.labuda.gdlunch.parser;

import com.labuda.gdlunch.entity.DailyMenu;
import com.labuda.gdlunch.entity.MenuItem;
import com.labuda.gdlunch.entity.Restaurant;
import com.labuda.gdlunch.tools.DateUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses weekly menu from Amphone hotel
 */
public class SabaidyParser extends AbstractRestaurantWebParser implements WeeklyParser {

    /**
     * Logger
     */
    private final static Logger log = LoggerFactory.getLogger(SabaidyParser.class);

    /**
     * Constructor
     */
    public SabaidyParser(Restaurant restaurant) {
        super(restaurant);
    }

    @Override
    public List<DailyMenu> parse() {
        List<DailyMenu> result = new ArrayList<>();

        try {
            Document document = Jsoup.connect(restaurant.getParserUrl()).get();
            Elements elements = document.select(".uk-grid li");
            Elements soups = elements.select("p em");
            Elements mainCourses = elements.select("p ~ ol");

            LocalDate mondayOfCurrentWeek = DateUtils.getMondayOfCurrentWeek();

            for (int i = 0; i < mainCourses.size(); i++) {
                DailyMenu dailyMenu = new DailyMenu();
                dailyMenu.setDate(mondayOfCurrentWeek.plusDays(i));
                dailyMenu.setRestaurant(restaurant);
                dailyMenu.getMenu().add(new MenuItem(soups.get(i).text(), 0.0f));

                Elements courses = mainCourses.get(i).select("li");
                for (Element course : courses) {
                    String item = course.text();
                    dailyMenu.getMenu().add(new MenuItem(item.substring(0, item.length() - 5),
                            parsePrice(course.text())));
                }

                result.add(dailyMenu);
            }
        } catch (Exception e) {
            log.error("Parsing failed", e);
        }

        return result;
    }
}
