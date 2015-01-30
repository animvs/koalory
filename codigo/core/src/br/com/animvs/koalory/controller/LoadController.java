package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalDataLoader;
import br.com.animvs.engine2.utils.AnimvsLoadController;

/**
 * Created by DALDEGAN on 23/01/2015.
 */
public final class LoadController extends AnimvsLoadController {
    public static final String UI_JSON_PATH = "data/graphics/ui/ui-main.json";
    public static final String UI_SKIN_PATH = "data/graphics/ui/ui-main.atlas";

    public static final String LEVEL_MAIN = "data/maps/levelSelect.tmx";
    public static final String LEVEL_GREENHILLS = "data/maps/greenHills.tmx";
    public static final String LEVEL_SANDPLAINS = "data/maps/sandPlains.tmx";
    public static final String LEVEL_FROSTPLATEAU = "data/maps/frostPlateau.tmx";
    public static final String LEVEL_CASTLE1 = "data/maps/castle1.tmx";

    public static final String ATLAS_CHARACTER = "data/graphics/mobiles/character/skeleton.atlas";
    public static final String ATLAS_KOALA = "data/graphics/mobiles/koala/skeleton.atlas";
    public static final String ATLAS_SHADOW = "data/graphics/mobiles/shadow/skeleton.atlas";
    public static final String ATLAS_OBJECTS = "data/graphics/objects/objects.txt";

    public static final String SKELETON_CHARACTER = "data/graphics/mobiles/character/skeleton.skel";
    public static final String SKELETON_KOALA = "data/graphics/mobiles/koala/skeleton.skel";
    public static final String SKELETON_SHADOW = "data/graphics/mobiles/shadow/skeleton.skel";

    public static final String TEXTURE_BACKGROUND = "data/graphics/background/background.png";

    public static final String SKELETON_COLOR = "data/graphics/objects/color.skel";
    public static final String SKELETON_PLATFORM = "data/graphics/objects/platform.skel";

    public static final String SOUND_FX_JUMP = "data/sounds/jump.wav";
    public static final String MUSIC_IN_GAME = "data/sounds/cenario.mp3";
    public static final String MUSIC_GAME_OVER = "data/sounds/game-over.mp3";
    public static final String SOUND_FX_DEATH_KOALA = "data/sounds/death-koala.wav";
    public static final String SOUND_FX_DEATH_CHARACTER = "data/sounds/death-character.wav";

    private boolean gameInitialized;

    private GameController controller;

    @Override
    protected Array<AnimvsLoadParameter> getResources() {
        Array<AnimvsLoadParameter> loadParameters = new Array<AnimvsLoadParameter>();

        loadParameters.add(new AnimvsLoadParameter(UI_SKIN_PATH, TextureAtlas.class));

        //Maps:
        loadParameters.add(new AnimvsLoadParameter(LEVEL_MAIN, TiledMap.class));
        loadParameters.add(new AnimvsLoadParameter(LEVEL_GREENHILLS, TiledMap.class));
        loadParameters.add(new AnimvsLoadParameter(LEVEL_SANDPLAINS, TiledMap.class));
        loadParameters.add(new AnimvsLoadParameter(LEVEL_FROSTPLATEAU, TiledMap.class));
        loadParameters.add(new AnimvsLoadParameter(LEVEL_CASTLE1, TiledMap.class));

        //Background:
        loadParameters.add(new AnimvsLoadParameter(TEXTURE_BACKGROUND, Texture.class));

        // Skeletal Animations:
        loadParameters.add(new AnimvsLoadParameter(SKELETON_CHARACTER, AnimacaoSkeletalData.class, createeSkeletalParameters(ATLAS_CHARACTER)));
        loadParameters.add(new AnimvsLoadParameter(SKELETON_KOALA, AnimacaoSkeletalData.class, createeSkeletalParameters(ATLAS_KOALA)));
        loadParameters.add(new AnimvsLoadParameter(SKELETON_SHADOW, AnimacaoSkeletalData.class, createeSkeletalParameters(ATLAS_SHADOW)));
        loadParameters.add(new AnimvsLoadParameter(SKELETON_COLOR, AnimacaoSkeletalData.class, createeSkeletalParameters(ATLAS_OBJECTS)));
        loadParameters.add(new AnimvsLoadParameter(SKELETON_PLATFORM, AnimacaoSkeletalData.class, createeSkeletalParameters(ATLAS_OBJECTS)));

        //Sounds:
        loadParameters.add(new AnimvsLoadParameter(SOUND_FX_JUMP, Sound.class));
        loadParameters.add(new AnimvsLoadParameter(SOUND_FX_DEATH_KOALA, Sound.class));
        loadParameters.add(new AnimvsLoadParameter(SOUND_FX_DEATH_CHARACTER, Sound.class));
        loadParameters.add(new AnimvsLoadParameter(MUSIC_IN_GAME, Music.class));
        loadParameters.add(new AnimvsLoadParameter(MUSIC_GAME_OVER, Music.class));

        return loadParameters;
    }

    public LoadController(GameController controller) {
        super();
        this.controller = controller;
    }

    public AssetManager getAssetManager() {
        return super.getAssetManager();
    }

    @Override
    public void update() {
        super.update();

        if (!gameInitialized && !getLoading()) {
            gameInitialized = true;

            Gdx.app.log("LOAD", "Resources loaded in " + (getLoadTimeSinceStarted() * 0.001f) + " seconds.");

            controller.initialize();
        }
    }

    private TextureLoader.TextureParameter getBackgroundTextureParameter() {
        TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
        textureParameter.minFilter = Texture.TextureFilter.Linear;
        textureParameter.magFilter = Texture.TextureFilter.Linear;
        textureParameter.format = Pixmap.Format.RGB565;
        textureParameter.wrapU = Texture.TextureWrap.Repeat;
        textureParameter.wrapV = Texture.TextureWrap.Repeat;

        return textureParameter;
    }

    private AnimacaoSkeletalDataLoader.AnimacaoSkeletalDataParameter createeSkeletalParameters(String atlasPath) {
        AnimacaoSkeletalDataLoader.AnimacaoSkeletalDataParameter skeletonParameters = new AnimacaoSkeletalDataLoader.AnimacaoSkeletalDataParameter();
        skeletonParameters.binario = true;
        skeletonParameters.caminhoAtlas = atlasPath;

        return skeletonParameters;
    }
}
