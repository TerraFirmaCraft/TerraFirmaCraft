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
import net.dries007.tfc.api.types.TFCRegistries;
import net.dries007.tfc.objects.ToolMaterialsTFC;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.api.types.Metal.Tier.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultMetals
{
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

    @SubscribeEvent
    public static void onPreRegisterMetal(TFCRegistries.RegisterPreBlock<Metal> event)
    {
        event.getRegistry().registerAll(
            new Metal(BISMUTH, TIER_I, 0.14, 270),
            new Metal(BISMUTH_BRONZE, TIER_I, 0.35, 985, ToolMaterialsTFC.BISMUTH_BRONZE),
            new Metal(BLACK_BRONZE, TIER_I, 0.35, 1070, ToolMaterialsTFC.BLACK_BRONZE),
            new Metal(BRASS, TIER_I, 0.35, 930),
            new Metal(BRONZE, TIER_I, 0.35, 950, ToolMaterialsTFC.BRONZE),
            new Metal(COPPER, TIER_I, 0.35, 1080, ToolMaterialsTFC.COPPER),
            new Metal(GOLD, TIER_I, 0.6, 1060),
            new Metal(LEAD, TIER_I, 0.22, 328),
            new Metal(NICKEL, TIER_I, 0.48, 1453),
            new Metal(ROSE_GOLD, TIER_I, 0.35, 960),
            new Metal(SILVER, TIER_I, 0.48, 961),
            new Metal(TIN, TIER_I, 0.14, 230),
            new Metal(ZINC, TIER_I, 0.21, 420),
            new Metal(STERLING_SILVER, TIER_I, 0.35, 900),
            new Metal(WROUGHT_IRON, TIER_III, 0.35, 1535, ToolMaterialsTFC.IRON),
            new Metal(PIG_IRON, TIER_IV, 0.35, 1535),
            new Metal(STEEL, TIER_IV, 0.35, 1540, ToolMaterialsTFC.STEEL),
            new Metal(PLATINUM, TIER_V, 0.35, 1730),
            new Metal(BLACK_STEEL, TIER_V, 0.35, 1485, ToolMaterialsTFC.BLACK_STEEL),
            new Metal(BLUE_STEEL, TIER_V, 0.35, 1540, ToolMaterialsTFC.BLUE_STEEL),
            new Metal(RED_STEEL, TIER_V, 0.35, 1540, ToolMaterialsTFC.RED_STEEL),
            new Metal(WEAK_STEEL, TIER_V, false, 0.35, 1540),
            new Metal(WEAK_BLUE_STEEL, TIER_V, false, 0.35, 1540),
            new Metal(WEAK_RED_STEEL, TIER_V, false, 0.35, 1540),
            new Metal(HIGH_CARBON_STEEL, TIER_V, false, 0.35, 1540),
            new Metal(HIGH_CARBON_BLUE_STEEL, TIER_V, false, 0.35, 1540),
            new Metal(HIGH_CARBON_RED_STEEL, TIER_V, false, 0.35, 1540),
            new Metal(HIGH_CARBON_BLACK_STEEL, TIER_V, false, 0.35, 1540),
            new Metal(UNKNOWN, TIER_I, false, 0.5, 1250)
        );
    }
}
