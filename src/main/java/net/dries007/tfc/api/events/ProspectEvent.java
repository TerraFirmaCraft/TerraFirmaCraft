package net.dries007.tfc.api.events;

import net.dries007.tfc.objects.items.metal.ItemProspectorPick.ProspectResult.Type;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Event fired when the Prospector's Pickaxe is used.
 * Carries all the data relative to the result displayed to the player.
 * One of the two subclasses will be used according to the logical side.
 */
public abstract class ProspectEvent extends Event
{

    public static class Server extends ProspectEvent
    {
        public Server(EntityPlayer player, BlockPos pos, Type type, ItemStack vein)
        {
            super(Side.SERVER, player, pos, type, vein);
        }
    }

    public static class Client extends ProspectEvent
    {
        public Client(EntityPlayer player, BlockPos pos, Type type, ItemStack vein)
        {
            super(Side.CLIENT, player, pos, type, vein);
        }
    }

    private Side side;
    private EntityPlayer player;
    private BlockPos pos;
    private Type type;
    private ItemStack vein;

    protected ProspectEvent(Side side, EntityPlayer player, BlockPos pos, Type type, ItemStack vein)
    {
        this.side = side;
        this.player = player;
        this.pos = pos;
        this.type = type;
        this.vein = vein;
    }

    public Side getSide()
    {
        return side;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public BlockPos getBlockPos()
    {
        return pos;
    }

    public Type getResultType()
    {
        return type;
    }

    public ItemStack getVein()
    {
        return vein;
    }
}
