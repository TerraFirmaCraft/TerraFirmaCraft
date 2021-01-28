/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.util;

import java.util.Random;

import net.minecraft.util.SharedSeedRandom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This exists to fix a horrible case of vanilla seeding, which led to noticeable issues of feature clustering.
 * The key issue was that features with a chance placement, applied sequentially, would appear to generate on the same chunk much more often than was expected.
 * This was then identified as the problem by the lovely KaptainWutax <3. The following is a excerpt / paraphrase from our conversation:
 *
 * So you're running setSeed(n), setSeed(n + 1) and setSeed(n + 2) on the 3 structure respectively.
 * And n is something we can compute given a chunk and seed.
 * setSeed applies an xor on the lowest 35 bits and assigns that value internally
 * But like, since your seeds are like 1 apart
 * Even after the xor they're at worst 1 apart
 * You can convince yourself of that quite easily
 * So now nextFloat() does seed = 25214903917 * seed + 11 and returns (seed >> 24) / 2^24
 * Sooo lets see what the actual difference in seeds are between your 2 features in the worst case:
 * a = 25214903917, b = 11
 * So a * (seed + 1) + b = a * seed + b + a
 * As you can see the internal seed only varies by "a" amount
 * Now we can measure the effect that big number has no the upper bits since the seed is shifted
 * 25214903917/2^24 = 1502.92539101839
 * And that's by how much the upper 24 bits will vary
 * The effect on the next float are 1502 / 2^24 = 8.95261764526367e-5
 * Blam, so the first nextFloat() between setSeed(n) and setSeed(n + 1) is that distance apart ^
 * Which as you can see... isn't that far from 0
 */
@Mixin(SharedSeedRandom.class)
public abstract class SharedSeedRandomMixin extends Random
{
    @Inject(method = "setFeatureSeed", at = @At("HEAD"), cancellable = true)
    private void inject$setFeatureSeed(long baseSeed, int index, int decoration, CallbackInfoReturnable<Long> cir)
    {
        setSeed(baseSeed);
        final long seed = (index * nextLong() * 203704237L) ^ (decoration * nextLong() * 758031792L) ^ baseSeed;
        setSeed(seed);
        cir.setReturnValue(seed);
    }
}
