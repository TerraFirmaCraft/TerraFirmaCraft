/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;
import java.util.function.DoubleSupplier;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public final class FoodTrait
{
    public static final Codec<FoodTrait> CODEC = FoodTraits.REGISTRY.byNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, FoodTrait> STREAM_CODEC = ByteBufCodecs.registry(FoodTraits.KEY);

    private final DoubleSupplier decayModifier;
    private final @Nullable String translationKey;

    public FoodTrait(DoubleSupplier decayModifier, @Nullable String translationKey)
    {
        this.decayModifier = decayModifier;
        this.translationKey = translationKey;
    }

    public float getDecayModifier()
    {
        return (float) decayModifier.getAsDouble();
    }

    /**
     * Adds information about the trait to the food stack
     *
     * @param text  The tooltip strings
     */
    public void addTooltipInfo(List<Component> text)
    {
        if (translationKey != null)
        {
            final MutableComponent component = Component.translatable(translationKey);
            if (decayModifier.getAsDouble() > 1)
            {
                component.withStyle(ChatFormatting.RED);
            }
            text.add(component);
        }
    }
}
