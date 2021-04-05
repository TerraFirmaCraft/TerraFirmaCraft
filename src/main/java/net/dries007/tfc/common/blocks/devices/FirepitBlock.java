package net.dries007.tfc.common.blocks.devices;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.common.tileentity.GrillTileEntity;
import net.dries007.tfc.common.tileentity.PotTileEntity;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.util.Helpers.fastGaussian;
import static net.minecraft.util.ActionResultType.*;

public class FirepitBlock extends DeviceBlock implements IForgeBlockProperties
{
    public static final BooleanProperty LIT = TFCBlockStateProperties.LIT;
    protected static final VoxelShape BASE_SHAPE = VoxelShapes.or(
        box(0, 0, 0.5, 3, 1.5, 3),
        box(5, 0, 0.5, 9, 1, 3),
        box(11, 0, 0, 13, 1.5, 2),
        box(9, 0, 0.5, 11, 0.5, 2),
        box(3, 0, 0, 5.5, 0.5, 2),
        box(13, 0, 0, 15.5, 1, 2),
        box(14, 0, 2, 16, 1, 4),
        box(14, 0, 6, 16, 1.5, 9),
        box(13.5, 0, 4, 15.5, 0.5, 6),
        box(13.5, 0, 10, 15.5, 1.5, 12),
        box(14, 0, 9, 16, 1, 11),
        box(1, 0, 3, 3, 1, 5),
        box(0, 0, 4, 1, 1, 6),
        box(1, 0, 5, 2, 1, 6),
        box(0, 0, 6, 3, 1.5, 10),
        box(0.5, 0, 10, 2.5, 1, 12),
        box(0, 0, 11, 3, 0.5, 13),
        box(1, 0, 13, 3, 1, 15),
        box(3, 0, 14, 5.5, 1.5, 16),
        box(5.5, 0, 14, 8.5, 1, 16),
        box(8.5, 0, 13.5, 10.5, 1.5, 15.5),
        box(10.5, 0, 14, 12.5, 0.5, 16),
        box(12.5, 0, 12, 15.5, 1, 15),
        box(2, 0, 2, 14, 1.0, 14)
    );

    public static boolean canSurvive(IWorldReader world, BlockPos pos)
    {
        return world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP);
    }

    protected static void tryExtinguish(World world, BlockPos pos, BlockState state)
    {
        FirepitTileEntity pit = Helpers.getTileEntity(world, pos, FirepitTileEntity.class);
        if (pit != null)
        {
            Helpers.playSound(world, pos, SoundEvents.FIRE_EXTINGUISH);
            pit.extinguish(state);
        }
    }

    private static void convertFirepitToGrill(World world, BlockPos pos, ItemStack stack, boolean lit)
    {
        FirepitTileEntity pit = Helpers.getTileEntity(world, pos, FirepitTileEntity.class);
        if (pit != null)
        {
            List<ItemStack> logs = pit.getLogs();
            float[] fields = pit.getFields();
            pit.dump();
            pit.clearContent();

            world.setBlock(pos, TFCBlocks.GRILL.get().defaultBlockState().setValue(LIT, lit), 3);
            stack.shrink(1);
            Helpers.playSound(world, pos, SoundEvents.CHAIN_PLACE);
            GrillTileEntity grill = Helpers.getTileEntity(world, pos, GrillTileEntity.class);
            if (grill != null)
            {
                grill.acceptData(logs, fields);
            }
        }
    }

    private static void convertFirepitToPot(World world, BlockPos pos, ItemStack stack, boolean lit)
    {
        FirepitTileEntity pit = Helpers.getTileEntity(world, pos, FirepitTileEntity.class);
        if (pit != null)
        {
            List<ItemStack> logs = pit.getLogs();
            float[] fields = pit.getFields();
            pit.dump();
            pit.clearContent();

            world.setBlock(pos, TFCBlocks.POT.get().defaultBlockState().setValue(LIT, lit), 3);
            stack.shrink(1);
            Helpers.playSound(world, pos, SoundEvents.BEEHIVE_SHEAR);
            PotTileEntity pot = Helpers.getTileEntity(world, pos, PotTileEntity.class);
            if (pot != null)
            {
                pot.acceptData(logs, fields);
            }
        }
    }

    public FirepitBlock(ForgeBlockProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LIT, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        if (!state.getValue(LIT)) return;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + getParticleHeightOffset();
        double z = pos.getZ() + 0.5;

        if (rand.nextInt(10) == 0)
            world.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        for (int i = 0; i < 1 + rand.nextInt(3); i++)
            world.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + fastGaussian(rand), y + rand.nextDouble(), z + fastGaussian(rand), 0, 0.07D, 0);
        for (int i = 0; i < rand.nextInt(4); i++)
            world.addParticle(ParticleTypes.SMOKE, x + fastGaussian(rand), y + rand.nextDouble(), z + fastGaussian(rand), 0, 0.005D, 0);
        if (rand.nextInt(8) == 1)
            world.addParticle(ParticleTypes.LARGE_SMOKE, x + fastGaussian(rand), y + rand.nextDouble(), z + fastGaussian(rand), 0, 0.005D, 0);
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && world.getBlockState(pos).getValue(LIT))
        {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(world, pos, entity);
    }

    @Override
    public void handleRain(World world, BlockPos pos)
    {
        FirepitTileEntity te = Helpers.getTileEntity(world, pos, FirepitTileEntity.class);
        if (te != null && world.getBlockState(pos).getValue(LIT))
            te.onRainDrop();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(worldIn, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (world.isClientSide() || hand.equals(Hand.OFF_HAND)) return SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        boolean lit = state.getValue(LIT);
        if (stack.sameItem(new ItemStack(TFCItems.POT.get())))
        {
            convertFirepitToPot(world, pos, stack, lit);
            return CONSUME;
        }
        else if (stack.sameItem(new ItemStack(TFCItems.WROUGHT_IRON_GRILL.get())))
        {
            convertFirepitToGrill(world, pos, stack, lit);
            return CONSUME;
        }
        else if (stack.getItem().is(TFCTags.Items.EXTINGUISHER))
        {
            tryExtinguish(world, pos, state);
            return SUCCESS;
        }
        else
        {
            FirepitTileEntity te = Helpers.getTileEntity(world, pos, FirepitTileEntity.class);
            if (te != null && player instanceof ServerPlayerEntity)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
                Helpers.playSound(world, pos, SoundEvents.SOUL_SAND_STEP);
                return SUCCESS;
            }
        }
        return FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos)
    {
        return canSurvive(world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return BASE_SHAPE;
    }

    protected double getParticleHeightOffset()
    {
        return 0.35D;
    }
}
