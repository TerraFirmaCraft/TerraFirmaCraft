/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;

public class NormalTreeFeature extends Feature<NormalTreeConfig>
{
    public NormalTreeFeature()
    {
        super(NormalTreeConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NormalTreeConfig config)
    {/*
        TemplateManager manager = ((ServerWorld) worldIn).getStructureTemplateManager();
        ResourceLocation base = new ResourceLocation(tree.getRegistryName() + "/base");
        ResourceLocation overlay = new ResourceLocation(tree.getRegistryName() + "/overlay");

        Template structureBase = manager.get(world.getMinecraftServer(), base);
        Template structureOverlay = manager.get(world.getMinecraftServer(), overlay);

        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return;
        }

        int height = heightMin + (heightRange > 0 ? rand.nextInt(heightRange) : 0);

        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, height, -size.getZ() / 2);

        StructureHelper.addStructureToWorld(world, pos, structureBase, settingsFull);
        if (structureOverlay != null)
        {
            StructureHelper.addStructureToWorld(world, pos, structureOverlay, settingsWeak);
        }

        final IBlockState log = BlockLogTFC.get(tree).getDefaultState().withProperty(PLACED, false);
        for (int i = 0; i < height; i++)
            world.setBlockState(pos.add(size.getX() / 2, i - height, size.getZ() / 2), log);
            */
        return false;
    }
}
