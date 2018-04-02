package net.dries007.tfc;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.dries007.tfc.Constants.MOD_ID;

/**
 * Top level items must be static, the subclasses' fields must not be static.
 */
@Config(modid = MOD_ID, category = "")
@Mod.EventBusSubscriber(modid = MOD_ID)
@Config.LangKey("config." + MOD_ID)
public class ConfigTFC
{
    @Config.Comment("General settings")
    @Config.LangKey("config." + MOD_ID + ".general")
    public static final GeneralCFG GENERAL = new GeneralCFG();

    @Config.Comment("Client side settings")
    @Config.LangKey("config." + MOD_ID + ".client")
    public static final ClientCFG CLIENT = new ClientCFG();

    public static class GeneralCFG
    {
        @Config.Comment("Various debug options. Activates some extra wand features.")
        @Config.LangKey("config." + MOD_ID + ".general.debug")
        public boolean debug = Launch.blackboard.get("fml.deobfuscatedEnvironment") != null;

        @Config.Comment("Debug worldgen [DANGER] Your world will be affected! Do not use on your proper world files!")
        @Config.LangKey("config." + MOD_ID + ".general.debugWorldGen")
        @Config.RequiresWorldRestart
        public boolean debugWorldGen = false;
    }

    public static class ClientCFG
    {
        @Config.Comment({"Only works client side!", "Servers require the world type to be set to 'tfc_classic'"})
        @Config.LangKey("config." + MOD_ID + ".client.makeWorldTypeClassicDefault")
        @Config.RequiresMcRestart
        public boolean makeWorldTypeClassicDefault = true;
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID))
        {
            TerraFirmaCraft.getLog().warn("Config changed");
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
        }
    }
}
