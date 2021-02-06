package net.dries007.tfc.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;

public class BurningLogPileTileEntity extends TFCTileEntity implements ITickableTileEntity
{
    private int logs;
    private long startBurningTick;

    public BurningLogPileTileEntity()
    {
        this(TFCTileEntities.BURNING_LOG_PILE.get());
    }

    public BurningLogPileTileEntity(TileEntityType<?> type)
    {
        super(type);
        logs = 0;
        startBurningTick = Calendars.SERVER.getTicks();
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        nbt.putInt("logs", logs);
        nbt.putLong("startBurningTick", startBurningTick);
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        logs = nbt.getInt("logs");
        startBurningTick = nbt.getLong("startBurningTick");
        return super.save(nbt);
    }

    @Override
    public void tick()
    {
        if (level != null && !level.isClientSide)
        {
            if ((int) (Calendars.SERVER.getTicks() - startBurningTick) > TFCConfig.SERVER.charcoalTicks.get())
            {
                createCharcoal();
            }
        }
    }

    public void light(int logsIn)
    {
        startBurningTick = Calendars.SERVER.getTicks();
        logs = logsIn;
        markDirtyFast();
    }

    /**
     * This function does some magic **** to not create floating charcoal. Don't touch unless broken
     *
     * @author AlcatrazEscapee, ported to 1.16 by EERussianguy
     */
    private void createCharcoal()
    {
        if (level == null) return;
        final BlockState pile = TFCBlocks.CHARCOAL_PILE.get().defaultBlockState();

        int j = 0;
        Block block;
        do
        {
            j++;
            block = level.getBlockState(worldPosition.below(j)).getBlock();
            // This is here so that the charcoal pile will collapse Bottom > Top
            // Because the pile scans Top > Bottom this is necessary to avoid floating blocks
            if (block.is(TFCBlocks.LOG_PILE.get()))
            {
                return;
            }
        } while (level.isEmptyBlock(worldPosition) || block.is(TFCBlocks.CHARCOAL_PILE.get()));

        double logs = this.logs * (0.25 + 0.25 * level.getRandom().nextFloat());
        int charcoal = (int) MathHelper.clamp(logs, 0, 8);
        if (charcoal == 0)
        {
            level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
            return;
        }
        if (j == 1)
        {
            // This log pile is at the bottom of the charcoal pit
            level.setBlockAndUpdate(worldPosition, pile.setValue(CharcoalPileBlock.LAYERS, charcoal));
            return;
        }
        for (int k = j - 1; k >= 0; k--)
        {
            // Climb back up from the bottom
            BlockPos downPos = worldPosition.below(k);
            BlockState state = level.getBlockState(downPos);
            if (level.isEmptyBlock(downPos))
            {
                // If it hits air, place the remaining pile in that block
                level.setBlockAndUpdate(downPos, pile.setValue(CharcoalPileBlock.LAYERS, charcoal));
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
                return;
            }

            if (state.is(TFCBlocks.CHARCOAL_PILE.get()))
            {
                // Place what it can in the existing charcoal pit, then continue climbing
                charcoal += state.getValue(CharcoalPileBlock.LAYERS);
                int toCreate = Math.min(charcoal, 8);
                level.setBlockAndUpdate(downPos, pile.setValue(CharcoalPileBlock.LAYERS, toCreate));
                charcoal -= toCreate;
            }

            if (charcoal <= 0)
            {
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
                return;
            }
        }
        // If you exit the loop, its arrived back at the original position OR needs to rest the original position, and needs to replace that block
        level.setBlockAndUpdate(worldPosition, pile.setValue(CharcoalPileBlock.LAYERS, charcoal));
    }
}
