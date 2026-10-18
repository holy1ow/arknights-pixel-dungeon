# Third-party notices

This file supplements LICENSE.txt, copyright headers, the in-game About screen, the complete license texts in `licenses/`, dependency archives and the bundled runtime's `legal/` directory. Original notices must be retained when redistributing. Some upstream JARs omit license metadata, so the applicable license texts are included separately in the game archive and this source repository.

## Game and media credits

Pixel Dungeon — Oleg Dolya (Watabou); Shattered Pixel Dungeon — Evan Debenham and contributors. Inspired by Brian Walker's Brogue. Splash and dungeon art — Aleksandar Komitov; composer — Lumine Haaristo; item pixel art — PumpkinVolt; sound effects — Celesti; additional pixel art — Alastair Braun; music — Cube Code; Pixel Dungeon GDX — Edu García; Shattered GDX assistance — Kevin MacMartin. Translations: the upstream Transifex translation community, with original translator entries retained in the language resources.

These upstream materials are retained from Shattered Pixel Dungeon 4.0.2. Game assets are covered by the upstream distribution's notices, with the sample-specific exceptions below. New operator artwork was produced with image generation and LibreSprite editing in the local project; its installed editable sources are included in `art/source/`. Underlying Arknights characters and trademarks are not owned by this project.

## Freesound samples

The following attribution is preserved from the upstream About screen; its Creative Commons Attribution group does not specify a license version. Consult the original sample entry for version-specific terms. Source: https://freesound.org/ ; Creative Commons licenses: https://creativecommons.org/licenses/ ; CC0: https://creativecommons.org/publicdomain/zero/1.0/ .

Creative Commons Attribution:

- SFX ATTACK SWORD 001.wav — JoelAudio.
- Pack: Slingshots and Longbows — saturdaysoundguy.
- Cracking/Crunching, A.wav — InspectorJ.
- Extracting a sword.mp3 — Taira Komori.
- Pack: Uni Sound Library — timmy h123.

Creative Commons Zero:

- Pack: Movie Foley: Swords — Black Snow.
- machine gun shot 2.flac; m240h machine gun burst 4.flac — qubodup.
- Pack: Onomatopoeia — Adam N.
- Pack: Watermelon — lolamadeus.
- metal chain — Mediapaja2009.
- Pack: Sword Clashes Pack — JohnBuhr.
- Pack: Metal Clangs and Pings — wilhellboy.
- Pack: Stabbing Stomachs & Crushing Skulls — TheFilmLook.
- Sheep bleating — zachrau.
- Lemon,Juicy,Squeeze,Fruit.wav; Lemon,Squeeze,Squishy,Fruit.wav — Filipe Chagas.

## Libraries and runtime

- libGDX 1.14.0, gdx-controllers 2.2.4 and associated loaders: Apache License 2.0. https://github.com/libgdx/libgdx ; https://github.com/libgdx/gdx-controllers .
- LWJGL and native modules: BSD-style licenses and bundled third-party native notices. https://github.com/LWJGL/lwjgl3 . Individual dependency JARs are preserved in the Windows application's `app/` directory, including their original license metadata.
- JSON-java **20240303**: Public Domain. https://github.com/stleary/JSON-java/tree/20240303 . The old upstream 20170516 dependency used a restrictive JSON license; the desktop-only edition uses the public-domain release to avoid carrying that restriction. Save/JSON behavior is covered by the desktop regression checks.
- JLayer 1.0.1-gdx (JavaZoom) and JOrbis 0.0.17 (JCraft): GNU LGPL, as specified in their source distributions. Their exact Maven source archives and POMs are included in `third-party-sources.zip`. The Windows application keeps them as separate JARs; users may replace/relink these libraries subject to the applicable licenses. Jamepad 2.26.5.0 and its SDL/native components retain their Apache/zlib and component-specific notices in their source/archive metadata.
- Gradle Wrapper / Gradle: Apache License 2.0. https://github.com/gradle/gradle . Build plugin `org.beryx.runtime` 2.0.1: Apache License 2.0, https://github.com/beryx/badass-runtime-plugin . They are build tools, not part of the game runtime.
- Eclipse Temurin / OpenJDK 17.0.17+10, Windows x64: GNU GPL v2 with the Classpath Exception, plus component-specific notices in the runtime's `legal/` directory. The package contains a jlink module subset with unchanged runtime implementations. Exact upstream source: https://github.com/adoptium/jdk17u/tree/jdk-17.0.17%2B10 ; source archive: https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.17%2B10/OpenJDK17U-jdk-sources_17.0.17_10.tar.gz . Runtime binaries: https://github.com/adoptium/temurin17-binaries/releases/tag/jdk-17.0.17%2B10 .

OpenAL Soft 1.23.1 is the native audio implementation bundled with LWJGL 3.3.3 (verified from the packaged DLL version string). Its LGPL source and the LWJGL 3.3.3 native binding/build source are included alongside the Java library sources. Portions of this software are copyright © the FreeType Project (www.freetype.org). All rights reserved. FreeType is distributed under its FreeType License option, reproduced in `licenses/FreeType-FTL.txt`.

The GPL license of the game does not erase these component notices or grant third-party trademark rights. Source and license metadata for bundled libraries are archived alongside the Windows Release as `third-party-sources.zip`; that archive is development/legal material and is not installed into the game.

## 0.0.2 dependency reuse

0.0.2 uses the same external runtime JARs, Java 17 runtime and license files as v0.0.1; only the game modules changed. The corresponding source attachment remains available at https://github.com/holy1ow/arknights-pixel-dungeon/releases/download/v0.0.1/third-party-sources.zip . Its SHA-256 and byte-for-byte dependency comparison are recorded in the 0.0.2 verification report. It is intentionally not uploaded a second time. The complete license texts remain included in every game package and source checkout.
