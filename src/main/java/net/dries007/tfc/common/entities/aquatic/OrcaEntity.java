package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class OrcaEntity extends DolphinEntity
{
    public OrcaEntity(EntityType<? extends DolphinEntity> type, World world)
    {
        super(type, world);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new BreatheAirGoal(this));
        goalSelector.addGoal(0, new FindWaterGoal(this));
        goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        goalSelector.addGoal(4, new LookRandomlyGoal(this));
        goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
        goalSelector.addGoal(6, new MeleeAttackGoal(this, 2.0F, true));
        // currently setting a target causes NPE
        //goalSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, TFCCodEntity.class, true));
        goalSelector.addGoal(8, new FollowBoatGoal(this));
        goalSelector.addGoal(9, new AvoidEntityGoal<>(this, GuardianEntity.class, 8.0F, 1.0D, 1.0D));
        targetSelector.addGoal(1, (new HurtByTargetGoal(this, GuardianEntity.class)).setAlertOthers());
    }
}
