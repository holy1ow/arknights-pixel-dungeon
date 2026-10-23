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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.*;
import com.watabou.utils.*;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;

/** Isolated profile, actual GameScene and UI/OpenGL capture; no screenshot compositing. */
public class AmiyaItemChecks {
    static Path output;static int assertions;static List<String> rows=new ArrayList<>();
    static void check(boolean b,String message){assertions++;if(!b)throw new AssertionError(message);}
    static Object field(Class<?> c,Object o,String n)throws Exception{Field f=c.getDeclaredField(n);f.setAccessible(true);return f.get(o);}
    static void set(Class<?> c,Object o,String n,Object v)throws Exception{Field f=c.getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
    static Wand[] wands(){return new Wand[]{new WandOfMagicMissile(),new WandOfFireblast(),new WandOfFrost(),new WandOfLightning(),new WandOfDisintegration(),new WandOfPrismaticLight(),new WandOfCorrosion(),new WandOfLivingEarth(),new WandOfBlastWave(),new WandOfCorruption(),new WandOfWarding(),new WandOfRegrowth(),new WandOfTransfusion()};}
    public static class TargetRat extends Rat { {HP=HT=200;EXP=0;} @Override protected boolean act(){spend(TICK);return true;} }
    static void isolated(Hero h,TargetRat rat)throws Exception{
        HeroClass old=h.heroClass;HeroSubClass sub=h.subClass;
        for(HeroClass id:HeroClass.values()){
            h.heroClass=id;boolean eligible=id==HeroClass.AMIYA;
            for(int image:new int[]{ItemSpriteSheet.MASK,ItemSpriteSheet.CROWN,ItemSpriteSheet.MAGES_STAFF})check((AmiyaItemVisuals.itemTexture(image)!=null)==eligible,"item identity "+id);
            check((AmiyaItemVisuals.spellTexture(h,SpellSprite.CHARGE)!=null)==eligible,"charge identity "+id);
            for(Wand w:wands()){
                MagesStaff staff=new MagesStaff(w);String name=staff.name(),status=staff.status();
                check((AmiyaItemVisuals.element(staff)==w)==eligible,"element identity "+id);
                check(AmiyaItemVisuals.itemTexture(w.image())==null,"original wand untouched "+id);
                check(name.equals(staff.name()) && status.equals(staff.status()),"labels unchanged");
            }
        }
        h.heroClass=HeroClass.AMIYA;
        check(new TengusMask().name().equals("黑王冠的回响"),"localized echo name");check(new KingsCrown().name().equals("文明的存续"),"localized crown name");
        for(HeroSubClass f:new HeroSubClass[]{HeroSubClass.NONE,HeroSubClass.BATTLEMAGE,HeroSubClass.WARLOCK}){h.subClass=f;check(AmiyaItemVisuals.itemTexture(ItemSpriteSheet.MAGES_STAFF).contains(f==HeroSubClass.NONE?"caster-focus":f==HeroSubClass.BATTLEMAGE?"guard-sword":"medic-staff"),"form selection");}
        HeroDisguise disguise=new HeroDisguise();set(HeroDisguise.class,disguise,"cls",HeroClass.MAGE);disguise.attachTo(h);check(!AmiyaItemVisuals.active(),"disguise fallback");check(new TengusMask().name().equals(new TengusMask().trueName()),"original name when disguised");disguise.detach();
        ItemSprite sprite=new ItemSprite(new TengusMask());check(sprite.texture==TextureCache.get(AmiyaItemVisuals.ROOT+"echo-item.png"),"custom item texture");sprite.view(new WandOfFrost());check(sprite.texture==TextureCache.get(Assets.Sprites.ITEMS),"pooled item reset");
        SoulMark mark=Buff.prolong(rat,SoulMark.class,10);check(!mark.amiyaVisual(),"unattributed mark original");float cooldown=mark.visualcooldown();mark.visualSource(h);check(mark.amiyaVisual()&&cooldown==mark.visualcooldown(),"attribution only");
        Bundle bundle=new Bundle();mark.storeInBundle(bundle);SoulMark loaded=new SoulMark();loaded.restoreFromBundle(bundle);check(loaded.amiyaVisual(),"source persists");SoulMark legacy=new SoulMark();legacy.restoreFromBundle(new Bundle());check(!legacy.amiyaVisual(),"old save original fallback");
        BuffIcon small=new BuffIcon(mark,false),large=new BuffIcon(mark,true);check(small.width==7&&large.width==16,"native buff sizes");
        h.heroClass=HeroClass.MAGE;mark.visualSource(h);check(!mark.amiyaVisual(),"other source resets provenance");small.refresh(mark);check(small.texture==TextureCache.get(Assets.Interfaces.BUFFS_SMALL),"buff texture fallback");mark.detach();
        h.heroClass=old;h.subClass=sub;((HeroSprite)h.sprite).disguise(old);
    }
    static final String[] CASES={"caster-equipment","guard-equipment","medic-equipment","mage-equipment-control","shining-equipment-control","inventory","staff-details","mask-choice","crown-choice","echo-use","civilight-use","charge","insufficient","imbue","soul-mark","soul-details","mage-charge-control","mage-crown-control","mage-soul-control"};
    static void closeWindows()throws Exception{for(Gizmo g:new ArrayList<>(AmiyaEffectChecks.members(Game.scene())))if(g instanceof Window)((Window)g).hide();}
    static Window equipment(){
        Window win=new Window();win.resize(280,185);RenderedTextBlock title=PixelScene.renderTextBlock("装备与13种原版灌注 · 真实游戏组件",7);title.setPos(3,2);win.add(title);
        int i=0;for(Wand w:wands()){
            MagesStaff s=new MagesStaff(w);s.identify();int x=(i%3)*93,y=20+(i/3)*30;
            ItemSlot slot=new ItemSlot(s);slot.setRect(x,y,24,24);win.add(slot);
            ItemSprite element=new ItemSprite(w);element.x=x+27;element.y=y+4;win.add(element);
            RenderedTextBlock label=PixelScene.renderTextBlock(w.name(),6);label.maxWidth(45);label.setPos(x+45,y+3);win.add(label);i++;
        }
        ItemSlot a=new ItemSlot(new TengusMask()),b=new ItemSlot(new KingsCrown());a.setRect(100,151,24,24);b.setRect(133,151,24,24);win.add(a);win.add(b);
        MagesStaff empty=new MagesStaff(new WandOfFrost());empty.visualWand().curCharges=0;empty.identify();ItemSlot zero=new ItemSlot(empty);zero.setRect(166,151,24,24);win.add(zero);GameScene.show(win);return win;
    }
    static void capture(String name,int frame,float time,Hero h)throws Exception{
        Path d=output.resolve(name);java.nio.file.Files.createDirectories(d);Pixmap p=ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());PixmapIO.writePNG(Gdx.files.absolute(d.resolve(String.format("%03d.png",frame)).toString()),p,1,true);p.dispose();Point center=Camera.main.cameraToScreen(h.sprite.center().x,h.sprite.center().y);rows.add(name+","+frame+","+time+","+center.x+","+center.y);
    }
    public static void main(String[] args)throws Exception{
        output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);rows.add("scenario,frame,seconds,heroX,heroY");Path profile=java.nio.file.Files.createTempDirectory(output,"profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);Game.version="0.0.4";Game.versionCode=922;
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();config.setInitialVisible(false);config.setWindowedMode(1280,720);config.setForegroundFPS(60);config.setIdleFPS(60);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
            int scenario,wait=30,frame;boolean started,switching,fired;float time;TargetRat rat;MagesStaff staff;TengusMask mask;KingsCrown crown;boolean sawCrown;SoulMark mark;
            void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                public void beforeCreate(){try{String n=CASES[scenario];GamesInProgress.curSlot=1;GamesInProgress.selectedClass=n.startsWith("mage-")?HeroClass.MAGE:n.startsWith("shining-")?HeroClass.SHINING:HeroClass.AMIYA;InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;GameLog.wipe();Dungeon.init();com.shatteredpixel.shatteredpixeldungeon.levels.Level l=Dungeon.newLevel();l.mobs.clear();l.heaps.clear();l.traps.clear();l.plants.clear();int w=l.width(),center=w*(l.height()/2)+w/2;for(int y=-6;y<=6;y++)for(int x=-8;x<=8;x++)l.map[center+y*w+x]=Terrain.EMPTY;l.buildFlagMaps();l.cleanWalls();Dungeon.switchLevel(l,center);Dungeon.hero.subClass=n.startsWith("guard")?HeroSubClass.BATTLEMAGE:n.startsWith("medic")||n.contains("soul")?HeroSubClass.WARLOCK:HeroSubClass.NONE;}catch(Exception e){throw new RuntimeException(e);}}
                public void afterCreate(){try{Hero h=Dungeon.hero;rat=new TargetRat();rat.pos=h.pos+3;GameScene.add(rat);rat.state=rat.WANDERING;rat.sprite.hideSleep();Dungeon.observe();rat.sprite.visible=true;h.sprite.idle();staff=new MagesStaff(new WandOfFrost());staff.identify();h.belongings.weapon=staff;mask=new TengusMask();crown=new KingsCrown();mask.collect(h.belongings.backpack);crown.collect(h.belongings.backpack);h.belongings.armor=new ClothArmor();frame=0;time=0;fired=false;sawCrown=false;mark=null;wait=20;switching=false;if(scenario==0)isolated(h,rat);}catch(Exception e){throw new RuntimeException(e);}}
            });}
            void fire()throws Exception{
                Hero h=Dungeon.hero;String n=CASES[scenario];
                if(n.contains("equipment")){equipment();return;}
                if(n.equals("inventory")){for(Wand w:wands())w.identify().collect(h.belongings.backpack);GameScene.show(new WndBag(h.belongings.backpack));return;}
                if(n.equals("staff-details")){GameScene.show(new WndUseItem(null,staff));return;}
                if(n.equals("mask-choice")){mask.execute(h,"WEAR");check(h.subClass==HeroSubClass.NONE && h.belongings.backpack.items.contains(mask),"choice does not consume");return;}
                if(n.equals("crown-choice")){crown.execute(h,"WEAR");check(h.armorAbility==null && h.belongings.backpack.items.contains(crown),"armor choice does not consume");return;}
                if(n.equals("echo-use")){mask.execute(h,"WEAR");closeWindows();mask.choose(HeroSubClass.BATTLEMAGE);check(h.subClass==HeroSubClass.BATTLEMAGE&&!h.belongings.backpack.items.contains(mask),"original subclass/consumption");return;}
                if(n.contains("crown-control")||n.equals("civilight-use")){crown.upgradeArmor(h,h.belongings.armor,new ElementalBlast());check(h.belongings.armor instanceof ClassArmor && h.armorAbility instanceof ElementalBlast && !h.belongings.backpack.items.contains(crown),"original armor/consumption");return;}
                if(n.equals("charge")||n.equals("mage-charge-control")){ScrollOfRecharging scroll=new ScrollOfRecharging();scroll.collect(h.belongings.backpack);scroll.execute(h,"READ");check(h.buff(Recharging.class)!=null,"original recharging applied");return;}
                if(n.equals("insufficient")){Wand w=staff.visualWand();w.curCharges=0;int hp=h.HP;check(!w.tryToZap(h,rat.pos),"original insufficient gate");check(w.curCharges==0&&h.HP==hp,"failure no cost");return;}
                if(n.equals("imbue")){Wand w=new WandOfFireblast();w.identify();w.collect(h.belongings.backpack);staff.execute(h,"IMBUE");closeWindows();Object selector=field(MagesStaff.class,staff,"itemSelector");Method apply=selector.getClass().getDeclaredMethod("applyWand",Wand.class);apply.setAccessible(true);apply.invoke(selector,w);check(staff.visualWand()==w&&!h.belongings.backpack.items.contains(w),"original imbue consumes wand");return;}
                if(n.contains("soul")){Method proc=Wand.class.getDeclaredMethod("wandProc",Char.class,int.class,int.class);proc.setAccessible(true);for(int attempt=0;attempt<100 && rat.buff(SoulMark.class)==null;attempt++)proc.invoke(null,rat,0,1);mark=rat.buff(SoulMark.class);check(mark!=null,"production mark application");check(mark.amiyaVisual()==(h.heroClass==HeroClass.AMIYA),"mark source isolation");if(n.equals("soul-details"))GameScene.show(new WndInfoBuff(mark));}
            }
            void finishScenario()throws Exception{
                String n=CASES[scenario];Hero h=Dungeon.hero;
                if(n.equals("echo-use")||n.equals("civilight-use"))check(sawCrown,"crown rendered");
                if(n.equals("mage-crown-control"))check(!sawCrown,"other crown unchanged");
                boolean current=false;for(Gizmo g:AmiyaEffectChecks.members(h.sprite.parent))current|=g instanceof AmiyaItemVisuals.Crown && g.alive;check(!current,"crown expired");
                if(mark!=null){mark.detach();check(rat.buff(SoulMark.class)==null,"mark detaches");}
                java.nio.file.Files.write(output.resolve("frames.csv"),rows);System.out.println("PASS "+n);
            }
            @Override protected void update(){Game.elapsed=1f/60f;Game.timeTotal+=Game.elapsed;Game.realTime=com.badlogic.gdx.utils.TimeUtils.millis();Game.inputHandler.processAllEvents();com.watabou.noosa.audio.Music.INSTANCE.update();com.watabou.noosa.audio.Sample.INSTANCE.update();Game.scene().update();Camera.updateAll();}
            @Override public void render(){try{super.render();if(!started){started=true;start();return;}if(switching||Game.switchingScene()||--wait>0)return;time+=Game.elapsed;if(!fired&&time>=.3f){fired=true;fire();}for(Gizmo g:AmiyaEffectChecks.members(Dungeon.hero.sprite.parent))sawCrown|=g instanceof AmiyaItemVisuals.Crown && g.alive;
                if(frame%3==0)capture(CASES[scenario],frame,time,Dungeon.hero);frame++;
                if(time>2f){finishScenario();if(++scenario<CASES.length)start();else{java.nio.file.Files.writeString(output.resolve("result.txt"),"PASS "+assertions+" assertions; "+CASES.length+" actual render scenarios\n");Gdx.app.exit();}}
            }catch(Throwable e){e.printStackTrace();System.exit(1);}}
        },config);
    }
}
