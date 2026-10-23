// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HeroDisguise;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import java.util.EnumMap;

/** Display-only context. Never rolls random numbers or changes gameplay state. */
public final class HeroAppearance {
    public static final String PORTRAITS = "sprites/operators/portraits.png";
    public static final String SUBCLASS_ICONS = "interfaces/operators/subclasses.png";
    private static final EnumMap<HeroClass, EnumMap<HeroSubClass, HeroAppearance>> FORMS = new EnumMap<>(HeroClass.class);
    static {
        add(HeroClass.AMIYA, HeroSubClass.NONE, "amiya_base", 0, -1);
        add(HeroClass.AMIYA, HeroSubClass.BATTLEMAGE, "amiya_guard", 1, 0);
        add(HeroClass.AMIYA, HeroSubClass.WARLOCK, "amiya_medic", 2, 1);
        add(HeroClass.HELLAGUR, HeroSubClass.NONE, "hellagur_base", 3, -1);
        add(HeroClass.HELLAGUR, HeroSubClass.BERSERKER, "hellagur_berserker", 4, 2);
        add(HeroClass.HELLAGUR, HeroSubClass.GLADIATOR, "hellagur_gladiator", 5, 3);
        add(HeroClass.CROWNSLAYER, HeroSubClass.NONE, "crownslayer_base", 6, -1);
        add(HeroClass.CROWNSLAYER, HeroSubClass.ASSASSIN, "crownslayer_assassin", 7, 4);
        add(HeroClass.CROWNSLAYER, HeroSubClass.FREERUNNER, "crownslayer_freerunner", 8, 5);
        add(HeroClass.TYPHON, HeroSubClass.NONE, "typhon_base", 9, -1);
        add(HeroClass.TYPHON, HeroSubClass.SNIPER, "typhon_sniper", 10, 6);
        add(HeroClass.TYPHON, HeroSubClass.WARDEN, "typhon_warden", 11, 7);
        add(HeroClass.DEGENBRECHER, HeroSubClass.NONE, "degenbrecher_base", 12, -1);
        add(HeroClass.DEGENBRECHER, HeroSubClass.CHAMPION, "degenbrecher_champion", 13, 8);
        add(HeroClass.DEGENBRECHER, HeroSubClass.MONK, "degenbrecher_monk", 14, 9);
        add(HeroClass.SHINING, HeroSubClass.NONE, "shining_base", 15, -1);
        add(HeroClass.SHINING, HeroSubClass.PRIEST, "shining_priest", 16, 10);
        add(HeroClass.SHINING, HeroSubClass.PALADIN, "shining_paladin", 17, 11);
    }
    private static void add(HeroClass cls, HeroSubClass sub, String key, int portrait, int icon) {
        FORMS.computeIfAbsent(cls, c -> new EnumMap<>(HeroSubClass.class))
                .put(sub, new HeroAppearance(cls, sub, key, portrait, icon, 0));
    }
    public final HeroClass identity;
    public final HeroSubClass subclass;
    public final String key, sprite;
    public final int portraitSlot, iconSlot, armorTier, width, height;
    public final int paddingLeft, paddingTop, paddingRight, paddingBottom;
    private HeroAppearance(HeroClass cls, HeroSubClass sub, String key, int portrait, int icon, int tier) {
        identity=cls; subclass=sub; this.key=key; portraitSlot=portrait; iconSlot=icon; armorTier=tier;
        // Extra canvas preserves horns, weapons, and complete attack/death poses.
        // This changes frame bounds, not the character's pixel scale.
        boolean hellagurSubclass = cls==HeroClass.HELLAGUR
                && (sub==HeroSubClass.BERSERKER || sub==HeroSubClass.GLADIATOR);
        width=hellagurSubclass?36:(cls==HeroClass.DEGENBRECHER || cls==HeroClass.SHINING)?36:cls==HeroClass.TYPHON?32:cls==HeroClass.AMIYA?24:cls==HeroClass.HELLAGUR?20:cls.isOperator()?16:12;
        height=hellagurSubclass?28:cls==HeroClass.AMIYA?24:cls.isOperator()?21:15;
        // v4 keeps the approved 16x21 body at (2,2) inside a 24x24 art frame.
        // Hellagur's subclass art keeps the existing 20x21 world bounds and 16px
        // logical body: the art foot (18,24) maps to world foot (10,21).
        paddingLeft=hellagurSubclass?8:cls==HeroClass.AMIYA?2:0;
        paddingTop=hellagurSubclass?3:cls==HeroClass.AMIYA?2:0;
        paddingRight=hellagurSubclass?8:cls==HeroClass.AMIYA?6:0;
        paddingBottom=hellagurSubclass?4:cls==HeroClass.AMIYA?1:0;
        sprite=cls.isOperator() ? (key.equals("amiya_base")?Assets.Sprites.AMIYA:"sprites/operators/"+key+".png") : cls.spritesheet();
    }
    public int displayWidth() { return width-paddingLeft-paddingRight; }
    public int displayHeight() { return height-paddingTop-paddingBottom; }
    public static HeroAppearance resolve(HeroClass identity, HeroSubClass sub, int tier) {
        return resolve(identity, sub, tier, null);
    }
    public static HeroAppearance resolve(HeroClass identity, HeroSubClass sub, int tier, HeroClass disguise) {
        HeroClass cls=disguise==null?identity:disguise;
        if (cls==null) cls=HeroClass.WARRIOR;
        if (sub==null || (sub!=HeroSubClass.NONE && sub!=cls.subClasses()[0] && sub!=cls.subClasses()[1])) sub=HeroSubClass.NONE;
        if (!cls.isOperator()) return new HeroAppearance(cls,sub,null,-1,-1,tier);
        HeroAppearance form=FORMS.get(cls).get(sub);
        return new HeroAppearance(cls,sub,form.key,form.portraitSlot,form.iconSlot,tier);
    }
    public static HeroAppearance of(Hero hero) {
        HeroDisguise disguise=hero.buff(HeroDisguise.class);
        return resolve(hero.heroClass,hero.subClass,hero.tier(),disguise==null?null:disguise.getDisguise());
    }
    public String title() {
        return subclass==HeroSubClass.NONE?identity.title():identity.isOperator()?Messages.get(HeroClass.class,key+"_name"):subclass.title();
    }
    public String shortDesc() {
        if (subclass==HeroSubClass.NONE) return identity.shortDesc();
        return identity.isOperator() && subclass!=HeroSubClass.NONE?Messages.get(HeroClass.class,key+"_short_desc"):subclass.shortDesc();
    }
    public String description() {
        if (subclass==HeroSubClass.NONE) return identity.desc();
        // Keep the complete current rules, including the equipped staff effect.
        return identity.isOperator() && subclass!=HeroSubClass.NONE
                ?Messages.get(HeroClass.class,key+"_intro")+"\n\n"+subclass.title()+"\n\n"+subclass.desc():subclass.desc();
    }
    public Image portrait() {
        return identity.isOperator()?new Image(PORTRAITS,portraitSlot*24,0,24,32)
                :new Image(Assets.Sprites.AVATARS,identity.ordinal()*24,0,24,32);
    }
}
