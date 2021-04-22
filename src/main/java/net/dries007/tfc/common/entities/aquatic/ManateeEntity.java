package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.AquaticMovementController;

public class ManateeEntity extends TFCAbstractGroupFishEntity
{
    public ManateeEntity(EntityType<? extends AbstractGroupFishEntity> type, World worldIn)
    {
        super(type, worldIn);
        moveControl = new AquaticMovementController(this, true, 6);
    }

    @Override
    protected ItemStack getBucketItemStack()
    {
        return new ItemStack(Items.BONE);
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return SoundEvents.COD_FLOP;
    }
}
