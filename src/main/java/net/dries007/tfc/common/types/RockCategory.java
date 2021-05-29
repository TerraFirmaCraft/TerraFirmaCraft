/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Locale;

import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.TFCItemTier;
import net.dries007.tfc.common.items.tools.JavelinItem;
import net.dries007.tfc.common.items.tools.TFCAxeItem;
import net.dries007.tfc.common.items.tools.TFCShovelItem;
import net.dries007.tfc.common.items.tools.TFCToolItem;

public enum RockCategory implements IStringSerializable
{
    IGNEOUS_EXTRUSIVE(TFCItemTier.IGNEOUS_EXTRUSIVE, true),
    IGNEOUS_INTRUSIVE(TFCItemTier.IGNEOUS_INTRUSIVE, true),
    METAMORPHIC(TFCItemTier.METAMORPHIC, false),
    SEDIMENTARY(TFCItemTier.SEDIMENTARY, false);

    private final String serializedName;
    private final IItemTier itemTier;
    private final boolean hasAnvil;

    RockCategory(IItemTier itemTier, boolean hasAnvil)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.itemTier = itemTier;
        this.hasAnvil = hasAnvil;
    }

    public IItemTier getItemTier()
    {
        return itemTier;
    }

    public boolean hasAnvil()
    {
        return hasAnvil;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public enum ItemType
    {
        AXE(rock -> new TFCAxeItem(rock.getItemTier(), 1.5F, -3.2F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS))),
        AXE_HEAD(rock -> new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS))),
        HAMMER(rock -> new TFCToolItem(rock.getItemTier(), 1.0F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS))),
        HAMMER_HEAD(rock -> new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS))),
        HOE(rock -> new HoeItem(rock.getItemTier(), -1, -3.0f, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS))),
        HOE_HEAD(rock -> new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS))),
        JAVELIN(rock -> new JavelinItem(rock.getItemTier(), 0.7F, -1.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS))),
        JAVELIN_HEAD(rock -> new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS))),
        KNIFE(rock -> new TFCToolItem(rock.getItemTier(), 0.54F, -1.5F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS))),
        KNIFE_HEAD(rock -> new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS))),
        SHOVEL(rock -> new TFCShovelItem(rock.getItemTier(), 0.875F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS))),
        SHOVEL_HEAD(rock -> new Item((new Item.Properties()).tab(ItemGroup.TAB_MATERIALS)));

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