package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import br.com.animvs.engine2.matematica.Random;

/**
 * Created by ANSCHAU on 25/01/2015.
 */
public class SoundController {
    private GameController controller;

    public SoundController(GameController controller) {
        this.controller = controller;
    }

    public void playJump() {
        controller.getLoad().get(LoadController.SOUND_FX_JUMP, Sound.class).play(1f, Random.random(0.75f, 1.25f), 0f);
    }

    public void playDeathKoala() {
        controller.getLoad().get(LoadController.SOUND_FX_DEATH_KOALA, Sound.class).play(1f, Random.random(0.75f, 1.25f), 0f);
    }

    public void playDeathCharacter() {
        controller.getLoad().get(LoadController.SOUND_FX_DEATH_CHARACTER, Sound.class).play(1f, Random.random(0.75f, 1.25f), 0f);
    }

    public void playMusicInGame() {
        controller.getLoad().get(LoadController.MUSIC_GAME_OVER, Music.class).stop();
        if (!controller.getLoad().get(LoadController.MUSIC_IN_GAME, Music.class).isLooping()) {
            controller.getLoad().get(LoadController.MUSIC_IN_GAME, Music.class).setLooping(true);
            controller.getLoad().get(LoadController.MUSIC_IN_GAME, Music.class).play();
        }
    }

    public void playMusicGameOver() {
        controller.getLoad().get(LoadController.MUSIC_IN_GAME, Music.class).stop();
        controller.getLoad().get(LoadController.MUSIC_GAME_OVER, Music.class).pause();
        controller.getLoad().get(LoadController.MUSIC_GAME_OVER, Music.class).play();
    }

}
