/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

/**
 * An interface which defines a rotation. This includes a direction, speed, and current angle.
 * <p>
 * One primary instance of {@link Rotation.Tickable} is meant to be created by a source within a mechanical network.
 * Other components that only transfer rotations, should create non-tickable instances that simply refer to the origin rotation, but modify the direction (i.e. if the handedness changes).
 */
public interface Rotation
{
    static Rotation.Tickable of(Direction direction, float initialSpeed)
    {
        return new Rotation.Tickable() {

            float angle = 0;
            float speed = initialSpeed;

            @Override
            public void tick()
            {
                angle += speed;
                if (angle > Mth.TWO_PI)
                {
                    angle -= Mth.TWO_PI;
                }
                if (angle < 0)
                {
                    angle += Mth.TWO_PI;
                }
            }

            @Override
            public void set(float angle, float speed)
            {
                this.angle = angle;
                this.speed = speed;
            }

            @Override
            public float angle(float partialTick)
            {
                return angle + speed * partialTick;
            }

            @Override
            public float speed()
            {
                return speed;
            }

            @Override
            public Direction direction()
            {
                return direction;
            }
        };
    }

    static Rotation of(Rotation source, Direction direction)
    {
        return new Rotation() {
            @Override
            public float angle(float partialTick)
            {
                return source.angle(partialTick);
            }

            @Override
            public float speed()
            {
                return source.speed();
            }

            @Override
            public Direction direction()
            {
                return direction;
            }
        };
    }

    /**
     * @return The real angle for a given nullable rotation, including adjustments due to the hand of the rotation.
     */
    static float angle(@Nullable Rotation rotation, float partialTick)
    {
        return rotation == null ? 0f : clampToTwoPi(rotation.direction().getAxisDirection().getStep() * rotation.angle(partialTick));
    }

    private static float clampToTwoPi(float angle)
    {
        return angle < 0 ? Mth.TWO_PI + angle : angle;
    }

    /**
     * @return The current angle of rotation in radians, relative to the origin.
     */
    float angle(float partialTick);

    default float angle()
    {
        return angle(0);
    }

    /**
     * @return The current rotation speed, in radians per tick.
     */
    float speed();

    /**
     * @return The current direction of rotation, using a <strong>right-hand rule</strong>.
     */
    Direction direction();

    interface Tickable extends Rotation
    {
        void tick();

        void set(float angle, float speed);

        default void setSpeed(float speed)
        {
            set(angle(), speed);
        }

        default void reset()
        {
            set(0, 0);
        }

        default void loadFromTag(CompoundTag tag)
        {
            set(tag.getFloat("rtAngle"), tag.getFloat("rtSpeed"));
        }

        default void saveToTag(CompoundTag tag)
        {
            tag.putFloat("rtAngle", angle());
            tag.putFloat("rtSpeed", speed());
        }
    }
}
