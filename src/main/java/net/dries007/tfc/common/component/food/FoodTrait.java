/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public final class FoodTrait
{
    public static final Codec<Holder<FoodTrait>> CODEC = FoodTraits.REGISTRY.holderByNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FoodTrait>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FoodTraits.KEY);

    private final Supplier<Double> decayModifier;
    private final @Nullable String translationKey;

    public FoodTrait(Supplier<Double> decayModifier, @Nullable String translationKey)
    {
        this.decayModifier = decayModifier;
        this.translationKey = translationKey;
    }

    public float getDecayModifier()
    {
        return decayModifier.get().floatValue();
    }

    /**
     * Adds information about the trait to the food unsealedStack
     *
     * @param text  The tooltip strings
     */
    public void addTooltipInfo(Consumer<Component> text)
    {
        if (translationKey != null)
        {
            final MutableComponent component = Component.translatable(translationKey);
            if (getDecayModifier() > 1)
            {
                component.withStyle(ChatFormatting.RED);
            }
            text.accept(component);
        }
    }
}
