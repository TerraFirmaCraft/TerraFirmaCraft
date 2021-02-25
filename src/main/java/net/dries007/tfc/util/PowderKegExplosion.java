/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class PowderKegExplosion extends Explosion
{

    public PowderKegExplosion(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size, false, true);
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     *
     * (Forgive the Mojang copypasta)
     */
    @Override
    public void doExplosionB(boolean spawnParticles)
    {
        world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f);

        if (size >= 2.0F)
        {
            world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0d, 0.d, 0.0d);
        }

        for (BlockPos blockpos : affectedBlockPositions)
        {
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (spawnParticles)
            {
                double d0 = ((float) blockpos.getX() + world.rand.nextFloat());
                double d1 = ((float) blockpos.getY() + world.rand.nextFloat());
                double d2 = ((float) blockpos.getZ() + world.rand.nextFloat());
                double d3 = d0 - x;
                double d4 = d1 - y;
                double d5 = d2 - z;
                double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                d3 = d3 / d6;
                d4 = d4 / d6;
                d5 = d5 / d6;
                double d7 = 0.5d / (d6 / (double) size + 0.1d);
                d7 = d7 * (double) (world.rand.nextFloat() * world.rand.nextFloat() + 0.3f);
                d3 = d3 * d7;
                d4 = d4 * d7;
                d5 = d5 * d7;
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + x) / 2.0d, (d1 + y) / 2.0d, (d2 + z) / 2.0d, d3, d4, d5);
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
            }

            if (iblockstate.getMaterial() != Material.AIR)
            {
                if (block.canDropFromExplosion(this))
                {
                    block.dropBlockAsItemWithChance(world, blockpos, world.getBlockState(blockpos), 1f, 0); // 100% chance
                }
                block.onBlockExploded(world, blockpos, this);
            }
        }
    }

}
