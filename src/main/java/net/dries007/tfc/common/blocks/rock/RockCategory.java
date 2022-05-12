/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.items.JavelinItem;
import net.dries007.tfc.common.items.ToolItem;

public enum RockCategory implements StringRepresentable
{
    IGNEOUS_EXTRUSIVE(TFCTiers.IGNEOUS_EXTRUSIVE, 0f),
    IGNEOUS_INTRUSIVE(TFCTiers.IGNEOUS_INTRUSIVE, 0.2f),
    METAMORPHIC(TFCTiers.METAMORPHIC, -0.2f),
    SEDIMENTARY(TFCTiers.SEDIMENTARY, -0.4f);

    private final String serializedName;
    private final Tier itemTier;
    private final float hardnessModifier;

    RockCategory(Tier itemTier, float hardnessModifier)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.itemTier = itemTier;
        this.hardnessModifier = hardnessModifier;
    }

    public Tier getTier()
    {
        return itemTier;
    }

    public float getHardness()
    {
        return hardnessModifier;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public enum ItemType
    {
        AXE(rock -> new AxeItem(rock.getTier(), ToolItem.calculateVanillaAttackDamage(1.5F, rock.getTier()), -3.2F, new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        AXE_HEAD(rock -> new Item(new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        HAMMER(rock -> new ToolItem(rock.getTier(), 1.0F, -3.0F, TFCTags.Blocks.MINEABLE_WITH_HAMMER, new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        HAMMER_HEAD(rock -> new Item(new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        HOE(rock -> new HoeItem(rock.getTier(), -1, -3.0f, new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        HOE_HEAD(rock -> new Item(new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        JAVELIN(rock -> new JavelinItem(rock.getTier(), 0.7F, -1.8F, new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        JAVELIN_HEAD(rock -> new Item(new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        KNIFE(rock -> new ToolItem(rock.getTier(), 0.54F, -1.5F, TFCTags.Blocks.MINEABLE_WITH_KNIFE, new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        KNIFE_HEAD(rock -> new Item(new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        SHOVEL(rock -> new ShovelItem(rock.getTier(), ToolItem.calculateVanillaAttackDamage(0.875F, rock.getTier()), -3.0F, new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS))),
        SHOVEL_HEAD(rock -> new Item(new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS)));

        private final NonNullFunction<RockCategory, Item> itemFactory;

        ItemType(NonNullFunction<RockCategory, Item> itemFactory)
        {
            this.itemFactory = itemFactory;
        }

        public Item create(RockCategory category)
        {
            return itemFactory.apply(category);
        }
    }
}