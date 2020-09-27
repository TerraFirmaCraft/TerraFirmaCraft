/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import com.alcatrazescapee.mcjunitlib.DedicatedTestServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Helper class for accessing server / world during tests
 */
@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestHelpers
{
    public static MinecraftServer server = null;
    public static ServerWorld world = null;

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event)
    {
        server = event.getServer();
        world = event.getServer().overworld();
    }

    @Test
    void testHelpersInitialized()
    {
        Assertions.assertNotNull(server);
        Assertions.assertNotNull(world);

        Assertions.assertTrue(server instanceof DedicatedTestServer);

    }
}
