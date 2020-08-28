/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

import net.dries007.tfc.api.capability.damage.CapabilityDamageResistance;
import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.metal.CapabilityMetalItem;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.worldtracker.CapabilityWorldTracker;
import net.dries007.tfc.client.ClientEvents;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.TFCKeybindings;
import net.dries007.tfc.client.gui.overlay.PlayerDataOverlay;
import net.dries007.tfc.command.*;
import net.dries007.tfc.compat.patchouli.TFCPatchouliPlugin;
import net.dries007.tfc.network.*;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.entity.EntitiesTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.proxy.IProxy;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.fuel.FuelManager;
import net.dries007.tfc.util.json.JsonConfigRegistry;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.CapabilityChunkData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("FieldMayBeFinal")
@Mod.EventBusSubscriber
@Mod(modid = MOD_ID, name = TerraFirmaCraft.MOD_NAME, useMetadata = true, guiFactory = Constants.GUI_FACTORY, dependencies = "required:forge@[14.23.5.2816,);after:jei@[4.14.2,);after:crafttweaker@[4.1.11,);after:waila@(1.8.25,)")
public final class TerraFirmaCraft
{
    public static final String MOD_ID = "tfc";
    public static final String MOD_NAME = "TerraFirmaCraft";

    @Mod.Instance
    private static TerraFirmaCraft INSTANCE = null;

    @SidedProxy(modId = MOD_ID, clientSide = "net.dries007.tfc.proxy.ClientProxy", serverSide = "net.dries007.tfc.proxy.ServerProxy")
    private static IProxy PROXY = null;

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    public static Logger getLog()
    {
        return INSTANCE.log;
    }

    public static IProxy getProxy()
    {
        return PROXY;
    }

    public static WorldTypeTFC getWorldType()
    {
        return INSTANCE.worldTypeTFC;
    }

    public static SimpleNetworkWrapper getNetwork()
    {
        return INSTANCE.network;
    }

    public static TerraFirmaCraft getInstance()
    {
        return INSTANCE;
    }

    private final Logger log = LogManager.getLogger(MOD_ID);
    private final boolean isSignedBuild = true;
    private WorldTypeTFC worldTypeTFC;
    private SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log.debug("If you can see this, debug logging is working :)");
        if (!isSignedBuild)
        {
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");
        }

        // No need to sync config here, forge magic

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new TFCGuiHandler());
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        int id = 0;
        // Received on server
        network.registerMessage(new PacketGuiButton.Handler(), PacketGuiButton.class, ++id, Side.SERVER);
        network.registerMessage(new PacketPlaceBlockSpecial.Handler(), PacketPlaceBlockSpecial.class, ++id, Side.SERVER);
        network.registerMessage(new PacketSwitchPlayerInventoryTab.Handler(), PacketSwitchPlayerInventoryTab.class, ++id, Side.SERVER);
        network.registerMessage(new PacketOpenCraftingGui.Handler(), PacketOpenCraftingGui.class, ++id, Side.SERVER);
        network.registerMessage(new PacketCycleItemMode.Handler(), PacketCycleItemMode.class, ++id, Side.SERVER);
        network.registerMessage(new PacketStackFood.Handler(), PacketStackFood.class, ++id, Side.SERVER);

        // Received on client
        network.registerMessage(new PacketChunkData.Handler(), PacketChunkData.class, ++id, Side.CLIENT);
        network.registerMessage(new PacketCapabilityContainerUpdate.Handler(), PacketCapabilityContainerUpdate.class, ++id, Side.CLIENT);
        network.registerMessage(new PacketCalendarUpdate.Handler(), PacketCalendarUpdate.class, ++id, Side.CLIENT);
        network.registerMessage(new PacketFoodStatsUpdate.Handler(), PacketFoodStatsUpdate.class, ++id, Side.CLIENT);
        network.registerMessage(new PacketFoodStatsReplace.Handler(), PacketFoodStatsReplace.class, ++id, Side.CLIENT);
        network.registerMessage(new PacketPlayerDataUpdate.Handler(), PacketPlayerDataUpdate.class, ++id, Side.CLIENT);
        network.registerMessage(new PacketSpawnTFCParticle.Handler(), PacketSpawnTFCParticle.class, ++id, Side.CLIENT);

        EntitiesTFC.preInit();
        JsonConfigRegistry.INSTANCE.preInit(event.getModConfigurationDirectory());

        CapabilityChunkData.preInit();
        CapabilityItemSize.preInit();
        CapabilityItemHeat.preInit();
        CapabilityForgeable.preInit();
        CapabilityFood.preInit();
        CapabilityEgg.preInit();
        CapabilityPlayerData.preInit();
        CapabilityDamageResistance.preInit();
        CapabilityMetalItem.preInit();
        CapabilityWorldTracker.preInit();

        if (event.getSide().isClient())
        {
            ClientEvents.preInit();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (!isSignedBuild)
        {
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");
        }

        ItemsTFC.init();
        LootTablesTFC.init();
        CapabilityFood.init();
        TFCTriggers.init();

        if (event.getSide().isClient())
        {
            TFCKeybindings.init();
            // Enable overlay to render health, thirst and hunger bars, TFC style.
            // Also renders animal familiarity
            MinecraftForge.EVENT_BUS.register(PlayerDataOverlay.getInstance());
        }
        else
        {
            MinecraftServer server = FMLServerHandler.instance().getServer();
            if (server instanceof DedicatedServer)
            {
                PropertyManager settings = ((DedicatedServer) server).settings;
                if (ConfigTFC.General.OVERRIDES.forceTFCWorldType)
                {
                    // This is called before vanilla defaults it, meaning we intercept it's default with ours
                    // However, we can't actually set this due to fears of overriding the existing world
                    TerraFirmaCraft.getLog().info("Setting default level-type to `tfc_classic`");
                    settings.getStringProperty("level-type", "tfc_classic");
                }
            }
        }

        worldTypeTFC = new WorldTypeTFC();

        CapabilityItemSize.init();
        CapabilityItemHeat.init();
        CapabilityMetalItem.init();

        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "net.dries007.tfc.compat.waila.TOPPlugin");
        if (Loader.isModLoaded("patchouli"))
        {
            TFCPatchouliPlugin.init();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!isSignedBuild)
        {
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");
        }
        FuelManager.postInit();
        JsonConfigRegistry.INSTANCE.postInit();
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event)
    {
        // This is the latest point that we can possibly stop creating non-decaying stacks on both server + client
        // It should be safe to use as we're only using it internally
        FoodHandler.setNonDecaying(false);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        if (!isSignedBuild)
        {
            log.warn("You are not running an official build. Please do not use this and then report bugs or issues.");
        }

        event.registerServerCommand(new CommandStripWorld());
        event.registerServerCommand(new CommandHeat());
        event.registerServerCommand(new CommandPlayerTFC());
        event.registerServerCommand(new CommandTimeTFC());
        event.registerServerCommand(new CommandFindVeins());
        event.registerServerCommand(new CommandDebugInfo());

        // Initialize calendar for the current server
        CalendarTFC.INSTANCE.init(event.getServer());
    }
}
