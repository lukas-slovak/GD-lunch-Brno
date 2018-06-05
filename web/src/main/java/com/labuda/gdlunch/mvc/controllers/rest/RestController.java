package com.labuda.gdlunch.mvc.controllers.rest;

import com.labuda.gdlunch.dto.DailyMenuDTO;
import com.labuda.gdlunch.entity.Restaurant;
import com.labuda.gdlunch.facade.DailyMenuFacade;
import com.labuda.gdlunch.facade.RestaurantFacade;
import com.labuda.gdlunch.mvc.controllers.rest.entities.RestaurantRequest;
import com.labuda.gdlunch.parser.RestaurantsConfig;
import com.labuda.gdlunch.parser.entity.ParserDefinition;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final static Logger log = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private DailyMenuFacade dailyMenuFacade;

    @Autowired
    private RestaurantFacade restaurantFacade;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<String> listRestaurants() {
        return getRestaurantsFromConfig().stream().map(Restaurant::getName).sorted().collect(Collectors.toList());
    }

    @RequestMapping(value = "/lucky", method = RequestMethod.GET, produces = "application/json")
    public DailyMenuDTO lucky() {
        List<DailyMenuDTO> allMenus = dailyMenuFacade.getAllMenusForDate(LocalDate.now());
        return allMenus.get(new Random().nextInt(allMenus.size()));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json")
    public DailyMenuDTO getMenu(@RequestBody RestaurantRequest restaurantRequest) {
        List<Restaurant> restaurantsFromConfig = new ArrayList<>();


        for (ParserDefinition parserDefinition : RestaurantsConfig.obtain().getConfig()) {
            parserDefinition.getRestaurants().forEach(
                    restaurant -> restaurantsFromConfig.add(restaurant)
            );
        }

        List<Restaurant> filteredList = restaurantsFromConfig
                .stream()
                .filter(res -> res.getName().toLowerCase().contains(restaurantRequest.getRestaurantName()))
                .collect(Collectors.toList());


        return dailyMenuFacade.findDailyMenuByRestaurantNameAndDate(filteredList.get(0).getName(), LocalDate.now());
    }

    private List<Restaurant> getRestaurantsFromConfig() {
        List<Restaurant> restaurantsFromConfig = new ArrayList<>();

        for (ParserDefinition parserDefinition : RestaurantsConfig.obtain().getConfig()) {
            parserDefinition.getRestaurants().forEach(
                    restaurant -> restaurantsFromConfig.add(restaurant)
            );
        }

        return restaurantsFromConfig;
    }

}
