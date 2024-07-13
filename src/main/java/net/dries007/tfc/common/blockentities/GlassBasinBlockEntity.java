/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.util.Helpers;

public class GlassBasinBlockEntity extends TFCBlockEntity
{
    public static void ticks(Level level, BlockPos pos, BlockState state, GlassBasinBlockEntity glass)
    {
        if (glass.animationTicks != -1)
        {
            if (glass.animationTicks++ > 100)
            {
                level.setBlockAndUpdate(pos, glass.state);

                Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                final var random = level.getRandom();
                Supplier<Vec3> supplier = () -> new Vec3(Mth.nextDouble(level.getRandom(), -0.005F, 0.005F), Mth.nextDouble(random, -0.005F, 0.005F), Mth.nextDouble(random, -0.005F, 0.005F));
                ParticleUtils.spawnParticlesOnBlockFace(level, pos, ParticleTypes.SMOKE, UniformInt.of(4, 10), Direction.UP, supplier, 0.6);
            }
        }
    }

    private BlockState state;
    private int animationTicks = -1;

    public GlassBasinBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.GLASS_BASIN.get(), pos, state);
    }

    public GlassBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.state = Blocks.AIR.defaultBlockState();
    }

    public int getAnimationTicks()
    {
        return animationTicks;
    }

    public void setGlassItem(ItemStack item)
    {
        if (item.getItem() instanceof BlockItem bi)
        {
            state = bi.getBlock().defaultBlockState();
        }
        this.animationTicks = 0;
        markForSync();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        this.state = NbtUtils.readBlockState(getBlockGetter(), tag.getCompound("glassState"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.put("glassState", NbtUtils.writeBlockState(state));
    }
}
