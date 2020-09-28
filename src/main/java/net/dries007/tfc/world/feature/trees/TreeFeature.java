/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

public abstract class TreeFeature<C extends IFeatureConfig> extends Feature<C>
{
    protected TreeFeature(Codec<C> codec)
    {
        super(codec);
    }

    protected boolean isValidLocation(IWorld worldIn, BlockPos pos)
    {
        BlockState stateDown = worldIn.getBlockState(pos.below());
        if (!TFCTags.Blocks.TREE_GROWS_ON.contains(stateDown.getBlock()))
        {
            return false;
        }

        BlockState stateAt = worldIn.getBlockState(pos);
        return stateAt.getBlock() instanceof SaplingBlock || stateAt.isAir(worldIn, pos);
    }

    protected boolean isAreaClear(IWorld world, BlockPos pos, int radius, int height)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int y = 0; y < height; y++)
        {
            boolean passed = true;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos);
                    mutablePos.move(x, y, z);
                    BlockState stateAt = world.getBlockState(mutablePos);
                    if (!stateAt.isAir(world, mutablePos))
                    {
                        passed = false;
                        break;
                    }
                }
                if (!passed)
                {
                    break;
                }
            }
            if (passed)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the offset for a template such that under the transformation applied by settings, the origin is set to the center of the template.
     * This should be subtracted from the desired position when called to generate.
     */
    protected BlockPos getTransformedCenter(BlockPos size, PlacementSettings settings)
    {
        final BlockPos center = new BlockPos((size.getX() - 1) / 2, 0, (size.getZ() - 1) / 2);
        return Template.transform(new BlockPos((size.getX() - 1) / 2, 0, (size.getZ() - 1) / 2), settings.getMirror(), settings.getRotation(), settings.getRotationPivot());
    }
}