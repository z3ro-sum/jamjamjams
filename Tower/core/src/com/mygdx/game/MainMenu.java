package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;


public class MainMenu extends ScreenAdapter{
    private Main main;
    private Stage stage;
    private Skin skin;

    OrthographicCamera camera;
    TextureAtlas atlas;
    Button _PlayBtn;

    public MainMenu(Main m){
        this.main = m;
        
        //Camera for gameplay.
        this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        _SkinSetup();
    }
    
    private void _SkinSetup(){
        atlas = new TextureAtlas("UI/Atlas/Btns.pack");
        skin = new Skin(atlas);
        stage = new Stage();
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        //Setup style and actions for buttons.
        _InitButtons();

        //Add all buttons to stage.
        stage.addActor(_PlayBtn);
    }

    private void _InitButtons(){
        //* --- Play button style --- */
        ImageButtonStyle style = new ImageButtonStyle();
        style.up = skin.getDrawable("PlayBtnUp");
        style.over = skin.getDrawable("PlayBtnDown");
        style.down = skin.getDrawable("PlayBtnDown");

        _PlayBtn = new ImageButton(style);
        //Button w h & pos
        _PlayBtn.setHeight(50);
        _PlayBtn.setWidth(150); 
        _PlayBtn.setPosition(Gdx.graphics.getWidth() / 2 - _PlayBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        //* --- Play button actions --- */
        _PlayBtn.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            }
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    main.setScreen(new Scene01(camera));
                    dispose();
            }
        });
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1, true); //Color screen needed for buffer

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose(){
        stage.dispose();
    }
}
