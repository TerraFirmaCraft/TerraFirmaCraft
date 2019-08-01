/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.forge.ForgeSteps;

/**
 * Used to send an update from an {@link TEAnvilTFC} whenever a step is added or the recipe changes serverside
 *
 * @author AlcatrazEscapee
 */
public class PacketAnvilUpdate implements IMessage
{
    private BlockPos pos;
    private ResourceLocation recipe;
    private ForgeSteps steps;
    private int workProgress, workTarget;

    // no args constructor required for forge
    @SuppressWarnings("unused")
    @Deprecated
    public PacketAnvilUpdate() {}

    public PacketAnvilUpdate(@Nonnull TEAnvilTFC tile)
    {
        this.pos = tile.getPos();
        AnvilRecipe recipe = tile.getRecipe();
        this.recipe = recipe != null ? recipe.getRegistryName() : null;
        this.steps = tile.getSteps();
        this.workProgress = tile.getWorkingProgress();
        this.workTarget = tile.getWorkingTarget();
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        workProgress = buffer.readInt();
        workTarget = buffer.readInt();
        pos = BlockPos.fromLong(buffer.readLong());
        steps = ForgeSteps.deserialize(buffer.readInt());
        recipe = Helpers.readResourceLocation(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(workProgress);
        buffer.writeInt(workTarget);
        buffer.writeLong(pos.toLong());
        buffer.writeInt(steps.serialize());
        Helpers.writeResourceLocation(buffer, recipe);
    }

    public static class Handler implements IMessageHandler<PacketAnvilUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketAnvilUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    TEAnvilTFC te = Helpers.getTE(world, message.pos, TEAnvilTFC.class);
                    if (te != null)
                    {
                        AnvilRecipe recipe = message.recipe != null ? TFCRegistries.ANVIL.getValue(message.recipe) : null;
                        te.onReceivePacket(recipe, message.steps, message.workProgress, message.workTarget);
                    }
                });
            }
            return null;
        }
    }
}
