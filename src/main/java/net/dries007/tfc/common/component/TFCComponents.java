/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.forge.ForgingComponent;
import net.dries007.tfc.common.component.glass.GlassOperations;
import net.dries007.tfc.util.registry.RegistryHolder;

public final class TFCComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TerraFirmaCraft.MOD_ID);

    public static final Id<ForgingComponent> FORGING = register("forging", ForgingComponent.CODEC, ForgingComponent.STREAM_CODEC);
    public static final Id<ForgingBonus> FORGING_BONUS = register("forging_bonus", ForgingBonus.CODEC, ForgingBonus.STREAM_CODEC);

    public static final Id<GlassOperations> GLASS = register("glass", GlassOperations.CODEC, GlassOperations.STREAM_CODEC);

    public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event)
    {
        event.modifyMatching(e -> true, builder -> builder
            .set(FORGING.get(), ForgingComponent.DEFAULT)
            .set(FORGING_BONUS.get(), ForgingBonus.DEFAULT)
            .set(GLASS.get(), GlassOperations.DEFAULT)
        );
    }

    private static <T> Id<T> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new Id<>(COMPONENTS.register(name, () -> new DataComponentType.Builder<T>()
            .persistent(codec)
            .networkSynchronized(streamCodec)
            .build()));
    }

    public record Id<T>(DeferredHolder<DataComponentType<?>, DataComponentType<T>> holder)
        implements RegistryHolder<DataComponentType<?>, DataComponentType<T>> {}
}
