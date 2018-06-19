/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import net.dries007.tfc.client.ClientEvents;
import net.dries007.tfc.cmd.StripWorldCommand;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.entity.EntitiesTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.OreSpawnData;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkCapabilityHandler;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataMessage;
import net.dries007.tfc.world.classic.worldgen.*;

import static net.dries007.tfc.Constants.*;

@SuppressWarnings("DefaultAnnotationParam")
@Mod(modid = MOD_ID, name = MOD_NAME, useMetadata = true, guiFactory = GUI_FACTORY, canBeDeactivated = false, certificateFingerprint = SIGNING_KEY)
@Mod.EventBusSubscriber()
public class TerraFirmaCraft
{
    @Mod.Instance()
    private static TerraFirmaCraft instance = null;

    @Mod.Metadata()
    private static ModMetadata metadata = null;

    static
    {
        FluidRegistry.enableUniversalBucket();
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

    private boolean isSignedBuild = true;
    private Logger log;
    private WorldTypeTFC worldTypeTFC;
    private SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        log.debug("If you can see this, debug logging is working :)");
        if (!isSignedBuild)
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");

        // No need to sync config here, forge magic

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        int id = 0;
        network.registerMessage(ChunkDataMessage.Handler.class, ChunkDataMessage.class, ++id, Side.CLIENT);
        ChunkCapabilityHandler.preInit();

        CalenderTFC.reload();

        EntitiesTFC.preInit();
        FluidsTFC.preInit();

        OreSpawnData.preInit(event.getModConfigurationDirectory());

        if (event.getSide().isClient()) ClientEvents.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (!isSignedBuild)
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");

        OreDictionaryHelper.init();
        CreativeTabsTFC.init();
        ItemsTFC.init();

        worldTypeTFC = new WorldTypeTFC();
//        TreeRegistry.loadTrees();

        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.lavaFissureRarity, new WorldGenFissure(true, 20)), 0);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.waterFissureRarity, new WorldGenFissure(false, -1)), 0);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.lavaFissureClusterRarity, new WorldGenSurfaceFissureCluster(true)), 1);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.waterFissureClusterRarity, new WorldGenSurfaceFissureCluster(false)), 1);
        GameRegistry.registerWorldGenerator(new WorldGenOre(), 2);
        //todo: add cave decorator
        //todo: add forests
        //todo: add loose rocks
        GameRegistry.registerWorldGenerator(new WorldGenLooseRocks(), 5);
        GameRegistry.registerWorldGenerator(new WorldGenSoilPits(), 6);
        GameRegistry.registerWorldGenerator(new RarityBasedWorldGen(x -> x.largeRockRarity, new WorldGenLargeRocks()), 7);
        //todo: add plants
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!isSignedBuild)
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");

        OreSpawnData.reloadOreGen();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        if (!isSignedBuild)
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");
        event.registerServerCommand(new StripWorldCommand());
    }

    @Mod.EventHandler
    public void onIMC(FMLInterModComms.IMCEvent event)
    {
        //todo: provide nice API here.
    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        isSignedBuild = false;
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable()
        {
            @Override
            public String getLabel()
            {
                return Constants.MOD_NAME;
            }

            @Override
            public String call()
            {
                return "You are not running an official build. Please do not use this and then report bugs or issues.";
            }
        });
    }
}
