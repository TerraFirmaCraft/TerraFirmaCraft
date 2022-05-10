package net.dries007.tfc.common.entities.predator;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class FelinePredator extends Predator
{
    public final double CROUCH_SPEED_MOD;
    public final double SPRINT_SPEED_MOD;
    public final double ATTACK_DISTANCE_SQUARED;

    public static FelinePredator createNocturnal(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false);
    }

    public FelinePredator(EntityType<? extends Predator> type, Level level, boolean diurnal)
    {
        this(type, level, diurnal, 8, 20, 36, 0.8, 1.5);
    }
    public FelinePredator(EntityType<? extends Predator> type, Level level, boolean diurnal, int attackAnimLength, int walkAnimationLength, double attackDistanceSquared, double crouchSpeedMod, double sprintSpeedMod)
    {
        super(type, level, diurnal, attackAnimLength, walkAnimationLength);
        CROUCH_SPEED_MOD = crouchSpeedMod;
        SPRINT_SPEED_MOD = sprintSpeedMod;
        ATTACK_DISTANCE_SQUARED = attackDistanceSquared;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.ATTACK_DAMAGE, 7);
    }

}