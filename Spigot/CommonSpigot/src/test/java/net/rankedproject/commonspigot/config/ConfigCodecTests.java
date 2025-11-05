package net.rankedproject.commonspigot.config;

import com.google.inject.Injector;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.common.config.reader.ConfigReadOption;
import net.rankedproject.common.config.reader.ConfigReader;
import net.rankedproject.commonspigot.config.type.ConfigBukkitTest;
import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigCodecTests {

    @Test
    @DisplayName("Test location codec correct convert from string line")
    public void givenLocationCodec_whenGetLocationFromConfig_thenConvertingIsCorrect() {
        //given
        Injector injector = mock(Injector.class);
        ConfigReader configReader = mock(ConfigReader.class);

        when(injector.getInstance(ConfigReader.class)).thenReturn(configReader);
        when(configReader.get(eq(Location.class), any(ConfigReadOption.class))).thenReturn(new Location(null, 15.0, 20.0, 15.0));

        String path = "locations.location";
        //when
        Location location = ConfigProvider.get(ConfigBukkitTest.class, injector)
                .path(path)
                .get(Location.class);
        //then
        Assertions.assertEquals(new Location(null, 15.0, 20.0, 15.0), location);
    }
}