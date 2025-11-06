package net.rankedproject.commonspigot.config;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.inject.Injector;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.common.config.accessor.ConfigAccessor;
import net.rankedproject.commonspigot.config.type.ConfigBukkitLoaderBukkitParserTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

public class ConfigLoadTests {

    @Test
    @DisplayName("Test to load config with bukkit parser and bukkit loader correct functionality")
    public void givenConfigBukkitLoaderBukkitParserTest_whenLoadConfig_thenConfigLoadedCorrect() {
        // given
        Injector injector = mock(Injector.class);
        ConfigBukkitLoaderBukkitParserTest configMock = mock(ConfigBukkitLoaderBukkitParserTest.class);
        ConfigAccessor accessorMock = mock(ConfigAccessor.class);

        when(injector.getInstance(ConfigBukkitLoaderBukkitParserTest.class)).thenReturn(configMock);
        when(injector.getInstance(ConfigAccessor.class)).thenReturn(accessorMock);
        when(accessorMock.loadAsync(configMock)).thenReturn(CompletableFuture.completedFuture(null));

        // when
        CompletableFuture<?> future = ConfigProvider.load(ConfigBukkitLoaderBukkitParserTest.class, injector);

        // then
        assertTrue(future.isDone());
    }
}