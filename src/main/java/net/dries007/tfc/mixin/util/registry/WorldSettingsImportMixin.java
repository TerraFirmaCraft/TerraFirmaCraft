package net.dries007.tfc.mixin.util.registry;

import java.util.OptionalInt;

import com.google.gson.JsonElement;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DelegatingDynamicOps;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldSettingsImport;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This is a VERY ungodly hack, which is done only to log a pile of information when vanilla does it horrible error handling.
 * Loading world gen data packs produces errors that are unclear and painful at best, and outright hides them at worst.
 * This tries to inject logging messages at common points where errors occur and are hidden, or adds additional context to these locations.
 */
@Mixin(WorldSettingsImport.class)
public abstract class WorldSettingsImportMixin<T> extends DelegatingDynamicOps<T>
{
    @Shadow
    @Final
    private WorldSettingsImport.IResourceAccess resources;

    private WorldSettingsImportMixin(DynamicOps<T> dynamicOps)
    {
        super(dynamicOps);
    }

    @Redirect(method = "readAndRegisterElement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/WorldSettingsImport$IResourceAccess;parseElement(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/util/RegistryKey;Lcom/mojang/serialization/Decoder;)Lcom/mojang/serialization/DataResult;"), require = 0)
    private <E> DataResult<Pair<E, OptionalInt>> inject$readAndRegisterElement(WorldSettingsImport.IResourceAccess resourceAccess, DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> rootKey, RegistryKey<E> elementKey, Decoder<E> decoder, RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> mapCodec, ResourceLocation keyIdentifier)
    {
        // Call the original parse function and return the result. This redirect is simply used as an argument getter and injection point
        DataResult<Pair<E, OptionalInt>> dataResult = resources.parseElement(dynamicOps, rootKey, elementKey, decoder);

        // At this point we can do a couple extra checks, and spit out some more useful error information
        if (TFCConfig.COMMON.enableDevTweaks.get() && !dataResult.result().isPresent())
        {
            // There was some form of error! We need to log this and not just silently eat it
            String error = dataResult.error().map(DataResult.PartialResult::message).orElse("No error :(");
            TerraFirmaCraft.LOGGER.error("[Possible DFU FU] A data result was empty. This error may be swallowed! Root = {}, Object = {}, Error = {}", rootKey.location(), elementKey.location(), error);
        }

        return dataResult;
    }
}
