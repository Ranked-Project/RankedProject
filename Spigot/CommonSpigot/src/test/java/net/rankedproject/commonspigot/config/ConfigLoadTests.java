package net.rankedproject.commonspigot.config;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.inject.Injector;
import net.rankedproject.common.config.ConfigProvider;
import net.rankedproject.common.config.accessor.ConfigAccessor;
import net.rankedproject.commonspigot.config.type.ConfigBukkitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

public class ConfigLoadTests {

    @Test
    @DisplayName("Test to load config correct functionality")
    public void givenConfigBukkitTest_whenLoadConfig_thenConfigLoadedCorrect() {
        // given
        Injector injector = mock(Injector.class);
        ConfigBukkitTest configMock = mock(ConfigBukkitTest.class);
        ConfigAccessor accessorMock = mock(ConfigAccessor.class);

        when(injector.getInstance(ConfigBukkitTest.class)).thenReturn(configMock);
        when(injector.getInstance(ConfigAccessor.class)).thenReturn(accessorMock);
        when(accessorMock.loadAsync(configMock)).thenReturn(CompletableFuture.completedFuture(null));

        // when
        CompletableFuture<?> future = ConfigProvider.load(ConfigBukkitTest.class, injector);

        // then
        assertTrue(future.isDone());
    }
}