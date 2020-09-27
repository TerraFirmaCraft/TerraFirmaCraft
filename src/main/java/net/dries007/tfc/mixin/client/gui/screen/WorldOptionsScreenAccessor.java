package net.dries007.tfc.mixin.client.gui.screen;

import java.util.Optional;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.WorldOptionsScreen;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Used to set the preset and currently selected settings in order to make the TFC world type the default world type.
 *
 * @see CreateWorldScreenMixin
 */
@Mixin(WorldOptionsScreen.class)
public interface WorldOptionsScreenAccessor
{
    @Accessor("preset")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void accessor$setPreset(Optional<BiomeGeneratorTypeScreens> preset);

    @Accessor("registryHolder")
    DynamicRegistries.Impl accessor$getRegistryHolder();

    @Accessor("settings")
    DimensionGeneratorSettings accessor$getSettings();

    @Accessor("settings")
    void accessor$setSettings(DimensionGeneratorSettings settings);
}
