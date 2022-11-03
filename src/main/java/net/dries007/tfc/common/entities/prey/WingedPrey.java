/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.EntityHelpers;

public class WingedPrey extends Prey
{
    public float flapping = 1f;
    public float oFlap;
    public float flap;
    public float oFlapSpeed;
    public float flapSpeed;
    private float nextFlap = 1f;

    public WingedPrey(EntityType<? extends Prey> type, Level level, TFCSounds.EntitySound sounds)
    {
        super(type, level, sounds);
    }

    @Override
    protected boolean isFlapping()
    {
        return flyDist > nextFlap;
    }

    @Override
    protected void onFlap()
    {
        nextFlap = flyDist + flapSpeed / 2.0F;
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        oFlap = flap;
        oFlapSpeed = flapSpeed;
        flapSpeed += (onGround ? -1.0F : 4.0F) * 0.3F;
        flapSpeed = Mth.clamp(flapSpeed, 0.0F, 1.0F);
        if (isPassenger()) flapSpeed = 0F;
        if (!onGround && flapping < 1.0F)
        {
            flapping = 1.0F;
        }

        flapping *= 0.9F;
        final Vec3 move = getDeltaMovement();
        if (!onGround && move.y < 0.0D)
        {
            setDeltaMovement(move.multiply(1.0D, 0.6D, 1.0D));
        }
        flap += flapping * 2.0F;
    }

    @Override
    public boolean causeFallDamage(float amount, float speed, DamageSource src)
    {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        return EntityHelpers.pluck(player, hand, this) ? InteractionResult.sidedSuccess(level.isClientSide) : super.mobInteract(player, hand);
    }
}
