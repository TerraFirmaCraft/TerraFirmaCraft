package net.dries007.tfc.common.blocks;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ThatchBedBlock extends BedBlock
{
    private static final VoxelShape BED_SHAPE = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 9.0F, 16.0F);

    public ThatchBedBlock() {
        super(DyeColor.YELLOW, Properties.of(Material.REPLACEABLE_PLANT).strength(0.6F, 0.4F));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isClientSide())
        {
            if (canSetSpawn(worldIn) && !worldIn.isThundering()) //todo: figure out how spawning works
            {
                ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player; // lifted from RespawnAnchorBlock
                serverplayerentity.setRespawnPosition(worldIn.dimension(), player.blockPosition(), 0.0F, false, true);
                player.displayClientMessage(new TranslationTextComponent("tfc.thatch_bed.use"), true);
                return ActionResultType.SUCCESS;
            }
            else if (canSetSpawn(worldIn) && worldIn.isThundering())
            {
                player.displayClientMessage(new TranslationTextComponent("tfc.thatch_bed.thundering"), true);
                return ActionResultType.FAIL;
            }
            else if (!canSetSpawn(worldIn))
            {
                worldIn.explode(null, DamageSource.badRespawnPointExplosion(), null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 7.0F, true, Explosion.Mode.DESTROY);
            }
        }
        return ActionResultType.FAIL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        Direction facing = state.getValue(FACING);
        if (!(world.getBlockState(pos.relative(facing)).is(TFCBlocks.THATCH_BED.get())))
        {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) { return BED_SHAPE; }

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) { return null; }

    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
