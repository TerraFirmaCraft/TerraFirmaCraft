package net.dries007.tfc.common.entities.predator;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class FelinePredator extends Predator
{
    public final double crouchSpeedMod;
    public final double sprintSpeedMod;
    public final double attackDistanceSquared;

    public static FelinePredator createNocturnal(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false);
    }

    public FelinePredator(EntityType<? extends Predator> type, Level level, boolean diurnal)
    {
        this(type, level, diurnal, 8, 20, 36, 0.8, 1.5);
    }

    public FelinePredator(EntityType<? extends Predator> type, Level level, boolean diurnal, int attackAnimLength, int walkAnimationLength, double attackDistanceSquared, double crouchSpeedMod, double sprintSpeedMod)
    {
        super(type, level, diurnal, attackAnimLength, walkAnimationLength);
        this.crouchSpeedMod = crouchSpeedMod;
        this.sprintSpeedMod = sprintSpeedMod;
        this.attackDistanceSquared = attackDistanceSquared;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.ATTACK_DAMAGE, 7);
    }

}