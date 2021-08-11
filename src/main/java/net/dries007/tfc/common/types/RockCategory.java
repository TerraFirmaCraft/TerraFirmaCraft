/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Locale;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.TFCItemTier;
import net.dries007.tfc.common.items.tools.JavelinItem;
import net.dries007.tfc.common.items.tools.TFCAxeItem;
import net.dries007.tfc.common.items.tools.TFCShovelItem;
import net.dries007.tfc.common.items.tools.TFCToolItem;

public enum RockCategory implements StringRepresentable
{
    IGNEOUS_EXTRUSIVE(TFCItemTier.IGNEOUS_EXTRUSIVE, 0f),
    IGNEOUS_INTRUSIVE(TFCItemTier.IGNEOUS_INTRUSIVE, 0.2f),
    METAMORPHIC(TFCItemTier.METAMORPHIC, -0.2f),
    SEDIMENTARY(TFCItemTier.SEDIMENTARY, -0.4f);

    private final String serializedName;
    private final Tier itemTier;
    private final float hardnessModifier;

    RockCategory(Tier itemTier, float hardnessModifier)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.itemTier = itemTier;
        this.hardnessModifier = hardnessModifier;
    }

    public Tier getItemTier()
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
        AXE(rock -> new TFCAxeItem(rock.getItemTier(), 1.5F, -3.2F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        AXE_HEAD(rock -> new Item((new Item.Properties()).tab(CreativeModeTab.TAB_MATERIALS))),
        HAMMER(rock -> new TFCToolItem(rock.getItemTier(), 1.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        HAMMER_HEAD(rock -> new Item((new Item.Properties()).tab(CreativeModeTab.TAB_MATERIALS))),
        HOE(rock -> new HoeItem(rock.getItemTier(), -1, -3.0f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        HOE_HEAD(rock -> new Item((new Item.Properties()).tab(CreativeModeTab.TAB_MATERIALS))),
        JAVELIN(rock -> new JavelinItem(rock.getItemTier(), 0.7F, -1.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        JAVELIN_HEAD(rock -> new Item((new Item.Properties()).tab(CreativeModeTab.TAB_MATERIALS))),
        KNIFE(rock -> new TFCToolItem(rock.getItemTier(), 0.54F, -1.5F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        KNIFE_HEAD(rock -> new Item((new Item.Properties()).tab(CreativeModeTab.TAB_MATERIALS))),
        SHOVEL(rock -> new TFCShovelItem(rock.getItemTier(), 0.875F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        SHOVEL_HEAD(rock -> new Item((new Item.Properties()).tab(CreativeModeTab.TAB_MATERIALS)));

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