/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.Locale;
import java.util.function.DoubleSupplier;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.Helpers;

public enum ForgingBonus implements StringRepresentable
{
    NONE(() -> Double.POSITIVE_INFINITY),
    MODEST(TFCConfig.SERVER.anvilModestlyForgedThreshold::get),
    WELL(TFCConfig.SERVER.anvilWellForgedThreshold::get),
    EXPERT(TFCConfig.SERVER.anvilExpertForgedThreshold::get),
    PERFECT(TFCConfig.SERVER.anvilPerfectlyForgedThreshold::get);

    public static final Codec<ForgingBonus> CODEC = StringRepresentable.fromValues(ForgingBonus::values);
    public static final StreamCodec<ByteBuf, ForgingBonus> STREAM_CODEC = StreamCodecs.forEnum(ForgingBonus::values);

    private static final ForgingBonus[] VALUES = values();

    public static ForgingBonus byRatio(float ratio)
    {
        for (int i = ForgingBonus.VALUES.length - 1; i > 0; i--)
        {
            if (ForgingBonus.VALUES[i].minRatio.getAsDouble() > ratio)
            {
                return ForgingBonus.VALUES[i];
            }
        }
        return ForgingBonus.NONE;
    }

    private final String serializedName;
    private final DoubleSupplier minRatio;

    ForgingBonus(DoubleSupplier minRatio)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.minRatio = minRatio;
    }

    public MutableComponent getDisplayName()
    {
        return Helpers.translateEnum(this);
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public float efficiency()
    {
        return Helpers.lerp(ordinal() * 0.25f, 1.0f, TFCConfig.SERVER.anvilMaxEfficiencyMultiplier.get().floatValue());
    }

    public float durability()
    {
        return Helpers.lerp(ordinal() * 0.25f, 0f, TFCConfig.SERVER.anvilMaxDurabilityMultiplier.get().floatValue());
    }

    public float damage()
    {
        return Helpers.lerp(ordinal() * 0.25f, 1.0f, TFCConfig.SERVER.anvilMaxDamageMultiplier.get().floatValue());
    }
}
