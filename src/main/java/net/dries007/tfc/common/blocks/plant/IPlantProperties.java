package net.dries007.tfc.common.blocks.plant;

import net.minecraft.state.IntegerProperty;

import net.dries007.tfc.util.calendar.Month;

public interface IPlantProperties
{
    /**
     * Get the current value of the stage property for the given month.
     */
    int getMonthStage(Month month);

    /**
     * Get the stage property used for the growth of this plant
     */
    IntegerProperty getStageProperty();

    /**
     * A multiplier which represents how this plant influences entity movement through it
     */
    float getEntitySpeedModifier();
}
