package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nonnull;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.skill.CapabilityPlayerSkills;
import net.dries007.tfc.api.capability.skill.IPlayerSkills;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlockSlabTFC;
import net.dries007.tfc.objects.blocks.BlockStairsTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockRaw;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;

import static net.minecraft.block.BlockSlab.EnumBlockHalf.BOTTOM;
import static net.minecraft.block.BlockSlab.EnumBlockHalf.TOP;
import static net.minecraft.block.BlockSlab.HALF;

public class ItemMetalChisel extends ItemMetalTool
{
    public ItemMetalChisel(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (facing != null)
        {
            IPlayerSkills capability = player.getCapability(CapabilityPlayerSkills.CAPABILITY, null);

            if (capability != null)
            {
                IBlockState newState = null;

                switch (capability.getChiselMode())
                {
                    case SMOOTH:
                    {
                        if (BlocksTFC.isRawStone(state))
                        {
                            BlockRockRaw rawBlock = (BlockRockRaw) state.getBlock();
                            BlockRockVariant smoothBlock = BlockRockVariant.get(rawBlock.getRock(), Rock.Type.SMOOTH);
                            newState = smoothBlock.getDefaultState();
                        }
                    }
                    break;
                    case SLAB:
                    {
                        if (state.getBlock() instanceof BlockRockVariant)
                        {
                            BlockRockVariant oldBlock = (BlockRockVariant) state.getBlock();
                            Rock.Type type = oldBlock.getType();
                            if (type == Rock.Type.SMOOTH || type == Rock.Type.COBBLE || type == Rock.Type.BRICKS)
                            {
                                BlockSlabTFC.Half newBlock = BlockSlabTFC.Half.get(oldBlock.getRock(), type);
                                newState = newBlock.getDefaultState().withProperty(HALF, (hitY < 0.5) ? TOP : BOTTOM);
                            }
                        }
                    }
                    break;
                    case STAIR:
                    {
                        if (state.getBlock() instanceof BlockRockVariant)
                        {
                            BlockRockVariant oldBlock = (BlockRockVariant) state.getBlock();
                            Rock.Type type = oldBlock.getType();
                            if (type == Rock.Type.SMOOTH || type == Rock.Type.COBBLE || type == Rock.Type.BRICKS)
                            {
                                BlockStairsTFC newBlock = BlockStairsTFC.get(oldBlock.getRock(), type);
                                newState = newBlock.getDefaultState();
                            }
                        }
                    }
                    break;
                }

                if (newState != null)
                {
                    // play a sound
                    SoundType soundType = state.getBlock().getSoundType(state, worldIn, pos, player);
                    worldIn.playSound(player, pos, soundType.getHitSound(), SoundCategory.BLOCKS, 1.0f, soundType.getPitch());

                    if (!worldIn.isRemote)
                    {
                        // replace the block with a new block
                        worldIn.setBlockState(pos, newState);
                    }
                    else
                    {

                    }

                    return EnumActionResult.SUCCESS;
                }
                else
                {
                    return EnumActionResult.FAIL;
                }
            }
        }

        return EnumActionResult.FAIL;
    }
}
