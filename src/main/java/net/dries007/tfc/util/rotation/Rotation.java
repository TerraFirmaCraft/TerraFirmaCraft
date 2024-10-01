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

import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * An interface which defines a rotation. This includes a direction, speed, and current angle.
 * <p>
 * One primary instance of {@link Rotation.Tickable} is meant to be created by a source within a mechanical network.
 * Other components that only transfer rotations, should create non-tickable instances that simply refer to the origin rotation, but modify the direction (i.e. if the handedness changes).
 */
@Deprecated
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

    static Rotation.Tickable ofFake()
    {
        return new Rotation.Tickable()
        {
            @Override
            public void tick() {}

            @Override
            public void set(float angle, float speed) {}

            @Override
            public float angle(float partialTick)
            {
                // Ultimately we need to clamp `Calendars.CLIENT` to a small-enough value that we can add partialTick to it, and not suffer loss of precision
                // So, this should probably be 'good enough', in terms of number of bits sacrificed, vs. how often the player will see the wrap-around happen
                // We also upcast to double, perform the multiplication, then downcast to float to keep extra precision during the calculation
                return (float) ((((Calendars.CLIENT.getTicks() % (20 * ICalendar.TICKS_IN_DAY)) + (double) partialTick) * speed()) % Mth.TWO_PI);
            }

            @Override
            public float speed()
            {
                return Mth.TWO_PI / (8 * 20);
            }

            @Override
            public Direction direction()
            {
                return Direction.UP;
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
     * @return The current rotation speed, in radians per tick. Note that this <strong>can be negative!</strong>
     */
    float speed();

    /**
     * @return The absolute value of the current rotation speed, in radians per tick.
     */
    default float positiveSpeed()
    {
        return Mth.abs(speed());
    }

    /**
     * Returns using a <strong>left-hand rule</strong>.
     * Note that if {@link #speed()} is negative, this will be rotating with a <strong>right-hand rule</strong>
     * <p>
     * Rule: the thumb is the direction of rotation, and the rotating happens in the direction the fingers curl.
     *
     * @return The current direction of rotation.
     */
    Direction direction();

    /**
     * @return The current direction of rotation, depending on the current speed, to always be oriented in a <strong>left-hand rule</strong>
     */
    default Direction positiveDirection()
    {
        return speed() < 0 ? direction().getOpposite() : direction();
    }

    @Deprecated
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
