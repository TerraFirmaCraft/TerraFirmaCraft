package net.dries007.tfc.common.blocks;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCWallTorchBlock extends WallTorchBlock implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;

    public TFCWallTorchBlock(ForgeBlockProperties properties, IParticleData particleData)
    {
        super(properties.properties(), particleData);
        this.properties = properties;
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        return TFCBlocks.TORCH.get().use(state, world, pos, player, hand, result);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        TFCTorchBlock.onRandomTick(world, pos, TFCBlocks.DEAD_WALL_TORCH.get().defaultBlockState());
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TFCBlocks.TORCH.get().setPlacedBy(worldIn, pos, state, placer, stack);
    }
}
