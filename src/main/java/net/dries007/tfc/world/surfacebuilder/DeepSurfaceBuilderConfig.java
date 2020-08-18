/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.datafixers.Dynamic;

public class DeepSurfaceBuilderConfig extends SurfaceBuilderConfig
{
    public static DeepSurfaceBuilderConfig deserialize(Dynamic<?> configFactory)
    {
        BlockState topState = configFactory.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState underState = configFactory.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState underwaterState = configFactory.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState deepUnderState = configFactory.get("deep_under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new DeepSurfaceBuilderConfig(topState, underState, deepUnderState, underwaterState);
    }

    private final BlockState deepUnderMaterial;

    public DeepSurfaceBuilderConfig(BlockState topMaterial, BlockState underMaterial, BlockState deepUnderMaterial, BlockState underWaterMaterial)
    {
        super(topMaterial, underMaterial, underWaterMaterial);
        this.deepUnderMaterial = deepUnderMaterial;
    }

    public BlockState getDeepUnderMaterial()
    {
        return deepUnderMaterial;
    }
}
