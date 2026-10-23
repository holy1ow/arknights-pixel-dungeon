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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.*;
import com.watabou.utils.*;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

/** External test fixture. Actual GL and production trigger paths, isolated save directory. */
public class HellagurEffectChecks {
 static Path out;static int checks;static List<String> rows=new ArrayList<>();
 static void check(boolean v,String text){checks++;if(!v)throw new AssertionError(text);}
 static void set(Class<?> c,Object o,String n,Object v)throws Exception{AmiyaEffectChecks.set(c,o,n,v);}
 static Object call(Object o,String name,Class<?>[] types,Object...args)throws Exception{Method m=o.getClass().getDeclaredMethod(name,types);m.setAccessible(true);return m.invoke(o,args);}
 static List<HellagurEffects.Effect> effects(Hero h)throws Exception{List<HellagurEffects.Effect> a=new ArrayList<>();for(Gizmo g:AmiyaEffectChecks.members(h.sprite.parent))if(g instanceof HellagurEffects.Effect&&g.exists)a.add((HellagurEffects.Effect)g);return a;}
 static void clear(Hero h)throws Exception{for(HellagurEffects.Effect e:effects(h))e.killAndErase();}
 static Buff[] statuses(Hero h)throws Exception{
  BrokenSeal.WarriorShield shield=new BrokenSeal.WarriorShield();set(BrokenSeal.WarriorShield.class,shield,"cooldown",60);
  Endure.EndureTracker endure=new Endure.EndureTracker();endure.enduring=false;endure.damageBonus=10;endure.hitsLeft=2;
  Buff[] buffs={new Berserk(),new Combo(),shield,new HoldFast(),new Talent.ProvokedAngerTracker(),endure};for(Buff b:buffs)b.target=h;return buffs;
 }
 static void isolated(Hero h)throws Exception{
  HeroClass identity=h.heroClass;HeroSubClass sub=h.subClass;
  for(HeroClass cls:HeroClass.values()){
   h.heroClass=cls;
   int index=0;
   for(Buff b:statuses(h)){
    index++;String counter=b.iconTextDisplay();float fade=b.iconFadePercent();
    for(boolean big:new boolean[]{false,true}){
     BuffIcon icon=new BuffIcon(b,big);String path=HellagurEffects.status(b,big);
     check((path!=null)==(cls==HeroClass.HELLAGUR),"typed status identity "+cls+" "+index);
     check(icon.texture==TextureCache.get(path==null?(big?Assets.Interfaces.BUFFS_LARGE:Assets.Interfaces.BUFFS_SMALL):path),"selected texture");
     check(icon.width==(big?16:7)&&icon.height==(big?16:7),"independent status size");
     Image stock=new Image();stock.resetColor();b.tintIcon(stock);
     check(icon.rm==stock.rm&&icon.gm==stock.gm&&icon.bm==stock.bm&&icon.ra==stock.ra&&icon.ga==stock.ga&&icon.ba==stock.ba,"original tint/invert preserved");
     icon.refresh(BuffIndicator.ARMOR);check(icon.texture==TextureCache.get(big?Assets.Interfaces.BUFFS_LARGE:Assets.Interfaces.BUFFS_SMALL)&&icon.rm==1,"reuse resets texture and tint");icon.destroy();
    }
    check(counter.equals(b.iconTextDisplay())&&fade==b.iconFadePercent(),"counter and mask unchanged");
   }
   int n=effects(h).size();for(HellagurEffects.Kind k:HellagurEffects.Kind.values())HellagurEffects.show(h,k);
   check(effects(h).size()-n==(cls==HeroClass.HELLAGUR?15:0),"effects identity "+cls);clear(h);
  }
  h.heroClass=HeroClass.HELLAGUR;
  Buff nonowner=new Combo();nonowner.target=new Hero();check(HellagurEffects.status(nonowner,true)==null,"other owner rejected");
  Buff subclass=new Combo(){};subclass.target=h;check(HellagurEffects.status(subclass,true)==null,"derived buff not mistaken");
  Endure.EndureTracker enduring=new Endure.EndureTracker();enduring.target=h;check(HellagurEffects.status(enduring,true)==null,"enduring phase has no icon");
  Buff generic=new Barrier();generic.target=h;check(HellagurEffects.status(generic,true)==null,"generic shield original");
  HeroDisguise disguise=new HeroDisguise();set(HeroDisguise.class,disguise,"cls",HeroClass.WARRIOR);disguise.attachTo(h);
  check(!HellagurEffects.show(h,HellagurEffects.Kind.F01)&&HellagurEffects.status(statuses(h)[0],true)==null,"disguise suppresses effects and statuses");disguise.detach();
  h.heroClass=HeroClass.WARRIOR;disguise=new HeroDisguise();set(HeroDisguise.class,disguise,"cls",HeroClass.HELLAGUR);disguise.attachTo(h);check(!HellagurEffects.eligible(h),"fake Hellagur appearance insufficient");disguise.detach();h.heroClass=HeroClass.HELLAGUR;
  h.belongings.thrownWeapon=new ThrowingStone();HellagurEffects.melee(h,h.pos+1);check(effects(h).isEmpty(),"throwing stone no melee arc");h.belongings.thrownWeapon=null;
  // New decoration consumes no game RNG and does not own action completion.
  com.watabou.utils.Random.pushGenerator(919L);int expected=com.watabou.utils.Random.Int();com.watabou.utils.Random.popGenerator();
  com.watabou.utils.Random.pushGenerator(919L);float previous=Game.elapsed;Game.elapsed=.01f;
  for(HellagurEffects.Kind k:HellagurEffects.Kind.values())HellagurEffects.show(h,k);
  for(HellagurEffects.Effect e:effects(h))e.update();
  check(com.watabou.utils.Random.Int()==expected,"decoration consumes no gameplay RNG");com.watabou.utils.Random.popGenerator();
  for(HellagurEffects.Effect e:effects(h)){h.sprite.visible=false;e.update();check(!e.visible,"hidden owner hidden effect");h.sprite.visible=true;}
  h.heroClass=HeroClass.WARRIOR;for(HellagurEffects.Effect e:effects(h))e.update();check(effects(h).isEmpty(),"identity change clears live effects");h.heroClass=HeroClass.HELLAGUR;
  for(HellagurEffects.Kind k:HellagurEffects.Kind.values()){
   HellagurEffects.show(h,k);HellagurEffects.Effect e=effects(h).get(0);check(e.width==k.width&&e.height==k.height,"native canvas");
   Game.elapsed=.001f;e.update();float anchor=e.x+e.origin.x,foot=h.sprite.x+h.sprite.width()/2;check(Math.abs(anchor-foot)<.001,"right anchor");h.sprite.flipHorizontal=true;e.update();check(Math.abs(e.x+e.origin.x-foot)<.001,"left anchor");h.sprite.flipHorizontal=false;
   Game.elapsed=(k.totalMillis()+k.delayMillis())/1000f;e.update();check(effects(h).isEmpty(),"effect expires "+k);
  }
  int health=h.HP;h.HP=0;Berserk rage=Buff.affect(h,Berserk.class);set(Berserk.class,rage,"power",1f);
  Integer oldTalent=h.talents.get(2).put(Talent.DEATHLESS_FURY,1);
  HellagurEffects.show(h,HellagurEffects.Kind.F01);Game.elapsed=.01f;for(HellagurEffects.Effect e:effects(h))e.update();
  check(AmiyaEffectChecks.field(Berserk.class,rage,"state").toString().equals("NORMAL"),"rendering cannot activate Deathless Fury at zero HP");
  h.HP=health;rage.detach();if(oldTalent==null)h.talents.get(2).remove(Talent.DEATHLESS_FURY);else h.talents.get(2).put(Talent.DEATHLESS_FURY,oldTalent);clear(h);
  Game.elapsed=previous;h.heroClass=identity;h.subClass=sub;((HeroSprite)h.sprite).disguise(identity);
 }
 static final String[] CASES={"F01-base-right","F01-base-left","F01-berserker","F01-gladiator","F02","F03","F04","F05","F06","F07","F08","F09-right","F09-left","F10","F11","F12","F13","F14","F15","status","S01","S02","S03","S04","S05","S06","warrior-control","amiya-control"};
 public static void main(String[] args)throws Exception{
  out=Path.of(args[0]);java.nio.file.Files.createDirectories(out);Path profile=java.nio.file.Files.createTempDirectory(out,"profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);Game.version="0.0.4";Game.versionCode=922;
  int width=args.length>1?Integer.parseInt(args[1]):1280,height=args.length>2?Integer.parseInt(args[2]):720;
  Lwjgl3ApplicationConfiguration cfg=new Lwjgl3ApplicationConfiguration();cfg.setInitialVisible(false);cfg.setWindowedMode(width,height);cfg.setForegroundFPS(60);cfg.setIdleFPS(60);
  rows.add("case,frame,effects,heroHP,targetHP,screenX,screenY");
  new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
   int scenario,wait,frame,hp;boolean started,switching;long began=System.nanoTime();AmiyaEffectChecks.TargetRat rat;Set<HellagurEffects.Kind> seen=EnumSet.noneOf(HellagurEffects.Kind.class);float charge;
   String name(){return CASES[scenario];}
   void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
    public void beforeCreate(){try{GamesInProgress.curSlot=1;GamesInProgress.selectedClass=name().startsWith("warrior")?HeroClass.WARRIOR:name().startsWith("amiya")?HeroClass.AMIYA:HeroClass.HELLAGUR;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;Dungeon.init();com.shatteredpixel.shatteredpixeldungeon.levels.Level l=Dungeon.newLevel();int pos=AmiyaIconChecks.room(l);Dungeon.switchLevel(l,pos);if(name().contains("berserker")||name().equals("F07"))Dungeon.hero.subClass=HeroSubClass.BERSERKER;else if(name().contains("gladiator")||name().matches("F0[2-6]"))Dungeon.hero.subClass=HeroSubClass.GLADIATOR;Talent.initSubclassTalents(Dungeon.hero);}catch(Exception e){throw new RuntimeException(e);}}
    public void afterCreate(){try{Hero h=Dungeon.hero;rat=new AmiyaEffectChecks.TargetRat();rat.HP=rat.HT=2000;rat.pos=h.pos+(name().contains("left")?-1:1);GameScene.add(rat);Dungeon.observe();rat.sprite.visible=true;h.sprite.turnTo(h.pos,rat.pos);if(scenario==0)isolated(h);frame=0;wait=20;seen.clear();switching=false;}catch(Exception e){throw new RuntimeException(e);}}
   });}
   void fire()throws Exception{
    Hero h=Dungeon.hero;String n=name();hp=h.HP;
    if(n.startsWith("F01")||n.endsWith("control")){set(Hero.class,h,"attackTarget",rat);h.sprite.attack(rat.pos);}
    else if(n.matches("F0[2-6]")){
     Combo c=Buff.affect(h,Combo.class);set(Combo.class,c,"count",10);set(Combo.class,c,"comboTime",20f);Combo.ComboMove move=Combo.ComboMove.values()[Integer.parseInt(n.substring(1))-2];
     if(move==Combo.ComboMove.PARRY){Buff.affect(h,Combo.ParryTracker.class).parried=true;Combo.RiposteTracker t=Buff.affect(h,Combo.RiposteTracker.class);t.enemy=rat;t.act();}
     else{c.useMove(move);((CellSelector)AmiyaEffectChecks.field(GameScene.class,null,"cellSelector")).listener.onSelect(rat.pos);}
    }else if(n.equals("F07")){Berserk b=Buff.affect(h,Berserk.class);set(Berserk.class,b,"power",1f);b.doAction();}
    else if(n.equals("F08")||n.startsWith("F09")||n.equals("F15")){
     h.belongings.armor=new WarriorArmor();ArmorAbility ability=n.equals("F08")?new HeroicLeap():n.startsWith("F09")?new Shockwave():new Endure();h.armorAbility=ability;Talent.initArmorTalents(h);((ClassArmor)h.belongings.armor).charge=100;charge=ability.chargeUse(h);h.belongings.armor.execute(h,"ABILITY");if(ability.targetingPrompt()!=null)((CellSelector)AmiyaEffectChecks.field(GameScene.class,null,"cellSelector")).listener.onSelect(n.equals("F08")?h.pos+Dungeon.level.width():h.pos+(n.contains("left")?-4:4));
    }else if(n.equals("F10")){Endure.EndureTracker t=Buff.prolong(h,Endure.EndureTracker.class,12);t.damageBonus=20;t.endEnduring();check(!t.enduring&&t.damageBonus==20&&t.hitsLeft==1,"Endure original bonus");}
    else if(n.equals("F11")){TengusMask mask=new TengusMask();mask.collect();mask.execute(h,"WEAR");AmiyaItemChecks.closeWindows();mask.choose(HeroSubClass.BERSERKER);check(h.belongings.getItem(TengusMask.class)==null,"mask consumed once");}
    else if(n.equals("F12")){KingsCrown crown=new KingsCrown();crown.collect();crown.upgradeArmor(h,h.belongings.armor,new Endure());check(h.belongings.getItem(KingsCrown.class)==null&&h.belongings.armor instanceof WarriorArmor,"crown original upgrade");}
    else if(n.equals("F13")){BrokenSeal.WarriorShield shield=h.buff(BrokenSeal.WarriorShield.class);check(shield!=null&&shield.maxShield()>0,"starter seal");int before=shield.shielding(),max=shield.maxShield();shield.activate();check(shield.shielding()==before+max,"original shield gain");}
    else if(n.equals("F14")){BrokenSeal.WarriorShield shield=h.buff(BrokenSeal.WarriorShield.class);set(BrokenSeal.WarriorShield.class,shield,"cooldown",1);int before=shield.shielding();shield.act();check(!shield.coolingDown()&&shield.shielding()==before,"ready feedback grants nothing");}
    else if(n.startsWith("S")){Buff b=statuses(h)[Integer.parseInt(n.substring(1))-1];GameScene.show(new WndInfoBuff(b));}
    else if(n.equals("status")){for(Buff b:statuses(h)){b.attachTo(h);}BuffIndicator.refreshHero();GameScene.show(new WndHero());}
   }
   void capture()throws Exception{
    Hero h=Dungeon.hero;Path dir=out.resolve(name());java.nio.file.Files.createDirectories(dir);Pixmap p=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());PixmapIO.writePNG(Gdx.files.absolute(dir.resolve(String.format("%03d.png",frame)).toString()),p,1,true);p.dispose();Point xy=Camera.main.cameraToScreen(h.sprite.center().x,h.sprite.center().y);rows.add(name()+","+frame+",\""+seen+"\","+h.HP+","+rat.HP+","+xy.x+","+xy.y);
   }
   @Override protected void update(){Game.elapsed=1f/60f;Game.timeTotal+=Game.elapsed;Game.realTime=com.badlogic.gdx.utils.TimeUtils.millis();Game.inputHandler.processAllEvents();com.watabou.noosa.audio.Music.INSTANCE.update();com.watabou.noosa.audio.Sample.INSTANCE.update();Game.scene().update();Camera.updateAll();}
   @Override public void render(){try{
    super.render();if(!started){started=true;start();return;}if(System.nanoTime()-began>300_000_000_000L)throw new AssertionError("timeout "+name());if(switching||Game.switchingScene()||--wait>0)return;
    if(frame==0)fire();for(HellagurEffects.Effect e:effects(Dungeon.hero))seen.add(e.kind);
    check(hTextureMatches(),"fixture sprite matches actual appearance");
    if(frame==1||frame==5||frame==10||frame==18||frame==22||frame==30||frame==60||frame==68||frame==72)capture();
    if(++frame>(name().equals("F06")?240:100)){
     Hero h=Dungeon.hero;String n=name();if(n.startsWith("F"))check(seen.contains(HellagurEffects.Kind.valueOf(n.substring(0,3))),"real trigger observed "+n);if(n.endsWith("control"))check(seen.isEmpty(),"no Hellagur on other identity");check(effects(h).isEmpty(),"all effects cleaned "+n);check(h.HP==hp,"no new healing "+n);
     if(n.equals("F08")||n.startsWith("F09")||n.equals("F15"))check(((ClassArmor)h.belongings.armor).charge>=100-charge&&((ClassArmor)h.belongings.armor).charge<100,"original charge spent "+n);
     System.out.println("PASS "+n+" observed="+seen+" targetHP="+rat.HP);java.nio.file.Files.write(out.resolve("frames.csv"),rows);
     if(++scenario<CASES.length)start();else{java.nio.file.Files.writeString(out.resolve("result.txt"),"PASS "+checks+" assertions; "+CASES.length+" actual GL scenarios; "+(rows.size()-1)+" raw screenshots; isolated fixture, not natural playthrough\n");Gdx.app.exit();}
    }
   }catch(Throwable e){e.printStackTrace();System.exit(1);}}
   boolean hTextureMatches(){return Dungeon.hero.sprite.texture==TextureCache.get(HeroAppearance.of(Dungeon.hero).sprite);}
  },cfg);
 }
}
