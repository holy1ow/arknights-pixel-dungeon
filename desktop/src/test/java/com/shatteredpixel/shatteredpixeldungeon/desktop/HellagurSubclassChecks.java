package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.watabou.noosa.*;
import com.watabou.utils.*;
import java.nio.file.Path;
import java.util.*;
import static com.shatteredpixel.shatteredpixeldungeon.desktop.HeroClassChecks.*;

/** Real game renderer with supplied subclass/pose fixtures, not a natural playthrough. */
public class HellagurSubclassChecks extends OperatorCanvasChecks {
 static final String[] NAMES={"idle","run","die","attack","zap","operate","fly","read"};
 static final int[][] SLOTS={{0,0,0,1,0,0,1,1},{2,3,4,5,6,7},{8,9,10,11,12,11},{13,14,15,0},{13,14,15,0},{16,17,16,17},{18},{19,20,20,20,20,20,20,20,20,19}};
 static final float[] FPS={1,20,20,15,15,8,1,20};
 static final boolean[] LOOP={true,true,false,false,false,false,true,false};
 static void timeline(Hero h)throws Exception{
  HeroSprite s=(HeroSprite)h.sprite;TextureFilm film=HeroSprite.film(HeroAppearance.of(h));
  for(int a=0;a<NAMES.length;a++){
   Class<?> owner=(a==6||a==7)?HeroSprite.class:CharSprite.class;
   MovieClip.Animation anim=(MovieClip.Animation)field(s,owner,NAMES[a]);
   eq(anim.frames.length,SLOTS[a].length);eq(anim.delay,1f/FPS[a]);check(anim.looped==LOOP[a],"original loop flag "+NAMES[a]);
   for(int n=0;n<anim.frames.length;n++){
    RectF got=anim.frames[n],expected=film.get(SLOTS[a][n]);
    eq(got.left,expected.left);eq(got.top,expected.top);eq(got.right,expected.right);eq(got.bottom,expected.bottom);
   }
  }
 }
 public static void main(String[] args)throws Exception{
  output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);
  Path profile=java.nio.file.Files.createTempDirectory(output,"isolated-profile-");Gdx.files=new Lwjgl3Files();
  SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));
  FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);SPDSettings.interfaceSize(2);
  Game.version="0.0.4";Game.versionCode=922;
  Lwjgl3ApplicationConfiguration cfg=new Lwjgl3ApplicationConfiguration();cfg.setInitialVisible(false);cfg.setWindowedMode(1280,720);cfg.setForegroundFPS(60);cfg.setIdleFPS(60);
  new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
   HeroSubClass[] forms={HeroSubClass.BERSERKER,HeroSubClass.GLADIATOR};
   int stage,form,pose,side,wait=35;boolean started,switching;long began=System.nanoTime();List<String> positions=new ArrayList<>();
   void pose()throws Exception{Hero h=Dungeon.hero;HeroSprite s=(HeroSprite)h.sprite;h.subClass=forms[form];s.updateArmor();GameScene.updateAvatar();
    StatusPane status=(StatusPane)field(Game.scene(),GameScene.class,"status");Image avatar=(Image)field(status,StatusPane.class,"avatar");Visual bg=(Visual)field(status,StatusPane.class,"bg");
    eq(avatar.x,PixelScene.align(bg.x-avatar.width/2f+15));eq(avatar.y,PixelScene.align(bg.y-avatar.height/2f+16));
    s.paused=true;s.flipHorizontal=side==1;s.frame(HeroSprite.film(HeroAppearance.of(h)).get(pose));wait=3;}
   void shot(String tag)throws Exception{String name=forms[form].name().toLowerCase()+"-"+tag;capture(name);Hero h=Dungeon.hero;Point p=Camera.main.cameraToScreen(h.sprite.center().x,h.sprite.y+h.sprite.height());positions.add(name+","+p.x+","+p.y+","+Camera.main.zoom);java.nio.file.Files.write(output.resolve("positions.csv"),positions);}
   @Override public void render(){try{
    super.render();if(!started){started=true;switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){public void beforeCreate(){try{GamesInProgress.curSlot=1;GamesInProgress.selectedClass=HeroClass.HELLAGUR;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;Dungeon.init();Dungeon.switchLevel(Dungeon.newLevel(),-1);}catch(Exception e){throw new RuntimeException(e);}}public void afterCreate(){switching=false;wait=35;}});return;}
    if(System.nanoTime()-began>150_000_000_000L)throw new AssertionError("timeout");
    if(switching||Game.switchingScene()||--wait>0)return;Hero h=Dungeon.hero;HeroSprite s=(HeroSprite)h.sprite;
    if(stage==0){geometry(h);h.subClass=forms[form];s.updateArmor();timeline(h);particles(h);stage=1;pose();return;}
    if(stage==1){
     HeroAppearance ap=HeroAppearance.of(h);PointF tile=DungeonTilemap.tileCenterToWorld(h.pos);
     eq(s.x-ap.paddingLeft+18,tile.x);eq(s.y-ap.paddingTop+24,(h.pos/Dungeon.level.width()+1)*16-6);
     eq(s.bodyWidth(),16);eq(s.width(),20);eq(s.height(),21);
     shot(String.format("slot%02d-%s",pose,side==0?"right":"left"));
     if(++pose<21){pose();return;}pose=0;if(++side<2){pose();return;}side=0;
     s.flipHorizontal=false;s.frame(HeroSprite.film(HeroAppearance.of(h)).get(0));s.add(CharSprite.State.SHIELDED);s.aura(0x55AAFF,8);stage=2;wait=45;return;
    }
    if(stage==2){effects(h);shot("shield-aura");s.remove(CharSprite.State.SHIELDED);s.clearAura();s.add(CharSprite.State.BURNING);s.add(CharSprite.State.HEALING);stage=3;wait=45;return;}
    if(stage==3){shot("particles");s.remove(CharSprite.State.BURNING);s.remove(CharSprite.State.HEALING);s.add(CharSprite.State.FROZEN);stage=4;wait=30;return;}
    if(stage==4){shot("frozen");s.remove(CharSprite.State.FROZEN);if(++form<2){h.subClass=forms[form];s.updateArmor();timeline(h);particles(h);stage=1;pose();wait=90;}else{
     java.nio.file.Files.write(output.resolve("metrics.csv"),metrics);java.nio.file.Files.writeString(output.resolve("result.txt"),"PASS 84 actual rendered frame/flip captures + 6 common-effect captures; both forms original 8 action timelines; 7 armor rows; base/disguises/clones/clicks/particles checked. Fixture poses, not manual play.\n");Gdx.app.exit();}
    }
   }catch(Throwable e){e.printStackTrace();System.exit(1);}}
  },cfg);
 }
}
