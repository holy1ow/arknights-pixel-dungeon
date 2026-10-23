# Modification notice — 0.0.4, 2026-10-23

Maintainer: holy1ow. Selectively integrates approved Hellagur subclass sprites (B v14 / A v16), progression items (watch / Hot Snow), portraits (v20 D / v22 F), 27 talent selections, item/ability/status icons, 15 display effects and scoped Chinese/English progression text into public v0.0.3. The larger transparent subclass canvas preserves the original world body, feet, hit/click bounds and action timeline. Shared portrait atlas changes are limited to Hellagur cells 4 and 5. Amiya frozen artwork and the other four operators remain unchanged.

Visual hooks preserve original actions, combat callbacks, damage/healing/charge calculations, item consumption and save identifiers. The already-verified HeroSprite attack lock-order fix releases the outer sprite lock before inserting visual effects; the original CharSprite animation synchronization remains. No new mechanic or gameplay shortcut is added. Public six-operator selection, unlock/tutorial rules, package/save identity, disabled trainer, dependencies and save migration version 922 remain. Public game/build/Windows version is 0.0.4 (tag v0.0.4).

The playable ZIP contains no save or development/test material. New approved sources, reproducible tests, comparison and acceptance records are supplied separately. Existing historical versions and archives remain; third-party dependency source archive from v0.0.1 still applies. Corresponding source: https://github.com/holy1ow/arknights-pixel-dungeon/tree/v0.0.4 .

## Historical modification records

# Modification notice — 0.0.3, 2026-10-20

Maintainer: holy1ow. Corrects the public game version from 0.0.2 to 0.0.3 in build metadata and the About screen. Simplifies the Release download names and puts the self-contained Windows game first. Approved Amiya artwork, frozen visual integration, all gameplay, dependencies, package/save identity and upstream save migration version 922 are unchanged. Source: https://github.com/holy1ow/arknights-pixel-dungeon/tree/v0.0.3 .

## Previous frozen baseline record

# Modification notice — Amiya frozen public baseline, 2026-10-20

Maintainer: **holy1ow**. Selectively imports the already-approved frozen Amiya display integration into the existing 0.0.2 public desktop project. Artwork is copied without regenerating or modifying pixels/animation. This adds approved portraits, staff/item/crown/mask/armor visuals, charge and imbue feedback, soul mark, the final 27 talent icons and status selections, and beacon/teleport visuals. Three statuses retain original shared graphics with Amiya-only tint. The matching two item-message resources are copied byte-for-byte from the frozen integration; no new translations or wording are authored.

The gameplay code imports are the exact frozen implementations (only attribution/header and line-ending adaptation). Combat, healing, charge consumption, turn timing, class/subclass rules and original shields remain inherited. The six-reskin selection pool, independent package and .holy1ow saves, original unlock/tutorial behavior, About credits and JSON-java 20240303 are retained from the public edition. No new mechanics, artwork or feature branch is created. Internal version remains 0.0.2; the dated tag identifies this release.

This is a new public build, **not** a byte-identical copy of the historical 04 runtime. That original runtime used JSON-java 20170516 and upstream save identity, and remains preserved privately without repacking. The original 05 source archive omitted three disabled trainer build-support files; 06 supplements historical recovery. The corresponding source for the new public Windows binary is the dated public tag, not 05/06. The original five archives and their original checksum records are unchanged.

Full frozen histories and test evidence are retained as Release assets. Existing third-party source attachment from v0.0.1 still matches the unchanged public dependencies; no redundant upload. GPL and all upstream notices remain.

## Historical 0.0.2 modification record

# Modification notice — 0.0.2

Adapted by **holy1ow**, 2026-10-18, on top of published v0.0.1 (`39bd1a7132bf61bd05534a7f072d596ecb899670`). Approved Amiya v4 sprite sources and six effects were selectively imported from the developer workspace. All approved image/source bytes are preserved and tracked by `art/manifest.json` and `art/provenance/amiya-0.0.2.json`.

