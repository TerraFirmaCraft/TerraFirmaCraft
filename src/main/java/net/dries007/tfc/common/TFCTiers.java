/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

public final class TFCTiers
{
    // Stone ~ Vanilla Wood
    public static final Tier IGNEOUS_INTRUSIVE = of("igneous_intrusive", TFCTags.Blocks.NEEDS_STONE_TOOL, 60, 4.7f, 2.0f, 5);
    public static final Tier IGNEOUS_EXTRUSIVE = of("igneous_extrusive", TFCTags.Blocks.NEEDS_STONE_TOOL, 70, 4.7f, 2.0f, 5);
    public static final Tier SEDIMENTARY = of("sedimentary", TFCTags.Blocks.NEEDS_STONE_TOOL, 50, 4.0f, 2.0f, 5);
    public static final Tier METAMORPHIC = of("metamorphic", TFCTags.Blocks.NEEDS_STONE_TOOL, 55, 4.35f, 2.0f, 5);
    // Copper ~ Vanilla Stone
    public static final Tier COPPER = of("copper", TFCTags.Blocks.NEEDS_COPPER_TOOL, 600, 5.25f, 3.25f, 8);
    // Bronze ~ Vanilla Iron
    public static final Tier BRONZE = of("bronze", TFCTags.Blocks.NEEDS_BRONZE_TOOL, 1300, 7.3f, 4.0f, 13);
    public static final Tier BISMUTH_BRONZE = of("bismuth_bronze", TFCTags.Blocks.NEEDS_BRONZE_TOOL, 1200, 6.65f, 4.0f, 10);
    public static final Tier BLACK_BRONZE = of("black_bronze", TFCTags.Blocks.NEEDS_BRONZE_TOOL, 1460, 6.0f, 4.25f, 10);
    // Wrought Iron ~ Vanilla Iron
    public static final Tier WROUGHT_IRON = of("wrought_iron", TFCTags.Blocks.NEEDS_WROUGHT_IRON_TOOL, 2200, 8.0f, 4.75f, 12);
    // Steel ~ Vanilla Diamond
    public static final Tier STEEL = of("steel", TFCTags.Blocks.NEEDS_STEEL_TOOL, 3300, 9.5f, 5.75f, 12);
    // Black Steel ~ Vanilla Diamond
    public static final Tier BLACK_STEEL = of("black_steel", TFCTags.Blocks.NEEDS_BLACK_STEEL_TOOL, 4200, 11.0f, 7.0f, 17);
    // Colored Steel ~ Vanilla Netherite
    public static final Tier BLUE_STEEL = of("blue_steel", TFCTags.Blocks.NEEDS_COLORED_STEEL_TOOL, 6500, 12.0f, 9.0f, 22);
    public static final Tier RED_STEEL = of("red_steel", TFCTags.Blocks.NEEDS_COLORED_STEEL_TOOL, 6500, 12.0f, 9.0f, 22);

    private static Tier of(String name, TagKey<Block> tag, int uses, float speed, float damage, int enchantmentValue)
    {
        return new SimpleTier(tag, uses, speed, damage, enchantmentValue, () -> Ingredient.EMPTY)
        {
            @Override
            public String toString()
            {
                return name;
            }
        };
    }
}