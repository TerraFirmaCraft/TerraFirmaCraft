/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.TFCRegistries;
import net.dries007.tfc.objects.ToolMaterialsTFC;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.api.types.Metal.Tier.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultMetals
{
    /*
     * Metals
     */
    // Tier I
    private static final ResourceLocation BISMUTH = new ResourceLocation(MOD_ID, "bismuth");
    private static final ResourceLocation BISMUTH_BRONZE = new ResourceLocation(MOD_ID, "bismuth_bronze");
    private static final ResourceLocation BLACK_BRONZE = new ResourceLocation(MOD_ID, "black_bronze");
    private static final ResourceLocation BRASS = new ResourceLocation(MOD_ID, "brass");
    private static final ResourceLocation BRONZE = new ResourceLocation(MOD_ID, "bronze");
    private static final ResourceLocation COPPER = new ResourceLocation(MOD_ID, "copper");
    private static final ResourceLocation GOLD = new ResourceLocation(MOD_ID, "gold");
    private static final ResourceLocation LEAD = new ResourceLocation(MOD_ID, "lead");
    private static final ResourceLocation NICKEL = new ResourceLocation(MOD_ID, "nickel");
    private static final ResourceLocation ROSE_GOLD = new ResourceLocation(MOD_ID, "rose_gold");
    private static final ResourceLocation SILVER = new ResourceLocation(MOD_ID, "silver");
    private static final ResourceLocation TIN = new ResourceLocation(MOD_ID, "tin");
    private static final ResourceLocation ZINC = new ResourceLocation(MOD_ID, "zinc");
    private static final ResourceLocation STERLING_SILVER = new ResourceLocation(MOD_ID, "sterling_silver");
    // Tier III
    private static final ResourceLocation WROUGHT_IRON = new ResourceLocation(MOD_ID, "wrought_iron");
    private static final ResourceLocation PIG_IRON = new ResourceLocation(MOD_ID, "pig_iron");
    // Tier IV
    private static final ResourceLocation STEEL = new ResourceLocation(MOD_ID, "steel");
    // Tier V
    private static final ResourceLocation PLATINUM = new ResourceLocation(MOD_ID, "platinum");
    private static final ResourceLocation BLACK_STEEL = new ResourceLocation(MOD_ID, "black_steel");
    private static final ResourceLocation BLUE_STEEL = new ResourceLocation(MOD_ID, "blue_steel");
    private static final ResourceLocation RED_STEEL = new ResourceLocation(MOD_ID, "red_steel");
    private static final ResourceLocation WEAK_STEEL = new ResourceLocation(MOD_ID, "weak_steel");
    private static final ResourceLocation WEAK_BLUE_STEEL = new ResourceLocation(MOD_ID, "weak_blue_steel");
    private static final ResourceLocation WEAK_RED_STEEL = new ResourceLocation(MOD_ID, "weak_red_steel");
    private static final ResourceLocation HIGH_CARBON_STEEL = new ResourceLocation(MOD_ID, "high_carbon_steel");
    private static final ResourceLocation HIGH_CARBON_BLUE_STEEL = new ResourceLocation(MOD_ID, "high_carbon_blue_steel");
    private static final ResourceLocation HIGH_CARBON_RED_STEEL = new ResourceLocation(MOD_ID, "high_carbon_red_steel");
    private static final ResourceLocation HIGH_CARBON_BLACK_STEEL = new ResourceLocation(MOD_ID, "high_carbon_black_steel");
    // Tier I, Special
    private static final ResourceLocation UNKNOWN = new ResourceLocation(MOD_ID, "unknown");

    /*
     * Ores
     */
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
    public static void onPreRegisterOre(TFCRegistries.RegisterPreBlock<Ore> event)
    {
        event.getRegistry().registerAll(
            new Ore(NATIVE_COPPER, COPPER),
            new Ore(NATIVE_GOLD, GOLD),
            new Ore(NATIVE_PLATINUM, PLATINUM),
            new Ore(HEMATITE, PIG_IRON),
            new Ore(NATIVE_SILVER, SILVER),
            new Ore(CASSITERITE, TIN),
            new Ore(GALENA, LEAD),
            new Ore(BISMUTHINITE, BISMUTH),
            new Ore(GARNIERITE, NICKEL),
            new Ore(MALACHITE, COPPER),
            new Ore(MAGNETITE, PIG_IRON),
            new Ore(LIMONITE, PIG_IRON),
            new Ore(SPHALERITE, ZINC),
            new Ore(TETRAHEDRITE, COPPER),
            new Ore(BITUMINOUS_COAL),
            new Ore(LIGNITE),
            new Ore(KAOLINITE),
            new Ore(GYPSUM),
            new Ore(SATINSPAR),
            new Ore(SELENITE),
            new Ore(GRAPHITE),
            new Ore(KIMBERLITE),
            new Ore(PETRIFIED_WOOD),
            new Ore(SULFUR),
            new Ore(JET),
            new Ore(MICROCLINE),
            new Ore(PITCHBLENDE),
            new Ore(CINNABAR),
            new Ore(CRYOLITE),
            new Ore(SALTPETER),
            new Ore(SERPENTINE),
            new Ore(SYLVITE),
            new Ore(BORAX),
            new Ore(OLIVINE),
            new Ore(LAPIS_LAZULI)
        );
    }

    @SubscribeEvent
    public static void onPreRegisterMetal(TFCRegistries.RegisterPreBlock<Metal> event)
    {
        event.getRegistry().registerAll(
            new Metal(BISMUTH, TIER_I, true, 0.14f, 270, 0xFF33FF00, null),
            new Metal(BISMUTH_BRONZE, TIER_I, true, 0.35f, 985, 0xFF33FF01, ToolMaterialsTFC.BISMUTH_BRONZE),
            new Metal(BLACK_BRONZE, TIER_I, true, 0.35f, 1070, 0xFF33FF02, ToolMaterialsTFC.BLACK_BRONZE),
            new Metal(BRASS, TIER_I, true, 0.35f, 930, 0xFF33FF03, null),
            new Metal(BRONZE, TIER_I, true, 0.35f, 950, 0xFF33FF04, ToolMaterialsTFC.BRONZE),
            new Metal(COPPER, TIER_I, true, 0.35f, 1080, 0xFF33FF05, ToolMaterialsTFC.COPPER),
            new Metal(GOLD, TIER_I, true, 0.6f, 1060, 0xFF33FF06, null),
            new Metal(LEAD, TIER_I, true, 0.22f, 328, 0xFF33FF07, null),
            new Metal(NICKEL, TIER_I, true, 0.48f, 1453, 0xFF33FF08, null),
            new Metal(ROSE_GOLD, TIER_I, true, 0.35f, 960, 0xFF33FF09, null),
            new Metal(SILVER, TIER_I, true, 0.48f, 961, 0xFF33FF10, null),
            new Metal(TIN, TIER_I, true, 0.14f, 230, 0xFF33FF11, null),
            new Metal(ZINC, TIER_I, true, 0.21f, 420, 0xFF33FF12, null),
            new Metal(STERLING_SILVER, TIER_I, true, 0.35f, 900, 0xFF33FF13, null),
            new Metal(WROUGHT_IRON, TIER_III, true, 0.35f, 1535, 0xFF33FF14, ToolMaterialsTFC.IRON),
            new Metal(PIG_IRON, TIER_IV, true, 0.35f, 1535, 0xFF33FF15, null),
            new Metal(STEEL, TIER_IV, true, 0.35f, 1540, 0xFF33FF16, ToolMaterialsTFC.STEEL),
            new Metal(PLATINUM, TIER_V, true, 0.35f, 1730, 0xFF33FF17, null),
            new Metal(BLACK_STEEL, TIER_V, true, 0.35f, 1485, 0xFF33FF18, ToolMaterialsTFC.BLACK_STEEL),
            new Metal(BLUE_STEEL, TIER_V, true, 0.35f, 1540, 0xFF33FF19, ToolMaterialsTFC.BLUE_STEEL),
            new Metal(RED_STEEL, TIER_V, true, 0.35f, 1540, 0xFF33FF20, ToolMaterialsTFC.RED_STEEL),
            new Metal(WEAK_STEEL, TIER_V, false, 0.35f, 1540, 0xFF33FF21, null),
            new Metal(WEAK_BLUE_STEEL, TIER_V, false, 0.35f, 1540, 0xFF33FF22, null),
            new Metal(WEAK_RED_STEEL, TIER_V, false, 0.35f, 1540, 0xFF33FF23, null),
            new Metal(HIGH_CARBON_STEEL, TIER_V, false, 0.35f, 1540, 0xFF33FF24, null),
            new Metal(HIGH_CARBON_BLUE_STEEL, TIER_V, false, 0.35f, 1540, 0xFF33FF25, null),
            new Metal(HIGH_CARBON_RED_STEEL, TIER_V, false, 0.35f, 1540, 0xFF33FF26, null),
            new Metal(HIGH_CARBON_BLACK_STEEL, TIER_V, false, 0.35f, 1540, 0xFF33FF27, null),
            new Metal(UNKNOWN, TIER_I, false, 0.5f, 1250, 0xFF33FF28, null)
        );
    }
}
