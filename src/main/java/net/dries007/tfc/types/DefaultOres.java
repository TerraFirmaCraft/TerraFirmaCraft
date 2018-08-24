/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.Metal;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultOres
{
    public static final ResourceLocation NATIVE_COPPER = new ResourceLocation(MOD_ID, "native_copper");
    public static final ResourceLocation NATIVE_GOLD = new ResourceLocation(MOD_ID, "native_gold");
    public static final ResourceLocation NATIVE_PLATINUM = new ResourceLocation(MOD_ID, "native_platinum");
    public static final ResourceLocation HEMATITE = new ResourceLocation(MOD_ID, "hematite");
    public static final ResourceLocation NATIVE_SILVER = new ResourceLocation(MOD_ID, "native_silver");
    public static final ResourceLocation CASSITERITE = new ResourceLocation(MOD_ID, "cassiterite");
    public static final ResourceLocation GALENA = new ResourceLocation(MOD_ID, "galena");
    public static final ResourceLocation BISMUTHINITE = new ResourceLocation(MOD_ID, "bismuthinite");
    public static final ResourceLocation GARNIERITE = new ResourceLocation(MOD_ID, "garnierite");
    public static final ResourceLocation MALACHITE = new ResourceLocation(MOD_ID, "malachite");
    public static final ResourceLocation MAGNETITE = new ResourceLocation(MOD_ID, "magnetite");
    public static final ResourceLocation LIMONITE = new ResourceLocation(MOD_ID, "limonite");
    public static final ResourceLocation SPHALERITE = new ResourceLocation(MOD_ID, "sphalerite");
    public static final ResourceLocation TETRAHEDRITE = new ResourceLocation(MOD_ID, "tetrahedrite");
    public static final ResourceLocation BITUMINOUS_COAL = new ResourceLocation(MOD_ID, "bituminous_coal");
    public static final ResourceLocation LIGNITE = new ResourceLocation(MOD_ID, "lignite");
    public static final ResourceLocation KAOLINITE = new ResourceLocation(MOD_ID, "kaolinite");
    public static final ResourceLocation GYPSUM = new ResourceLocation(MOD_ID, "gypsum");
    public static final ResourceLocation SATINSPAR = new ResourceLocation(MOD_ID, "satinspar");
    public static final ResourceLocation SELENITE = new ResourceLocation(MOD_ID, "selenite");
    public static final ResourceLocation GRAPHITE = new ResourceLocation(MOD_ID, "graphite");
    public static final ResourceLocation KIMBERLITE = new ResourceLocation(MOD_ID, "kimberlite");
    public static final ResourceLocation PETRIFIED_WOOD = new ResourceLocation(MOD_ID, "petrified_wood");
    public static final ResourceLocation SULFUR = new ResourceLocation(MOD_ID, "sulfur");
    public static final ResourceLocation JET = new ResourceLocation(MOD_ID, "jet");
    public static final ResourceLocation MICROCLINE = new ResourceLocation(MOD_ID, "microcline");
    public static final ResourceLocation PITCHBLENDE = new ResourceLocation(MOD_ID, "pitchblende");
    public static final ResourceLocation CINNABAR = new ResourceLocation(MOD_ID, "cinnabar");
    public static final ResourceLocation CRYOLITE = new ResourceLocation(MOD_ID, "cryolite");
    public static final ResourceLocation SALTPETER = new ResourceLocation(MOD_ID, "saltpeter");
    public static final ResourceLocation SERPENTINE = new ResourceLocation(MOD_ID, "serpentine");
    public static final ResourceLocation SYLVITE = new ResourceLocation(MOD_ID, "sylvite");
    public static final ResourceLocation BORAX = new ResourceLocation(MOD_ID, "borax");
    public static final ResourceLocation OLIVINE = new ResourceLocation(MOD_ID, "olivine");
    public static final ResourceLocation LAPIS_LAZULI = new ResourceLocation(MOD_ID, "lapis_lazuli");

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistryEvent.RegisterPreBlock<Ore> event)
    {
        IForgeRegistry<Ore> r = event.getRegistry();
        r.registerAll(
            new Ore(NATIVE_COPPER, Metal.COPPER),
            new Ore(NATIVE_GOLD, Metal.GOLD),
            new Ore(NATIVE_PLATINUM, Metal.PLATINUM),
            new Ore(HEMATITE, Metal.PIG_IRON),
            new Ore(NATIVE_SILVER, Metal.SILVER),
            new Ore(CASSITERITE, Metal.TIN),
            new Ore(GALENA, Metal.LEAD),
            new Ore(BISMUTHINITE, Metal.BISMUTH),
            new Ore(GARNIERITE, Metal.NICKEL),
            new Ore(MALACHITE, Metal.COPPER),
            new Ore(MAGNETITE, Metal.PIG_IRON),
            new Ore(LIMONITE, Metal.PIG_IRON),
            new Ore(SPHALERITE, Metal.ZINC),
            new Ore(TETRAHEDRITE, Metal.COPPER),
            new Ore(BITUMINOUS_COAL, null),
            new Ore(LIGNITE, null),
            new Ore(KAOLINITE, null),
            new Ore(GYPSUM, null),
            new Ore(SATINSPAR, null),
            new Ore(SELENITE, null),
            new Ore(GRAPHITE, null),
            new Ore(KIMBERLITE, null),
            new Ore(PETRIFIED_WOOD, null),
            new Ore(SULFUR, null),
            new Ore(JET, null),
            new Ore(MICROCLINE, null),
            new Ore(PITCHBLENDE, null),
            new Ore(CINNABAR, null),
            new Ore(CRYOLITE, null),
            new Ore(SALTPETER, null),
            new Ore(SERPENTINE, null),
            new Ore(SYLVITE, null),
            new Ore(BORAX, null),
            new Ore(OLIVINE, null),
            new Ore(LAPIS_LAZULI, null)
        );
    }
}
