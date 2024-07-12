/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforge.neoforged.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.common.capabilities.food.FoodCapability;

/**
 * This is where we set the content of creative tabs, including both the icon and the display items, as non-decaying. This also affects the
 * view in JEI, as that is populated from creative tab content.
 * <p>
 * This is done as a mixin at the latest possible time for one primary reason: we want this to happen last, once creative tab content is all
 * populated, so other mods' creative tabs will also be set non-decaying. Note that for addon mods, we cannot use
 * {@link BuildCreativeModeTabContentsEvent} for this purpose, even with a listener priority set because the event will respect priority SECOND,
 * and respect mod load order FIRST, which effectively makes priority <strong>entirely useless</strong> for listening to mod bus events. Unless we
 * listen to every single mod bus which is a farce.
 * <p>
 * todo: 1.21 NeoForge, this hack solution shouldn't strictly be necessary, as mod bus events respect priority first, mod second, to confirm
 */
@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin
{
    @Mutable @Shadow @Final private Supplier<ItemStack> iconGenerator;
    @Shadow private Collection<ItemStack> displayItems;


    @Inject(method = "buildContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;rebuildSearchTree()V"))
    private void setCreativeTabContentNonDecaying(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci)
    {
        iconGenerator = FoodCapability.createNonDecayingStack(iconGenerator.get());
        for (ItemStack stack : displayItems)
        {
            FoodCapability.setStackNonDecaying(stack);
        }
    }
}
