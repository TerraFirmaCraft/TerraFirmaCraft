package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryMetal;

public class WeatheringMetalSlab extends SlabBlock implements IClimateWeatheringBlock
{
    TFCWeatherState weatherState;
    RegistryMetal metal;

    public WeatheringMetalSlab(Properties properties, TFCWeatherState weatherState, RegistryMetal metal)
    {
        super(properties);
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
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.BLOCK_SLAB);
            }
            case EXPOSED -> {
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.EXPOSED_BLOCK_SLAB);
            }
            case WEATHERED -> {
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.WEATHERED_BLOCK_SLAB);
            }
        }
        return TFCBlocks.METALS.get(metal).get(Metal.BlockType.OXIDIZED_BLOCK_SLAB);
    }

    @Override
    public BlockState getNext(BlockState blockState)
    {
        return this.getNext().defaultBlockState()
            .setValue(TYPE, blockState.getValue(TYPE))
            .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED));
    }

    @Override
    public TFCWeatherState getAge()
    {
        return weatherState;
    }
}
