package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.dries007.tfc.util.Helpers;

@Mixin(ShovelItem.class)
public abstract class ShovelMixin extends DiggerItem
{
    public ShovelMixin(Tier tier, TagKey<Block> blocks, Properties properties)
    {
        super(tier, blocks, properties);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity)
    {
        if (state.getBlock() instanceof SnowLayerBlock)
        {
            return true;
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }
}
