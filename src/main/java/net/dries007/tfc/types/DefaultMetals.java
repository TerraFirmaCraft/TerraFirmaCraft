/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.ArmorMaterialTFC;
import net.dries007.tfc.objects.ToolMaterialsTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.api.types.Metal.Tier.*;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DefaultMetals
{
    /*
     * Metals
     */
    public static final ResourceLocation BISMUTH = new ResourceLocation(MOD_ID, "bismuth");
    public static final ResourceLocation BISMUTH_BRONZE = new ResourceLocation(MOD_ID, "bismuth_bronze");
    public static final ResourceLocation BLACK_BRONZE = new ResourceLocation(MOD_ID, "black_bronze");
    public static final ResourceLocation BRASS = new ResourceLocation(MOD_ID, "brass");
    public static final ResourceLocation BRONZE = new ResourceLocation(MOD_ID, "bronze");
    public static final ResourceLocation COPPER = new ResourceLocation(MOD_ID, "copper");
    public static final ResourceLocation GOLD = new ResourceLocation(MOD_ID, "gold");
    public static final ResourceLocation LEAD = new ResourceLocation(MOD_ID, "lead");
    public static final ResourceLocation NICKEL = new ResourceLocation(MOD_ID, "nickel");
    public static final ResourceLocation ROSE_GOLD = new ResourceLocation(MOD_ID, "rose_gold");
    public static final ResourceLocation SILVER = new ResourceLocation(MOD_ID, "silver");
    public static final ResourceLocation TIN = new ResourceLocation(MOD_ID, "tin");
    public static final ResourceLocation ZINC = new ResourceLocation(MOD_ID, "zinc");
    public static final ResourceLocation STERLING_SILVER = new ResourceLocation(MOD_ID, "sterling_silver");
    public static final ResourceLocation WROUGHT_IRON = new ResourceLocation(MOD_ID, "wrought_iron");
    public static final ResourceLocation PIG_IRON = new ResourceLocation(MOD_ID, "pig_iron");
    public static final ResourceLocation STEEL = new ResourceLocation(MOD_ID, "steel");
    public static final ResourceLocation PLATINUM = new ResourceLocation(MOD_ID, "platinum");
    public static final ResourceLocation BLACK_STEEL = new ResourceLocation(MOD_ID, "black_steel");
    public static final ResourceLocation BLUE_STEEL = new ResourceLocation(MOD_ID, "blue_steel");
    public static final ResourceLocation RED_STEEL = new ResourceLocation(MOD_ID, "red_steel");
    public static final ResourceLocation WEAK_STEEL = new ResourceLocation(MOD_ID, "weak_steel");
    public static final ResourceLocation WEAK_BLUE_STEEL = new ResourceLocation(MOD_ID, "weak_blue_steel");
    public static final ResourceLocation WEAK_RED_STEEL = new ResourceLocation(MOD_ID, "weak_red_steel");
    public static final ResourceLocation HIGH_CARBON_STEEL = new ResourceLocation(MOD_ID, "high_carbon_steel");
    public static final ResourceLocation HIGH_CARBON_BLUE_STEEL = new ResourceLocation(MOD_ID, "high_carbon_blue_steel");
    public static final ResourceLocation HIGH_CARBON_RED_STEEL = new ResourceLocation(MOD_ID, "high_carbon_red_steel");
    public static final ResourceLocation HIGH_CARBON_BLACK_STEEL = new ResourceLocation(MOD_ID, "high_carbon_black_steel");
    public static final ResourceLocation UNKNOWN = new ResourceLocation(MOD_ID, "unknown");

    /*
     * Ores
     */
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
    public static void onPreRegisterOre(TFCRegistryEvent.RegisterPreBlock<Ore> event)
    {
        event.getRegistry().registerAll(
            new Ore(NATIVE_COPPER, COPPER, 0.75D, 0.33D),
            new Ore(NATIVE_GOLD, GOLD, 0.25D, 0.15D),
            new Ore(NATIVE_PLATINUM, PLATINUM, 1D, 0.002D),
            new Ore(HEMATITE, WROUGHT_IRON, false),
            new Ore(NATIVE_SILVER, SILVER, 0.50D, 0.15D),
            new Ore(CASSITERITE, TIN),
            new Ore(GALENA, LEAD),
            new Ore(BISMUTHINITE, BISMUTH),
            new Ore(GARNIERITE, NICKEL),
            new Ore(MALACHITE, COPPER),
            new Ore(MAGNETITE, WROUGHT_IRON, false),
            new Ore(LIMONITE, WROUGHT_IRON, false),
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
    public static void onPreRegisterMetal(TFCRegistryEvent.RegisterPreBlock<Metal> event)
    {
        event.getRegistry().registerAll(
            new Metal(BISMUTH, TIER_I, true, 0.14f, 270, 0xFF486B72, null, null),
            new Metal(BISMUTH_BRONZE, TIER_II, true, 0.35f, 985, 0xFF418E4F, ToolMaterialsTFC.BISMUTH_BRONZE, ArmorMaterialTFC.BISMUTH_BRONZE),
            new Metal(BLACK_BRONZE, TIER_II, true, 0.35f, 1070, 0xFF3B2636, ToolMaterialsTFC.BLACK_BRONZE, ArmorMaterialTFC.BLACK_BRONZE),
            new Metal(BRASS, TIER_I, true, 0.35f, 930, 0xFF96892E, null, null),
            new Metal(BRONZE, TIER_II, true, 0.35f, 950, 0xFF7C5E33, ToolMaterialsTFC.BRONZE, ArmorMaterialTFC.BRONZE),
            new Metal(COPPER, TIER_I, true, 0.35f, 1080, 0xFFB64027, ToolMaterialsTFC.COPPER, ArmorMaterialTFC.COPPER),
            new Metal(GOLD, TIER_I, true, 0.6f, 1060, 0xFFDCBF1B, null, null),
            new Metal(LEAD, TIER_I, true, 0.22f, 328, 0xFF40494D, null, null),
            new Metal(NICKEL, TIER_I, true, 0.48f, 1453, 0xFF4E4E3C, null, null),
            new Metal(ROSE_GOLD, TIER_I, true, 0.35f, 960, 0xFFEB7137, null, null),
            new Metal(SILVER, TIER_I, true, 0.48f, 961, 0xFF949495, null, null),
            new Metal(TIN, TIER_I, true, 0.14f, 230, 0xFF90A4BB, null, null),
            new Metal(ZINC, TIER_I, true, 0.21f, 420, 0xFFBBB9C4, null, null),
            new Metal(STERLING_SILVER, TIER_I, true, 0.35f, 900, 0xFFAC927B, null, null),
            new Metal(WROUGHT_IRON, TIER_III, true, 0.35f, 1535, 0xFF989897, ToolMaterialsTFC.WROUGHT_IRON, ArmorMaterialTFC.WROUGHT_IRON),
            new Metal(PIG_IRON, TIER_III, true, 0.35f, 1535, 0xFF6A595C, null, null),
            new Metal(STEEL, TIER_IV, true, 0.35f, 1540, 0xFF5F5F5F, ToolMaterialsTFC.STEEL, ArmorMaterialTFC.STEEL),
            new Metal(PLATINUM, TIER_V, true, 0.35f, 1730, 0xFF9DADC0, null, null),
            new Metal(BLACK_STEEL, TIER_V, true, 0.35f, 1485, 0xFF111111, ToolMaterialsTFC.BLACK_STEEL, ArmorMaterialTFC.BLACK_STEEL),
            new Metal(BLUE_STEEL, TIER_VI, true, 0.35f, 1540, 0xFF2D5596, ToolMaterialsTFC.BLUE_STEEL, ArmorMaterialTFC.BLUE_STEEL),
            new Metal(RED_STEEL, TIER_VI, true, 0.35f, 1540, 0xFF700503, ToolMaterialsTFC.RED_STEEL, ArmorMaterialTFC.RED_STEEL),
            new Metal(WEAK_STEEL, TIER_IV, false, 0.35f, 1540, 0xFF111111, null, null),
            new Metal(WEAK_BLUE_STEEL, TIER_V, false, 0.35f, 1540, 0xFF2D5596, null, null),
            new Metal(WEAK_RED_STEEL, TIER_V, false, 0.35f, 1540, 0xFF700503, null, null),
            new Metal(HIGH_CARBON_STEEL, TIER_III, false, 0.35f, 1540, 0xFF5F5F5F, null, null),
            new Metal(HIGH_CARBON_BLUE_STEEL, TIER_V, false, 0.35f, 1540, 0xFF2D5596, null, null),
            new Metal(HIGH_CARBON_RED_STEEL, TIER_V, false, 0.35f, 1540, 0xFF700503, null, null),
            new Metal(HIGH_CARBON_BLACK_STEEL, TIER_IV, false, 0.35f, 1540, 0xFF111111, null, null),
            new Metal(UNKNOWN, TIER_I, false, 0.5f, 1250, 0xFF2F2B27, null, null)
        );
    }

    @SubscribeEvent
    public static void onRegisterAlloyRecipe(RegistryEvent.Register<AlloyRecipe> event)
    {
        event.getRegistry().registerAll(
            new AlloyRecipe.Builder(BISMUTH_BRONZE).add(ZINC, 0.2, 0.3).add(COPPER, 0.5, 0.65).add(BISMUTH, 0.1, 0.2).build(),
            new AlloyRecipe.Builder(BLACK_BRONZE).add(COPPER, 0.5, 0.7).add(SILVER, 0.1, 0.25).add(GOLD, 0.1, 0.25).build(),
            new AlloyRecipe.Builder(BRONZE).add(COPPER, 0.88, 0.92).add(TIN, 0.08, 0.12).build(),
            new AlloyRecipe.Builder(BRASS).add(COPPER, 0.88, 0.92).add(ZINC, 0.08, 0.12).build(),
            new AlloyRecipe.Builder(ROSE_GOLD).add(COPPER, 0.15, 0.3).add(GOLD, 0.7, 0.85).build(),
            new AlloyRecipe.Builder(STERLING_SILVER).add(COPPER, 0.2, 0.4).add(SILVER, 0.6, 0.8).build(),
            new AlloyRecipe.Builder(WEAK_STEEL).add(STEEL, 0.5, 0.7).add(NICKEL, 0.15, 0.25).add(BLACK_BRONZE, 0.15, 0.25).build(),
            new AlloyRecipe.Builder(WEAK_BLUE_STEEL).add(BLACK_STEEL, 0.5, 0.55).add(STEEL, 0.2, 0.25).add(BISMUTH_BRONZE, 0.1, 0.15).add(STERLING_SILVER, 0.1, 0.15).build(),
            new AlloyRecipe.Builder(WEAK_RED_STEEL).add(BLACK_STEEL, 0.5, 0.55).add(STEEL, 0.2, 0.25).add(BRASS, 0.1, 0.15).add(ROSE_GOLD, 0.1, 0.15).build()
        );
    }
}
