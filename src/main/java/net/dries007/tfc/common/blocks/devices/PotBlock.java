package net.dries007.tfc.common.blocks.devices;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.IPotRecipe;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.common.tileentity.PotTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.TFCDamageSources;

import static net.minecraft.util.ActionResultType.*;

public class PotBlock extends FirepitBlock
{
    private static final VoxelShape POT_SHAPE = VoxelShapes.or(
        box(4, 6, 3, 12, 9, 4),
        box(5, 9, 4, 12, 10, 5),
        box(4, 10, 3, 12, 11, 4),
        box(12, 6, 4, 13, 9, 12),
        box(11, 9, 5, 12, 10, 12),
        box(12, 10, 4, 13, 11, 12),
        box(4, 6, 12, 12, 9, 13),
        box(4, 9, 11, 11, 10, 12),
        box(4, 10, 12, 12, 11, 13),
        box(3, 6, 4, 4, 9, 12),
        box(4, 9, 4, 5, 10, 11),
        box(3, 10, 4, 4, 11, 12),
        box(4, 5, 4, 12, 7, 12),
        box(0, 12, 7.5, 16, 13, 8.5),
        box(1, 0, 7.5, 2, 12, 8.5),
        box(14, 0, 7.5, 15, 12, 8.5),
        box(7.5, 11, 3, 8.5, 13, 4),
        box(7.5, 13, 4, 8.5, 14, 12),
        box(7.5, 11, 12, 8.5, 13, 13));

    private static void convertPotToFirepit(World world, BlockPos pos)
    {
        PotTileEntity pot = Helpers.getTileEntity(world, pos, PotTileEntity.class);
        if (pot != null && !pot.hasOutput())
        {
            Helpers.spawnItem(world, pos, new ItemStack(TFCItems.POT.get()));
            Helpers.playSound(world, pos, SoundEvents.BEEHIVE_SHEAR);
            List<ItemStack> logs = pot.getLogs();
            float[] fields = pot.getFields();
            pot.dump();
            pot.clearContent();

            world.setBlock(pos, TFCBlocks.FIREPIT.get().defaultBlockState().setValue(FirepitBlock.LIT, false), 3);
            FirepitTileEntity pit = Helpers.getTileEntity(world, pos, FirepitTileEntity.class);
            if (pit != null)
            {
                pit.acceptData(logs, fields);
            }
        }
    }

    public PotBlock(ForgeBlockProperties properties)
    {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        super.animateTick(state, world, pos, rand);
        PotTileEntity te = Helpers.getTileEntity(world, pos, PotTileEntity.class);
        if (te != null && te.isBoiling())
        {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < rand.nextInt(5) + 4; i++)
                world.addParticle(TFCParticles.BUBBLE.get(), false, x + rand.nextFloat() * 0.375 - 0.1875, y + 0.625, z + rand.nextFloat() * 0.375 - 0.1875, 0, 0.05D, 0);
            world.addParticle(TFCParticles.STEAM.get(), false, x, y + 0.8, z, Helpers.fastGaussian(rand), 0.5, Helpers.fastGaussian(rand));
            world.playLocalSound(x, y, z, SoundEvents.WATER_AMBIENT, SoundCategory.BLOCKS, 1.0F, rand.nextFloat() * 0.7F + 0.4F, false);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (world.isClientSide() || hand.equals(Hand.OFF_HAND)) return SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        boolean lit = state.getValue(LIT);
        if (stack.isEmpty() && player.isShiftKeyDown())
        {
            if (lit)//can't take stuff out if it's lit
            {
                player.hurt(TFCDamageSources.POT, 1.0F);
                Helpers.playSound(world, pos, SoundEvents.LAVA_EXTINGUISH);
            }
            else
            {
                convertPotToFirepit(world, pos);
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
            PotTileEntity te = Helpers.getTileEntity(world, pos, PotTileEntity.class);
            if (te != null && player instanceof ServerPlayerEntity)
            {
                if (!te.isBoiling())
                {
                    if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, null))
                    {
                        return CONSUME;
                    }
                    else if (te.hasOutput())
                    {
                        IPotRecipe.Output output = te.getOutput();
                        // retrieve the output via right click
                        if (!output.isEmpty())
                            output.onExtract(world, pos, stack);
                        // if there's nothing left, set it to null so we can start the process over
                        if (output.isEmpty())
                            te.setFinished();
                        return SUCCESS;
                    }
                    NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
                    Helpers.playSound(world, pos, SoundEvents.SOUL_SAND_STEP);
                    return SUCCESS;
                }
            }
        }
        return FAIL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.or(POT_SHAPE, BASE_SHAPE);
    }

    @Override
    protected double getParticleHeightOffset()
    {
        return 0.8D;
    }
}
