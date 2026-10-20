// Arknights Pixel Dungeon: Amiya frozen visual integration, holy1ow, 2026-10-20.
// SPDX-License-Identifier: GPL-3.0-or-later

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.watabou.noosa.Image;
import java.util.Locale;

/** Appearance routing only; shared talent/buff atlases and mechanics stay intact. */
public final class AmiyaIconVisuals {
    private AmiyaIconVisuals() {}
    public static final String ROOT = "interfaces/operators/amiya/";

    public static String talent(HeroClass displayedClass, Talent talent) {
        if (displayedClass != HeroClass.AMIYA || !AmiyaEffects.eligible(Dungeon.hero)) return null;
        switch (talent) {
            case EMPOWERING_MEAL: case SCHOLARS_INTUITION: case LINGERING_MAGIC: case BACKUP_BARRIER:
            case ENERGIZING_MEAL: case INSCRIBED_POWER: case WAND_PRESERVATION: case ARCANE_VISION:
            case SHIELD_BATTERY: case DESPERATE_POWER: case ALLY_WARP: case EMPOWERED_STRIKE:
            case MYSTICAL_CHARGE: case EXCESS_CHARGE: case SOUL_EATER: case SOUL_SIPHON:
            case NECROMANCERS_MINIONS: case BLAST_RADIUS: case ELEMENTAL_POWER: case REACTIVE_BARRIER:
            case WILD_POWER: case FIRE_EVERYTHING: case CONSERVED_MAGIC: case TELEFRAG:
            case REMOTE_BEACON: case LONGRANGE_WARP: case HEROIC_ENERGY:
                // Ratmogrify has a distinct original icon/meaning.
                if (talent == Talent.HEROIC_ENERGY
                        && com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify.useRatroicEnergy) return null;
                return ROOT + "talents/" + talent.name().toLowerCase(Locale.ROOT) + ".png";
            default: return null;
        }
    }

    public static String status(Buff buff, boolean large) {
        if (!(buff.target instanceof Hero) || !AmiyaEffects.eligible((Hero)buff.target)) return null;
        String id;
        // Exact classes: subclasses with their own icon semantics keep their original appearance.
        if (buff.getClass() == Recharging.class) id = "recharging";
        else if (buff.getClass() == Barrier.class) id = "barrier";
        else if (buff.getClass() == WandEmpower.class) id = "wand-empower";
        else if (buff.getClass() == ArtifactRecharge.class) id = "artifact-recharge";
        else return null;
        return ROOT + "status/" + id + (large ? "-16.png" : "-7.png");
    }

    /** Selected statuses retain their original atlas frames, with Amiya's blue tint. */
    public static boolean tintStatus(Buff buff, Image icon) {
        if (!(buff.target instanceof Hero) || !AmiyaEffects.eligible((Hero) buff.target)) return false;
        if (buff.getClass() != Talent.LingeringMagicTracker.class
                && buff.getClass() != ScrollEmpower.class
                && buff.getClass() != WandOfMagicMissile.MagicCharge.class) return false;
        icon.hardlight(0x45B5E0);
        return true;
    }
}
