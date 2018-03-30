package net.dries007.tfc;

import net.dries007.tfc.objects.CreativeTab;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static net.dries007.tfc.Constants.*;

@Mod(modid = MOD_ID, name = MOD_NAME, useMetadata = true, guiFactory = GUI_FACTORY)
@Mod.EventBusSubscriber()
public class TerraFirmaCraft
{
    @Mod.Instance(MOD_ID)
    public static TerraFirmaCraft INSTANCE;

    private Logger log;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();

        log.info(Arrays.toString(ConfigManager.getModConfigClasses(MOD_ID)));

        // No need to sync config here, forge magic;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        CreativeTab.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    public static Logger log()
    {
        return INSTANCE.log;
    }
}
