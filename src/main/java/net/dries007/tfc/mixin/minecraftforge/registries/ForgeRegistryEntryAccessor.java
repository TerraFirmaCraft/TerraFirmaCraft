package net.dries007.tfc.mixin.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Required to fix a forge bug
 *
 * @see net.dries007.tfc.mixin.minecraftforge.common.ForgeHooksMixin
 */
@Mixin(ForgeRegistryEntry.class)
public interface ForgeRegistryEntryAccessor
{
    @Accessor(value = "registryName", remap = false)
    void accessor$setRegistryName(ResourceLocation registryName);
}
