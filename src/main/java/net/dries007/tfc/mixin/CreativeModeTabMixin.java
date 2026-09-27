/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Collection;
import java.util.Set;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.common.component.food.FoodCapability;

/**
 * This cannot be done in {@link BuildCreativeModeTabContentsEvent}: because it dispatches ot each mod in order...
 * Modifying the entries during the event also changes their hash while they are inside
 * the event's hash sets, which breaks any later calls with a food item
 * <p>
 * So we wait until every mod has finished, then modify the stacks and rebuild the sets so they hash correctly.
 */
@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin
{
    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @Shadow public abstract ItemStack getIconItem();

    @Inject(method = "buildContents", at = @At("TAIL"))
    private void setCreativeTabContentNonDecaying(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci)
    {
        FoodCapability.setTransientNonDecaying(getIconItem());
        displayItems = withNonDecayingContent(displayItems);
        displayItemsSearchTab = withNonDecayingContent(displayItemsSearchTab);
    }

    @Unique
    private static Set<ItemStack> withNonDecayingContent(Collection<ItemStack> stacks)
    {
        final Set<ItemStack> result = ItemStackLinkedSet.createTypeAndComponentsSet();
        for (ItemStack stack : stacks)
        {
            result.add(FoodCapability.setTransientNonDecaying(stack));
        }
        return result;
    }
}
