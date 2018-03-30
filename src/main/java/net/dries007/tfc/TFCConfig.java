package net.dries007.tfc;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.dries007.tfc.Constants.MOD_ID;

@Config(modid = MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID)
@Config.LangKey("config." + MOD_ID + ".title")
public class TFCConfig
{
    @Config.Comment("General settings")
    @Config.LangKey("config." + MOD_ID + ".general.title")
    public static final General GENERAL = new General();

    public static class General
    {
        @Config.Comment("Enable debug")
        @Config.LangKey("config." + MOD_ID + ".general.debug")
        public boolean debug = Launch.blackboard.get("fml.deobfuscatedEnvironment") != null;
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID))
        {
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);

            TerraFirmaCraft.log().info("Config changed");
        }
    }
}
