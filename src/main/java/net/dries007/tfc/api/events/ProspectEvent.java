package net.dries007.tfc.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Event fired when the Prospector's Pickaxe is used.
 * Carries all the data relative to the result displayed to the player.
 * One of the two subclasses will be used according to the logical side.
 */
public abstract class ProspectEvent extends Event {

    public static class Server extends ProspectEvent {
        public Server(EntityPlayer player, BlockPos pos, ResultType type, String ore, double score){
            super(player, pos, type, ore, score);
        }
    }

    public static class Client extends ProspectEvent {
        public Client(EntityPlayer player, BlockPos pos, ResultType type, String ore, double score){
            super(player, pos, type, ore, score);
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

        public final String translation;

        ResultType(String translation){
            this.translation = translation;
        }
    }

    public final EntityPlayer player;
    public final BlockPos pos;
    public final ResultType type;
    public final String ore;
    public final double score;

    public ProspectEvent(EntityPlayer player, BlockPos pos, ResultType type, String ore, double score){
        this.player = player;
        this.pos = pos;
        this.type = type;
        this.ore = ore;
        this.score = score;
    }
}
