/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.util.data.Fertilizer;

import static net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType.*;

public class FarmlandBlockEntity extends TFCBlockEntity implements IFarmland
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
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        loadNutrients(nbt);
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        saveNutrients(nbt);
        super.saveAdditional(nbt, provider);
    }

    public void addHoeOverlayInfo(Level level, BlockPos pos, Consumer<Component> text, boolean includeHydration, boolean includeNutrients)
    {
        if (includeHydration)
        {
            final int value = FarmlandBlock.getHydration(level, pos);
            final MutableComponent hydration = Component.translatable("tfc.tooltip.farmland.hydration", value);
            text.accept(hydration);
        }

        if (includeNutrients)
        {
            addTooltipInfo(text);
        }
    }

    @Override
    public float getNutrient(NutrientType type)
    {
        return switch (type)
            {
                case NITROGEN -> nitrogen;
                case PHOSPHOROUS -> phosphorous;
                case POTASSIUM -> potassium;
            };
    }

    @Override
    public void setNutrient(NutrientType type, float value)
    {
        setNutrientWithoutSync(type, value);
        markForSync();
    }

    @Override
    public void addNutrients(Fertilizer fertilizer, float multiplier)
    {
        // Override to not send three sync packets
        setNutrientWithoutSync(NITROGEN, getNutrient(NITROGEN) + (fertilizer.nitrogen() * multiplier));
        setNutrientWithoutSync(PHOSPHOROUS, getNutrient(PHOSPHOROUS) + (fertilizer.phosphorus() * multiplier));
        setNutrientWithoutSync(POTASSIUM, getNutrient(POTASSIUM) + (fertilizer.potassium() * multiplier));
        markForSync();
    }

    private void setNutrientWithoutSync(NutrientType type, float value)
    {
        value = Mth.clamp(value, 0, 1);
        switch (type)
        {
            case NITROGEN -> nitrogen = value;
            case PHOSPHOROUS -> phosphorous = value;
            case POTASSIUM -> potassium = value;
        }
    }

    public enum NutrientType
    {
        NITROGEN, PHOSPHOROUS, POTASSIUM;

        public static final NutrientType[] VALUES = values();
    }
}
