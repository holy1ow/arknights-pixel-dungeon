package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.*;
import com.watabou.utils.*;
import java.nio.file.Path;
import java.util.*;

/** Actual packaged UI: original atlas and frame, Amiya-only tint, unchanged counters and shield. */
public class AmiyaStatusTintChecks {
 static int assertions;static Path output;static List<String> shots=new ArrayList<>();
 static void check(boolean b,String label){assertions++;if(!b)throw new AssertionError(label);}
 static Buff[] statuses(){return new Buff[]{new Talent.LingeringMagicTracker(),new ScrollEmpower(),new WandOfMagicMissile.MagicCharge()};}
 static void color(Image im,Image expected){check(im.rm==expected.rm&&im.gm==expected.gm&&im.bm==expected.bm,"RGB tint");}
 static void checks(Hero h)throws Exception{
  HeroClass initial=h.heroClass;
  for(HeroClass cls:HeroClass.values()){
   h.heroClass=cls;
   for(Buff buff:statuses()){
    buff.target=h;String text=buff.iconTextDisplay(),desc=buff.desc();float fade=buff.iconFadePercent();
    for(boolean large:new boolean[]{false,true}){
     BuffIcon im=new BuffIcon(buff,large);Image expected=new Image();if(cls==HeroClass.AMIYA)expected.hardlight(0x45B5E0);else buff.tintIcon(expected);
     color(im,expected);check(im.texture==TextureCache.get(large?Assets.Interfaces.BUFFS_LARGE:Assets.Interfaces.BUFFS_SMALL),"original atlas for all heroes");
     BuffIcon plain=new BuffIcon(buff.icon(),large);RectF f=im.frame(),p=plain.frame();check(f.left==p.left&&f.top==p.top&&f.right==p.right&&f.bottom==p.bottom,"original frame");check(im.width==(large?16:7),"original dimensions");
     im.refresh(buff.icon());check(im.rm==1&&im.gm==1&&im.bm==1,"pooled reset");im.refresh(buff);color(im,expected);im.destroy();plain.destroy();
    }
    check(text.equals(buff.iconTextDisplay())&&desc.equals(buff.desc())&&fade==buff.iconFadePercent(),"counter description and fade unchanged");
   }
  }
  h.heroClass=HeroClass.AMIYA;
  for(Buff buff:new Buff[]{new Talent.LingeringMagicTracker(){},new ScrollEmpower(){},new WandOfMagicMissile.MagicCharge(){},new Recharging(),new WandEmpower(),new Burning()}){
   buff.target=h;check(!AmiyaIconVisuals.tintStatus(buff,new Image()),"unselected/exact class isolation");
  }
  for(Buff buff:statuses()){
   buff.target=new Rat();check(!AmiyaIconVisuals.tintStatus(buff,new Image()),"mob isolation");buff.target=null;check(!AmiyaIconVisuals.tintStatus(buff,new Image()),"null target isolation");
  }
  HeroDisguise disguise=new HeroDisguise();AmiyaItemChecks.set(HeroDisguise.class,disguise,"cls",HeroClass.MAGE);disguise.attachTo(h);
  for(Buff buff:statuses()){buff.target=h;check(!AmiyaIconVisuals.tintStatus(buff,new Image()),"Amiya disguised as Mage stays original");}
  disguise.detach();h.heroClass=HeroClass.MAGE;disguise=new HeroDisguise();AmiyaItemChecks.set(HeroDisguise.class,disguise,"cls",HeroClass.AMIYA);disguise.attachTo(h);
  for(Buff buff:statuses()){buff.target=h;check(!AmiyaIconVisuals.tintStatus(buff,new Image()),"Mage disguised as Amiya stays original");}
  disguise.detach();h.heroClass=initial;
  ((com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite)h.sprite).disguise(initial);
  check(h.sprite.texture==TextureCache.get(HeroAppearance.of(h).sprite),"restore visible sprite after identity probes");
 }
 static void closeWindows()throws Exception{for(Gizmo g:new ArrayList<>(AmiyaEffectChecks.members(Game.scene())))if(g instanceof Window)((Window)g).hide();}
 static void capture(String name){Pixmap p=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());PixmapIO.writePNG(Gdx.files.absolute(output.resolve(name+".png").toString()),p,-1,true);p.dispose();shots.add(name);}
 public static void main(String[] args)throws Exception{
  output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);Path profile=java.nio.file.Files.createTempDirectory(output,"profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);Game.version="0.0.2";Game.versionCode=922;
  Lwjgl3ApplicationConfiguration cfg=new Lwjgl3ApplicationConfiguration();cfg.setInitialVisible(false);cfg.setWindowedMode(1280,720);cfg.setForegroundFPS(60);cfg.setIdleFPS(60);
  new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
   final String[] names={"amiya-caster","amiya-guard","amiya-medic","mage-control"};int scenario,stage,wait=30;boolean started,switching;Buff[] buffs;
   void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
    public void beforeCreate(){try{GamesInProgress.curSlot=1;GamesInProgress.selectedClass=scenario==3?HeroClass.MAGE:HeroClass.AMIYA;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;GameLog.wipe();Dungeon.init();Dungeon.switchLevel(Dungeon.newLevel(),-1);Dungeon.hero.subClass=scenario==1?HeroSubClass.BATTLEMAGE:scenario==2?HeroSubClass.WARLOCK:HeroSubClass.NONE;}catch(Exception e){throw new RuntimeException(e);}}
    public void afterCreate(){try{
     Hero h=Dungeon.hero;if(scenario==0)checks(h);
     buffs=new Buff[]{Buff.prolong(h,Talent.LingeringMagicTracker.class,5),Buff.affect(h,ScrollEmpower.class),Buff.prolong(h,WandOfMagicMissile.MagicCharge.class,4)};
     ((ScrollEmpower)buffs[1]).reset(3);WandOfMagicMissile wand=new WandOfMagicMissile();wand.upgrade(3);((WandOfMagicMissile.MagicCharge)buffs[2]).setup(wand);
     Buff.affect(h,Barrier.class).setShield(12);BuffIndicator.refreshHero();
     check(buffs[1].iconTextDisplay().equals("3"),"scroll count 3");check(((WandOfMagicMissile.MagicCharge)buffs[2]).level()==wand.buffedLvl(),"original wand level");
     stage=0;wait=150;switching=false;
    }catch(Exception e){throw new RuntimeException(e);}}
   });}
   @Override public void render(){try{
    super.render();if(!started){started=true;start();return;}if(switching||Game.switchingScene()||--wait>0)return;
    String name=names[scenario];wait=25;
    if(stage==0){capture(name+"-hud");WndHero.lastIdx=0;GameScene.show(new WndHero());stage++;}
    else if(stage==1){capture(name+"-status");closeWindows();if(scenario==0){GameScene.show(new WndInfoBuff(buffs[0]));stage++;}else finishScenario();}
    else{capture(name+"-detail-"+(stage-2));closeWindows();if(stage<4){GameScene.show(new WndInfoBuff(buffs[stage-1]));stage++;}else finishScenario();}
   }catch(Throwable e){e.printStackTrace();System.exit(1);}}
   void finishScenario()throws Exception{System.out.println("PASS "+names[scenario]);if(++scenario<4)start();else{java.nio.file.Files.write(output.resolve("screenshots.txt"),shots);java.nio.file.Files.writeString(output.resolve("result.txt"),"PASS "+assertions+" assertions; "+shots.size()+" real OpenGL screenshots; all hero identities; original shield retained\n");Gdx.app.exit();}}
  },cfg);
 }
}