Production changes are limited to Amiya frame padding/body placement (`HeroAppearance`, `CharSprite`, `HeroSprite`, `MirrorSprite`, and the `LightAllySprite` display in `PowerOfMany`), the display-only `AmiyaEffects` class, and visual calls in `Hero`, `Mob`, `Wand`, `WandOfMagicMissile`, and `HeroSprite`. The About screen's displayed release number is updated. Elemental wand implementations, damage/healing/charge formulas, class/subclass rules, languages, shared textures including the yellow charge marker, and other characters' artwork are unchanged. The original missile instance, trajectory, timing, random calls and combat callback remain in control.

The public six-reskin roster, independent application/save identity, original unlock/tutorial behavior, disabled trainer bridge, JSON-java 20240303, license texts and credits remain. Version-only build metadata, targeted tests, previews, asset verification and archive scripts are updated. Dependency versions are unchanged; their matching source attachment is reused from v0.0.1.

## Historical 0.0.1 modification record

# Modification notice — 0.0.1

Publication follow-up: `SPD-classes/build.gradle` changes JSON-java from 20170516 to the public-domain 20240303 release for the desktop distribution. The original copyright notices are retained. Publication documents, the asset verification script and source/art manifests were added on 2026-10-18. The final roster render check uses a clean profile with only Hellagur unlocked.

Adapted and published by **holy1ow**, 2026-10-18. Based on Shattered Pixel Dungeon 4.0.2, commit `57a4e06a4caf162446d1c28caa7983f0493fecf0`. Original copyright and license notices remain in the sources.

Changes include operator art/display integration, a six-reskin selection roster and random picker, desktop packaging fixes, offline news/update adapters, independent application/save identity, and publication documentation. Combat rules, progression, item behavior and class unlock requirements remain inherited. Internal original class identifiers are retained for mechanics and save compatibility; they are absent from the playable selection roster. No character-specific skill redesign is included.

The following imported or changed files differ from that upstream commit (generated publication documents are additional):

