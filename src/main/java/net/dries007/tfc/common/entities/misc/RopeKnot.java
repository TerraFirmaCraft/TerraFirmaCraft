/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.RopeAnchorBlock;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class RopeKnot extends LeashFenceKnotEntity implements Leashable
{
    @Nullable
    public static RopeKnot getNewKnotAtLocation(Level level, BlockPos pos)
    {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        for (RopeKnot entity : level.getEntitiesOfClass(RopeKnot.class, new AABB(x - 1.0, y - 1.0, z - 1.0, x + 1.0, y + 1.0, z + 1.0)))
        {
            if (entity.getPos().equals(pos))
            {
                return null; // return null if there's already something here.
            }
        }

        final RopeKnot newEntity = new RopeKnot(level, pos);
        level.addFreshEntity(newEntity);
        return newEntity;
    }

    @Nullable
    private Leashable.LeashData leashData;

    public RopeKnot(EntityType<RopeKnot> type, Level level)
    {
        super(type, level);
    }

    public RopeKnot(Level level, BlockPos pos)
    {
        super(level, pos); // sets position with incorrect entity type, so we override below
    }

    @Override
    public @Nullable ItemEntity spawnAtLocation(ItemStack stack, float y)
    {
        if (stack.getItem() == Items.LEAD)
            return null;
        return super.spawnAtLocation(stack);
    }

    @Override
    public EntityType<?> getType()
    {
        return TFCEntities.ROPE_KNOT.get();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getPickResult()
    {
        return new ItemStack(TFCItems.ROPE);
    }

    @Override
    public boolean survives()
    {
        return level().getBlockState(pos).getBlock() instanceof RopeAnchorBlock;
    }

    @Override
    public @Nullable LeashData getLeashData()
    {
        return leashData;
    }

    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData)
    {
        this.leashData = leashData;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        this.writeLeashData(compound, this.leashData);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        this.leashData = this.readLeashData(compound);
    }

    @Override
    public void remove(Entity.RemovalReason reason)
    {
        if (!this.level().isClientSide && reason.shouldDestroy() && this.isLeashed())
            this.dropLeash(true, true);

        super.remove(reason);
    }

    @Override
    public void elasticRangeLeashBehaviour(Entity leashHolder, float distance)
    {
        // no-op
    }
}
