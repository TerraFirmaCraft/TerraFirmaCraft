/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.surfacebuilders;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.datafixers.Dynamic;

public class TFCSurfaceBuilderConfig extends SurfaceBuilderConfig
{
    public static TFCSurfaceBuilderConfig deserialize(Dynamic<?> dynamic)
    {
        BlockState top = dynamic.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState under = dynamic.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState underwater = dynamic.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        int soilLayers = dynamic.get("soil_layers").asInt(3);
        return new TFCSurfaceBuilderConfig(top, under, underwater, soilLayers);
    }

    private final int soilLayers;

    public TFCSurfaceBuilderConfig(BlockState topMaterial, BlockState underMaterial, BlockState underWaterMaterial, int soilLayers)
    {
        super(topMaterial, underMaterial, underWaterMaterial);
        this.soilLayers = soilLayers;
    }

    public int getSoilLayersForHeight(int y, Random random)
    {
        int maxHeight = 140 + random.nextInt(3) - random.nextInt(3);
        if (y > maxHeight)
        {
            return 0;
        }
        return (int) MathHelper.clamp(0.08f * (maxHeight - y) * soilLayers, 1, soilLayers);
    }
}
