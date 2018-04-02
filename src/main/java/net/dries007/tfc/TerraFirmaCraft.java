package net.dries007.tfc;

import net.dries007.tfc.objects.CreativeTab;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event)
    {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        // No need to sync config here, forge magic
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        CreativeTab.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        worldTypeTFC = new WorldTypeTFC();
//        for (BlockTFCVariant b : BlocksTFC.getAllBlockTFCVariants()) b.getVariant(BlockTFCVariant.Type.RAW);
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
}
