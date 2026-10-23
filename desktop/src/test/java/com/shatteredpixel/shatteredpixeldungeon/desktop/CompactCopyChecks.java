package com.shatteredpixel.shatteredpixeldungeon.desktop;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.noosa.*;
import com.watabou.utils.FileUtils;
import java.nio.file.Path;
import java.util.*;
import java.lang.reflect.*;
/** Compact desktop GL review of production windows from packaged JARs; isolated fixture. */
public class CompactCopyChecks {
 static int checks;static Path out;
 static List<Object> children(Group g)throws Exception{Field f=Group.class.getDeclaredField("members");f.setAccessible(true);return new ArrayList<>((List<Object>)f.get(g));}
 static void click(Object o)throws Exception{Method m=o.getClass().getDeclaredMethod("onClick");m.setAccessible(true);m.invoke(o);}
 static void check(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 static void bounds(Window w)throws Exception{for(Object o:children(w))if(o instanceof RenderedTextBlock){RenderedTextBlock t=(RenderedTextBlock)o;check(t.left()>=-1&&t.right()<=w.camera.width+1,"horizontal text bounds");check(t.top()>=-1&&t.bottom()<=w.camera.height+1,"vertical text bounds");}}
 public static void main(String[] args)throws Exception{
  out=Path.of(args[0]);java.nio.file.Files.createDirectories(out);Path profile=java.nio.file.Files.createTempDirectory(out,"profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);Game.version="0.0.4";Game.versionCode=922;
  SPDSettings.interfaceSize(0);
  String[] names={"watch-details","subclass-choice","subclass-confirm","hot-snow-details","armor-choice","armor-confirm","armor-without-item","no-armor-message"};
  Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();config.setInitialVisible(false);config.setWindowedMode(540,960);config.setForegroundFPS(60);
  new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
   int index,frame;boolean started,switching,shown;
   void start(){switching=true;shown=false;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){public void beforeCreate(){try{GamesInProgress.curSlot=1;GamesInProgress.selectedClass=HeroClass.HELLAGUR;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.init();Dungeon.switchLevel(Dungeon.newLevel(),-1);}catch(Exception e){throw new RuntimeException(e);}}public void afterCreate(){frame=0;switching=false;}});}
   void show()throws Exception{Hero h=Dungeon.hero;h.belongings.armor=new PlateArmor();TengusMask m=new TengusMask();KingsCrown c=new KingsCrown();m.collect();c.collect();Window w=null;
    switch(index){case 0:w=new WndUseItem(null,m);break;case 1:case 2:w=new WndChooseSubclass(m,h);break;case 3:w=new WndUseItem(null,c);break;case 4:case 5:w=new WndChooseAbility(c,h.belongings.armor,h);break;case 6:w=new WndChooseAbility(null,h.belongings.armor,h);break;case 7:h.belongings.armor=null;c.execute(h,"WEAR");check(h.belongings.getItem(KingsCrown.class)==c,"no armor retains item");break;}
    if(w!=null){GameScene.show(w);bounds(w);if(index==2||index==5){for(Object o:children(w))if(o instanceof RedButton){click(o);break;}for(Object o:children(Game.scene()))if(o instanceof WndOptions)bounds((Window)o);}}
   }
   @Override public void render(){try{super.render();if(!started){started=true;start();return;}if(switching||Game.switchingScene())return;if(!shown&&frame++>=40){show();shown=true;frame=0;}else if(shown&&++frame==40){Pixmap p=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());PixmapIO.writePNG(Gdx.files.absolute(out.resolve(names[index]+".png").toString()),p,1,true);p.dispose();System.out.println("PASS "+names[index]);}else if(shown&&frame>45){if(++index<names.length)start();else{java.nio.file.Files.writeString(out.resolve("result.txt"),"PASS "+checks+" bounds/condition assertions; 8 production UI scenes at540x960; packaged JARs, not mobile hardware\n");Gdx.app.exit();}}}catch(Throwable e){e.printStackTrace();System.exit(1);}}
  },config);
 }
}
