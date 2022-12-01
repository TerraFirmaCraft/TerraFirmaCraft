/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Locale;
import java.util.function.Function;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;

import net.dries007.tfc.common.TFCItemGroup;
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

    public Tier getTier()
    {
        return itemTier;
    }

    public float hardness(float base)
    {
        return base + hardnessModifier;
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
        AXE(rock -> new AxeItem(rock.getTier(), ToolItem.calculateVanillaAttackDamage(1.5F, rock.getTier()), -3.2F, properties())),
        AXE_HEAD,
        HAMMER(rock -> new ToolItem(rock.getTier(), ToolItem.calculateVanillaAttackDamage(1f, rock.getTier()), -3.0F, TFCTags.Blocks.MINEABLE_WITH_HAMMER, properties())),
        HAMMER_HEAD,
        HOE(rock -> new TFCHoeItem(rock.getTier(), -1, -3.0f, properties())),
        HOE_HEAD,
        JAVELIN(rock -> new JavelinItem(rock.getTier(), ToolItem.calculateVanillaAttackDamage(1.0F, rock.getTier()), -2.2F, properties(), "stone")),
        JAVELIN_HEAD,
        KNIFE(rock -> new ToolItem(rock.getTier(), ToolItem.calculateVanillaAttackDamage(0.6f, rock.getTier()), -2.0F, TFCTags.Blocks.MINEABLE_WITH_KNIFE, properties())),
        KNIFE_HEAD,
        SHOVEL(rock -> new ShovelItem(rock.getTier(), ToolItem.calculateVanillaAttackDamage(0.875F, rock.getTier()), -3.0F, properties())),
        SHOVEL_HEAD;

        public static Item.Properties properties()
        {
            return new Item.Properties().tab(TFCItemGroup.ROCK_STUFFS);
        }

        private final Function<RockCategory, Item> itemFactory;

        ItemType()
        {
            this(rock -> new Item(properties()));
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