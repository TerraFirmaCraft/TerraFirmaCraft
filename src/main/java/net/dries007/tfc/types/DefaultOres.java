/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.TFCRegistries;
import net.dries007.tfc.objects.Metal;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultOres
{

    private static final ResourceLocation NATIVE_COPPER = new ResourceLocation(MOD_ID, "native_copper");
    private static final ResourceLocation NATIVE_GOLD = new ResourceLocation(MOD_ID, "native_gold");
    private static final ResourceLocation NATIVE_PLATINUM = new ResourceLocation(MOD_ID, "native_platinum");
    private static final ResourceLocation HEMATITE = new ResourceLocation(MOD_ID, "hematite");
    private static final ResourceLocation NATIVE_SILVER = new ResourceLocation(MOD_ID, "native_silver");
    private static final ResourceLocation CASSITERITE = new ResourceLocation(MOD_ID, "cassiterite");
    private static final ResourceLocation GALENA = new ResourceLocation(MOD_ID, "galena");
    private static final ResourceLocation BISMUTHINITE = new ResourceLocation(MOD_ID, "bismuthinite");
    private static final ResourceLocation GARNIERITE = new ResourceLocation(MOD_ID, "garnierite");
    private static final ResourceLocation MALACHITE = new ResourceLocation(MOD_ID, "malachite");
    private static final ResourceLocation MAGNETITE = new ResourceLocation(MOD_ID, "magnetite");
    private static final ResourceLocation LIMONITE = new ResourceLocation(MOD_ID, "limonite");
    private static final ResourceLocation SPHALERITE = new ResourceLocation(MOD_ID, "sphalerite");
    private static final ResourceLocation TETRAHEDRITE = new ResourceLocation(MOD_ID, "tetrahedrite");
    private static final ResourceLocation BITUMINOUS_COAL = new ResourceLocation(MOD_ID, "bituminous_coal");
    private static final ResourceLocation LIGNITE = new ResourceLocation(MOD_ID, "lignite");
    private static final ResourceLocation KAOLINITE = new ResourceLocation(MOD_ID, "kaolinite");
    private static final ResourceLocation GYPSUM = new ResourceLocation(MOD_ID, "gypsum");
    private static final ResourceLocation SATINSPAR = new ResourceLocation(MOD_ID, "satinspar");
    private static final ResourceLocation SELENITE = new ResourceLocation(MOD_ID, "selenite");
    private static final ResourceLocation GRAPHITE = new ResourceLocation(MOD_ID, "graphite");
    private static final ResourceLocation KIMBERLITE = new ResourceLocation(MOD_ID, "kimberlite");
    private static final ResourceLocation PETRIFIED_WOOD = new ResourceLocation(MOD_ID, "petrified_wood");
    private static final ResourceLocation SULFUR = new ResourceLocation(MOD_ID, "sulfur");
    private static final ResourceLocation JET = new ResourceLocation(MOD_ID, "jet");
    private static final ResourceLocation MICROCLINE = new ResourceLocation(MOD_ID, "microcline");
    private static final ResourceLocation PITCHBLENDE = new ResourceLocation(MOD_ID, "pitchblende");
    private static final ResourceLocation CINNABAR = new ResourceLocation(MOD_ID, "cinnabar");
    private static final ResourceLocation CRYOLITE = new ResourceLocation(MOD_ID, "cryolite");
    private static final ResourceLocation SALTPETER = new ResourceLocation(MOD_ID, "saltpeter");
    private static final ResourceLocation SERPENTINE = new ResourceLocation(MOD_ID, "serpentine");
    private static final ResourceLocation SYLVITE = new ResourceLocation(MOD_ID, "sylvite");
    private static final ResourceLocation BORAX = new ResourceLocation(MOD_ID, "borax");
    private static final ResourceLocation OLIVINE = new ResourceLocation(MOD_ID, "olivine");
    private static final ResourceLocation LAPIS_LAZULI = new ResourceLocation(MOD_ID, "lapis_lazuli");

    @SubscribeEvent
    public static void onPreRegisterRockCategory(TFCRegistries.RegisterPreBlock<Ore> event)
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
