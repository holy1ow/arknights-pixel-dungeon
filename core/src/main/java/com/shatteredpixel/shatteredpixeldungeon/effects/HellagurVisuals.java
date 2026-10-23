// Arknights Pixel Dungeon: approved Hellagur visual integration, holy1ow, 2026-10-23.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAppearance;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import java.util.Locale;

/** Approved appearance only. No actions, RNG, state mutation, or combat callbacks. */
public final class HellagurVisuals {
    private HellagurVisuals() {}
    public static final String ROOT = "interfaces/operators/hellagur/";

    public static boolean eligible(Hero hero) {
        return hero != null && hero.heroClass == HeroClass.HELLAGUR
                && HeroAppearance.of(hero).identity == HeroClass.HELLAGUR;
    }

    private static boolean displayed(HeroClass cls) {
        // Explicit class is required even when there is no live hero (selection preview).
        return cls == HeroClass.HELLAGUR && (Dungeon.hero == null || eligible(Dungeon.hero));
    }

    public static String talent(HeroClass cls, Talent talent) {
        if (!displayed(cls)) return null;
        switch (talent) {
            case HEARTY_MEAL: case VETERANS_INTUITION: case PROVOKED_ANGER: case IRON_WILL:
            case IRON_STOMACH: case LIQUID_WILLPOWER: case RUNIC_TRANSFERENCE: case LETHAL_MOMENTUM:
            case IMPROVISED_PROJECTILES: case HOLD_FAST: case STRONGMAN: case ENDLESS_RAGE:
            case DEATHLESS_FURY: case ENRAGED_CATALYST: case CLEAVE: case LETHAL_DEFENSE:
            case ENHANCED_COMBO: case BODY_SLAM: case IMPACT_WAVE: case DOUBLE_JUMP:
            case EXPANDING_WAVE: case STRIKING_WAVE: case SHOCK_FORCE: case SUSTAINED_RETRIBUTION:
            case SHRUG_IT_OFF: case EVEN_THE_ODDS: case HEROIC_ENERGY:
                if (talent == Talent.HEROIC_ENERGY && Ratmogrify.useRatroicEnergy) return null;
                return ROOT + "talents/" + talent.name().toLowerCase(Locale.ROOT) + ".png";
            default: return null;
        }
    }

    public static String itemTexture(int image) {
        if (!eligible(Dungeon.hero)) return null;
        // Approved item artwork only. Seal/shard/stone retain the shared original.
        if (image == ItemSpriteSheet.MASK) return ROOT + "items/mask.png";
        if (image == ItemSpriteSheet.CROWN) return ROOT + "items/crown.png";
        if (image == ItemSpriteSheet.WORN_SHORTSWORD) return ROOT + "items/worn_shortsword.png";
        if (image == ItemSpriteSheet.ARMOR_WARRIOR) return ROOT + "items/armor_warrior.png";
        return null;
    }

    public static String armorAbilityTexture(HeroClass cls, int icon) {
        if (!displayed(cls)) return null;
        switch (icon) {
            case HeroIcon.HEROIC_LEAP: return ROOT + "abilities/heroic_leap.png";
            case HeroIcon.SHOCKWAVE: return ROOT + "abilities/shockwave.png";
            case HeroIcon.ENDURE: return ROOT + "abilities/endure.png";
            default: return null;
        }
    }

    public static String actionTexture(ActionIndicator.Action action) {
        // The action belongs to this hero; a shared icon number alone is insufficient.
        if (action instanceof Combo && ((Combo) action).target == Dungeon.hero
                && eligible(Dungeon.hero)) return ROOT + "abilities/combo_action.png";
        return null;
    }
}
