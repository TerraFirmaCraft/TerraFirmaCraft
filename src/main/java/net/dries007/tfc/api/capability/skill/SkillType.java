/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.skill;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import net.dries007.tfc.util.skills.ProspectingSkill;
import net.dries007.tfc.util.skills.SimpleSkill;
import net.dries007.tfc.util.skills.SmithingSkill;

/**
 * The instance (i.e. Block or Item) of a skill. This holds the name, class, and supplier for the skill capability
 * To create a new skill all you need to do is create a new instance of this class. Registration will be automatically handled for you.
 *
 * @param <S> the skill class
 */
@SuppressWarnings("WeakerAccess")
public final class SkillType<S extends Skill>
{
    public static final SkillType<ProspectingSkill> PROSPECTING;
    public static final SkillType<SmithingSkill> SMITHING;
    public static final SkillType<SimpleSkill> AGRICULTURE;
    public static final SkillType<SimpleSkill> BUTCHERING;

    private static final List<SkillType<? extends Skill>> SKILLS = new ArrayList<>();
    private static final Set<String> SKILL_NAMES = new HashSet<>();

    static
    {
        // This needs to happen after SKILL_NAMES and SKILLS are initialized, otherwise it causes an NPE
        PROSPECTING = new SkillType<>("prospecting", ProspectingSkill::new);
        SMITHING = new SkillType<>("smithing", SmithingSkill::new);
        AGRICULTURE = new SkillType<>("agriculture", SimpleSkill::new);
        BUTCHERING = new SkillType<>("butchering", SimpleSkill::new);
    }

    @Nonnull
    public static List<SkillType<? extends Skill>> getSkills()
    {
        return SKILLS;
    }

    @Nonnull
    public static Map<String, Skill> createSkillMap(IPlayerSkills rootInstance)
    {
        return SKILLS.stream().collect(Collectors.toMap(SkillType::getName, e -> e.skillSupplier.apply(rootInstance)));
    }

    private final String name;
    private final Function<IPlayerSkills, S> skillSupplier;

    public SkillType(String name, Function<IPlayerSkills, S> skillSupplier)
    {
        this.name = name;
        this.skillSupplier = skillSupplier;

        if (SKILL_NAMES.contains(name))
        {
            throw new IllegalArgumentException("Can't register multiple skills with the same name!");
        }

        SKILLS.add(this);
        SKILL_NAMES.add(name);
    }

    @Nonnull
    public String getName()
    {
        return name;
    }
}
