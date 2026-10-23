// Arknights Pixel Dungeon: approved Hellagur visual integration, holy1ow, 2026-10-23.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.*;
import com.watabou.utils.PointF;

/** Approved V6 display only. No actors, RNG, combat callbacks, waits or saved state. */
public final class HellagurEffects {
    private HellagurEffects() {}
    public enum Kind {
        F01("F01-melee", 32, 32, 16, 24, false, 35, 60, 60, 45, 40),
        F02("F02-clobber", 32, 32, 16, 16, false, 35, 50, 45, 40, 40),
        F03("F03-slam", 32, 32, 16, 24, false, 35, 65, 65, 65, 40),
        F04("F04-parry", 32, 32, 16, 16, false, 30, 45, 45, 50, 40),
        F05("F05-crush", 48, 32, 24, 24, false, 45, 65, 65, 65, 40),
        F06("F06-fury", 32, 32, 16, 24, false, 25, 35, 35, 25, 30),
        F07("F07-berserk", 40, 32, 20, 28, true, 50, 80, 100, 100, 50),
        F08("F08-leap", 48, 32, 24, 24, false, 50, 90, 100, 110, 50),
        F09("F09-shockwave", 48, 40, 4, 20, false, 70, 120, 130, 140, 40),
        F10("F10-endure", 40, 32, 20, 28, true, 60, 100, 100, 100, 40),
        F11("F11-mask", 32, 40, 16, 34, true, 160, 220, 260, 260, 100),
        F12("F12-crown", 32, 40, 16, 34, true, 120, 200, 220, 240, 80),
        F13("F13-seal", 40, 40, 20, 34, true, 50, 80, 90, 100, 40),
        F14("F14-seal-ready", 40, 40, 20, 34, true, 40, 70, 70, 40),
        F15("F15-endure-brace", 40, 32, 20, 28, true, 50, 70, 70, 40)
        ;
        public final String texture;
        public final int width, height, ax, ay;
        public final boolean behind;
        public final int[] durations;
        Kind(String file, int width, int height, int ax, int ay, boolean behind, int... durations) {
            texture="effects/operators/hellagur/"+file+".png";
            this.width=width;this.height=height;this.ax=ax;this.ay=ay;this.behind=behind;this.durations=durations;
        }
        public int totalMillis(){int sum=0;for(int d:durations)sum+=d;return sum;}
        // The original red screen flash lasts one second; only this decoration waits.
        public int delayMillis(){return this==F07?1050:0;}
    }
    public static boolean eligible(Char owner) {
        return owner instanceof Hero && owner==Dungeon.hero && HellagurVisuals.eligible((Hero)owner);
    }
    private static boolean drawable(Char owner) {
        return eligible(owner) && owner.sprite!=null && owner.sprite.parent!=null;
    }
    public static String status(Buff buff, boolean large) {
        if (!eligible(buff.target) || buff.icon()==BuffIndicator.NONE) return null;
        String id;
        if(buff.getClass()==Berserk.class) id="S01";
        else if(buff.getClass()==Combo.class) id="S02";
        else if(buff.getClass()==BrokenSeal.WarriorShield.class) id="S03";
        else if(buff.getClass()==HoldFast.class) id="S04";
        else if(buff.getClass()==Talent.ProvokedAngerTracker.class) id="S05";
        else if(buff.getClass()==Endure.EndureTracker.class) id="S06";
        else return null;
        return HellagurVisuals.ROOT+"status/"+id+(large?"-16.png":"-7.png");
    }
    public static void melee(Hero hero,int cell) {
        if(drawable(hero) && !(hero.belongings.attackingWeapon() instanceof MissileWeapon)
                && Dungeon.level!=null && Dungeon.level.adjacent(hero.pos,cell)) show(hero,Kind.F01);
    }
    public static void combo(Hero hero,Char enemy,Combo.ComboMove move) {
        if(!drawable(hero) || enemy==null || enemy.sprite==null || !enemy.sprite.visible) return;
        Kind kind;
        switch(move){
            case CLOBBER:kind=Kind.F02;break;
            case SLAM:kind=Kind.F03;break;
            case PARRY:kind=Kind.F04;break;
            case CRUSH:kind=Kind.F05;break;
            case FURY:kind=Kind.F06;break;
            default:return;
        }
        Effect image=add(hero,kind);
        if(kind==Kind.F02 || kind==Kind.F04) image.fixed=enemy.sprite.center();
        if(kind==Kind.F03) image.fixed=foot(enemy.sprite);
        image.targetSprite=enemy.sprite;
        image.position();
    }
    /** Returns true only when the optional replacement was actually created. */
    public static boolean show(Char owner,Kind kind) {
        if(!drawable(owner)) return false;
        add((Hero)owner,kind);return true;
    }
    public static void shockwave(Hero hero,int target) {
        if(!drawable(hero)) return;
        Effect image=add(hero,Kind.F09);
        image.fixed=hero.sprite.center();
        PointF end=DungeonTilemap.raisedTileCenterToWorld(target);
        image.angle=(float)Math.toDegrees(Math.atan2(end.y-image.fixed.y,end.x-image.fixed.x));
        image.flipHorizontal=false;image.position();
        // Original FORCE_CONE rays remain: they own geometry, RNG, lifetime and hit callback.
    }
    private static PointF foot(CharSprite sprite){return new PointF(sprite.x+sprite.width()/2,sprite.y+sprite.height());}
    private static Effect add(Hero owner,Kind kind) {
        Effect image=new Effect(owner,kind);
        if(kind.behind) owner.sprite.parent.addToBack(image);else owner.sprite.parent.addToFront(image);
        image.position();return image;
    }
    public static final class Effect extends Image {
        public final Kind kind;
        public final Hero owner;
        private final HeroSubClass form;
        private final TextureFilm film;
        private float age;
        private PointF fixed;
        private CharSprite targetSprite;
        public Effect(Hero owner,Kind kind) {
            super(kind.texture);this.owner=owner;this.kind=kind;form=HeroAppearance.of(owner).subclass;
            film=new TextureFilm(texture,kind.width,kind.height);frame(film.get(0));
            age=-kind.delayMillis()/1000f;
            flipHorizontal=owner.sprite.flipHorizontal;visible=age>=0 && owner.sprite.visible;alpha(owner.sprite.am);
        }
        private void position() {
            PointF point=fixed;
            if(point==null){point=foot(owner.sprite);flipHorizontal=owner.sprite.flipHorizontal;}
            int ax=flipHorizontal?kind.width-kind.ax:kind.ax;
            origin.set(ax,kind.ay);x=point.x-ax;y=point.y-kind.ay;
        }
        @Override public void update() {
            super.update();
            // Hero.isAlive() can activate Deathless Fury; rendering must never query it.
            if(!drawable(owner) || owner.sprite.parent!=parent || HeroAppearance.of(owner).subclass!=form) {
                killAndErase();return;
            }
            age+=Game.elapsed;
            if(age<0){visible=false;return;}
            float ms=age*1000;
            if(ms>=kind.totalMillis()){killAndErase();return;}
            int frame=0;while(frame<kind.durations.length-1 && ms>=kind.durations[frame])ms-=kind.durations[frame++];
            frame(film.get(frame));position();
            visible=owner.sprite.visible && (targetSprite==null || targetSprite.visible);alpha(owner.sprite.am);
        }
    }
}
