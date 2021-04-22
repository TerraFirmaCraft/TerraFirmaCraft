package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class OrcaEntity extends DolphinEntity
{
    public OrcaEntity(EntityType<? extends DolphinEntity> type, World worldIN)
    {
        super(type, worldIN);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new BreatheAirGoal(this));
        this.goalSelector.addGoal(0, new FindWaterGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 2.0F, true));
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, GuardianEntity.class, 8.0F, 1.0D, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, GuardianEntity.class)).setAlertOthers());
    }
}
