package net.dries007.tfc.common.items;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThatchBedBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;

public class HideItem extends Item
{
    private final Size size;
    private final Stage stage;

    public HideItem(Size size, Stage stage)
    {
        super(new Properties().tab(TFCItemGroup.MISC));
        this.size = size;
        this.stage = stage;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();
        if (!world.isClientSide())
        {
            PlayerEntity player = context.getPlayer();
            if (player != null && (size == Size.LARGE && stage == Stage.RAW))
            {
                BlockPos basePos = context.getClickedPos();
                Direction facing = context.getHorizontalDirection();
                BlockState bed = TFCBlocks.THATCH_BED.get().defaultBlockState();
                Block thatch = TFCBlocks.THATCH.get();
                Direction[] directions = {facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise()};
                for (Direction d : directions)
                {
                    BlockPos headPos = basePos.relative(d, 1);
                    if (world.getBlockState(basePos).is(thatch) && world.getBlockState(headPos).is(thatch))
                    {
                        BlockPos playerPos = player.blockPosition();
                        if (playerPos != headPos && playerPos != basePos)
                        {
                            world.setBlock(basePos, bed.setValue(ThatchBedBlock.PART, BedPart.FOOT).setValue(ThatchBedBlock.FACING, d), 16);
                            world.destroyBlock(headPos, false);
                            world.setBlock(headPos, bed.setValue(ThatchBedBlock.PART, BedPart.HEAD).setValue(ThatchBedBlock.FACING, d.getOpposite()), 16);
                            context.getItemInHand().shrink(1);
                            if (!world.dimensionType().bedWorks())
                            {
                                world.explode(null, DamageSource.badRespawnPointExplosion(), null, headPos.getX() + 0.5D, headPos.getY() + 0.5D, (double) headPos.getZ() + 0.5D, 10.0F, true, Explosion.Mode.DESTROY);
                                return ActionResultType.FAIL;
                            }
                            return ActionResultType.SUCCESS;
                        }

                    }
                }
            }
        }
        return ActionResultType.FAIL;
    }


    public enum Size
    {
        SMALL,
        MEDIUM,
        LARGE
    }

    public enum Stage
    {
        PREPARED,
        RAW,
        SCRAPED,
        SHEEPSKIN,
        SOAKED
    }
}
