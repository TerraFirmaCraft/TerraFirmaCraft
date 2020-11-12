package net.dries007.tfc;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import com.alcatrazescapee.mcjunitlib.framework.IntegrationTestManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class IntegrationTestEntryPoint
{
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event)
    {
        IntegrationTestManager.setup(MOD_ID);
    }
}
