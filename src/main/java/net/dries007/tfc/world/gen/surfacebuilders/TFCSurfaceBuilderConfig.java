/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.surfacebuilders;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.datafixers.Dynamic;

public class TFCSurfaceBuilderConfig extends SurfaceBuilderConfig
{
    public static TFCSurfaceBuilderConfig deserialize(Dynamic<?> dynamic)
    {
        BlockState top = dynamic.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState under = dynamic.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState underwater = dynamic.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        int soilLayers = dynamic.get("soilLayers").asInt(3);
        return new TFCSurfaceBuilderConfig(top, under, underwater, soilLayers);
    }

    private final int soilLayers;

    public TFCSurfaceBuilderConfig(BlockState topMaterial, BlockState underMaterial, BlockState underWaterMaterial, int soilLayers)
    {
        super(topMaterial, underMaterial, underWaterMaterial);
        this.soilLayers = soilLayers;
    }

    public int getSoilLayers()
    {
        return soilLayers;
    }
}
