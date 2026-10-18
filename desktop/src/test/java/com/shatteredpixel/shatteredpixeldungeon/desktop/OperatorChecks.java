// Arknights Pixel Dungeon: modified/adapted by holy1ow, 2026-10-18.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.gltextures.TextureCache;
import com.watabou.utils.*;
import java.io.*;
import java.util.*;
import static com.shatteredpixel.shatteredpixeldungeon.desktop.HeroClassChecks.*;

/** Differential tests against the actual six original classes, with isolated profiles. */
final class OperatorChecks {
    static class CountingHero extends Hero {
        int attacks,operations;
        @Override public void onAttackComplete(){attacks++;}
        @Override public void onOperateComplete(){operations++;}
    }
    static class AnimationProbe extends HeroClassChecks.UnlinkedHeroSprite {
        @Override public void turnTo(int from,int to){}
        void rates(){eq(run.delay,1f/20);eq(attack.delay,1f/15);}
        void tick(){com.watabou.noosa.Game.elapsed=.05f;updateAnimation();}
    }
    static void animations(HeroClass cls,HeroSubClass sub) {
        CountingHero h=new CountingHero();h.heroClass=cls;h.subClass=sub;Dungeon.hero=h;
        AnimationProbe sprite=new AnimationProbe();float before=h.cooldown();
        sprite.attack(0);for(int i=0;i<25;i++)sprite.tick();check(h.attacks==1,"single attack callback "+cls+sub);
        sprite.operate(0);for(int i=0;i<25;i++)sprite.tick();check(h.operations==1,"single operate callback");
        sprite.read();for(int i=0;i<25;i++)sprite.tick();check(h.operations==2,"single read callback");
        int[] zaps={0};sprite.zap(0,()->{zaps[0]++;sprite.idle();});for(int i=0;i<25;i++)sprite.tick();check(zaps[0]==1,"single zap callback");
        h.HP=0;sprite.updateArmor();for(int i=0;i<25;i++)sprite.tick();
        RectF death=HeroSprite.film(HeroAppearance.resolve(cls,sub,h.tier())).get(11);eq(sprite.frame().left,death.left);eq(sprite.frame().top,death.top);check(h.attacks==1 && h.operations==2,"death has no action callback");
        eq(h.cooldown(),before);sprite.rates();
    }
    static Hero init(HeroClass cls) {
        GamesInProgress.selectedClass=cls; Dungeon.seed=12345; Dungeon.init(); return Dungeon.hero;
    }
    static Bundle save(Hero hero) { Bundle b=new Bundle();hero.storeInBundle(b);return b; }
    static Hero load(Hero hero) throws Exception {
        ByteArrayOutputStream out=new ByteArrayOutputStream();Bundle.write(save(hero),out);
        Hero result=new Hero();Dungeon.hero=result;result.restoreFromBundle(Bundle.read(new ByteArrayInputStream(out.toByteArray())));return result;
    }
    static String loadout(Hero hero) { Bundle b=new Bundle();hero.belongings.storeInBundle(b);return b.toString().replaceAll("\"set_id\":-?\\d+", "\"set_id\":0"); }
    static ArrayList<Float> behavior(HeroClass cls, HeroSubClass sub) {
        Hero h=init(cls);h.subClass=sub;Talent.initSubclassTalents(h);
        h.sprite=new HeroClassChecks.UnlinkedHeroSprite();h.sprite.visible=false;
        h.lvl=12; h.HT=80;h.HP=25;
        Rat enemy=new Rat();enemy.HT=enemy.HP=50;enemy.pos=10;
        ArrayList<Float> result=new ArrayList<>();
        com.watabou.utils.Random.pushGenerator(435);
        for(int i=0;i<12;i++) { result.add((float)h.damageRoll()); result.add(h.attackDelay()); result.add(h.speed()); result.add((float)h.drRoll()); }
        switch(sub) {
            case BERSERKER: {
                Berserk rage=Buff.affect(h,Berserk.class);rage.damage(100);
                result.add(rage.damageFactor(20));result.add((float)rage.currentShieldBoost());
                for(int i=0;i<5;i++)rage.act();result.add(rage.damageFactor(20));
                rage.damage(400);rage.doAction();result.add((float)rage.shielding());for(int i=0;i<3;i++)rage.act();result.add((float)rage.shielding());break;
            }
            case GLADIATOR: {
                Combo combo=Buff.affect(h,Combo.class);for(int i=0;i<5;i++)combo.hit(enemy);
                Bundle state=new Bundle();combo.storeInBundle(state);result.add((float)state.getInt("count"));result.add(state.getFloat("combotime"));break;
            }
            case ASSASSIN: {
                h.invisible=1;Preparation prep=Buff.affect(h,Preparation.class);
                for(int i=0;i<10;i++){prep.act();result.add((float)prep.attackLevel()); result.add(prep.canKO(enemy)?1f:0f);}break;
            }
            case FREERUNNER: {
                Momentum momentum=Buff.affect(h,Momentum.class);for(int i=0;i<7;i++)momentum.gainStack();
                Bundle state=new Bundle();momentum.storeInBundle(state);result.add((float)state.getInt("stacks"));
                result.add(momentum.speedMultiplier());momentum.act();momentum.act();result.add(momentum.iconFadePercent());
                Bundle running=new Bundle();running.put("freerun_turns",14);running.put("freerun_CD",38);running.put("stacks",0);momentum.restoreFromBundle(running);
                result.add(momentum.speedMultiplier());result.add((float)momentum.evasionBonus(h.lvl,2));momentum.act();result.add(momentum.iconFadePercent());break;
            }
            case SNIPER: case WARDEN: {
                SpiritBow bow=h.belongings.getItem(SpiritBow.class);result.add((float)bow.buffedLvl());result.add(bow.delayFactor(h));result.add((float)bow.min());result.add((float)bow.max());
                com.shatteredpixel.shatteredpixeldungeon.levels.Level level=new com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel();level.setSize(8,8);Dungeon.level=level;
                h.pos=10;com.shatteredpixel.shatteredpixeldungeon.actors.Actor.add(h);level.map[10]=com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS;
                com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass.trample(level,10);result.add((float)level.map[10]);check(level.map[10]==com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS,"huntress grass preservation");
                if(sub==HeroSubClass.WARDEN){com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass grass=new com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass();grass.pos=10;grass.activate(h);check(h.buff(Healing.class)!=null,"warden plant special benefit");result.add(h.buff(Healing.class).cooldown());}
                break;
            }
            case CHAMPION: {
                MeleeWeapon weapon=(MeleeWeapon)h.belongings.weapon;
                result.add(weapon.abilityChargeUse(h,enemy));Bundle state=new Bundle();h.buff(MeleeWeapon.Charger.class).storeInBundle(state);result.add((float)state.getInt("charges"));MeleeWeapon.Charger charge=h.buff(MeleeWeapon.Charger.class);result.add((float)charge.chargeCap());charge.act();result.add(charge.partialCharge);break;
            }
            case MONK: {
                MonkEnergy energy=Buff.affect(h,MonkEnergy.class);energy.gainEnergy(enemy);result.add(energy.energy);
                for(MonkEnergy.MonkAbility a:MonkEnergy.MonkAbility.abilities){result.add((float)a.energyCost());energy.energy=20;energy.abilityUsed(a);result.add(energy.energy);}break;
            }
            case PRIEST: case PALADIN:
                result.add(GuidingLight.INSTANCE.chargeUse(h));result.add(HolyWeapon.INSTANCE.chargeUse(h));result.add(HolyWard.INSTANCE.chargeUse(h));result.add(Radiance.INSTANCE.chargeUse(h));result.add(Smite.INSTANCE.chargeUse(h));
                Buff.affect(h,GuidingLight.GuidingLightPriestCooldown.class);result.add(GuidingLight.INSTANCE.chargeUse(h));
                HolyWeapon.HolyWepBuff holy=Buff.affect(h,HolyWeapon.HolyWepBuff.class,50);holy.extend(500);result.add(holy.cooldown());
                for(int tier=1;tier<=4;tier++)result.add((float)ClericSpell.getSpellList(h,tier).size());break;
            case BATTLEMAGE: {
                com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff staff=(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff)h.belongings.weapon;
                try {
                    java.lang.reflect.Field field=staff.getClass().getDeclaredField("wand");field.setAccessible(true);
                    com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand wand=(com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand)field.get(staff);
                    wand.curCharges=0;wand.partialCharge=0;result.add((float)staff.proc(h,enemy,10));result.add(wand.partialCharge);
                    check(wand.partialCharge==.5f,"staff melee recharge");
                }catch(ReflectiveOperationException e){throw new RuntimeException(e);}break;
            }
            case WARLOCK: {
                Buff.affect(enemy,SoulMark.class);h.HP=25;enemy.defenseProc(h,9);result.add((float)h.HP);check(h.HP==29,"physical mark healing rounding");
                enemy.HP=2;h.HP=25;enemy.defenseProc(h,50);result.add((float)h.HP);check(h.HP==26,"mark healing capped by effective remaining HP");
                h.HP=25;enemy.damage(1,new com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile());check(h.HP==25,"wand direct damage grants no mark heal");result.add((float)h.HP);break;
            }
            default:break;
        }
        com.watabou.utils.Random.popGenerator();return result;
    }
    static void run() throws Exception {
        check(HeroClass.gameplayClasses().length==6,"six gameplay pools");
        Set<HeroClass> pools=new HashSet<>();for(HeroClass cls:HeroClass.values())pools.add(cls.gameplayClass());check(pools.size()==6,"no duplicate gameplay pools");
        for(HeroClass cls:HeroClass.values()) {
            HeroClass source=cls.gameplayClass();Hero base=init(source);String expected=loadout(base);
            ArrayList<LinkedHashMap<Talent,Integer>> talents=base.talents;
            Hero h=init(cls);check(loadout(h).equals(expected),"exact loadout "+cls+"\nEXPECTED "+expected+"\nACTUAL "+loadout(h));check(h.talents.equals(talents),"all talents "+cls);
            eq(h.HT,base.HT);eq(h.STR,base.STR);
            check(cls.isUnlocked()==source.isUnlocked(),"unlock "+cls);check(cls.masteryBadge()==source.masteryBadge(),"mastery "+cls);
            check(Arrays.equals(cls.subClasses(),source.subClasses()),"subclass enums "+cls);
            check(ClassArmor.upgrade(h,h.belongings.armor).getClass()==ClassArmor.upgrade(base,base.belongings.armor).getClass(),"class armor "+cls);
            check(RemainsItem.get(cls).getClass()==RemainsItem.get(source).getClass(),"remains "+cls);
            for(int i=0;i<3;i++)check(cls.armorAbilities()[i].getClass()==source.armorAbilities()[i].getClass(),"armor ability "+cls+" "+i);
            for(HeroSubClass sub:new HeroSubClass[]{HeroSubClass.NONE,cls.subClasses()[0],cls.subClasses()[1]}) {
                h=init(cls);h.subClass=sub;if(sub!=HeroSubClass.NONE)Talent.initSubclassTalents(h);
                Hero restored=load(h);check(restored.heroClass==cls && restored.subClass==sub,"roundtrip "+cls+" "+sub);check(restored.talents.equals(h.talents),"roundtrip talents");
                check(restored.className().equals(HeroAppearance.resolve(cls,sub,0).title()),"loaded title");
                Rankings.Record rec=new Rankings.Record();rec.heroClass=cls;rec.subClass=sub;rec.armorTier=4;rec.gameID="qa";
                Bundle rb=new Bundle();rec.storeInBundle(rb);Rankings.Record rr=new Rankings.Record();rr.restoreFromBundle(rb);check(rr.heroClass==cls && rr.subClass==sub,"record roundtrip");
                rb.remove("subclass");rr.restoreFromBundle(rb);check(rr.subClass==HeroSubClass.NONE,"legacy record explicit base fallback");
                for(int tier=0;tier<7;tier++) {
                    HeroAppearance a=HeroAppearance.resolve(cls,sub,tier);var tx=TextureCache.get(a.sprite);var film=HeroSprite.film(a);
                    for(int frame=0;frame<21;frame++) { RectF f=film.get(frame);eq(f.left*tx.width,frame*a.width);eq(f.top*tx.height,tier*a.height);check(f.right<=1 && f.bottom<=1,"form bounds "+a.key); }
                    var icon=HeroSprite.icon(cls,sub,tier);eq(icon.width,a.width);eq(icon.height,a.height);
                    var portrait=a.portrait();eq(portrait.width,24);eq(portrait.height,32);new HeroIcon(cls,sub);
                }
                if(cls.isOperator()) {
                    check(HeroAppearance.resolve(cls,sub,0).portraitSlot>=0,"explicit portrait slot");
                    for(Languages lang:new Languages[]{Languages.CHI_SMPL,Languages.ENGLISH}) {
                        Messages.setup(lang);HeroAppearance a=HeroAppearance.resolve(cls,sub,0);
                        check(!a.title().contains(Messages.NO_TEXT_FOUND),"localized title "+cls+sub+lang);
                        check(!a.shortDesc().contains(Messages.NO_TEXT_FOUND) && !a.description().contains(Messages.NO_TEXT_FOUND),"localized complete appearance text "+cls+sub+lang);
                        check(!cls.desc().contains("The "+source.name().substring(0,1)+source.name().substring(1).toLowerCase()),"independent base description "+cls);
                        if(sub!=HeroSubClass.NONE) {check(a.description().contains(sub.desc()),"complete unchanged rules "+a.key);check(!a.shortDesc().contains(Messages.NO_TEXT_FOUND),"localized short "+a.key);}
                    }
                    Messages.setup(Languages.CHI_SMPL);
                }
                for(HeroClass target:HeroClass.values()) {
                    HeroAppearance disguise=HeroAppearance.resolve(cls,sub,4,target);
                    check(disguise.identity==target,"disguise identity");
                    check(disguise.subclass==HeroSubClass.NONE || Arrays.asList(target.subClasses()).contains(disguise.subclass),"invalid cross-class subclass fallback");
                }
                if(sub!=HeroSubClass.NONE)check(behavior(cls,sub).equals(behavior(source,sub)),"behavior trace "+cls+" "+sub);
                animations(cls,sub);
            }
            // Resolving every appearance must leave the gameplay RNG stream untouched.
            com.watabou.utils.Random.pushGenerator(812);int expectedRandom=com.watabou.utils.Random.Int();com.watabou.utils.Random.popGenerator();
            com.watabou.utils.Random.pushGenerator(812);for(HeroSubClass sub:cls.subClasses())for(int tier=0;tier<7;tier++)HeroAppearance.resolve(cls,sub,tier).portrait();
            check(com.watabou.utils.Random.Int()==expectedRandom,"display RNG isolation "+cls);com.watabou.utils.Random.popGenerator();
            System.out.println("PASS paired loadout/talents/subclasses/armor/unlock/save/record/behavior: "+cls+" -> "+source);
        }
        System.out.println("PASS all 126 operator armor/form mappings and six original display regressions");
    }
}



