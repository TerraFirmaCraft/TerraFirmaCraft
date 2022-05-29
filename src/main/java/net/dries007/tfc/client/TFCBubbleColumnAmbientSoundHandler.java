/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TFCBubbleColumnAmbientSoundHandler implements AmbientSoundHandler
{
    private final LocalPlayer player;
    private boolean wasInBubbleColumn;
    private boolean firstTick = true;

    public TFCBubbleColumnAmbientSoundHandler(LocalPlayer localPlayer)
    {
        player = localPlayer;
    }

    @Override
    public void tick()
    {
        Level level = player.level;
        BlockState stateAt = level.getBlockStatesIfLoaded(player.getBoundingBox().inflate(0.0D, -0.4F, 0.0D).deflate(1.0E-6D)).filter((state) -> state.getBlock() instanceof BubbleColumnBlock).findFirst().orElse(null);
        if (stateAt != null)
        {
            if (!wasInBubbleColumn && !firstTick && stateAt.is(Blocks.BUBBLE_COLUMN) && !player.isSpectator())
            {
                if (stateAt.getValue(BubbleColumnBlock.DRAG_DOWN))
                {
                    player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
                }
                else
                {
                    player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
                }
            }
            wasInBubbleColumn = true;
        }
        else
        {
            wasInBubbleColumn = false;
        }

        firstTick = false;
    }
}
