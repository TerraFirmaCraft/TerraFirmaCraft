package net.dries007.tfc.common.blocks.devices;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.common.tileentity.GrillTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.TFCDamageSources;

import static net.minecraft.util.ActionResultType.PASS;
import static net.minecraft.util.ActionResultType.SUCCESS;


public class GrillBlock extends FirepitBlock
{
    private static final VoxelShape GRILL_SHAPE = VoxelShapes.or(
        box(2, 9.5, 3, 14, 10, 13),
        box(2, 0, 13, 3, 11, 14),
        box(13, 0, 13, 14, 11, 14),
        box(2, 0, 2, 3, 11, 3),
        box(13, 0, 2, 14, 11, 3));

    public GrillBlock(ForgeBlockProperties properties)
    {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (world.isClientSide()) return PASS;
        if (hand.equals(Hand.OFF_HAND)) return PASS;
        ItemStack stack = player.getItemInHand(hand);
        boolean lit = state.getValue(LIT);
        if (stack.isEmpty() && player.isShiftKeyDown())
        {
            if (lit)//can't take stuff out if it's lit
            {
                player.hurt(TFCDamageSources.GRILL, 1.0F);
                Helpers.playSound(world, pos, SoundEvents.LAVA_EXTINGUISH);
            }
            else
            {
                convertGrillToFirepit(world, pos);
            }
            return SUCCESS;
        }
        else if (stack.getItem().is(TFCTags.Items.EXTINGUISHER))
        {
            tryExtinguish(world, pos, state);
            return SUCCESS;
        }
        else
        {
            GrillTileEntity te = Helpers.getTileEntity(world, pos, GrillTileEntity.class);
            if (te != null && player instanceof ServerPlayerEntity)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
                Helpers.playSound(world, pos, SoundEvents.SOUL_SAND_STEP);
                return SUCCESS;
            }
        }
        return PASS;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        makeBaseEffects(state, world, pos, rand, 0.8D);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.or(GRILL_SHAPE, BASE_SHAPE);
    }

    private static void convertGrillToFirepit(World world, BlockPos pos)
    {
        GrillTileEntity grill = Helpers.getTileEntity(world, pos, GrillTileEntity.class);
        if (grill != null)
        {
            Helpers.spawnItem(world, pos, new ItemStack(TFCItems.WROUGHT_IRON_GRILL.get()));
            Helpers.playSound(world, pos, SoundEvents.CHAIN_BREAK);
            List<ItemStack> logs = grill.getLogs();
            float[] fields = grill.getFields();
            grill.onRemoveGrill();

            world.setBlock(pos, TFCBlocks.FIREPIT.get().defaultBlockState().setValue(FirepitBlock.LIT, false), 3);
            FirepitTileEntity pit = Helpers.getTileEntity(world, pos, FirepitTileEntity.class);
            if (pit != null)
            {
                pit.acceptData(logs, fields);
            }
        }
    }
}
