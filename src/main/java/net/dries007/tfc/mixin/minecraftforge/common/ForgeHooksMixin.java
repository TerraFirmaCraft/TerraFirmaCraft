package net.dries007.tfc.mixin.minecraftforge.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.ForgeHooks;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.mixin.minecraftforge.registries.ForgeRegistryEntryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forge you dingus you have to set the biome registry name if you're going to monkey-patch the codec otherwise it's not a proper codec.
 *
 * Issue: https://github.com/MinecraftForge/MinecraftForge/issues/7433
 * PR: https://github.com/MinecraftForge/MinecraftForge/pull/7434
 */
@Mixin(ForgeHooks.class)
@SuppressWarnings("ConstantConditions")
public abstract class ForgeHooksMixin
{
    @Inject(method = "enhanceBiome", at = @At("RETURN"), cancellable = true, remap = false)
    private static void inject$enhanceBiome(ResourceLocation name, Biome.Climate climate, Biome.Category category, Float depth, Float scale, BiomeAmbience effects, BiomeGenerationSettings gen, MobSpawnInfo spawns, RecordCodecBuilder.Instance<Biome> codec, ForgeHooks.BiomeCallbackFunction callback, CallbackInfoReturnable<Biome> cir)
    {
        Biome biome = cir.getReturnValue();
        if (biome.getRegistryName() == null)
        {
            ((ForgeRegistryEntryAccessor) (Object) biome).accessor$setRegistryName(name); // Avoid errors due to assuming this is a registry override
        }
    }
}
