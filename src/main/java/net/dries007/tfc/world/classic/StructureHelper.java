/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.BlockRotationProcessor;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class StructureHelper
{
    /**
     * This is a copy of the method included in the Template class, with some key differences.
     * This will ignore TEs / Entities, and does less checks for bad usage, since it will only be used for tree worldgen
     * It will do an additional check that the block is replaceable; important for tree growth; as to not replace other blocks
     *
     * @param worldIn     the world
     * @param pos         the position
     * @param template    the template
     * @param placementIn the placement settings
     */
    public static void addStructureToWorld(World worldIn, BlockPos pos, Template template, PlacementSettings placementIn)
    {
        ITemplateProcessor templateProcessor = new BlockRotationProcessor(pos, placementIn);
        StructureBoundingBox structureboundingbox = placementIn.getBoundingBox();

        for (Template.BlockInfo template$blockinfo : template.blocks)
        {
            BlockPos blockpos = Template.transformedBlockPos(placementIn, template$blockinfo.pos).add(pos);
            Template.BlockInfo template$blockinfo1 = templateProcessor.processBlock(worldIn, blockpos, template$blockinfo);

            if (template$blockinfo1 != null)
            {
                Block block1 = template$blockinfo1.blockState.getBlock();

                if ((!placementIn.getIgnoreStructureBlock() || block1 != Blocks.STRUCTURE_BLOCK) && (structureboundingbox == null || structureboundingbox.isVecInside(blockpos)))
                {
                    IBlockState stateToPlace = template$blockinfo1.blockState.withMirror(placementIn.getMirror()).withRotation(placementIn.getRotation());
                    IBlockState stateToReplace = worldIn.getBlockState(blockpos);

                    if (stateToReplace.getMaterial().isReplaceable() || stateToReplace.getBlock() instanceof BlockLeavesTFC || stateToReplace.getBlock() instanceof BlockSaplingTFC)
                    {
                        worldIn.setBlockState(blockpos, stateToPlace, 2);
                    }
                }
            }
        }

        for (Template.BlockInfo template$blockinfo2 : template.blocks)
        {
            BlockPos blockpos1 = Template.transformedBlockPos(placementIn, template$blockinfo2.pos).add(pos);

            if (structureboundingbox == null || structureboundingbox.isVecInside(blockpos1))
            {
                worldIn.notifyNeighborsRespectDebug(blockpos1, template$blockinfo2.blockState.getBlock(), false);
            }

        }
    }

    /**
     * This only sets the properties used by ITreeGenerator.addStructureToWorld
     *
     * @return A default set of placement settings for tree generation
     */

    public static PlacementSettings getDefaultSettings()
    {
        return new PlacementSettings().setIgnoreStructureBlock(false);
    }

    /**
     * This only sets the properties used by ITreeGenerator.addStructureToWorld
     *
     * @param rand For generating random settings
     * @return A set of placement settings with random rotation
     */
    public static PlacementSettings getRandomSettings(Random rand)
    {
        return getDefaultSettings().setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)]);
    }
}
