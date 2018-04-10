package net.dries007.tfc;

import net.dries007.tfc.client.ClientEvents;
import net.dries007.tfc.cmd.StripWorldCommand;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.entity.EntitiesTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkCapabilityHandler;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataMessage;
import net.dries007.tfc.world.classic.worldgen.*;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import static net.dries007.tfc.Constants.*;

@SuppressWarnings("DefaultAnnotationParam")
@Mod(modid = MOD_ID, name = MOD_NAME, useMetadata = true, guiFactory = GUI_FACTORY, canBeDeactivated = false)
@Mod.EventBusSubscriber()
public class TerraFirmaCraft
{
    @Mod.Instance()
    private static TerraFirmaCraft instance = null;

    @Mod.Metadata()
    private static ModMetadata metadata = null;

    private Logger log;
    private WorldTypeTFC worldTypeTFC;
    private SimpleNetworkWrapper network;

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();

        // No need to sync config here, forge magic

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        int id = 0;
        network.registerMessage(ChunkDataMessage.Handler.class, ChunkDataMessage.class, ++id, Side.CLIENT);

        ChunkCapabilityHandler.preInit();
        CalenderTFC.reload();

        EntitiesTFC.preInit();
        FluidsTFC.preInit();

        if (event.getSide().isClient()) ClientEvents.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        CreativeTabsTFC.init();

        worldTypeTFC = new WorldTypeTFC();

        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.lavaFissureRarity, new WorldGenFissure(true, 20)), 0);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.waterFissureRarity, new WorldGenFissure(false, -1)), 0);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.lavaFissureClusterRarity, new WorldGenSurfaceFissureCluster(true)), 1);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.waterFissureClusterRarity, new WorldGenSurfaceFissureCluster(false)), 1);
        GameRegistry.registerWorldGenerator(new WorldGenOre(), 2);
        //todo: add cave decorator
        //todo: add forests
        //todo: add loose rocks
        GameRegistry.registerWorldGenerator(new WorldGenSoilPits(), 6);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.largeRockRarity, new WorldGenLargeRocks()), 7);
        //todo: add large rocks
        //todo: add plants
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new StripWorldCommand());
    }

    public static Logger getLog()
    {
        return instance.log;
    }

    public static String getVersion()
    {
        return metadata.version;
    }

    public static WorldTypeTFC getWorldTypeTFC()
    {
        return instance.worldTypeTFC;
    }

    public static SimpleNetworkWrapper getNetwork()
    {
        return instance.network;
    }

    public static TerraFirmaCraft getInstance()
    {
        return instance;
    }
}
