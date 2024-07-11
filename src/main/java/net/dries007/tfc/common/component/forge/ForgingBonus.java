/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.List;
import java.util.Locale;
import java.util.function.DoubleSupplier;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.TFCComponents;
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

    public static final ForgingBonus DEFAULT = NONE;
    private static final ForgingBonus[] VALUES = values();

    public static ForgingBonus byRatio(float ratio)
    {
        for (int i = VALUES.length - 1; i > 0; i--)
        {
            if (VALUES[i].minRatio.getAsDouble() > ratio)
            {
                return VALUES[i];
            }
        }
        return NONE;
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        final ForgingBonus bonus = get(stack);
        if (bonus != NONE)
        {
            tooltips.add(Helpers.translateEnum(bonus).withStyle(ChatFormatting.GREEN));
        }
    }

    /**
     * Mimics unbreaking behavior for higher forging bonuses.
     *
     * @return {@code true} if the damage was consumed.
     * @see ItemStack#hurt(int, RandomSource, ServerPlayer)
     */
    public static boolean applyLikeUnbreaking(ItemStack stack, RandomSource random)
    {
        if (stack.isDamageableItem())
        {
            final ForgingBonus bonus = get(stack);
            if (bonus != NONE)
            {
                return random.nextFloat() < bonus.durability();
            }
        }
        return false;
    }

    /**
     * Get the forging bonus currently attached to an item stack.
     */
    public static ForgingBonus get(ItemStack stack)
    {
        return stack.getOrDefault(TFCComponents.FORGING_BONUS, DEFAULT);
    }

    /**
     * Set the forging bonus on an item stack
     */
    public static void set(ItemStack stack, ForgingBonus bonus)
    {
        stack.set(TFCComponents.FORGING_BONUS, bonus);
    }

    private final String serializedName;
    private final DoubleSupplier minRatio;

    ForgingBonus(DoubleSupplier minRatio)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.minRatio = minRatio;
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
