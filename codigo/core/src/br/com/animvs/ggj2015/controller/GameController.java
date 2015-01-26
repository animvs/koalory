package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import br.com.animvs.engine2.graphics.font.AnimvsFontController;
import br.com.animvs.engine2.graphics.font.AnimvsFontInfo;
import br.com.animvs.engine2.internationalization.AnimvsLanguageController;
import br.com.animvs.engine2.utils.AnimvsIntCrypto;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.graphics.GGJ15ShaderLoad;
import br.com.animvs.ggj2015.entities.engine.graphics.shaders.ShaderColor;

/**
 * Created by ANSCHAU on 23/01/2015.
 */
public final class GameController implements Disposable {

    private boolean inGame;
    private boolean initialized;

    private LoadController load;
    private UIController ui;
    private PhysicsController physics;
    private StageController stage;
    private LevelController level;
    private EntitiesController entities;

    private AnimvsIntCrypto crypto;
    private AnimvsFontController fonts;
    private AnimvsLanguageController language;
    private BackgroundController background;

    private Vector2 cameraDesiredPosition;
    private Vector2 cameraPositionCache;

    private ShaderColor shaderColor;

    private float colorRecovered;
    private int lives;

    private SoundController sound;

    public SoundController getSound() {
        return sound;
    }

    public UIController getUiController() {
        return ui;
    }

    public PhysicsController getPhysics() {
        return physics;
    }

    public StageController getStage() {
        return stage;
    }

    public AnimvsFontController getFonts() {
        return fonts;
    }

    public LoadController getLoad() {
        return load;
    }

    public LevelController getLevel() {
        return level;
    }

    public EntitiesController getEntities() {
        return entities;
    }

    public int getLives() {
        return lives;
    }

    public void subtractLife() {
        lives--;
    }

    public float getColorRecovered() {
        return colorRecovered;
    }

    public ShaderColor getShaderColor() {
        return shaderColor;
    }

    public AnimvsLanguageController getLanguage() {
        return language;
    }

    public GameController() {
        crypto = new AnimvsIntCrypto(Configurations.H_C);
        load = new LoadController(this);
        cameraDesiredPosition = new Vector2();
        cameraPositionCache = new Vector2();
    }

    public void load() {
        load.setShader(new GGJ15ShaderLoad());
        load.load();
    }

    public void startMatch() {
        lives = Configurations.GAMEPLAY_LIVES_AT_START;
        inGame = true;
        colorRecovered = 0f;

        entities.restart();

        level.loadMap(LoadController.LEVEL_GREEN_RIVER);
        getUiController().showUIInGame();

        ui.castValueColors();

        if (lives > 0)
            if (entities.getPlayer(0) != null)
                entities.getPlayer(0).spawn(MathUtils.randomBoolean());

        //entities.createSpawner(new Vector2(750f, 650f), 3f, -0.5f);
    }

    public void endMatch() {
        inGame = false;
    }

    public void addColorRecovered(float toAdd) {
        colorRecovered += toAdd;
    }

    public void initialize() {
        if (!initialized) {
            initialized = true;


            this.sound = new SoundController(this);

            this.language = new AnimvsLanguageController("pt-br", Configurations.CORE_LANGUAGE_PATH);
            this.fonts = new AnimvsFontController(language, "br.com.animvs.ggj2015.fontscache", Configurations.CORE_LANGUAGE_PATH, createFontsInfo(), "0123456789%", (int) Configurations.RESOLUTION_REAL.x);
            this.fonts.loadFonts();

            this.ui = new UIController(this, LoadController.UI_JSON_PATH, Configurations.RESOLUTION_REAL);
            this.physics = new PhysicsController(this, Configurations.DEBUG_PHYSICS, 250f, 0.004f, new Vector2(0f, -10f));
            //this.physics = new PhysicsController(this, Configurations.DEBUG_PHYSICS, 100f, 0.001f, new Vector2());
            this.stage = new StageController(this);
            this.entities = new EntitiesController(this);
            this.shaderColor = new ShaderColor(this);
            this.background = new BackgroundController(this);

            level = new LevelController(this);

            physics.initialize();
            stage.initialize();
            entities.initialize();

            this.colorRecovered = 0f; //starts with 0% of recovered colors
            this.lives = Configurations.GAMEPLAY_LIVES_AT_START; // starts with 5 lives

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            getUiController().showUIInitial();
        }
    }

    public void resize(int width, int height) {
        if (stage != null)
            stage.resize(width, height);

        if (ui != null)
            ui.resize(width, height);
    }

    public void update() {
        load.update();

        if (initialized) {
            if (inGame) {
                physics.update(Gdx.graphics.getDeltaTime());
                entities.update(false);

                background.render();

                stage.getViewport().apply();
                level.render();
                stage.update();

                if (physics != null && Configurations.DEBUG_PHYSICS)
                    physics.renderDebug(stage.getCamera().combined);

                checkGameOver();

                if (cameraDesiredPosition.x != stage.getCamera().position.x || cameraDesiredPosition.y != stage.getCamera().position.y) {
                    cameraPositionCache.set(stage.getCamera().position.x, stage.getCamera().position.y);

                    cameraPositionCache.lerp(cameraDesiredPosition, Gdx.graphics.getDeltaTime() * Configurations.CORE_CAMERA_SPEED_MULTIPLIER);
                    stage.getCamera().position.set(cameraPositionCache.x, cameraPositionCache.y, stage.getCamera().position.z);
                    stage.getCamera().update();
                }
            } else
                entities.update(true);
            ui.render();
        }
    }

    public void updateCameraDesiredPosition() {
        cameraDesiredPosition.set(entities.getPlayer(0).getX(), entities.getPlayer(0).getY());

        /*stage.getCamera().position.x = entities.getPlayer(0).getX();
        stage.getCamera().position.y = entities.getPlayer(0).getY();
        stage.getCamera().update();*/
    }

    @Override
    public void dispose() {
        disposeQuietly(load);
        disposeQuietly(physics);
        disposeQuietly(ui);
        disposeQuietly(level);
        disposeQuietly(background);
    }

    private final void disposeQuietly(Disposable disposable) {
        try {
            disposable.dispose();
        } catch (Exception e) {
        }
    }

    public void checkGameOver() {
        if ((lives == 0) && (entities.getPlayersAlive() == 0)) {
            getUiController().showUIGameOver();
            endMatch();
        }
    }

    private Array<AnimvsFontInfo> createFontsInfo() {
        float ratio = Gdx.graphics.getHeight() / 768f;

        Array<AnimvsFontInfo> fontsInfo = new Array<AnimvsFontInfo>();
        fontsInfo.add(new AnimvsFontInfo("small-font", 17, (int) (295 * ratio)));
        fontsInfo.add(new AnimvsFontInfo("default-font", 27, (int) (512 * ratio)));
        fontsInfo.add(new AnimvsFontInfo("big-font", 37, (int) (512 * ratio)));
        fontsInfo.add(new AnimvsFontInfo("minimal-font", 13, (int) (256 * ratio)));

        return fontsInfo;
    }

    private static void TMP_tests() {
        //Reservado para inicialização de testes:
    }
}
