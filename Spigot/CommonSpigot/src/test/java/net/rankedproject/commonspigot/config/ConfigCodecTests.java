package net.rankedproject.commonspigot.config;

import com.google.inject.Injector;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.common.config.reader.ConfigReadOption;
import net.rankedproject.common.config.reader.ConfigReader;
import net.rankedproject.commonspigot.config.type.ConfigBukkitLoaderBukkitParserTest;
import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigCodecTests {

    @Test
    @DisplayName("Test location codec correct convert location object from string line")
    public void givenLocationCodecAndPath_whenGetLocationFromConfig_thenConvertingIsCorrect() {
        //given
        Injector injector = mock(Injector.class);
        ConfigReader configReader = mock(ConfigReader.class);

        when(injector.getInstance(ConfigReader.class)).thenReturn(configReader);
        when(configReader.get(eq(Location.class), any(ConfigReadOption.class))).thenReturn(new Location(null, 0.0, 0.0, 0.0));

        String path = "locations.location";
        //when
        Location location = ConfigProvider.get(ConfigBukkitLoaderBukkitParserTest.class, injector)
                .path(path)
                .get(Location.class);
        //then
        Assertions.assertEquals(new Location(null, 0.0, 0.0, 0.0), location);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    @DisplayName("Test location codec correct convert location list from string list")
    public void givenLocationCodecAndPath_whenGetLocationListFromConfig_thenConvertingIsCorrect() {
        //given
        Injector injector = mock(Injector.class);
        ConfigReader configReader = mock(ConfigReader.class);

        when(injector.getInstance(ConfigReader.class)).thenReturn(configReader);

        List<Location> fakeLocations = List.of(
                new Location(null, 0.0, 0.0, 0.0),
                new Location(null, 1.0, 1.0, 1.0)
        );
        when(configReader.getAsList(eq(Location.class), any(ConfigReadOption.class))).thenReturn((List) fakeLocations);
        String path = "locations";
        //when
        List<? extends Location> locationList = ConfigProvider.get(ConfigBukkitLoaderBukkitParserTest.class, injector)
                .path(path)
                .getAsList(Location.class);
        //then
        Assertions.assertAll("first_location",
                () -> Assertions.assertEquals(0.0, locationList.getFirst().getX()),
                () -> Assertions.assertEquals(0.0, locationList.getFirst().getY()),
                () -> Assertions.assertEquals(0.0, locationList.getFirst().getZ())
        );

        Assertions.assertAll("second_location",
                () -> Assertions.assertEquals(1.0, locationList.get(1).getX()),
                () -> Assertions.assertEquals(1.0, locationList.get(1).getY()),
                () -> Assertions.assertEquals(1.0, locationList.get(1).getZ())
        );
    }
}