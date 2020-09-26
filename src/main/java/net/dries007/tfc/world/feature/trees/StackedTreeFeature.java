package net.dries007.tfc.world.feature.trees;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import com.mojang.serialization.Codec;

public class StackedTreeFeature extends TreeFeature<StackedTreeConfig>
{
    public StackedTreeFeature(Codec<StackedTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator chunkGenerator, Random random, BlockPos pos, StackedTreeConfig config)
    {
        if (!isValidLocation(worldIn, pos) || !isAreaClear(worldIn, pos, config.radius, 2))
        {
            return false;
        }

        final TemplateManager manager = getTemplateManager(worldIn);
        final PlacementSettings settings = getPlacementSettings(new ChunkPos(pos), random);
        final BlockPos centerVariation = getCenterVariation(new BlockPos(config.trunk.width, 0, config.trunk.width), random);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().set(pos);

        // Trunk first
        int trunkHeight = placeTrunk(worldIn, pos, centerVariation, random, config.trunk);
        mutablePos.move(0, trunkHeight, 0);

        for (StackedTreeConfig.Layer layer : config.layers)
        {
            // Place each layer
            int layerCount = layer.getCount(random);
            for (int i = 0; i < layerCount; i++)
            {
                final ResourceLocation structureId = layer.templates.get(random.nextInt(layer.templates.size()));
                final Template structure = manager.getOrCreate(structureId);
                placeTemplateInWorld(structure, settings, worldIn, mutablePos.subtract(getCenteredOffset(structure.getSize(), centerVariation, settings)));
                mutablePos.move(0, structure.getSize().getY(), 0);
            }
        }
        return true;
    }
}
