/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import java.util.function.IntFunction;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.util.Fertilizer;

public class FarmlandBlockEntity extends TFCBlockEntity
{
    private float nitrogen, phosphorous, potassium;

    public FarmlandBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.FARMLAND.get(), pos, state);
    }

    protected FarmlandBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        nitrogen = phosphorous = potassium = 0;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        nitrogen = nbt.getFloat("n");
        phosphorous = nbt.getFloat("p");
        potassium = nbt.getFloat("k");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("n", nitrogen);
        nbt.putFloat("p", phosphorous);
        nbt.putFloat("k", potassium);
        super.saveAdditional(nbt);
    }

    public void addHoeOverlayInfo(Level level, BlockPos pos, List<Component> text, boolean includeHydration, boolean includeNutrients)
    {
        if (includeHydration)
        {
            final int value = FarmlandBlock.getHydration(level, pos);
            final MutableComponent hydration = Helpers.translatable("tfc.tooltip.farmland.hydration", value);
            text.add(hydration);

        }

        if (includeNutrients)
        {
            text.add(Helpers.translatable("tfc.tooltip.farmland.nutrients", format(nitrogen), format(phosphorous), format(potassium)));
        }
    }

    private String format(float value)
    {
        return String.format("%.2f", value * 100);
    }


    /**
     * Consume up to {@code amount} of nutrient {@code type}.
     * Additionally, increase all other nutrients by 1/6 the consumed value (effectively, recovering 33% of the consumed nutrients)
     * @return The amount of nutrient {@code type} that was actually consumed.
     */
    public float consumeNutrientAndResupplyOthers(NutrientType type, float amount)
    {
        final float startValue = getNutrient(type);
        final float consumed = Math.min(startValue, amount);

        setNutrient(type, startValue - consumed);
        for (NutrientType other : NutrientType.VALUES)
        {
            if (other != type)
            {
                addNutrient(other, consumed * (1 / 6f));
            }
        }

        return consumed;
    }

    public float getNutrient(NutrientType type)
    {
        return switch (type)
            {
                case NITROGEN -> nitrogen;
                case PHOSPHOROUS -> phosphorous;
                case POTASSIUM -> potassium;
            };
    }

    public void addNutrients(Fertilizer fertilizer)
    {
        nitrogen = Math.min(1, nitrogen + fertilizer.getNitrogen());
        phosphorous = Math.min(1, phosphorous + fertilizer.getPhosphorus());
        potassium = Math.min(1, potassium + fertilizer.getPotassium());
        markForSync();
    }

    public void addNutrient(NutrientType type, float value)
    {
        setNutrient(type, getNutrient(type) + value);
    }

    public void setNutrient(NutrientType type, float value)
    {
        value = Mth.clamp(value, 0, 1);
        switch (type)
        {
            case NITROGEN -> nitrogen = value;
            case PHOSPHOROUS -> phosphorous = value;
            case POTASSIUM -> potassium = value;
        }
        markForSync();
    }

    public enum NutrientType
    {
        NITROGEN, PHOSPHOROUS, POTASSIUM;

        private static final NutrientType[] VALUES = values();
    }
}
