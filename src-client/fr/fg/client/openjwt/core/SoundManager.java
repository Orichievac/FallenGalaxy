/*
Copyright 2010 Jeremie Gottero

This file is part of Fallen Galaxy.

Fallen Galaxy is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Fallen Galaxy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Fallen Galaxy. If not, see <http://www.gnu.org/licenses/>.
*/

package fr.fg.client.openjwt.core;

import java.util.HashMap;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.allen_sauer.gwt.voices.client.handler.PlaybackCompleteEvent;
import com.allen_sauer.gwt.voices.client.handler.SoundHandler;
import com.allen_sauer.gwt.voices.client.handler.SoundLoadStateChangeEvent;

import fr.fg.client.openjwt.animation.TimerHandler;
import fr.fg.client.openjwt.animation.TimerManager;

public final class SoundManager implements SoundHandler {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private final static SoundManager instance = new SoundManager();
	
	private SoundController soundController;
	
	private HashMap<String, SoundConfig> soundsConfig, musicsConfig;
	
	private HashMap<String, Sound> sounds, musics;
	
	private int soundVolume, musicVolume, generalVolume;
	
	private Sound currentMusic;
	
	private String[] playlist;
	
	private int playlistIndex;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	private SoundManager() {
		soundController = new SoundController();
		sounds = new HashMap<String, Sound>();
		musics = new HashMap<String, Sound>();
		soundsConfig = new HashMap<String, SoundConfig>();
		musicsConfig = new HashMap<String, SoundConfig>();
		soundVolume = 0;
		musicVolume = 0;
		generalVolume = 0;
		playlist = new String[0];
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void configSound(String soundName, int volume, boolean streaming) {
		if (soundsConfig.containsKey(soundName))
			return;
		
		soundsConfig.put(soundName, new SoundConfig(volume, streaming));
	}

	public void configMusic(String musicName, int volume) {
		if (musicsConfig.containsKey(musicName))
			return;
		
		musicsConfig.put(musicName, new SoundConfig(volume, true));
	}
	
	public void loadSound(String soundName) {
		if (soundVolume == 0 || generalVolume == 0)
			return;
		
		SoundConfig config = soundsConfig.get(soundName);
		
		if (config == null)
			throw new RuntimeException(
					"Sound '" + soundName + "' must be configured first.");
		
		if (sounds.containsKey(soundName))
			return;
		
		try {
			Sound sound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG,
					Config.getMediaUrl() + "sound/" + soundName + ".mp3",
					config.isStreaming());
			sound.setVolume((config.getVolume() * generalVolume * soundVolume) / 10000);
			
			sounds.put(soundName, sound);
		} catch (Exception e) {
			sounds.put(soundName, null);
		}
	}

