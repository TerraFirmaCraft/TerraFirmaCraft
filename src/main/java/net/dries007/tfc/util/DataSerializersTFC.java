package net.dries007.tfc.util;

import java.io.IOException;
import javax.annotation.Nonnull;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

import static net.minecraft.network.datasync.DataSerializers.registerSerializer;

public final class DataSerializersTFC
{
    public static final DataSerializer<Long> LONG = new DataSerializer<Long>()
    {
        @Override
        public void write(PacketBuffer buf, @Nonnull Long value)
        {
            buf.writeLong(value);
        }

        @Override
        public Long read(PacketBuffer buf) throws IOException
        {
            return buf.readLong();
        }

        @Override
        public DataParameter<Long> createKey(int id)
        {
            return new DataParameter<>(id, this);
        }

        @Override
        @Nonnull
        public Long copyValue(@Nonnull Long value)
        {
            return value;
        }
    };

    static
    {
        registerSerializer(LONG);
    }
}
