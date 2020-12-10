package net.dries007.tfc.api.events;

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
public abstract class ProspectEvent extends Event {

    public static class Server extends ProspectEvent {
        public Server(EntityPlayer player, BlockPos pos, ResultType type, ItemStack vein){
            super(Side.SERVER, player, pos, type, vein);
        }
    }

    public static class Client extends ProspectEvent {
        public Client(EntityPlayer player, BlockPos pos, ResultType type, ItemStack vein){
            super(Side.CLIENT, player, pos, type, vein);
        }
    }

    public enum ResultType {
        VERY_LARGE ("tfc.propick.found_very_large"),
        LARGE      ("tfc.propick.found_large"),
        MEDIUM     ("tfc.propick.found_medium"),
        SMALL      ("tfc.propick.found_small"),
        TRACES     ("tfc.propick.found_traces"),

        FOUND      ("tfc.propick.found"),         // right click on block
        NOTHING    ("tfc.propick.found_nothing"); // nothing interesting here

        private static final ResultType[] VALUES = values();
        public final String translation;

        ResultType(String translation){
            this.translation = translation;
        }

        public static ResultType valueOf(int ordinal){
            return VALUES[ordinal];
        }
    }

    private Side side;
    private EntityPlayer player;
    private BlockPos pos;
    private ResultType type;
    private ItemStack vein;

    protected ProspectEvent(Side side, EntityPlayer player, BlockPos pos, ResultType type, ItemStack vein){
        this.side = side;
        this.player = player;
        this.pos = pos;
        this.type = type;
        this.vein = vein;
    }

    public Side getSide(){
        return side;
    }

    public EntityPlayer getPlayer(){
        return player;
    }

    public BlockPos getBlockPos(){
        return pos;
    }

    public ResultType getResultType(){
        return type;
    }

    public ItemStack getVein(){
        return vein;
    }
}
