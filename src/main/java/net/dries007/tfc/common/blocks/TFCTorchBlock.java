package net.dries007.tfc.common.blocks;


import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
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

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCTorchBlock extends TorchBlock implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;

    public TFCTorchBlock(ForgeBlockProperties properties, IParticleData particle)
    {
        super(properties.properties(), particle);
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
        if (!world.isClientSide())
        {
            ItemStack held = player.getItemInHand(hand);
            if (held.getItem().is(TFCTags.Items.CAN_BE_LIT_ON_TORCH))
            {
                held.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.TORCH.get()));
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        onRandomTick(world, pos, TFCBlocks.DEAD_TORCH.get().defaultBlockState());
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(worldIn, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            te.resetCounter();
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    public static void onRandomTick(ServerWorld world, BlockPos pos, BlockState placeState)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            if (!world.isClientSide() && te.getTicksSinceUpdate() > TFCConfig.SERVER.torchTicks.get() && TFCConfig.SERVER.torchTicks.get() > 0)
            {
                world.setBlockAndUpdate(pos, placeState);
            }
        }
    }
}
