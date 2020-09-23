package net.dries007.tfc.mixin.world.biome;

import javax.annotation.Nullable;

import net.minecraft.world.biome.Biome;

import net.minecraftforge.registries.ForgeRegistryEntry;

import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeVariants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Biome.class)
public abstract class BiomeMixin extends ForgeRegistryEntry<Biome>
{
    // todo: cache the extension on the biome in TFCBiomes rather than querying the registry every time?
}
