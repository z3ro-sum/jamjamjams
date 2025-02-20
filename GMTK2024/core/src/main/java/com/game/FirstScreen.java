package com.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    
    PerspectiveCamera camera;
    ModelBatch batch;
    Environment environment;
    btCollisionConfiguration collisionConfiguration;
    btDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;
    EContactListener contactListener;
    Music music;

    Player player;
    Array<GameObject> instances;
    G3dModelLoader g3dLoader = new G3dModelLoader(new JsonReader());
   
    DebugDrawer debugDrawer;
    DirectionalShadowLight shadowLight;
    ModelBatch shadowBatch;
    
    @Override
    public void show() {
        /* Set up physics scene where objs will be added and lighting */
        Bullet.init();
        batch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add((shadowLight = new DirectionalShadowLight(1004, 1004, 30f, 30f, 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f, -.2f));
        environment.shadowMap = shadowLight;
        
        /* Camera */
        camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 20f, 80f);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
        
        /***
         * Initialize world and collision logic
         */
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3(0, -15f, 0));
        contactListener = new EContactListener(); 
        instances = new Array<>();
        
        /* Engine Debugs Init Only */
        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawAabb);
        dynamicsWorld.setDebugDrawer(debugDrawer);
        
        /* Background music for the level */
        music = Gdx.audio.newMusic(Gdx.files.internal("Sound/gmtk24-froginawell.wav"));
        music.setVolume(0.1f);                 // sets the volume to half the maximum volume
        music.setLooping(true);                // will repeat playback until music.stop() is called
        music.play();                          // resumes the playback

        SpawnObjects(); // Instantiate static models and game objects with logic

        //Engine inputs. will need game inputs later and disable engine inputs when game is on.
        InputMultiplexer multiplexer = new InputMultiplexer();
        //Add inputs for player movement and for ui when game is paused
        Gdx.input.setInputProcessor(player);
        
        //Test shadows for better lighting
        shadowBatch = new ModelBatch(new DepthShaderProvider());
        
        /*When camera.lookAt() is called the camera shakes TODO: fix shaking TEMPORARY FIX set angle or look on init*/
        camera.rotate(Vector3.X, -20);
        //camera.lookAt(player.GetGameObject().body.getWorldTransform().getTranslation(new Vector3())); // Make the camera look at the player
    }
    
    private void SpawnObjects(){
        
        /*Player init*/
        player = new Player();
        instances.add(player.GetGameObject());
        dynamicsWorld.addRigidBody(player.GetGameObject().body);
        
        //static objs init here
        AddInstance("counters.g3dj", Constants.KINEMATIC_FLAG, null, 0f, null, Constants.SOLIDSURFACE_TAG, Constants.PLAYER_TAG);
        AddInstance("floor.g3dj", Constants.KINEMATIC_FLAG, null, 0f, null, Constants.SOLIDSURFACE_TAG, Constants.PLAYER_TAG);
        AddInstance("key-flask.g3dj", Constants.DYNAMIC_FLAG, null, 1f, new btBoxShape(new Vector3(1f,1f,1f)), Constants.INTERACTABLE_TAG, Constants.PLAYER_TAG);
    }
    
    public void AddInstance(String str, int cflag, Vector3 pos, float mass, btCollisionShape shape, int contactFlag, int contactFilter){
        try {
            Model m = g3dLoader.loadModel(Gdx.files.internal("models/" + str));
            GameObject obj = new GameObject(m, str.substring(0, str.lastIndexOf('.')), shape, mass, cflag, pos, contactFlag, contactFilter);
            instances.add(obj);
            dynamicsWorld.addRigidBody(obj.body);
            Gdx.app.log("Instance", "Model " + str + " loaded to environment and added successfully.");
        } catch (Exception e) {
            Gdx.app.error("Instance", "Error loading model.", e);
        } 
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) System.exit(0); //Exit Game
    
        // Calculate the desired camera position based on the player position
        Vector3 playerPosition = player.GetGameObject().body.getWorldTransform().getTranslation(new Vector3());

        float desiredZ = Math.min(playerPosition.z + 30f, 40f); // Constrain Z-axis position
        Vector3 desiredPosition = new Vector3(playerPosition.x, playerPosition.y + 10f, desiredZ);
        camera.position.lerp(desiredPosition, 0.1f);
        //camera.lookAt(playerPosition.); // Make the camera look at the player
        camera.update();
        
        
        player.Update();

        /* Time logic and physics renders */
        dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        shadowLight.begin(Vector3.Zero, camera.direction);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(instances);
        shadowBatch.end();
        shadowLight.end();
        
        /* Render solid objects first */
        batch.begin(camera);
        batch.render(instances, environment);
        batch.end();

        // Clear depth buffer so wireframes draw over everything
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        // Render wireframes
        debugDrawer.begin(camera);
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        for (GameObject obj : instances)
            obj.dispose();
        
        music.dispose();
        instances.clear();
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        contactListener.dispose();
        collisionConfiguration.dispose();
        batch.dispose();
        shadowBatch.dispose();
    }
}