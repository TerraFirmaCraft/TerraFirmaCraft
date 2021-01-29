/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.function.Predicate;

import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.TFCItemTier;
import net.dries007.tfc.common.items.tools.JavelinItem;
import net.dries007.tfc.common.items.tools.TFCAxeItem;
import net.dries007.tfc.common.items.tools.TFCShovelItem;
import net.dries007.tfc.common.items.tools.TFCToolItem;

public enum RockCategory implements Predicate<Rock>
{
    IGNEOUS_EXTRUSIVE(TFCItemTier.IGNEOUS_EXTRUSIVE, true, true, false, true),
    IGNEOUS_INTRUSIVE(TFCItemTier.IGNEOUS_INTRUSIVE, false, true, true, true),
    METAMORPHIC(TFCItemTier.METAMORPHIC, true, true, true, false),
    SEDIMENTARY(TFCItemTier.SEDIMENTARY, true, true, false, false);

    private final IItemTier itemTier;
    private final boolean layer1;
    private final boolean layer2;
    private final boolean layer3;
    private final boolean hasAnvil;

    /**
     * A rock category.
     *
     * @param itemTier The tool material used for rock tools made of this rock
     * @param hasAnvil if this rock should be able to create a rock anvil
     */
    RockCategory(IItemTier itemTier, boolean layer1, boolean layer2, boolean layer3, boolean hasAnvil)
    {
        this.itemTier = itemTier;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.hasAnvil = hasAnvil;
    }

    public IItemTier getItemTier()
    {
        return itemTier;
    }

    @Override
    public boolean test(Rock rock)
    {
        return rock.getCategory() == this;
    }

    public boolean hasAnvil()
    {
        return hasAnvil;
    }

    @Override
    public String toString()
    {
        return name().toLowerCase();
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

    public enum Layer implements Predicate<Rock>
    {
        BOTTOM(3, x -> x.layer3),
        MIDDLE(2, x -> x.layer2),
        TOP(1, x -> x.layer1);

        public final int layer;
        private final Predicate<RockCategory> filter;

        Layer(int layer, Predicate<RockCategory> filter)
        {
            this.layer = layer;
            this.filter = filter;
        }

        @Override
        public boolean test(Rock rock)
        {
            return filter.test(rock.getCategory());
        }
    }
}