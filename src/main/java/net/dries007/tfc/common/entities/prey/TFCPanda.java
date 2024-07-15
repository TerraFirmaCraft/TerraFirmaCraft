/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;

public class TFCPanda extends Panda
{
    public TFCPanda(EntityType<? extends Panda> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(goalSelector, 2); // breeding
        goalSelector.addGoal(6, new PandaAvoidGoal(this));
    }

    /**
     * We still set this properly just to let spawn egg clicking / other mod interactions work correctly.
     */
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent)
    {
        final Panda panda = TFCEntities.PANDA.get().create(level);
        if (panda != null)
        {
            if (parent instanceof Panda pandaParent)
            {
                panda.setGeneFromParents(this, pandaParent);
            }
            panda.setAttributes();
        }
        return panda;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (isFood(player.getItemInHand(hand)) && isBaby())
        {
            return InteractionResult.PASS; // prevents aging up behavior
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canFallInLove()
    {
        return false;
    }

    public static class PandaAvoidGoal extends TFCAvoidEntityGoal<PathfinderMob>
    {
        private final Panda panda;

        public PandaAvoidGoal(Panda panda)
        {
            super(panda, PathfinderMob.class, 8f, 2, 2, TFCTags.Entities.HUNTS_LAND_PREY);
            this.panda = panda;
        }

        public boolean canUse()
        {
            return this.panda.isWorried() && this.panda.canPerformAction() && super.canUse();
        }
    }
}
