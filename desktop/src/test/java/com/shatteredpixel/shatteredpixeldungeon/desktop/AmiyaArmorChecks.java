package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HeroDisguise;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaItemVisuals;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
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
import java.nio.file.Path;
import java.lang.reflect.Method;

/** Real production windows in GameScene, isolated profile, captured from OpenGL. */
public class AmiyaArmorChecks {
    static int assertions;
    static void check(boolean b,String label){assertions++;if(!b)throw new AssertionError(label);}
    static ArmorAbility[] abilities(){return new ArmorAbility[]{new ElementalBlast(),new WildMagic(),new WarpBeacon()};}
    static final String[] CASES={"amiya-medic","amiya-caster","amiya-guard","mage-stats","shining-stats","amiya-inventory","amiya-armor","mage-armor","amiya-elemental","amiya-wild","amiya-beacon","mage-elemental","amiya-disguised","amiya-choice"};
    static void isolated(Hero h)throws Exception {
        HeroClass original=h.heroClass;HeroSubClass sub=h.subClass;
        for(HeroClass cls:HeroClass.values()){
            h.heroClass=cls;
            boolean amiya=cls==HeroClass.AMIYA;
            String armor=AmiyaItemVisuals.itemTexture(ItemSpriteSheet.ARMOR_MAGE);
            check((armor!=null)==amiya,"armor identity "+cls);
            ItemSprite item=new ItemSprite(new MageArmor());
            check(item.texture==TextureCache.get(amiya?AmiyaItemVisuals.ROOT+"class-armor.png":Assets.Sprites.ITEMS),"actual armor image "+cls);
            item.view(new ClothArmor());check(item.texture==TextureCache.get(Assets.Sprites.ITEMS),"item reuse reset "+cls);item.destroy();
            for(ArmorAbility ability:abilities()){
                String desc=ability.desc();float cost=ability.chargeUse(h);
                HeroIcon icon=new HeroIcon(cls,ability);
                String texture=AmiyaItemVisuals.armorAbilityTexture(cls,ability.icon());
                check((texture!=null)==amiya,"ability identity "+cls);
                check(icon.texture==TextureCache.get(texture==null?Assets.Interfaces.HERO_ICONS:texture),"actual ability image "+cls);
                check(icon.width==16&&icon.height==16,"native dimensions");
                check(desc.equals(ability.desc())&&cost==ability.chargeUse(h),"display does not mutate description/cost");
                HeroIcon other=new HeroIcon(HeroClass.MAGE,ability);check(other.texture==TextureCache.get(Assets.Interfaces.HERO_ICONS),"explicit other class remains original");
                icon.destroy();other.destroy();
            }
            for(Wand wand:AmiyaItemChecks.wands())check(AmiyaItemVisuals.itemTexture(wand.image())==null,"element wand unchanged "+cls);
        }
        h.heroClass=HeroClass.AMIYA;
        HeroIcon held=new HeroIcon(HeroClass.AMIYA,new WildMagic());
        Method refresh=HeroIcon.class.getDeclaredMethod("refreshArmorAppearance");refresh.setAccessible(true);
        HeroDisguise disguise=new HeroDisguise();AmiyaItemChecks.set(HeroDisguise.class,disguise,"cls",HeroClass.MAGE);disguise.attachTo(h);
        refresh.invoke(held);check(held.texture==TextureCache.get(Assets.Interfaces.HERO_ICONS),"existing icon falls back under disguise");
        check(AmiyaItemVisuals.itemTexture(ItemSpriteSheet.ARMOR_MAGE)==null,"armor disguise fallback");
        disguise.detach();refresh.invoke(held);check(held.texture==TextureCache.get(AmiyaItemVisuals.ROOT+"wild-magic.png"),"icon restores after disguise");held.destroy();
        h.heroClass=HeroClass.MAGE;HeroDisguise asAmiya=new HeroDisguise();AmiyaItemChecks.set(HeroDisguise.class,asAmiya,"cls",HeroClass.AMIYA);asAmiya.attachTo(h);
        check(AmiyaItemVisuals.armorAbilityTexture(HeroClass.AMIYA,HeroIcon.WILD_MAGIC)==null,"appearance alone is not actual identity");asAmiya.detach();
        h.heroClass=original;h.subClass=sub;
        for(HeroSubClass form:new HeroSubClass[]{HeroSubClass.NONE,HeroSubClass.BATTLEMAGE,HeroSubClass.WARLOCK}){
            h.subClass=form;check(AmiyaItemVisuals.itemTexture(ItemSpriteSheet.ARMOR_MAGE)!=null,"armor all Amiya forms");
            HeroIcon icon=new HeroIcon(HeroClass.AMIYA,form);
            check(icon.texture==TextureCache.get(form==HeroSubClass.NONE?Assets.Interfaces.HERO_ICONS:HeroAppearance.SUBCLASS_ICONS),"subclass icon route preserved");icon.destroy();
        }
        h.subClass=sub;
        check(AmiyaItemVisuals.armorAbilityTexture(HeroClass.AMIYA,HeroIcon.RATMOGRIFY)==null,"unrelated ability unchanged");
    }
    public static void main(String[] args)throws Exception{
        Path output=Path.of(args[0]);java.nio.file.Files.createDirectories(output);
        AmiyaItemChecks.output=output;AmiyaItemChecks.rows.clear();AmiyaItemChecks.rows.add("scenario,frame,seconds,heroX,heroY");
        Path profile=java.nio.file.Files.createTempDirectory(output,"profile-");Gdx.files=new Lwjgl3Files();SPDSettings.set(new Lwjgl3Preferences(new Lwjgl3FileHandle(profile.resolve("settings.xml").toString(),Files.FileType.Absolute)));FileUtils.setDefaultFileProperties(Files.FileType.Absolute,profile+"/");SPDSettings.language(Languages.CHI_SMPL);SPDSettings.music(false);SPDSettings.soundFx(false);SPDSettings.intro(false);Game.version="0.0.3";Game.versionCode=922;
        Lwjgl3ApplicationConfiguration config=new Lwjgl3ApplicationConfiguration();config.setInitialVisible(false);config.setWindowedMode(1280,720);config.setForegroundFPS(60);config.setIdleFPS(60);
        new Lwjgl3Application(new ShatteredPixelDungeon(new DesktopPlatformSupport(){@Override public void updateSystemUI(){}}){
            int scenario,wait=20,frame;boolean started,switching,fired;float time;MageArmor armor;
            void start(){switching=true;ShatteredPixelDungeon.switchNoFade(GameScene.class,new Game.SceneChangeCallback(){
                public void beforeCreate(){try{
                    String n=CASES[scenario];GamesInProgress.curSlot=1;GamesInProgress.selectedClass=n.startsWith("mage-")?HeroClass.MAGE:n.startsWith("shining-")?HeroClass.SHINING:HeroClass.AMIYA;
                    InterlevelScene.mode=InterlevelScene.Mode.DESCEND;Dungeon.seed=12345;GameLog.wipe();Dungeon.init();com.shatteredpixel.shatteredpixeldungeon.levels.Level l=Dungeon.newLevel();l.mobs.clear();l.heaps.clear();l.traps.clear();l.plants.clear();int w=l.width(),center=w*(l.height()/2)+w/2;for(int y=-6;y<=6;y++)for(int x=-8;x<=8;x++)l.map[center+y*w+x]=Terrain.EMPTY;l.buildFlagMaps();l.cleanWalls();Dungeon.switchLevel(l,center);
                    Dungeon.hero.subClass=n.equals("amiya-guard")?HeroSubClass.BATTLEMAGE:n.equals("amiya-caster")||n.startsWith("mage-")||n.startsWith("shining-")?HeroSubClass.NONE:HeroSubClass.WARLOCK;
                }catch(Exception e){throw new RuntimeException(e);}}
                public void afterCreate(){try{
                    Hero h=Dungeon.hero;h.sprite.idle();h.belongings.weapon=new MagesStaff(new WandOfFrost());h.belongings.weapon.identify();armor=new MageArmor();armor.identify();armor.charge=100;h.belongings.armor=armor;h.armorAbility=new ElementalBlast();Dungeon.observe();
                    if(scenario==0)isolated(h);
                    frame=0;time=0;wait=20;fired=false;switching=false;
                }catch(Exception e){throw new RuntimeException(e);}}
            });}
            void fire()throws Exception{
                Hero h=Dungeon.hero;String n=CASES[scenario];
                if(n.endsWith("inventory")){for(Wand w:AmiyaItemChecks.wands())w.identify().collect(h.belongings.backpack);GameScene.show(new WndBag(h.belongings.backpack));}
                else if(n.endsWith("armor")){GameScene.show(new WndUseItem(null,armor));}
                else if(n.endsWith("choice")){GameScene.show(new WndChooseAbility(new KingsCrown(),armor,h));}
                else if(n.endsWith("elemental")||n.endsWith("wild")||n.endsWith("beacon")||n.endsWith("disguised")){
                    if(n.endsWith("disguised")){HeroDisguise d=new HeroDisguise();AmiyaItemChecks.set(HeroDisguise.class,d,"cls",HeroClass.MAGE);d.attachTo(h);}
                    h.armorAbility=n.endsWith("wild")?new WildMagic():n.endsWith("beacon")?new WarpBeacon():new ElementalBlast();
                    GameScene.show(new WndInfoArmorAbility(h.heroClass,h.armorAbility));
                } else {WndHero.lastIdx=0;GameScene.show(new WndHero());}
                check(armor.charge==100,"opening UI does not spend armor charge "+n);
            }
            @Override protected void update(){Game.elapsed=1f/60f;Game.timeTotal+=Game.elapsed;Game.realTime=com.badlogic.gdx.utils.TimeUtils.millis();Game.inputHandler.processAllEvents();com.watabou.noosa.audio.Music.INSTANCE.update();com.watabou.noosa.audio.Sample.INSTANCE.update();Game.scene().update();Camera.updateAll();}
            @Override public void render(){try{
                super.render();if(!started){started=true;start();return;}if(switching||Game.switchingScene()||--wait>0)return;time+=Game.elapsed;
                if(!fired&&time>=.3f){fired=true;fire();}
                if(frame==48||frame==60)AmiyaItemChecks.capture(CASES[scenario],frame,time,Dungeon.hero);frame++;
                if(frame>65){java.nio.file.Files.write(output.resolve("frames.csv"),AmiyaItemChecks.rows);System.out.println("PASS "+CASES[scenario]);if(++scenario<CASES.length)start();else{java.nio.file.Files.writeString(output.resolve("result.txt"),"PASS "+assertions+" assertions; "+CASES.length+" actual render scenarios\n");Gdx.app.exit();}}
            }catch(Throwable e){e.printStackTrace();System.exit(1);}}
        },config);
    }
}
