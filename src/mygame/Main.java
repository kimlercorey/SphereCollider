package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import com.jme3.scene.shape.Sphere;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.texture.Texture;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.RenderState.FaceCullMode;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    // prepare shapes
    private Box box;
    private Sphere sphereMesh;
    
    // prepare geometries
    private Geometry boxGeometry;
    private Geometry sphereGeometry;
    
    // prepare the physics
    private BulletAppState bulletAppState;
    
    
    // prepare textures
    private Texture boxTexture;
    
    
    // prepare texture keys - not used... yet!
    //private TextureKey boxTextureKey;
    
    
    // prepare materials
    private Material boxMaterial;
    private Material sphereMaterial;
    
    
    // rigid body controls
    private RigidBodyControl boxPhysics;
    
    // prepare lights
    AmbientLight ambientLight;
    DirectionalLight sunDirectionalLight;

    @Override
    public void simpleInitApp() {

        // init physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        initLight();
        initBox();
        initSphere();
    }

    public void initCamera() {
        cam.setLocation(new Vector3f(0.0f, 3.0f, 3.0f));
        cam.lookAt(new Vector3f(0.0f, 4.0f, 0.0f), Vector3f.UNIT_Y);
    }
    
    public void initLight() {
        /**
         * A white ambient light source.
         */
        //ambientLight = new AmbientLight();
        //ambientLight.setColor(ColorRGBA.White);
        //rootNode.addLight(ambientLight);
        
        sunDirectionalLight = new DirectionalLight();
        sunDirectionalLight.setColor(ColorRGBA.White);
        sunDirectionalLight.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        rootNode.addLight(sunDirectionalLight);
    }

    public void initSphere() {
        /**
         * Illuminated bumpy rock with shiny effect. Uses Texture from
         * jme3-test-data library! Needs light source!
         */
        sphereMesh = new Sphere(32, 32, 2f); // 32, 32, 2f
        sphereGeometry = new Geometry("Shiny rock", sphereMesh);
        sphereMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(sphereMesh);   // for lighting effect
        sphereMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        sphereMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        sphereMaterial.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        //shinyMat.setTexture("GlowMap", assetManager.loadTexture("Textures/glowmap.png")); // requires glow filter!
        sphereMaterial.setBoolean("UseMaterialColors", true);  // needed for shininess
        sphereMaterial.setColor("Specular", ColorRGBA.White); // needed for shininess
        sphereMaterial.setColor("Diffuse", ColorRGBA.White); // needed for shininess
        sphereMaterial.setFloat("Shininess", 64f); // shininess from 1-128
        sphereGeometry.setMaterial(sphereMaterial);
        sphereGeometry.setLocalTranslation(0.0f, 2.0f, 2.0f);
        sphereGeometry.setLocalScale(0.2f, 0.2f, 0.2f);
        rootNode.attachChild(sphereGeometry);
    }

    public void initBox() {

        // create a box
        box = new Box(5.0f, 5.0f, 5.0f);


        boxGeometry = new Geometry("Box", box);
        boxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxTexture = assetManager.loadTexture("Textures/Tiles/tile.jpg");
        boxTexture.setWrap(WrapMode.Repeat);
        box.scaleTextureCoordinates(new Vector2f(4.0f, 4.0f));

        // render the inside of the box
        boxMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);
        boxMaterial.setTexture("ColorMap", boxTexture);
        boxGeometry.setMaterial(boxMaterial);
        boxGeometry.setLocalTranslation(0.0f, 0.0f, 0.0f);

        // physics for the box
        boxPhysics = new RigidBodyControl(0.0f);
        boxGeometry.addControl(boxPhysics);
        bulletAppState.getPhysicsSpace().add(boxPhysics);

        rootNode.attachChild(boxGeometry);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
