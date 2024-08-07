package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryMetal;

public class WeatheringMetalStairs extends StairBlock implements IClimateWeatheringBlock
{
    TFCWeatherState weatherState;
    RegistryMetal metal;

    public WeatheringMetalStairs(BlockState state,  Properties properties, TFCWeatherState weatherState, RegistryMetal metal)
    {
        super(state, properties);
        this.weatherState = weatherState;
        this.metal = metal;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource source) {
        this.applyChangeOverTime(state, serverLevel, pos, source);
    }

    public boolean isRandomlyTicking(BlockState state) {
        return this.getAge().hasNext() && getMaterialModifier() > 0;
    }

    @Override
    public Block getNext()
    {
        return getByWeatherState(weatherState.getNext(), metal).get();
    }

    @Override
    public Block getPrevious()
    {
        return getByWeatherState(weatherState.getPrevious(), metal).get();
    }

    @Override
    public float getMaterialModifier()
    {
        return 1-metal.weathering().getResistance();
    }

    public static TFCBlocks.Id<Block> getByWeatherState(TFCWeatherState weatherState, RegistryMetal metal)
    {
        switch(weatherState){
            case UNAFFECTED -> {
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.BLOCK_STAIRS);
            }
            case EXPOSED -> {
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.EXPOSED_BLOCK_STAIRS);
            }
            case WEATHERED -> {
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.WEATHERED_BLOCK_STAIRS);
            }
        }
        return TFCBlocks.METALS.get(metal).get(Metal.BlockType.OXIDIZED_BLOCK_STAIRS);
    }

    @Override
    public BlockState getNext(BlockState blockState)
    {
        return this.getNext().defaultBlockState()
            .setValue(FACING, blockState.getValue(FACING))
            .setValue(HALF, blockState.getValue(HALF))
            .setValue(SHAPE, blockState.getValue(SHAPE))
            .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED));
    }

    @Override
    public TFCWeatherState getAge()
    {
        return weatherState;
    }
}
