/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class TFCShieldItem extends ShieldItem
{
    private final Tier tier;

    public TFCShieldItem(Tier tier, Properties builder)
    {
        super(builder.defaultDurability(tier.getUses()));
        this.tier = tier;
        if (FMLEnvironment.dist.isClient()) {
            ItemProperties.register(this, new ResourceLocation("blocking"), (stack, world, living, i) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0f : 0.0f);
        }
    }

    @Override
    public int getEnchantmentValue()
    {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair)
    {
        return tier.getRepairIngredient().test(repair);
    }
}