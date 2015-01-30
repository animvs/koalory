package br.com.animvs.koalory.entities.engine.graphics.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import br.com.animvs.koalory.controller.GameController;

/**
 * Created by ANSCHAU on 25/01/2015.
 */
public class UICredits extends UIBase {

    public UICredits(GameController controller, String caminhoUISkin) {
        super(controller, caminhoUISkin);
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
        tbRodape.add(logo).width(460f * getGameController().getUI().getRatio().x).height(142f * getGameController().getUI().getRatio().y).colspan(3).row();
        tbRodape.add(imgCharacter).width(224f * getGameController().getUI().getRatio().x).height(407f * getGameController().getUI().getRatio().y);
        tbRodape.add(createTableCredit()).pad(100f * getGameController().getUI().getRatio().x);
        tbRodape.add(imgKoala).width(149f * getGameController().getUI().getRatio().x).height(212f * getGameController().getUI().getRatio().y).bottom().row();

        tbRodape.add(btnBack).width(80f * getGameController().getUI().getRatio().x).height(80f * getGameController().getUI().getRatio().y);

        getWindow().add().fill().expand().row();
        getWindow().add(tbRodape).fillX().expandX().row();
        getWindow().add().fill().expand();

        /*setDebug(true);
        getWindow().setDebug(true);
        tbRodape.setDebug(true);*/
    }

    @Override
    protected void eventVisible() {
        getGameController().getUI().getGameController().getSound().playMusicInGame();
    }

    @Override
    protected void eventNotVisible() {

    }

    @Override
    protected void eventoBack() {

    }

    private Table createTableCredit() {
        Table tbCredits = new Table(getUiSkin());

        Label lblCredit = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.credit"), getUiSkin(), "title-white");
        lblCredit.setAlignment(Align.center);

        String nmSkinNome = "default";
        String nmSkinAtributos = "small";
        //Dados Andrei
        Label lblAndrei = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.andrei"), getUiSkin(), nmSkinNome);
        lblAndrei.setAlignment(Align.center);
        Label lblAtrAndrei = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.atrAndrei"), getUiSkin(), nmSkinAtributos);
        lblAtrAndrei.setAlignment(Align.center);

        //Dados Anderson
        Label lblAnderson = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.anderson"), getUiSkin(), nmSkinNome);
        lblAnderson.setAlignment(Align.center);
        Label lblAtrAnderson = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.atrAnderson"), getUiSkin(), nmSkinAtributos);
        lblAtrAnderson.setAlignment(Align.center);

        //Dados Cleber
        Label lblcleber = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.cleber"), getUiSkin(), nmSkinNome);
        lblcleber.setAlignment(Align.center);
        Label lblAtrCleber = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.atrCleber"), getUiSkin(), nmSkinAtributos);
        lblAtrCleber.setAlignment(Align.center);

        //Dados Jardel
        Label lblJardel = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.jardel"), getUiSkin(), nmSkinNome);
        lblJardel.setAlignment(Align.center);
        Label lblAtrJardel = new Label(getGameController().getUI().getGameController().getLanguage().getLang().getValor("main.ui.initial.atrJardel"), getUiSkin(), nmSkinAtributos);
        lblAtrJardel.setAlignment(Align.center);

        tbCredits.add(lblCredit).colspan(3).row();

        float padX = 70f;
        float padY = 30f;

        tbCredits.add(lblAnderson).padRight(padX * getGameController().getUI().getRatio().x);
        tbCredits.add(lblAndrei).row();

        tbCredits.add(lblAtrAnderson).padBottom(padY * getGameController().getUI().getRatio().y).padRight(padX * getGameController().getUI().getRatio().x);
        tbCredits.add(lblAtrAndrei).padBottom(padY * getGameController().getUI().getRatio().y).row();

        tbCredits.add(lblcleber).padRight(padX * getGameController().getUI().getRatio().x);
        tbCredits.add(lblJardel).row();

        tbCredits.add(lblAtrCleber).padRight(padX * getGameController().getUI().getRatio().x);
        tbCredits.add(lblAtrJardel);

        return tbCredits;
    }

    @Override
    public void eventActionButtonPressed() {
        goToUIInitial();
    }

    private void goToUIInitial() {
        getGameController().getUI().showUIInitial();
    }
}
