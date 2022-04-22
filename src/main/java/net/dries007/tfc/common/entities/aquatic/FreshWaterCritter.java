package net.dries007.tfc.common.entities.aquatic;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FreshWaterCritter extends AquaticCritter
{
    public FreshWaterCritter(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(Fluids.WATER);
    }
}
