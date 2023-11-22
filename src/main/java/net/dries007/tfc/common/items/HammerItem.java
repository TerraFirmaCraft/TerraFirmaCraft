/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class HammerItem extends ToolItem
{
    @Nullable
    private final ResourceLocation metalTexture;

    public HammerItem(Tier tier, float attackDamage, float attackSpeed, Properties properties)
    {
        this(tier, attackDamage, attackSpeed, properties, (ResourceLocation) null);
    }

    public HammerItem(Tier tier, float attackDamage, float attackSpeed, Properties properties, String metalName)
    {
        this(tier, attackDamage, attackSpeed, properties, Helpers.identifier("block/metal/smooth/" + metalName));
    }

    public HammerItem(Tier tier, float attackDamage, float attackSpeed, Properties properties, @Nullable ResourceLocation metalTexture)
    {
        super(tier, attackDamage, attackSpeed, TFCTags.Blocks.MINEABLE_WITH_HAMMER, properties);
        this.metalTexture = metalTexture;
    }

    @Nullable
    public ResourceLocation getMetalTexture()
    {
        return metalTexture;
    }
}
