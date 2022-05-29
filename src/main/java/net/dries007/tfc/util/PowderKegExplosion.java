/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.*;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PowderKegExplosion extends Explosion
{

    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity source;
    private final float size;

    public PowderKegExplosion(Level level, Entity entity, double x, double y, double z, float size)
    {
        super(level, entity, x, y, z, size, false, BlockInteraction.NONE);
        this.level = level;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.source = entity;
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     *
     * (Forgive the Mojang copypasta)
     */
    @Override
    public void finalizeExplosion(boolean spawnParticles)
    {
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        if (size >= 2.0F)
        {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0d, 0.d, 0.0d);
        }else
        {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
        }

        List<BlockPos> affectedBlockPositions = this.getToBlow();
        ObjectArrayList<Pair<ItemStack, BlockPos>> allDrops = new ObjectArrayList<>();
        Collections.shuffle(affectedBlockPositions, this.level.random);

        for (BlockPos blockpos : affectedBlockPositions)
        {
            BlockState blockstate = level.getBlockState(blockpos);
            Block block = blockstate.getBlock();

            if (spawnParticles)
            {
                double d0 = ((float) blockpos.getX() + this.level.random.nextFloat());
                double d1 = ((float) blockpos.getY() + this.level.random.nextFloat());
                double d2 = ((float) blockpos.getZ() + this.level.random.nextFloat());
                double d3 = d0 - this.x;
                double d4 = d1 - this.y;
                double d5 = d2 - this.z;
                double d6 = Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5));
                d3 = d3 / d6;
                d4 = d4 / d6;
                d5 = d5 / d6;
                double d7 = 0.5d / (d6 / (double) this.size + 0.1d);
                d7 = d7 * (double) (this.level.random.nextFloat() * this.level.random.nextFloat() + 0.3f);
                d3 = d3 * d7;
                d4 = d4 * d7;
                d5 = d5 * d7;
                level.addParticle(ParticleTypes.EXPLOSION, (d0 + this.x) / 2.0d, (d1 + this.y) / 2.0d, (d2 + this.z) / 2.0d, d3, d4, d5);
                level.addParticle(ParticleTypes.EXPLOSION, d0, d1, d2, d3, d4, d5);
            }

            if (blockstate.getMaterial() != Material.AIR)
            {
                BlockPos blockpos1 = blockpos.immutable();
                this.level.getProfiler().push("explosion_blocks");
                if (blockstate.canDropFromExplosion(this.level, blockpos, this) && this.level instanceof ServerLevel) {
                    BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
                    LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);

                    blockstate.getDrops(lootcontext$builder).forEach((p_46074_) -> {
                        addBlockDrops(allDrops, p_46074_, blockpos1);
                    });
                }

                blockstate.onBlockExploded(this.level, blockpos, this);
                this.level.getProfiler().pop();
                /*if (isSmall)
                {
                    block.dropBlockAsItemWithChance(this.level, blockpos, blockstate, 1f, 0);
                }
                else
                {
                    // noinspection deprecation
                    if(!this.level.isClientSide()) {
                        List<ItemStack> drops = block.getDrops(blockstate, (ServerLevel) this.level, blockpos, source, 0, 0);
                        float chance = ForgeEventFactory.fireBlockHarvesting(drops, this.level, blockpos, blockstate, 0, 1f, false, null);
                        if (this.level.random.nextFloat() <= chance) {
                            for (ItemStack stack : drops) {
                                //noinspection all
                                allDrops.add(stack); //addAll is unsupported
                            }
                        }
                    }
                }
                block.onBlockExploded(blockstate, this.level, blockpos, this);*/
            }

            for(Pair<ItemStack, BlockPos> pair : allDrops) {
                Block.popResource(this.level, pair.getSecond(), pair.getFirst());
            }

        }

    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> allDrops, ItemStack drop, BlockPos dropPos) {
        int i = allDrops.size();

        for(int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = allDrops.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, drop)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, drop, 16);
                allDrops.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (drop.isEmpty()) {
                    return;
                }
            }
        }

        allDrops.add(Pair.of(drop, dropPos));
    }

}