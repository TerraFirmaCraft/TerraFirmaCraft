package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.ai.LayEggBlockGoal;

public class TFCTurtleEntity extends AbstractTurtleEntity
{
    public TFCTurtleEntity(EntityType<? extends TurtleEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(1, new LayEggBlockGoal(this, 1.0D, TFCBlocks.PENGUIN_EGG));
    }
}
