package net.dries007.tfc;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
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
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld().getDimension().getType() == DimensionType.OVERWORLD)
        {
            world = (ServerWorld) event.getWorld();
        }
    }

    @Test
    void testGotServer()
    {
        Assertions.assertNotNull(server);
        Assertions.assertTrue(server instanceof DedicatedTestServer);
    }

    @Test
    void testGotWorld()
    {
        Assertions.assertNotNull(world);
    }
}
