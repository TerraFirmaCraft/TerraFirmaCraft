/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.EnumSet;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.ForgeHooks;

import net.dries007.tfc.common.entities.ThrownJavelin;
import net.dries007.tfc.common.items.JavelinItem;

/**
 * {@link net.minecraft.world.entity.ai.goal.RangedBowAttackGoal}
 */
public class JavelinAttackGoal<T extends Mob> extends Goal
{
    private final T mob;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public JavelinAttackGoal(T mob, double speed, float attackRadius)
    {
        this.mob = mob;
        speedModifier = speed;
        attackRadiusSqr = attackRadius * attackRadius;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        return mob.getTarget() != null && isHoldingJavelin();
    }

    protected boolean isHoldingJavelin()
    {
        return mob.isHolding(is -> is.getItem() instanceof JavelinItem);
    }

    @Override
    public boolean canContinueToUse()
    {
        return (canUse() || !mob.getNavigation().isDone()) && isHoldingJavelin();
    }

    @Override
    public void start()
    {
        super.start();
        mob.setAggressive(true);
    }

    @Override
    public void stop()
    {
        super.stop();
        mob.setAggressive(false);
        seeTime = 0;
        attackTime = -1;
        mob.stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick()
    {
        return true;
    }

    @Override
    public void tick()
    {
        LivingEntity livingentity = mob.getTarget();
        if (livingentity != null)
        {
            double distSquared = mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean seeing = mob.getSensing().hasLineOfSight(livingentity);
            boolean hasSeen = seeTime > 0;
            if (seeing != hasSeen) seeTime = 0;

            seeTime += seeing ? 1 : -1;

            if (!(distSquared > attackRadiusSqr) && seeTime >= 20)
            {
                mob.getNavigation().stop();
                ++strafingTime;
            }
            else
            {
                mob.getNavigation().moveTo(livingentity, speedModifier);
                strafingTime = -1;
            }

            if (strafingTime >= 20)
            {
                if (mob.getRandom().nextFloat() < 0.3D)
                {
                    strafingClockwise = !strafingClockwise;
                }

                if (mob.getRandom().nextFloat() < 0.3D)
                {
                    strafingBackwards = !strafingBackwards;
                }

                strafingTime = 0;
            }

            if (strafingTime > -1)
            {
                if (distSquared > (attackRadiusSqr * 0.75F))
                {
                    strafingBackwards = false;
                }
                else if (distSquared < (attackRadiusSqr * 0.25F))
                {
                    strafingBackwards = true;
                }

                mob.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
                mob.lookAt(livingentity, 30.0F, 30.0F);
            }
            else
            {
                mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }

            if (mob.isUsingItem())
            {
                if (!seeing && seeTime < -60)
                {
                    mob.stopUsingItem();
                }
                else if (seeing)
                {
                    if (mob.getTicksUsingItem() >= 20)
                    {
                        mob.stopUsingItem();
                        performRangedAttack(livingentity);
                        attackTime = mob.level.getDifficulty() == Difficulty.HARD ? 20 : 40;
                    }
                }
            }
            else if (--attackTime <= 0 && seeTime >= -60)
            {
                mob.startUsingItem(getHand());
            }

        }
    }

    public void performRangedAttack(LivingEntity target)
    {
        final ItemStack itemstack = getWeaponInHand();

        final ThrownJavelin javelin = new ThrownJavelin(mob.level, mob, itemstack);
        final double dx = target.getX() - mob.getX();
        final double dy = target.getY(1 / 3d) - javelin.getY();
        final double dz = target.getZ() - mob.getZ();
        final double dist = Math.sqrt(dx * dx + dz * dz);

        javelin.shoot(dx, dy + dist * 0.2F, dz, 1.6F, (14 - mob.level.getDifficulty().getId() * 4));
        mob.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
        mob.level.addFreshEntity(javelin);
    }

    public ItemStack getWeaponInHand()
    {
        return mob.getItemInHand(getHand());
    }

    public InteractionHand getHand()
    {
        return ProjectileUtil.getWeaponHoldingHand(mob, item -> item instanceof JavelinItem);
    }
}
