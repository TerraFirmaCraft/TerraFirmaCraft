/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Locale;
import java.util.function.Function;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.items.JavelinItem;
import net.dries007.tfc.common.items.TFCHoeItem;
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

    public Tier tier()
    {
        return itemTier;
    }

    public float hardness(float base)
    {
        return base + hardnessModifier;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public enum ItemType
    {
        AXE(rock -> new AxeItem(rock.tier(), tool(rock, 1.5f, -3.2f))),
        AXE_HEAD,
        HAMMER(rock -> new ToolItem(rock.tier(),  TFCTags.Blocks.MINEABLE_WITH_HAMMER, tool(rock, 1f, -3.0f))),
        HAMMER_HEAD,
        HOE(rock -> new TFCHoeItem(rock.tier(), tool(rock, -1f, -3.0f))),
        HOE_HEAD,
        JAVELIN(rock -> new JavelinItem(rock.tier(), tool(rock, 0.7f, -2.2f))),
        JAVELIN_HEAD,
        KNIFE(rock -> new ToolItem(rock.tier(), TFCTags.Blocks.MINEABLE_WITH_KNIFE, tool(rock, 0.6f, -2.0f))),
        KNIFE_HEAD,
        SHOVEL(rock -> new ShovelItem(rock.tier(), tool(rock, 0.875f, -3.0f))),
        SHOVEL_HEAD;

        public static Item.Properties tool(RockCategory rock, float attackDamageFactor, float attackSpeed)
        {
            return new Item.Properties().attributes(ToolItem.productAttributes(rock.tier(), attackDamageFactor, attackSpeed));
        }

        private final Function<RockCategory, Item> itemFactory;

        ItemType()
        {
            this(rock -> new Item(new Item.Properties()));
        }

        ItemType(Function<RockCategory, Item> itemFactory)
        {
            this.itemFactory = itemFactory;
        }

        public Item create(RockCategory category)
        {
            return itemFactory.apply(category);
        }
    }
}