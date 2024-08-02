/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType;
import net.dries007.tfc.util.data.Fertilizer;

import static net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType.*;

/**
 * Implement on block entities that hold nitrogen, phosphorous, and potassium values to allow fertilization and consuming nutrients.
 */
public interface IFarmland
{
    static void addNutrientParticles(ServerLevel level, BlockPos pos, Fertilizer fertilizer)
    {
        final float n = fertilizer.nitrogen(), p = fertilizer.phosphorus(), k = fertilizer.potassium();
        for (int i = 0; i < (int) (n > 0 ? Mth.clamp(n * 10, 1, 5) : 0); i++)
        {
            level.sendParticles(TFCParticles.NITROGEN.get(), pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat() / 5D, pos.getZ() + level.random.nextFloat(), 0, 0D, 0D, 0D, 1D);
        }
        for (int i = 0; i < (int) (p > 0 ? Mth.clamp(p * 10, 1, 5) : 0); i++)
        {
            level.sendParticles(TFCParticles.PHOSPHORUS.get(), pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat() / 5D, pos.getZ() + level.random.nextFloat(), 0, 0D, 0D, 0D, 1D);
        }
        for (int i = 0; i < (int) (k > 0 ? Mth.clamp(k * 10, 1, 5) : 0); i++)
        {
            level.sendParticles(TFCParticles.POTASSIUM.get(), pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat() / 5D, pos.getZ() + level.random.nextFloat(), 0, 0D, 0D, 0D, 1D);
        }
    }

    /**
     * @return the amount [0-1] of the nutrient of {@code type}.
     */
    float getNutrient(NutrientType type);

    /**
     * Implementations should clamp {@code value} on a range [0, 1]
     *
     * @param type the nutrient type to be set
     * @param value the amount (clamped [0-1]) of the nutrient to set
     */
    void setNutrient(NutrientType type, float value);

    default void addNutrient(NutrientType type, float value)
    {
        setNutrient(type, getNutrient(type) + value);
    }

    default void addNutrients(Fertilizer fertilizer)
    {
        addNutrients(fertilizer, 1);
    }

    default void addNutrients(Fertilizer fertilizer, float multiplier)
    {
        addNutrient(NITROGEN, fertilizer.nitrogen() * multiplier);
        addNutrient(PHOSPHOROUS, fertilizer.phosphorus() * multiplier);
        addNutrient(POTASSIUM, fertilizer.potassium() * multiplier);
    }


    /**
     * Consume up to {@code amount} of nutrient {@code type}.
     * Resupplies other nutrient by 1/6 of the amount consumed.
     * @return The amount of nutrient {@code type} that was actually consumed.
     */
    default float consumeNutrientAndResupplyOthers(NutrientType type, float amount)
    {
        final float startValue = getNutrient(type);
        final float consumed = Math.min(startValue, amount);

        setNutrient(type, startValue - consumed);
        for (NutrientType other : NutrientType.VALUES)
        {
            if (other != type)
            {
                addNutrient(other, consumed * 1 / 6f);
            }
        }

        return consumed;
    }

    default boolean isMaxedOut()
    {
        return getNutrient(NITROGEN) == 1 && getNutrient(PHOSPHOROUS) == 1 && getNutrient(POTASSIUM) == 1;
    }

    default void saveNutrients(CompoundTag nbt)
    {
        nbt.putFloat("n", getNutrient(NITROGEN));
        nbt.putFloat("p", getNutrient(PHOSPHOROUS));
        nbt.putFloat("k", getNutrient(POTASSIUM));
    }

    default void loadNutrients(CompoundTag nbt)
    {
        setNutrient(NITROGEN, nbt.getFloat("n"));
        setNutrient(PHOSPHOROUS, nbt.getFloat("p"));
        setNutrient(POTASSIUM, nbt.getFloat("k"));
    }

    /**
     * Add tooltip info which is shown from both the farmland hoe overlay, <strong>and</strong> when looking at a crop planted above this block.
     * This is important, as it should only add information that wouldn't already be visible from the crop itself.
     */
    default void addTooltipInfo(Consumer<Component> text)
    {
        text.accept(Component.translatable("tfc.tooltip.farmland.nutrients", format(getNutrient(NutrientType.NITROGEN)), format(getNutrient(NutrientType.PHOSPHOROUS)), format(getNutrient(NutrientType.POTASSIUM))));
    }

    private String format(float value)
    {
        return String.format("%.2f", value * 100);
    }


}
