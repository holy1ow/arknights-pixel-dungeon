// Arknights Pixel Dungeon: Amiya frozen visual integration, holy1ow, 2026-10-20.
// SPDX-License-Identifier: GPL-3.0-or-later

/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaIconVisuals;
import com.shatteredpixel.shatteredpixeldungeon.effects.HellagurVisuals;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

public class TalentIcon extends Image {

	private static TextureFilm film;
	private static final int SIZE = 16;
	private Talent talent;
	private HeroClass displayedClass;
	private String appearanceTexture;

	public TalentIcon(Talent talent){
		this(talent.icon());
		this.talent = talent;
		appearance(Dungeon.hero == null ? null : Dungeon.hero.heroClass);
	}

	public void appearance(HeroClass displayedClass) {
		this.displayedClass = displayedClass;
		refreshAppearance();
	}

	private void refreshAppearance() {
		if (talent == null) return;
		String selected = AmiyaIconVisuals.talent(displayedClass, talent);
		if (selected == null) selected = HellagurVisuals.talent(displayedClass, talent);
		String path = selected == null ? Assets.Interfaces.TALENT_ICONS : selected;
		if (!path.equals(appearanceTexture)) {
			texture(path);
			if (selected == null) frame(film.get(talent.icon()));
			else frame(0, 0, SIZE, SIZE);
			appearanceTexture = path;
		}
	}

	@Override public void update() {
		super.update();
		refreshAppearance();
	}

	public TalentIcon(int icon){
		super( Assets.Interfaces.TALENT_ICONS );

		if (film == null) film = new TextureFilm(texture, SIZE, SIZE);

		frame(film.get(icon));
	}

}
