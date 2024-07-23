/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.plant.KrummholzBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;

public class KrummholzFeature extends Feature<KrummholzConfig>
{
    public KrummholzFeature(Codec<KrummholzConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<KrummholzConfig> context)
    {
        final KrummholzConfig config = context.config();
        final BlockState block = config.block().defaultBlockState();
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final int height = config.height().sample(context.random());

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        cursor.setWithOffset(pos, 0, -1, 0);

        final BlockState below = level.getBlockState(cursor);
        // todo: this should not exist, this should use a tag specified by the config, not three separate checks
        boolean validBlock = Helpers.isBlock(below, TFCTags.Blocks.TREE_GROWS_ON);
        if (config.spawnsOnStone())
            validBlock |= Helpers.isBlock(below, Tags.Blocks.STONES);
        if (config.spawnsOnGravel())
            validBlock |= Helpers.isBlock(below, Tags.Blocks.GRAVELS);
        if (!validBlock)
            return false;

        cursor.move(0, 1, 0);
        if (!block.canSurvive(level, pos) || !level.getFluidState(pos).isEmpty())
            return false;
        int maxHeight = 1;
        for (int i = 0; i < height; i++)
        {
            cursor.setWithOffset(pos, 0, i, 0);
            maxHeight = i + 1;
            if (!EnvironmentHelpers.isWorldgenReplaceable(level, cursor))
            {
                break;
            }
        }
        for (int i = 0; i < maxHeight; i++)
        {
            cursor.setWithOffset(pos, 0, i, 0);
            BlockState newState = i == maxHeight - 1 ? Helpers.setProperty(block, KrummholzBlock.TIP, true) : block;
            if (i == 0)
                newState = Helpers.setProperty(newState, KrummholzBlock.BOTTOM, true);
            level.setBlock(cursor, newState, 2);
        }

        return true;
    }
}
