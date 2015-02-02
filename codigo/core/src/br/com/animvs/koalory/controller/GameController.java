package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import br.com.animvs.engine2.graphics.font.AnimvsFontController;
import br.com.animvs.engine2.graphics.font.AnimvsFontInfo;
import br.com.animvs.engine2.internationalization.AnimvsLanguageController;
import br.com.animvs.engine2.utils.AnimvsIntCrypto;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.engine.graphics.KoaloryShaderLoad;
import br.com.animvs.koalory.entities.engine.graphics.ParallaxCamera;
import br.com.animvs.koalory.entities.engine.graphics.shaders.ShaderColor;

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
    private PlayersController players;
    private CameraController camera;
    private InputController input;
    private String nextMap;
    private ProfileController profile;

    private ShaderColor shaderColor;

    private float colorRecovered;
    private int lives;

    private SoundController sound;

    public boolean getInGame() {
        return inGame;
    }

    public SoundController getSound() {
        return sound;
    }

    public UIController getUI() {
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

    public PlayersController getPlayers() {
        return players;
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

    public InputController getInput() {
        return input;
    }

    public CameraController getCamera() {
        return camera;
    }

    public ProfileController getProfile() {
        return profile;
    }

    public GameController() {
        crypto = new AnimvsIntCrypto(Configurations.H_C);
        load = new LoadController(this);
    }

    public void load() {
        load.setShader(new KoaloryShaderLoad());
        load.load();
    }

    public void startMatch(String map) {
        lives = Configurations.GAMEPLAY_LIVES_AT_START;
        inGame = true;
        colorRecovered = 0f;

        if (map == null)
            nextMap = "levelSelect";
        else
            nextMap = map;

        /*if (entities.getPlayer(0) != null)
            entities.getPlayer(0).spawn(MathUtils.randomBoolean());*/

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
            this.fonts = new AnimvsFontController(language, "br.com.animvs.koalory.fontscache", Configurations.CORE_LANGUAGE_PATH, createFontsInfo(), "0123456789%", (int) Configurations.RESOLUTION_REAL.x);
            this.fonts.loadFonts();

            this.ui = new UIController(this, LoadController.UI_JSON_PATH, Configurations.RESOLUTION_REAL);

            int minFPS = Gdx.app.getType() == Application.ApplicationType.Desktop ? 60 : 20;
            int maxFPS = Gdx.app.getType() == Application.ApplicationType.Desktop ? 60 : 30;

            this.physics = new PhysicsController(this, Configurations.DEBUG_PHYSICS, 250f, 0.004f, new Vector2(0f, -10f), minFPS, maxFPS);
            //this.physics = new PhysicsController(this, Configurations.DEBUG_PHYSICS, 100f, 0.001f, new Vector2());

            ParallaxCamera newCamera = new ParallaxCamera(Configurations.RESOLUTION_REAL.x, Configurations.RESOLUTION_REAL.y);
            this.camera = new CameraController(this, newCamera);
            this.stage = new StageController(this, newCamera);

            this.entities = new EntitiesController(this);
            this.shaderColor = new ShaderColor(this);
            this.background = new BackgroundController(this);
            this.players = new PlayersController(this);
            this.input = new InputController(this);
            this.profile = new ProfileController(this);

            level = new LevelController(this);

            physics.initialize();
            stage.initialize();
            entities.initialize();
            players.initialize();
            camera.initialize();
            input.initialize();
            profile.initialize();

            this.colorRecovered = 0f; //starts with 0% of recovered colors
            this.lives = Configurations.GAMEPLAY_LIVES_AT_START; // starts with 5 lives

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            getUI().showUIInitial();
        }
    }

    public void resize(int width, int height) {
        if (stage != null)
            stage.resize(width, height);

        if (ui != null)
            ui.resize(width, height);

        if (background != null)
            background.resize(width, height);
    }

    public void update() {
        load.update();

        if (initialized) {
            if (nextMap != null) {
                clean();
                level.loadMap(nextMap);
                nextMap = null;

                return;
            }

            input.update();

            if (inGame) {
                players.update();
                physics.update(Gdx.graphics.getDeltaTime());
                entities.update();

                background.render();

                stage.getViewport().apply();
                level.render();
                stage.update();

                if (physics != null && Configurations.DEBUG_PHYSICS)
                    physics.renderDebug(stage.getCamera().combined);

                entities.processMatchEnd();
                camera.update();
            }

            ui.render();
        }
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

    private void clean() {
        entities.restart();
        players.restart();
        stage.restart();
        physics.restart();

        getUI().showUIInGame();
        ui.castValueColors();

        if (level != null)
            level.dispose();

        Gdx.app.log("PHYSICS", "Bodies remaining after clean: " + physics.getWorld().getBodyCount());
    }
}
