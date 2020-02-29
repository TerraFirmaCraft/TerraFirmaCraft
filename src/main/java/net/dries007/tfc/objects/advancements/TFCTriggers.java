/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public final class TFCTriggers
{
    public static final LitTrigger LIT_TRIGGER = new LitTrigger();
    public static final FamiliarizationTrigger FAMILIARIZATION_TRIGGER = new FamiliarizationTrigger();

    public static void init()
    {
        CriteriaTriggers.register(LIT_TRIGGER);
        CriteriaTriggers.register(FAMILIARIZATION_TRIGGER);
    }
}
