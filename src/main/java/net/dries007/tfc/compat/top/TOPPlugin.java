package net.dries007.tfc.compat.top;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import net.dries007.tfc.TerraFirmaCraft;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;


@Mod(modid = MOD_ID + "_top_plugin", name = TerraFirmaCraft.MOD_NAME + "_TheOneProbePlugin")
@Mod.EventBusSubscriber
public class TOPPlugin
{
    private static final @Nonnull
    String MODID_TOP = "theoneprobe";

    @Optional.Method(modid = MODID_TOP)
    public static void outputHorizontalText(IProbeInfo iProbeInfo, String text)
    {
        iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
            .text(text);
    }

    @Mod.EventHandler
    public void fmlPreInitEvent(final FMLPreInitializationEvent event)
    {
        if (Loader.isModLoaded(MODID_TOP))
        {
            RegisterProviders(event);
        }


    }

    private void RegisterProviders(FMLPreInitializationEvent event)
    {

        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.BarrelProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.BerryBushProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.CropProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.BlastFurnaceProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.BloomeryProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.CrucibleProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.FruitTreeProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.OreProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.PitKilnProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.PlacedItemProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.RockProvider");
        FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "net.dries007.tfc.compat.top.AnimalProvider");


    }


}
