/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * The type object which identifies a given climate model. These are <strong>not serialized</strong>, must be chosen on initialization
 * of each new world, and can change in existing worlds.
 *
 * @param codec A codec used to sync the climate model to client when first initialized
 * @see ClimateModel
 */
public record ClimateModelType<T extends ClimateModel>(StreamCodec<ByteBuf, T> codec) {}