	public void loadMusic(String musicName) {
		if (musicVolume == 0 || generalVolume == 0)
			return;
		
		SoundConfig config = musicsConfig.get(musicName);
		
		if (config == null)
			throw new RuntimeException(
					"Music '" + musicName + "' must be configured first.");
		
		if (musics.containsKey(musicName))
			return;
		
		try {
			Sound music = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG,
					Config.getMediaUrl() + "sound/" + musicName + ".mp3",
					config.isStreaming());
			music.setVolume((config.getVolume() * generalVolume * musicVolume) / 10000);
			
			musics.put(musicName, music);
		} catch (Exception e) {
			musics.put(musicName, null);
		}
	}
	
	public Sound getSound(String soundName) {
		if (soundVolume == 0 || generalVolume == 0)
			return null;
		
		if (!sounds.containsKey(soundName))
			loadSound(soundName);
		
		return sounds.get(soundName);
	}
	
	public void playSound(String soundName) {
		if (soundVolume == 0 || generalVolume == 0)
			return;
		
		if (!sounds.containsKey(soundName))
			loadSound(soundName);
		
		Sound sound = sounds.get(soundName);
		
		if (sound != null)
			sound.play();
	}
	
	public void setPlaylist(String[] playlist) {
		this.playlist = playlist;

		if (musicVolume == 0 || generalVolume == 0)
			return;
		
		for (String musicName : playlist)
			if (!musics.containsKey(musicName))
				loadMusic(musicName);
	}
	
	public void playMusic(int index) {
		if (musicVolume == 0 || generalVolume == 0)
			return;
		
		if (index < 0 || index >= playlist.length)
			throw new IllegalArgumentException("Invalid music index.");
		
		for (String musicName : playlist)
			if (!musics.containsKey(musicName))
				loadMusic(musicName);
		
		if (currentMusic != null) {
			TimerManager.register(new FadeOut(currentMusic,
				musicsConfig.get(playlist[playlistIndex]).getVolume() *
				musicVolume * generalVolume / 10000));
			currentMusic.removeEventHandler(this);
			currentMusic = null;
		}
		
		currentMusic = musics.get(playlist[index]);
		
		if (currentMusic != null) {
			playlistIndex = index;
			currentMusic.addEventHandler(this);
			currentMusic.setVolume(musicsConfig.get(playlist[index]).getVolume() *
				musicVolume * generalVolume / 10000);
			currentMusic.play();
			currentMusic.setVolume(musicsConfig.get(playlist[index]).getVolume() *
				musicVolume * generalVolume / 10000);
		}
	}
	
	public int getSoundVolume() {
		return soundVolume;
	}
	
	public void setSoundVolume(int volume) {
		soundVolume = volume;
		
		if (volume == 0) {
			for (Sound sound : sounds.values())
				sound.stop();
		} else {
			for (String soundName : sounds.keySet())
				sounds.get(soundName).setVolume(
						(soundsConfig.get(soundName).getVolume() *
						generalVolume * soundVolume) / 10000);
		}
	}
	
	public int getMusicVolume() {
		return musicVolume;
	}
	
	public void setMusicVolume(int volume) {
		int oldVolume = musicVolume;
		
		musicVolume = volume;
		
		if (volume == 0) {
			for (Sound music : musics.values())
				music.stop();
		} else {
			for (String musicName : musics.keySet())
				musics.get(musicName).setVolume(
					(musicsConfig.get(musicName).getVolume() *
					generalVolume * musicVolume) / 10000);
			
			if (oldVolume == 0 && generalVolume > 0 && playlist.length > 0)
				playMusic(0);
		}
	}
	
	public int getGeneralVolume() {
		return generalVolume;
	}
	
	public void setGeneralVolume(int volume) {
		int oldVolume = generalVolume;
		generalVolume = volume;
		
		if (volume == 0) {
			for (Sound sound : sounds.values())
				sound.stop();
			for (Sound music : musics.values())
				music.stop();
		} else {
			for (String soundName : sounds.keySet())
				sounds.get(soundName).setVolume(
						(soundsConfig.get(soundName).getVolume() *
						generalVolume * soundVolume) / 10000);
			for (String musicName : musics.keySet())
				musics.get(musicName).setVolume(
						(musicsConfig.get(musicName).getVolume() *
						generalVolume * musicVolume) / 10000);
			
			if (oldVolume == 0 && musicVolume > 0 && playlist.length > 0)
				playMusic(0);
		}
	}
	
	public void onPlaybackComplete(PlaybackCompleteEvent event) {
		currentMusic.removeEventHandler(this);
		currentMusic = null;
		
		if (playlistIndex == playlist.length - 1)
			playlistIndex = 0;
		else
			playlistIndex++;
		
		currentMusic = musics.get(playlist[playlistIndex]);
		
		if (currentMusic != null) {
			currentMusic.addEventHandler(this);
			currentMusic.setVolume(musicsConfig.get(playlist[playlistIndex]).getVolume() *
				musicVolume * generalVolume / 10000);
			currentMusic.play();
		}
	}

	public void onSoundLoadStateChange(SoundLoadStateChangeEvent event) {
		// Non utilisé
	}
	
	public final static SoundManager getInstance() {
		return instance;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private class FadeOut implements TimerHandler {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private Sound sound;
		private int volume;
		private int fadeOut;
		private int initialVolume;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public FadeOut(Sound sound, int volume) {
			this.sound = sound;
			this.volume = volume;
			this.fadeOut = 100;
			this.initialVolume = sound.getVolume();
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public void update(int interpolation) {
			fadeOut -= 5;
			
			sound.setVolume(volume * fadeOut / 100);
		}
		
		public boolean isFinished() {
			return fadeOut <= 0;
		}
		
		public void destroy() {
			sound.setVolume(initialVolume);
			sound = null;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private class SoundConfig {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private int volume;
		
		private boolean streaming;

		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public SoundConfig(int volume, boolean streaming) {
			this.volume = volume;
			this.streaming = streaming;
		}

		// ----------------------------------------------------- METHODES -- //
		
		public int getVolume() {
			return volume;
		}

		public boolean isStreaming() {
			return streaming;
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
