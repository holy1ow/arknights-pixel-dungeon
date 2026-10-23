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

import com.shatteredpixel.shatteredpixeldungeon.effects.HellagurEffects;
import com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaItemVisuals;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

public class BuffIcon extends Image {

	private static TextureFilm smallFilm;
	private static final int SML_SIZE = 7;

	private static TextureFilm largeFilm;
	private static final int LRG_SIZE = 16;

	private final boolean large;

	public BuffIcon(Buff buff, boolean large){
		super( large ? Assets.Interfaces.BUFFS_LARGE : Assets.Interfaces.BUFFS_SMALL );
		this.large = large;
		refresh(buff);
	}

	public BuffIcon(int icon, boolean large){
		super( large ? Assets.Interfaces.BUFFS_LARGE : Assets.Interfaces.BUFFS_SMALL );
		this.large = large;
		refresh(icon);
	}

	public void refresh(Buff buff){
        String hellagur = HellagurEffects.status(buff, large);
        if (hellagur != null) {
            texture(hellagur);
            frame(0, 0, large ? LRG_SIZE : SML_SIZE, large ? LRG_SIZE : SML_SIZE);
            resetColor();
            buff.tintIcon(this);
            return;
        }
        String selected = com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaIconVisuals.status(buff, large);
        if (selected != null) {
            texture(selected);
            frame(0, 0, large ? LRG_SIZE : SML_SIZE, large ? LRG_SIZE : SML_SIZE);
            resetColor();
            return;
        }
        if(buff instanceof com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark
                && ((com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark)buff).amiyaVisual()){
            texture(AmiyaItemVisuals.ROOT+(large?"soul-mark":"soul-mark-small")+".png");
            frame(0,0,large?LRG_SIZE:SML_SIZE,large?LRG_SIZE:SML_SIZE);resetColor();return;
        }
		refresh(buff.icon());
		if (!com.shatteredpixel.shatteredpixeldungeon.effects.AmiyaIconVisuals.tintStatus(buff, this)) {
			buff.tintIcon(this);
		}
	}

	public void refresh(int icon){
        texture(large?Assets.Interfaces.BUFFS_LARGE:Assets.Interfaces.BUFFS_SMALL);
        resetColor();
		if (large){
			if (largeFilm == null) largeFilm = new TextureFilm(texture, LRG_SIZE, LRG_SIZE);
			frame(largeFilm.get(icon));
		} else {
			if (smallFilm == null ) smallFilm = new TextureFilm(texture, SML_SIZE, SML_SIZE);
			frame(smallFilm.get(icon));
		}
	}

}
