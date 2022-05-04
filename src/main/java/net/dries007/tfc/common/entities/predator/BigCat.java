package net.dries007.tfc.common.entities.predator;

import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.predator.PredatorAi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.Optional;

public class BigCat extends Predator {

    public Vec3 location = null;
    public Vec3 prevLocation = null;
    public float walkProgress = 0;
    public float walkAnimationLength = 20f; //Length of walk animation in ticks
    public final double crouchSpeedMod;
    public final double sprintSpeedMod;
    public final double attackDistanceSquared;


    public static BigCat createDiurnalCat(EntityType<? extends BigCat> type, Level level)
    {
        return new BigCat(type, level, false);
    }

    public BigCat(EntityType<? extends Predator> type, Level level, boolean diurnal) {
        this(type, level, diurnal, 8, 36, 0.8, 1.5);
    }
    public BigCat(EntityType<? extends Predator> type, Level level, boolean diurnal, int attackAnimLength, double attackDistanceSquared, double crouchSpeedMod, double sprintSpeedMod) {
        super(type, level, diurnal, attackAnimLength);
        this.crouchSpeedMod = crouchSpeedMod;
        this.sprintSpeedMod = sprintSpeedMod;
        this.attackDistanceSquared = attackDistanceSquared;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(8, new BigCatAttackGoal(this));
        this.targetSelector.addGoal(1, new BigCatNearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(1, new BigCatNearestAttackableTargetGoal<>(this, Pig.class, false));
        super.registerGoals();
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.ATTACK_DAMAGE, 5);
    }

    @Override
    public void tick()
    {

        //Variable for smooth walk animation
        prevLocation = location;
        location = this.position();
        if (walkProgress >= walkAnimationLength) {
            walkProgress = 0F;
        }
        if (this.isMoving() || walkProgress > 0F) {
        walkProgress++;
        }
        super.tick();
    }

    public boolean isMoving() {
        return !(location == prevLocation);
    }

    static class BigCatNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final BigCat bigCat;

        public BigCatNearestAttackableTargetGoal(BigCat cat, Class<T> target, boolean bool) {
            super(cat, target, bool);
            bigCat = cat;
        }

        public boolean canUse() {
            return !this.bigCat.isSleeping() && super.canUse();
        }


    }

    @Override
    public void customServerAiStep() {

        double speedMod = this.getMoveControl().getSpeedModifier();
        if (this.getMoveControl().hasWanted() && speedMod == this.crouchSpeedMod) {
            this.setPose(Pose.CROUCHING);
            this.setSprinting(false);
        } else if (speedMod == this.sprintSpeedMod) {
            this.setPose(Pose.STANDING);
            this.setSprinting(true);
        } else {
            this.setSprinting(false);
            this.setPose(Pose.STANDING);
        }
        super.customServerAiStep();
    }

    static class BigCatAttackGoal extends OcelotAttackGoal {
        private final BigCat bigCat;
        @Nullable
        private LivingEntity target;
        private int attackTime;

        public BigCatAttackGoal(BigCat bigCat) {
            super((Mob)bigCat);
            this.bigCat = bigCat;
        }

        @Override
        public void tick() {
            this.target = this.bigCat.getTarget();
            //this.target won't be null because super.canUse checks against it
            this.bigCat.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            double catSize = (double) (this.bigCat.getBbWidth() * 2.0F * this.bigCat.getBbWidth() * 2.0F);
            double targetDistance = this.bigCat.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            double speedMod = 1.0;
            if (targetDistance > catSize && targetDistance < bigCat.attackDistanceSquared) {
                speedMod = bigCat.sprintSpeedMod;
            } else if (targetDistance < 225.0D) { //If within 15 blocks of target
                speedMod = bigCat.crouchSpeedMod;
            }
            this.bigCat.getNavigation().moveTo(this.target, speedMod);
            this.attackTime = Math.max(this.attackTime - 1, 0);
            if (!(targetDistance > catSize)) {
                if (this.attackTime <= 0) {
                    this.attackTime = 20;
                    this.bigCat.doHurtTarget(this.target);
                }
            }
        }
    }
}
