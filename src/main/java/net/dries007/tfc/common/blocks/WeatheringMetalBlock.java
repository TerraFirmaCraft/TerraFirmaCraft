package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryMetal;

public class WeatheringMetalBlock extends Block implements IClimateWeatheringBlock
{
    static final float BASE_METAL_CHANCE = 0.015f;
    TFCWeatherState weatherState;
    RegistryMetal metal;

    public WeatheringMetalBlock(Properties properties, TFCWeatherState weatherState, RegistryMetal metal)
    {
        super(properties);
        this.weatherState = weatherState;
        this.metal = metal;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < BASE_METAL_CHANCE) {
            this.applyChangeOverTime(state, serverLevel, pos, random);
        }
    }

    public boolean isRandomlyTicking(BlockState state) {
        return this.getAge().hasNext() && metal.weathering() != Metal.WeatheringType.NONE;
    }

    @Override
    public Block getNext()
    {
        return metal.getFullBlock(weatherState.getNext()).get();
    }

    @Override
    public Block getPrevious()
    {
        return metal.getFullBlock(weatherState.getPrevious()).get();
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
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.BLOCK);
            }
            case EXPOSED -> {
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.EXPOSED_BLOCK);
            }
            case WEATHERED -> {
                return TFCBlocks.METALS.get(metal).get(Metal.BlockType.WEATHERED_BLOCK);
            }
        }
        return TFCBlocks.METALS.get(metal).get(Metal.BlockType.OXIDIZED_BLOCK);
    }

    @Override
    public BlockState getNext(BlockState blockState)
    {
        return this.getNext().defaultBlockState();
    }

    @Override
    public BlockState getPrevious(BlockState state)
    {
        return this.getPrevious().defaultBlockState();
    }

    @Override
    public TFCWeatherState getAge()
    {
        return weatherState;
    }
}
