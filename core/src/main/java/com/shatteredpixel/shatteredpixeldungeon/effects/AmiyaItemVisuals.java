// Arknights Pixel Dungeon: Amiya frozen visual integration, holy1ow, 2026-10-20.
// SPDX-License-Identifier: GPL-3.0-or-later

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAppearance;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

/** Display selection only. No combat RNG, item consumption, or action callbacks. */
public final class AmiyaItemVisuals {
    private AmiyaItemVisuals() {}
    public static final String ROOT="interfaces/operators/amiya/";
    public static boolean active(){return AmiyaEffects.eligible(Dungeon.hero);}
    public static String itemTexture(int image){
        if(!active())return null;
        if(image==ItemSpriteSheet.MASK)return ROOT+"echo-item.png";
        if(image==ItemSpriteSheet.CROWN)return ROOT+"civilight-item.png";
        if(image==ItemSpriteSheet.ARMOR_MAGE)return ROOT+"class-armor.png";
        if(image==ItemSpriteSheet.MAGES_STAFF){
            HeroSubClass form=HeroAppearance.of(Dungeon.hero).subclass;
            return ROOT+(form==HeroSubClass.BATTLEMAGE?"guard-sword":form==HeroSubClass.WARLOCK?"medic-staff":"caster-focus")+".png";
        }
        return null;
    }
    public static String armorAbilityTexture(HeroClass displayedClass, int icon){
        if(displayedClass!=HeroClass.AMIYA || !active())return null;
        switch(icon){
            case com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon.ELEMENTAL_BLAST: return ROOT+"elemental-blast.png";
            case com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon.WILD_MAGIC: return ROOT+"wild-magic.png";
            case com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon.WARP_BEACON: return ROOT+"warp-beacon.png";
            default: return null;
        }
    }
    public static Wand element(Item item){
        return active() && item instanceof MagesStaff ? ((MagesStaff)item).visualWand() : null;
    }
    public static boolean blueCharge(Item item){return active() && (item instanceof MagesStaff || item instanceof Wand);}
    public static void insufficient(Hero hero){if(AmiyaEffects.eligible(hero)&&hero.sprite!=null&&hero.sprite.parent!=null)SpellSprite.show(hero,SpellSprite.AMIYA_INSUFFICIENT);}
    public static void imbue(Hero hero){if(AmiyaEffects.eligible(hero)&&hero.sprite!=null&&hero.sprite.parent!=null)SpellSprite.show(hero,SpellSprite.AMIYA_IMBUE);}
    public static String spellTexture(Char ch,int index){
        if(!(ch instanceof Hero) || !AmiyaEffects.eligible((Hero)ch))return null;
        return index==SpellSprite.CHARGE?ROOT+"charge-feedback.png":index==SpellSprite.AMIYA_INSUFFICIENT?ROOT+"charge-insufficient.png":index==SpellSprite.AMIYA_IMBUE?ROOT+"imbue-transfer.png":null;
    }
    public static boolean crown(Hero hero,boolean complete){
        if(!AmiyaEffects.eligible(hero)||hero.sprite==null||hero.sprite.parent==null)return false;
        hero.sprite.parent.addToFront(new Crown(hero,complete));return true;
    }
    public static final class Crown extends Image {
        private final Hero owner;private final TextureFilm film;private final int[] durations;private float age;
        public final boolean complete;
        Crown(Hero hero,boolean complete){
            super(ROOT+(complete?"civilight-use":"echo-use")+".png");owner=hero;this.complete=complete;
            film=new TextureFilm(texture,24,24);durations=complete?new int[]{80,140,120,160}:new int[]{150,300,200,350};frame(film.get(0));position();
        }
        private void position(){x=owner.sprite.center().x-12;y=owner.sprite.y-21;visible=owner.sprite.visible;alpha(owner.sprite.am);flipHorizontal=owner.sprite.flipHorizontal;}
        @Override public void update(){
            super.update();if(!AmiyaEffects.eligible(owner)||owner.sprite.parent!=parent){killAndErase();return;}
            age+=Game.elapsed;float ms=age*1000;int i=0;while(i<4&&ms>=durations[i])ms-=durations[i++];
            if(i==4){killAndErase();return;}frame(film.get(i));position();
        }
    }
    public static Image soul(SoulMark mark,Char target){
        if(target.sprite==null||target.sprite.parent==null)return null;
        Image icon=new Image(ROOT+"soul-mark.png"){
            @Override public void update(){
                super.update();if(target.buff(SoulMark.class)!=mark||target.sprite==null||target.sprite.parent!=parent){killAndErase();return;}
                visible=mark.amiyaVisual()&&target.sprite.visible;x=target.sprite.center().x-8;y=target.sprite.y-18;alpha(target.sprite.am);
            }
        };
        icon.visible=false;target.sprite.parent.addToFront(icon);icon.update();return icon;
    }
}
