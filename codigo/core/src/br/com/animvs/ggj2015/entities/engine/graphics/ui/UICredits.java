package br.com.animvs.ggj2015.entities.engine.graphics.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import br.com.animvs.ggj2015.controller.UIController;

/**
 * Created by ANSCHAU on 25/01/2015.
 */
public class UICredits extends UIBase {

    private UIController controller;

    public UICredits(UIController controller, AssetManager assetManager, String caminhoUISkin, ArrayMap<String, BitmapFont> fontes) {
        super(controller, assetManager, caminhoUISkin, fontes);
        this.controller = controller;
    }

    @Override
    protected void eventBuild(int width, int height, float ratioX, float ratioY) {

        getWindow().setBackground("frame-blue");

        ImageButton btnBack = new ImageButton(getUiSkin(), "btn-back");
        btnBack.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                goToUIInitial();
                return true;
            }
        });

        Image logo = new Image(getUiSkin(), "logo");
        Image imgCharacter = new Image(getUiSkin(), "character");
        Image imgKoala = new Image(getUiSkin(), "koala");

        Table tbRodape = new Table(getUiSkin());
        tbRodape.add(logo).width(460f * getController().getRatio().x).height(142f * getController().getRatio().y).colspan(3).row();
        tbRodape.add(imgCharacter).width(224f * getController().getRatio().x).height(407f * getController().getRatio().y);
        tbRodape.add(createTableCredit()).pad(100f * getController().getRatio().x);
        tbRodape.add(imgKoala).width(149f * getController().getRatio().x).height(212f * getController().getRatio().y).bottom().row();

        tbRodape.add(btnBack).width(80f * getController().getRatio().x).height(80f * getController().getRatio().y);

        getWindow().add().fill().expand().row();
        getWindow().add(tbRodape).fillX().expandX().row();
        getWindow().add().fill().expand();

        /*setDebug(true);
        getWindow().setDebug(true);
        tbRodape.setDebug(true);*/
    }

    @Override
    protected void eventVisible() {
        controller.getGameController().getSound().playMusicInGame();
    }

    @Override
    protected void eventNotVisible() {

    }

    @Override
    protected void eventoBack() {

    }

    private Table createTableCredit() {
        Table tbCredits = new Table(getUiSkin());

        Label lblCredit = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.credit"), getUiSkin(), "title-white");
        lblCredit.setAlignment(Align.center);

        String nmSkinNome = "default";
        String nmSkinAtributos = "small";
        //Dados Andrei
        Label lblAndrei = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.andrei"), getUiSkin(), nmSkinNome);
        lblAndrei.setAlignment(Align.center);
        Label lblAtrAndrei = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.atrAndrei"), getUiSkin(), nmSkinAtributos);
        lblAtrAndrei.setAlignment(Align.center);

        //Dados Anderson
        Label lblAnderson = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.anderson"), getUiSkin(), nmSkinNome);
        lblAnderson.setAlignment(Align.center);
        Label lblAtrAnderson = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.atrAnderson"), getUiSkin(), nmSkinAtributos);
        lblAtrAnderson.setAlignment(Align.center);

        //Dados Cleber
        Label lblcleber = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.cleber"), getUiSkin(), nmSkinNome);
        lblcleber.setAlignment(Align.center);
        Label lblAtrCleber = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.atrCleber"), getUiSkin(), nmSkinAtributos);
        lblAtrCleber.setAlignment(Align.center);

        //Dados Jardel
        Label lblJardel = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.jardel"), getUiSkin(), nmSkinNome);
        lblJardel.setAlignment(Align.center);
        Label lblAtrJardel = new Label(controller.getGameController().getLanguage().getLang().getValor("main.ui.initial.atrJardel"), getUiSkin(), nmSkinAtributos);
        lblAtrJardel.setAlignment(Align.center);

        tbCredits.add(lblCredit).colspan(3).row();

        float padX = 70f;
        float padY = 30f;

        tbCredits.add(lblAnderson).padRight(padX * getController().getRatio().x);
        tbCredits.add(lblAndrei).row();

        tbCredits.add(lblAtrAnderson).padBottom(padY * getController().getRatio().y).padRight(padX * getController().getRatio().x);
        tbCredits.add(lblAtrAndrei).padBottom(padY * getController().getRatio().y).row();

        tbCredits.add(lblcleber).padRight(padX * getController().getRatio().x);
        tbCredits.add(lblJardel).row();

        tbCredits.add(lblAtrCleber).padRight(padX * getController().getRatio().x);
        tbCredits.add(lblAtrJardel);

        return tbCredits;
    }

    @Override
    public void eventActionButtonPressed() {
        goToUIInitial();
    }

    private void goToUIInitial() {
        controller.showUIInitial();
    }
}
