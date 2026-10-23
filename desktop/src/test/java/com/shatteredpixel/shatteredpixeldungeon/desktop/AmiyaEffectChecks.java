// Arknights Pixel Dungeon: adapted by holy1ow, 2026-10-18; Amiya visual integration.
// SPDX-License-Identifier: GPL-3.0-or-later
package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.watabou.noosa.*;
import com.watabou.utils.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

/** Actual GameScene/OpenGL capture with production wand selection, attacks and SoulMark healing.
 * A stationary test rat and isolated room keep timing and visual comparison reproducible. */
public class AmiyaEffectChecks {
    static Path output;
    static List<String> rows=new ArrayList<>();
    static int assertions;
    static void check(boolean result,String message){assertions++;if(!result)throw new AssertionError(message);}
    static Object field(Class<?> type,Object obj,String name)throws Exception{Field f=type.getDeclaredField(name);f.setAccessible(true);return f.get(obj);}
    static void set(Class<?> type,Object obj,String name,Object value)throws Exception{Field f=type.getDeclaredField(name);f.setAccessible(true);f.set(obj,value);}
    static List<Gizmo> members(Group group)throws Exception{return new ArrayList<>((List<Gizmo>)field(Group.class,group,"members"));}
    static Set<AmiyaEffects.Kind> kinds(Hero h)throws Exception{
        Set<AmiyaEffects.Kind> r=EnumSet.noneOf(AmiyaEffects.Kind.class);
        for(Gizmo g:members(h.sprite.parent))if(g instanceof AmiyaEffects.Effect && g.exists)r.add(((AmiyaEffects.Effect)g).kind);
        return r;
    }
    static void clearFx(Hero h)throws Exception{for(Gizmo g:members(h.sprite.parent))if(g instanceof AmiyaEffects.Effect)g.killAndErase();}
    public static class TargetRat extends Rat {
        {HP=HT=200;defenseSkill=0;EXP=0;}
        // A damage probe must not randomly absorb the staff's minimum damage.
        // Only this isolated fixture changes; production Rat remains untouched.
        @Override public int drRoll(){return 0;}
        @Override protected boolean act(){spend(TICK);return true;}
    }
    static class Case {
        String name;HeroClass identity;HeroSubClass form;String action;boolean left;
        Case(String n,HeroClass h,HeroSubClass s,String a,boolean l){name=n;identity=h;form=s;action=a;left=l;}
    }
    static final Case[] CASES={
        new Case("caster-right",HeroClass.AMIYA,HeroSubClass.NONE,"magic",false),
        new Case("caster-left",HeroClass.AMIYA,HeroSubClass.NONE,"magic",true),
        new Case("guard-right",HeroClass.AMIYA,HeroSubClass.BATTLEMAGE,"melee",false),
        new Case("guard-left",HeroClass.AMIYA,HeroSubClass.BATTLEMAGE,"melee",true),
        new Case("medic-right",HeroClass.AMIYA,HeroSubClass.WARLOCK,"magic",false),
        new Case("medic-left",HeroClass.AMIYA,HeroSubClass.WARLOCK,"magic",true),
        new Case("medic-recovery",HeroClass.AMIYA,HeroSubClass.WARLOCK,"recovery",false),
        new Case("medic-fire",HeroClass.AMIYA,HeroSubClass.WARLOCK,"fire",false),
        new Case("medic-frost",HeroClass.AMIYA,HeroSubClass.WARLOCK,"frost",false),
        new Case("medic-lightning",HeroClass.AMIYA,HeroSubClass.WARLOCK,"lightning",false),
        new Case("mage-control",HeroClass.MAGE,HeroSubClass.NONE,"magic",false),
        new Case("shining-control",HeroClass.SHINING,HeroSubClass.NONE,"magic",false),
        new Case("mage-recovery-control",HeroClass.MAGE,HeroSubClass.WARLOCK,"recovery",false)
    };
    static void isolatedChecks(Hero h,TargetRat rat)throws Exception{
        HeroClass identity=h.heroClass;HeroSubClass sub=h.subClass;
        for(HeroClass id:HeroClass.values()){
            h.heroClass=id;
            check(AmiyaEffects.genericMagic(h,new WandOfMagicMissile())==(id==HeroClass.AMIYA),"identity whitelist "+id);
            for(Wand w:new Wand[]{new WandOfFireblast(),new WandOfFrost(),new WandOfLightning(),new WandOfDisintegration(),new WandOfCorrosion(),new WandOfRegrowth(),new WandOfTransfusion(),new WandOfLivingEarth(),new WandOfWarding(),new WandOfPrismaticLight(),new WandOfCorruption(),new WandOfBlastWave()})check(!AmiyaEffects.genericMagic(h,w),"element whitelist "+id+" "+w.getClass());
        }
        h.heroClass=HeroClass.AMIYA;h.subClass=HeroSubClass.BATTLEMAGE;
        Wand cursed=new WandOfMagicMissile();cursed.cursed=true;check(!AmiyaEffects.genericMagic(h,cursed),"curse preserved");
        HeroDisguise disguise=new HeroDisguise();set(HeroDisguise.class,disguise,"cls",HeroClass.MAGE);disguise.attachTo(h);
        check(!AmiyaEffects.genericMagic(h,new WandOfMagicMissile()),"disguise suppresses art");disguise.detach();
        h.belongings.thrownWeapon=new ThrowingStone();AmiyaEffects.swordSwing(h,rat.pos);AmiyaEffects.swordHit(h,rat,true);check(kinds(h).isEmpty(),"thrown weapon has no sword effects");h.belongings.thrownWeapon=null;
        AmiyaEffects.swordHit(h,rat,false);check(kinds(h).isEmpty(),"miss has no sword hit");
        h.subClass=HeroSubClass.WARLOCK;Buff.affect(rat,SoulMark.class);int oldHP=h.HP;h.HP=h.HT;
        rat.defenseProc(h,10);check(kinds(h).isEmpty(),"full HP has no recovery");
        h.HP=h.HT-10;rat.defenseProc(h,0);check(kinds(h).isEmpty(),"zero restoration has no recovery");
        int before=h.HP;rat.defenseProc(h,10);check(h.HP==Math.min(h.HT,before+4),"original SoulMark heal amount");check(kinds(h).contains(AmiyaEffects.Kind.HEAL_RETURN),"positive restoration art");
        clearFx(h);Buff.detach(rat,SoulMark.class);h.HP=oldHP;
        // Two stock missiles advance identically; the decoration never receives either callback.
        h.subClass=HeroSubClass.NONE;int[] callbacks={0,0};Group reference=new Group();
        MagicMissile a=MagicMissile.boltFromChar(reference,MagicMissile.MAGIC_MISSILE,h.sprite,rat.pos,()->callbacks[0]++);
        MagicMissile b=MagicMissile.boltFromChar(h.sprite.parent,MagicMissile.MAGIC_MISSILE,h.sprite,rat.pos,()->callbacks[1]++);
        AmiyaEffects.decorateMissile(h,new WandOfMagicMissile(),b,rat.pos);float saved=Game.elapsed;Game.elapsed=0.01f;
        for(int i=0;i<100;i++){a.update();b.update();check(a.on==b.on&&callbacks[0]==callbacks[1],"stock flight/callback timing");check(a.x==b.x&&a.y==b.y,"stock path unchanged");}
        check(callbacks[0]==1&&callbacks[1]==1,"one callback each");Game.elapsed=saved;reference.destroy();b.killAndErase();clearFx(h);
        h.heroClass=identity;h.subClass=sub;((HeroSprite)h.sprite).disguise(identity);
    }
    static void capture(String name,int frame,float elapsed,Hero h,TargetRat rat)throws Exception{
        Path dir=output.resolve(name);java.nio.file.Files.createDirectories(dir);
        Pixmap image=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        PixmapIO.writePNG(Gdx.files.absolute(dir.resolve(String.format("%03d.png",frame)).toString()),image,1,true);image.dispose();
        Point p=Camera.main.cameraToScreen(h.sprite.center().x,h.sprite.center().y);
        Point q=Camera.main.cameraToScreen(rat.sprite.center().x,rat.sprite.center().y);
        rows.add(name+","+frame+","+elapsed+","+p.x+","+p.y+","+q.x+","+q.y+","+h.HP+","+rat.HP+",\""+kinds(h)+"\"");
    }
    public static void main(String[] args)throws Exception{
        output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);rows.add("scenario,frame,seconds,heroX,heroY,targetX,targetY,heroHP,targetHP,effects");
        Path profile=java.nio.file.Files.createTempDirectory(output,"profile-");Gdx.files=new Lwjgl3Files();
        SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));
        FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");
        SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);
        Game.version="0.0.4";Game.versionCode=922;
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();config.setInitialVisible(false);config.setWindowedMode(1280,720);config.setForegroundFPS(60);config.setIdleFPS(60);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
            int scenario,wait=30,frame,charges,hp;boolean started,switching,fired;float elapsed;TargetRat rat;Wand wand;Set<AmiyaEffects.Kind> seen;
            long began=System.nanoTime();
            void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                public void beforeCreate(){try{
                    Case c=CASES[scenario];GamesInProgress.curSlot=1;GamesInProgress.selectedClass=c.identity;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;Dungeon.init();
                    com.shatteredpixel.shatteredpixeldungeon.levels.Level level=Dungeon.newLevel();level.mobs.clear();level.heaps.clear();level.traps.clear();level.plants.clear();
                    int w=level.width(),center=w*(level.height()/2)+w/2;
                    for(int y=-6;y<=6;y++)for(int x=-8;x<=8;x++)level.map[center+y*w+x]=Terrain.EMPTY;
                    level.buildFlagMaps();level.cleanWalls();Dungeon.switchLevel(level,center);Dungeon.hero.subClass=c.form;
                }catch(Exception e){throw new RuntimeException(e);}}
                public void afterCreate(){try{
                    Hero h=Dungeon.hero;Case c=CASES[scenario];rat=new TargetRat();rat.pos=h.pos+(c.left?-1:1)*(c.action.equals("melee")||c.action.equals("recovery")?1:5);GameScene.add(rat);
                    Dungeon.observe();rat.sprite.visible=true;rat.sprite.turnTo(rat.pos,h.pos);h.sprite.turnTo(h.pos,rat.pos);h.sprite.idle();
                    frame=0;elapsed=0;fired=false;wand=null;seen=EnumSet.noneOf(AmiyaEffects.Kind.class);wait=30;switching=false;
                    if(scenario==0){
                        isolatedChecks(h,rat);
                        // Let the isolated healing assertion's floating text expire
                        // before recording the separate caster scenario.
                        wait=180;
                    }
                }catch(Exception e){throw new RuntimeException(e);}}
            });}
            void fire()throws Exception{
                Hero h=Dungeon.hero;Case c=CASES[scenario];hp=h.HP;
                if(c.action.equals("melee")||c.action.equals("recovery")){
                    if(c.action.equals("recovery")){h.HP=h.HT-10;hp=h.HP;Buff.affect(rat,SoulMark.class);}
                    h.sprite.attack(rat.pos,()->{h.attack(rat,1,0,Float.POSITIVE_INFINITY);h.sprite.idle();});
                }else{
                    wand=c.action.equals("fire")?new WandOfFireblast():c.action.equals("frost")?new WandOfFrost():c.action.equals("lightning")?new WandOfLightning():new WandOfMagicMissile();
                    wand.identify();charges=wand.curCharges;wand.execute(h,Wand.AC_ZAP);
                    ((CellSelector)field(GameScene.class,null,"cellSelector")).listener.onSelect(rat.pos);
                }
            }
            void finishScenario()throws Exception{
                Case c=CASES[scenario];Hero h=Dungeon.hero;
                if(c.identity!=HeroClass.AMIYA)check(seen.isEmpty(),"other identity unchanged "+c.name);
                else if(c.action.equals("magic")){check(seen.contains(AmiyaEffects.Kind.ARTS_PROJECTILE)&&seen.contains(AmiyaEffects.Kind.ARTS_IMPACT),"generic magic art "+c.name);if(c.form==HeroSubClass.WARLOCK)check(seen.contains(AmiyaEffects.Kind.STAFF_RELEASE),"staff release "+c.name);}
                else if(c.action.equals("melee"))check(seen.contains(AmiyaEffects.Kind.SWORD_ARC)&&seen.contains(AmiyaEffects.Kind.SWORD_IMPACT),"sword art "+c.name);
                else if(c.action.equals("recovery"))check(seen.contains(AmiyaEffects.Kind.HEAL_RETURN),"recovery art");
                else check(!seen.contains(AmiyaEffects.Kind.ARTS_PROJECTILE)&&!seen.contains(AmiyaEffects.Kind.ARTS_IMPACT)&&seen.contains(AmiyaEffects.Kind.STAFF_RELEASE),"element art preserved "+c.name);
                if(wand!=null){check(wand.curCharges<charges,"charge spent "+c.name);if(!c.action.equals("fire"))check(wand.curCharges==charges-1,"one charge "+c.name);}
                check(rat.HP<200,"original attack did damage "+c.name);
                if(c.action.equals("recovery"))check(h.HP>hp,"original restoration "+c.name);
                else check(h.HP==hp,"no invented heal "+c.name);
                check(kinds(h).isEmpty(),"effects expire "+c.name);
                System.out.println("PASS "+c.name+" effects="+seen+" targetHP="+rat.HP+" heroHP="+h.HP);
                java.nio.file.Files.write(output.resolve("frames.csv"),rows);
            }
            // Fixed render clock prevents PNG encoding time from skipping short effect frames.
            @Override protected void update(){
                Game.elapsed=1f/60f;Game.timeTotal+=Game.elapsed;Game.realTime=com.badlogic.gdx.utils.TimeUtils.millis();
                Game.inputHandler.processAllEvents();com.watabou.noosa.audio.Music.INSTANCE.update();com.watabou.noosa.audio.Sample.INSTANCE.update();
                Game.scene().update();Camera.updateAll();
            }
            @Override public void render(){try{
                super.render();if(!started){started=true;start();return;}if(System.nanoTime()-began>240_000_000_000L)throw new AssertionError("timeout");
                if(switching||Game.switchingScene()||--wait>0)return;
                elapsed+=Game.elapsed;
                if(!fired&&elapsed>=0.3f){fired=true;fire();}
                seen.addAll(kinds(Dungeon.hero));if(frame%2==0)capture(CASES[scenario].name,frame,elapsed,Dungeon.hero,rat);frame++;
                if(elapsed>1.8f){finishScenario();if(++scenario<CASES.length)start();else{java.nio.file.Files.writeString(output.resolve("result.txt"),"PASS "+assertions+" assertions; "+CASES.length+" real-render scenarios\n");Gdx.app.exit();}}
            }catch(Throwable e){e.printStackTrace();System.exit(1);}}
        },config);
    }
}
