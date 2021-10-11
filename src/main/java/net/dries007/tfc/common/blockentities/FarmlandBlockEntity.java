/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.soil.FarmlandBlock;

public class FarmlandBlockEntity extends TFCBlockEntity
{
    private float nitrogen, phosphorous, potassium;
    @Nullable private Integer hydration; // Cached, not generally valid. Only used for non-critical situations (like hoe overlays)

    public FarmlandBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.FARMLAND.get(), pos, state);
    }

    protected FarmlandBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        nitrogen = phosphorous = potassium = 0;
        hydration = null;
    }

    @Override
    public void load(CompoundTag nbt)
    {
        nitrogen = nbt.getFloat("n");
        phosphorous = nbt.getFloat("p");
        potassium = nbt.getFloat("k");
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putFloat("n", nitrogen);
        nbt.putFloat("p", phosphorous);
        nbt.putFloat("k", potassium);
        return super.save(nbt);
    }

    public void addHoeOverlayInfo(Level level, BlockPos pos, List<Component> text, @Nullable IntFunction<Component> hydrationValidity, boolean includeNutrients)
    {
        final int value = getCachedHydration(level, pos);
        final MutableComponent hydration = new TranslatableComponent("tfc.tooltip.farmland.hydration", value);
        if (hydrationValidity != null)
        {
            hydration.append(hydrationValidity.apply(value));
        }

        text.add(hydration);
        if (includeNutrients)
        {
            text.add(new TranslatableComponent("tfc.tooltip.farmland.nutrients", nitrogen, phosphorous, potassium));
        }
    }

    private int getCachedHydration(LevelAccessor level, BlockPos pos)
    {
        if (hydration == null)
        {
            hydration = FarmlandBlock.getHydration(level, pos);
        }
        return hydration;
    }
}
