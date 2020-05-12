/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.blocks.devices.BlockSluice;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ItemGem;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;

@ParametersAreNonnullByDefault
public class TESluice extends TEBase implements ITickable
{
    public static final int MAX_SOIL = 50;

    public static boolean isValidFluid(Fluid fluid)
    {
        return fluid == FluidsTFC.FRESH_WATER.get() || fluid == FluidsTFC.SALT_WATER.get();
    }

    private int soil;
    private int ticksRemaining, delayTimer;

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (ticksRemaining > 0)
            {
                if (--ticksRemaining <= 0)
                {
                    if (Constants.RNG.nextDouble() < ConfigTFC.Devices.SLUICE.oreChance)
                    {
                        ChunkPos myPos = world.getChunk(pos).getPos();
                        int radius = ConfigTFC.Devices.SLUICE.radius;
                        //Copy from Helper method, but only look for workable chunks
                        List<Chunk> chunks = new ArrayList<>();
                        for (int x = myPos.x - radius; x <= myPos.x + radius; x++)
                        {
                            for (int z = myPos.z - radius; z <= myPos.z + radius; z++)
                            {
                                Chunk chunk = world.getChunk(x, z);
                                ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
                                if (chunkData.canWork(1) && chunkData.getGeneratedVeins().stream().anyMatch(vein -> vein.getType() != null && vein.getType().getOre() != null))
                                {
                                    chunks.add(chunk);
                                }
                            }
                        }
                        if (chunks.size() > 0)
                        {
                            Chunk workingChunk = chunks.get(Constants.RNG.nextInt(chunks.size()));
                            ChunkDataTFC chunkData = ChunkDataTFC.get(workingChunk);

                            // Only check for not null veins
                            List<Vein> veinList = chunkData.getGeneratedVeins()
                                .stream().filter(vein -> vein.getType() != null && vein.getType().getOre() != null)
                                .collect(Collectors.toList());

                            //noinspection ConstantConditions
                            Ore ore = veinList.get(Constants.RNG.nextInt(veinList.size())).getType().getOre();
                            ItemStack output = new ItemStack(ItemSmallOre.get(ore));
                            Helpers.spawnItemStack(world, getFrontWaterPos(), output);
                            chunkData.addWork();
                        }
                    }
                    if (Constants.RNG.nextDouble() < ConfigTFC.Devices.SLUICE.gemChance)
                    {
                        Gem dropGem;
                        if (Constants.RNG.nextDouble() < ConfigTFC.Devices.SLUICE.diamondGemChance)
                        {
                            dropGem = Gem.DIAMOND;
                        }
                        else
                        {
                            dropGem = Gem.getRandomDropGem(Constants.RNG);
                        }
                        Gem.Grade grade = Gem.Grade.randomGrade(Constants.RNG);
                        Helpers.spawnItemStack(world, getFrontWaterPos(), ItemGem.get(dropGem, grade, 1));
                    }
                    consumeSoil();
                }
            }
            if (--delayTimer <= 0)
            {
                delayTimer = 20;
                Fluid flowing = getFlowingFluid();
                //Try placing the output block if has input flow and is allowed fluid
                if (flowing != null && isValidFluid(flowing))
                {
                    BlockPos frontPos = getFrontWaterPos();
                    if (world.getBlockState(frontPos).getMaterial().isReplaceable())
                    {
                        world.setBlockState(frontPos, flowing.getBlock().getDefaultState());
                    }
                }
                //Consume inputs
                if (soil < MAX_SOIL)
                {
                    for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(1), EntitySelectors.IS_ALIVE))
                    {
                        ItemStack stack = entityItem.getItem();
                        if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockRockVariant)
                        {
                            BlockRockVariant rockBlock = (BlockRockVariant) ((ItemBlock) stack.getItem()).getBlock();
                            if (rockBlock.getType() == Rock.Type.SAND || rockBlock.getType() == Rock.Type.GRAVEL)
                            {
                                soil += 20; // Overflows to not consume an stack until a full soil worth is consumed
                                stack.shrink(1);
                                if (stack.getCount() <= 0)
                                {
                                    entityItem.setDead();
                                    break;
                                }
                                markForBlockUpdate();
                            }
                        }
                    }
                }
                if (ticksRemaining <= 0)
                {
                    consumeSoil();
                }
            }
        }
    }

    @Nullable
    public Fluid getFlowingFluid()
    {
        BlockFluidBase block = getFlowingFluidBlock();
        return block == null ? null : block.getFluid();
    }

    @Nullable
    public EnumFacing getBlockFacing()
    {
        if (!hasWorld() || !(world.getBlockState(pos).getBlock() instanceof BlockSluice))
        {
            return null;
        }
        return world.getBlockState(pos).getValue(BlockHorizontal.FACING);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        soil = nbt.getInteger("soil");
        ticksRemaining = nbt.getInteger("ticksRemaining");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("soil", soil);
        nbt.setInteger("ticksRemaining", ticksRemaining);
        return super.writeToNBT(nbt);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    public int getSoil()
    {
        return soil;
    }

    @Nullable
    private BlockFluidBase getFlowingFluidBlock()
    {
        if (!hasWorld() || !(world.getBlockState(pos).getBlock() instanceof BlockSluice))
        {
            return null;
        }
        EnumFacing sluiceFacing = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
        BlockPos fluidInputPos = pos.up().offset(sluiceFacing);
        IBlockState state = world.getBlockState(fluidInputPos);
        Block block = state.getBlock();
        if (block instanceof BlockFluidBase)
        {
            return ((BlockFluidBase) block);
        }
        return null;
    }

    private BlockPos getFrontWaterPos()
    {
        //noinspection ConstantConditions
        return pos.down().offset(getBlockFacing().getOpposite(), 2);
    }

    /**
     * Checks if this sluice has flowing fluid (only allowed ones)
     *
     * @return true if the entrance and the output blocks are the same fluid and in the allowed predicate
     */
    private boolean hasFlow()
    {

        Fluid fluid = getFlowingFluid();
        if (fluid == null || !isValidFluid(fluid))
        {
            return false;
        }
        IBlockState frontState = world.getBlockState(getFrontWaterPos());
        Block block = frontState.getBlock();
        if (block instanceof BlockFluidBase)
        {
            return ((BlockFluidBase) block).getFluid() == fluid;
        }
        return false;
    }

    private void consumeSoil()
    {
        if (soil > 0 && hasFlow())
        {
            soil--;
            ticksRemaining = ConfigTFC.Devices.SLUICE.ticks;
            markForBlockUpdate();
        }
        else
        {
            ticksRemaining = 0;
            if (soil > 0)
            {
                soil = 0;
                markForBlockUpdate();
            }
        }
    }
}