- `.gitattributes`
- `.gitignore`
- `art/exports/16fd0f59ed589da799ffa72d9f2c03f78a59856bcf5b99d18c18caeb9436d5ce.png`
- `art/exports/19c5a9981e538b76bf20cf9e1cec69046a0799eff6df82b4720e3a1afbee6314.png`
- `art/exports/214f3f7d704e1a6ae803577677296dbb83ff499e9e42cc1681716e35c332ccb0.png`
- `art/exports/2a4c1aaafe6d38e7ad0415c7d927e8326a502bf3c8a6ca4e779a20fee43b7441.png`
- `art/exports/3296bc8d1585c2bbfbaed6b8f63032471577a37474463c2a5b4b2fbb3adeaa8c.png`
- `art/exports/36805d37b089f8aed5649b52c474013461ac47b05df9d6d454b86ccb699f6fc1.png`
- `art/exports/3836b636bcd1fa4491fc4cb30a23eaab187071bdc377ca35e47313a585b679c1.png`
- `art/exports/3f67a60ccd156192363dcdc8338f9fc2d76697b0581860016a3f482785ff7fd8.png`
- `art/exports/4a245712818cf8618f7038e17f42998dc025f4705369077a2ad18af7eaa27e3e.png`
- `art/exports/4d374b30cbf626065f3d9a6e98d355aa16a2b81f6adaaa82bf9f626c68d77115.png`
- `art/exports/4d5651cc0d48506f0f43ca3bb0216175bb8785c2a66b6631e910f56afde1224c.png`
- `art/exports/522844bbb5c3ee1d1ccf4d0ec15c1dc5935e3e3729c8c7e9491cd30469cea058.png`
- `art/exports/5493d8c062bd3f312a4b53c1b92cc3a1e3da3567df66574f367582ba24c2cf0d.png`
- `art/exports/553fd7f28d61f1d12165dbe56679d8084ed8a169c35ba77b17dbe9e0485a79ce.png`
- `art/exports/65e173f8f8753eb1c2803a21e9dbf0b7e70cf6cc255c6f5cc6fcc3366eb53493.png`
- `art/exports/776445536c4a26bfb4ad1454628da3e0120f33b1f6a728c03582a5d39fc7df9b.png`
- `art/exports/7bef4c35a93a258fc027c9e2d88aa06e0aa38e643e76f1b85ff51e1c072edd79.png`
- `art/exports/83c79c9c738eb9736008e81802a156c2d816895616e85667459ac5d2e49edfdc.png`
- `art/exports/8be41ecf839e2b9620dd9336c1b7bb3dc162e0df86eadb2e3f12536fbd2d0c15.png`
- `art/exports/92f9ae81c61955203b04906b8385399f5c9c112154279704e43d09bfb2b9ac6d.png`
- `art/exports/a4f625b155f29a48b23a6beddea2704abb3d0ae12e6b7a743a765ec7391fd71e.png`
- `art/exports/ae16d08608240eefd536680a0f8e9df163f712b7439cb57c821bc9e4943edd29.png`
- `art/exports/be1b8fa256c2f581f74de92d86f28bdaed8f359b5d10afec16420dcd010358ea.png`
- `art/exports/c459fd20569d07090134d3e0e046ab805a6dc325f746b0286cd196ba12a2f75e.png`
- `art/exports/d0a55cd8264b1047b753eaf516b3b1953a3211034b2c2ca351b83c4a194e4b5b.png`
- `art/exports/d7ccd0184769c2f8705fe695b69f5cfe6eecea45e0986ee3e449fb111fb47ad3.png`
- `art/exports/d929f44362215b0d7bcec31cd35f51995d7cbbc81a0359607e69659ab51f5d1c.png`
- `art/exports/e7ce1ec32e040b66d00c7d5cacc510b528b99ad99f2450fa15f5c7f0eb3ebbd8.png`
- `art/exports/f01f65007e889ae7918a361fc4115e506a10b2bb7e07430b946834a1f6100f6d.png`
- `art/exports/f775c3fe3608820962bd3d4eaa93f3de93c5d2c96e56f85763acdbd43a693454.png`
- `art/manifest.json`
- `art/source/02168c0fed2a108756fb492000ee8c944a4c9dc4651d7b6635eea5dbdd187fa8.aseprite`
- `art/source/0322730f61530732f9941bc3289754e2acb49725b111c68b057b09f33ee0361b.aseprite`
- `art/source/0907cfb7e41d6daa1e56dc6c1f2ee86683e8b2465dc69e6badf3dc0f400bf513.aseprite`
- `art/source/1cda0257a683228bf3d5fccccfdab63704faaac6095ae0cd476e535e8aebc225.aseprite`
- `art/source/1e1a94fb7d49c520daf9285d05c3c07d346be4f6696aa85a31daea05cf7e3aec.aseprite`
- `art/source/210979a8474c4758a8539aea5c1fd3eee387a2ddc06d2b0e550189cd988cc7b0.aseprite`
- `art/source/2683e95afab53072473118be191d798d3aefea4c7da0546cd74b923323afc9bd.aseprite`
- `art/source/279f2f94e51a0018628444f80bb9580f258fbadb6ab4f3753edcde750553efb0.aseprite`
- `art/source/2849ad89f1b7722450ce1b65ce496ffede229c490e7a0c042075071710d58c14.aseprite`
- `art/source/2913c81e412acd1f19f829b6aad7b91305aa61628db9d4250e6e985c84fc9481.aseprite`
- `art/source/3ee6c84535aad4de3ab33437ad9ccf671bebe828a04c6fb7d1094db73123269f.aseprite`
- `art/source/4341e0b44e65e7c6fa847754c0d0f2ae98ae7313b5637c2fd23d1a0fc72e2cc2.aseprite`
- `art/source/4770269abb2fcdd029beddc543085a7fba48c343a84ffedcd8f6fd3acc9c0a00.aseprite`
- `art/source/52e91616a288fdd3371105261c4208f17d3bd6937628bbb63a11bf737bafff2b.aseprite`
- `art/source/56fb86b1873d340c4a07ea8b6a751e64f53c7c3b4c896fde24a25f605891ca86.aseprite`
- `art/source/5f33b8cf36da4354102f3bc97ad1407653d8249b65e715ad4dee68d1e0ddec17.aseprite`
- `art/source/61626d415a1797d4db463b50bd28ad4e525de4a83bd25dc1d22e71532ad0107d.aseprite`
- `art/source/62c3b04a5fd2c40da2b98d60de97d6e61126bce2b1c15748dee68da9f574d55a.aseprite`
- `art/source/631e4b1b0743de157f7213d19864552794df397082e7bfe4fcd5183142c9b8b0.aseprite`
- `art/source/75940315af404e52dd86d5b180292c1bb6f25e892837cb7cfc080d128a36f1fa.aseprite`
- `art/source/78752ccffec9ed936a3a1ef4805d32ece2807a7bc21356eef5139f70c2237327.aseprite`
- `art/source/787c5b91cb3d4a7c852049f773f3ada6eef27dd880a12be2e0cfdb549423d008.aseprite`
- `art/source/79314dc172423d11e6afd6279274bda5dc61f2a0ff8bee651efe4457fdb09d22.aseprite`
- `art/source/822b05a83a0d9608344f1ee4bd24e42c8eaa47c599101145eaf3cac269f1c977.aseprite`
- `art/source/8add8132f9df8ac6770545ce78b7e37c0f58a00b08e368472927b41525c9f76e.aseprite`
- `art/source/8dd50365e1eb66330c5f65c96a48120cf2901a2fdd8bfc8623acad607548ec46.aseprite`
- `art/source/9dcba2f63ae06cccf575e4316e760aace4898c4ecdc4740caaae0f0d80bb7b47.aseprite`
- `art/source/a4ccf63bed5baac0da36f1a30ab17f55f08b04b9ef5c4c90d01dce637003165e.aseprite`
- `art/source/a7013974398e33e85125123fbc70a4612b0b4a8b24923a67763c43b95e1a5460.aseprite`
- `art/source/b7d61b95c793ba72a82d27beb548db32b0e284c13cd02f0968303d4408735a76.aseprite`
- `art/source/c28fd003e0a670494e141bf2ac3dffb6da73572cbb91103f61d91e1cdfb61c4a.aseprite`
- `art/source/c66bdd59ea1e2498eb927279e0010fe3c1b1d5017027b668eaa965d80d7df2ee.aseprite`
- `art/source/ca388b50805baff3d70a8815eba78608a850b3a4ee85d901696dab110c16b8b5.aseprite`
- `art/source/cc4dc657ad9d2223961b0bcce3ee3e8a6f4b8736e108f82e5f8e3ad0d52f65b5.aseprite`
- `art/source/cccda7b9ac58125698b4189203a10d82fa4018bdd91eeb8dd66a047d95766775.aseprite`
- `art/source/d0b6cd088020ef056274288117e3bda98d0b3f9bbee4c9bdcd7c6d3da3706107.aseprite`
- `art/source/d438f857ae4e1c933e5241ad7e70a09790e6f3fe9d063c3246de6553f8ea01b5.aseprite`
- `art/source/e26ce105452b4148dee1c47073ddfb90060703a70dd1726ec58b487cc280a27e.aseprite`
- `art/source/e3e639ed8bd2ce9055c480e48302f923efd541c075d272e4fbe55d2ee2f1a590.aseprite`
- `art/source/ebc32b6cc9f0ce3cc764048540fbb62694951b5d53b7737211ade7f3cffdc06e.aseprite`
- `art/source/f6ad6d978513a88af160a5e9136adfe338338976d54f35a569e8029143c6872a.aseprite`
- `art/source/ff71daa2e471fa9f4115c0d242ca978ed30fc031a2c6e8f6536b446dff7e3309.aseprite`
- `Build-Desktop.cmd`
- `build.gradle`
- `core/build.gradle`
- `core/src/main/assets/interfaces/operators/subclasses.png`
- `core/src/main/assets/messages/actors/actors.properties`
- `core/src/main/assets/messages/actors/actors_zh.properties`
- `core/src/main/assets/messages/scenes/scenes.properties`
- `core/src/main/assets/messages/scenes/scenes_be.properties`
- `core/src/main/assets/messages/scenes/scenes_cs.properties`
- `core/src/main/assets/messages/scenes/scenes_de.properties`
- `core/src/main/assets/messages/scenes/scenes_el.properties`
- `core/src/main/assets/messages/scenes/scenes_eo.properties`
- `core/src/main/assets/messages/scenes/scenes_es.properties`
- `core/src/main/assets/messages/scenes/scenes_fr.properties`
- `core/src/main/assets/messages/scenes/scenes_hu.properties`
- `core/src/main/assets/messages/scenes/scenes_in.properties`
- `core/src/main/assets/messages/scenes/scenes_it.properties`
- `core/src/main/assets/messages/scenes/scenes_ja.properties`
- `core/src/main/assets/messages/scenes/scenes_ko.properties`
- `core/src/main/assets/messages/scenes/scenes_nl.properties`
- `core/src/main/assets/messages/scenes/scenes_pl.properties`
- `core/src/main/assets/messages/scenes/scenes_pt.properties`
- `core/src/main/assets/messages/scenes/scenes_ru.properties`
- `core/src/main/assets/messages/scenes/scenes_sv.properties`
- `core/src/main/assets/messages/scenes/scenes_tr.properties`
- `core/src/main/assets/messages/scenes/scenes_uk.properties`
- `core/src/main/assets/messages/scenes/scenes_vi.properties`
- `core/src/main/assets/messages/scenes/scenes_zh-hant.properties`
- `core/src/main/assets/messages/scenes/scenes_zh.properties`
- `core/src/main/assets/messages/windows/windows.properties`
- `core/src/main/assets/messages/windows/windows_be.properties`
- `core/src/main/assets/messages/windows/windows_cs.properties`
- `core/src/main/assets/messages/windows/windows_de.properties`
- `core/src/main/assets/messages/windows/windows_el.properties`
- `core/src/main/assets/messages/windows/windows_eo.properties`
- `core/src/main/assets/messages/windows/windows_es.properties`
- `core/src/main/assets/messages/windows/windows_fr.properties`
- `core/src/main/assets/messages/windows/windows_hu.properties`
- `core/src/main/assets/messages/windows/windows_in.properties`
- `core/src/main/assets/messages/windows/windows_it.properties`
- `core/src/main/assets/messages/windows/windows_ja.properties`
- `core/src/main/assets/messages/windows/windows_ko.properties`
- `core/src/main/assets/messages/windows/windows_nl.properties`
- `core/src/main/assets/messages/windows/windows_pl.properties`
- `core/src/main/assets/messages/windows/windows_pt.properties`
- `core/src/main/assets/messages/windows/windows_ru.properties`
- `core/src/main/assets/messages/windows/windows_sv.properties`
- `core/src/main/assets/messages/windows/windows_tr.properties`
- `core/src/main/assets/messages/windows/windows_uk.properties`
- `core/src/main/assets/messages/windows/windows_vi.properties`
- `core/src/main/assets/messages/windows/windows_zh-hant.properties`
- `core/src/main/assets/messages/windows/windows_zh.properties`
- `core/src/main/assets/splashes/amiya.jpg`
- `core/src/main/assets/splashes/operators/crownslayer.jpg`
- `core/src/main/assets/splashes/operators/degenbrecher.jpg`
- `core/src/main/assets/splashes/operators/hellagur.jpg`
- `core/src/main/assets/splashes/operators/shining.jpg`
- `core/src/main/assets/splashes/operators/typhon.jpg`
- `core/src/main/assets/sprites/amiya.png`
- `core/src/main/assets/sprites/avatars.png`
- `core/src/main/assets/sprites/operators/amiya_guard.png`
- `core/src/main/assets/sprites/operators/amiya_medic.png`
- `core/src/main/assets/sprites/operators/crownslayer_assassin.png`
- `core/src/main/assets/sprites/operators/crownslayer_base.png`
- `core/src/main/assets/sprites/operators/crownslayer_freerunner.png`
- `core/src/main/assets/sprites/operators/degenbrecher_base.png`
- `core/src/main/assets/sprites/operators/degenbrecher_champion.png`
- `core/src/main/assets/sprites/operators/degenbrecher_monk.png`
- `core/src/main/assets/sprites/operators/hellagur_base.png`
- `core/src/main/assets/sprites/operators/hellagur_berserker.png`
- `core/src/main/assets/sprites/operators/hellagur_gladiator.png`
- `core/src/main/assets/sprites/operators/portraits.png`
- `core/src/main/assets/sprites/operators/shining_base.png`
- `core/src/main/assets/sprites/operators/shining_paladin.png`
- `core/src/main/assets/sprites/operators/shining_priest.png`
- `core/src/main/assets/sprites/operators/typhon_base.png`
- `core/src/main/assets/sprites/operators/typhon_sniper.png`
- `core/src/main/assets/sprites/operators/typhon_warden.png`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Actor.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/HoldFast.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Hunger.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/abilities/cleric/PowerOfMany.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroAppearance.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroSubClass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/GuidingLight.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Talent.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Mob.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/Blacksmith.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/Shopkeeper.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/VaultMirror.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/Wandmaker.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Skeleton.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Assets.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Badges.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/FloatingText.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/ShieldHalo.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/ArcaneResin.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/Armor.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/ClassArmor.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/glyphs/Stone.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/Artifact.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/BrokenSeal.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/keys/WornKey.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/KindofMisc.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/KindOfWeapon.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/remains/RemainsItem.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfEnergy.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/rings/RingOfForce.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls/exotic/ScrollOfMetamorphosis.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/TengusMask.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/wands/Wand.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/MeleeWeapon.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/missiles/MissileWeapon.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/features/HighGrass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/Level.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Rankings.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/AboutScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/GameScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/HeroSelectScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/PixelScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/RankingsScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/StartScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/SurfaceScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/TitleScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/WelcomeScene.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/SPDSettings.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/CharSprite.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/HeroSprite.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/MirrorSprite.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/tiles/FogOfWar.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/trainer/TrainerBridge.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/CharHealthIndicator.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/HeroIcon.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/Icons.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndChooseSubclass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndGame.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndGameInProgress.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndHeroInfo.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndInfoSubclass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndRanking.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSettings.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndUpgrade.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndVictoryCongrats.java`
- `desktop/build.gradle`
- `desktop/src/main/java/com/shatteredpixel/shatteredpixeldungeon/desktop/DesktopLauncher.java`
- `desktop/src/test/java/com/shatteredpixel/shatteredpixeldungeon/desktop/HeroClassChecks.java`
- `desktop/src/test/java/com/shatteredpixel/shatteredpixeldungeon/desktop/HeroSelectionChecks.java`
- `desktop/src/test/java/com/shatteredpixel/shatteredpixeldungeon/desktop/OperatorCanvasChecks.java`
- `desktop/src/test/java/com/shatteredpixel/shatteredpixeldungeon/desktop/OperatorChecks.java`
- `desktop/src/test/java/com/shatteredpixel/shatteredpixeldungeon/desktop/OperatorRuntimeChecks.java`
- `docs/images/amiya-idle.gif`
- `docs/images/crownslayer-idle.gif`
- `docs/images/degenbrecher-idle.gif`
- `docs/images/hellagur-idle.gif`
- `docs/images/shining-idle.gif`
- `docs/images/typhon-idle.gif`
- `gradle.properties`
- `services/news/offlineNews/build.gradle`
- `services/news/offlineNews/src/main/java/com/shatteredpixel/shatteredpixeldungeon/services/news/NewsImpl.java`
- `services/updates/offlineUpdates/build.gradle`
- `services/updates/offlineUpdates/src/main/java/com/shatteredpixel/shatteredpixeldungeon/services/updates/UpdateImpl.java`
- `settings.gradle`
- `Start-Game.cmd`
