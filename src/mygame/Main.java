package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.scene.Spatial;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener{

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
 // text padding   
 private int tp = 20;
 private int minimumNumberOfSpheresToBeginCountingIterations = 3;
 private BitmapText[] BitmapTextArray;
 
 // Collision tracking
 private String[] CollisionArray;
 private String lastA ="";
 private String lastB="";
 
 private int numberOfStartingSpheres = 3;
 private float sphereOffset = 1.5f;
 
 
 
 
 private Box mainbox;
 private Geometry boxGeometry;   
 private Texture boxTexture;   
 private Material boxMaterial;
 private RigidBodyControl boxPhysics;
 
 private Integer numberOfObjectsInScene;
 
 private static final SphereCollisionShape bulletCollisionShape;

     /** Prepare the Physics Application State (jBullet) */
  private BulletAppState bulletAppState;
 
  /** Prepare Materials */
  Material wall_mat;
  Material stone_mat;
  Material floor_mat;
  
  /** bounciness **/
 float bounciness = .99f;
 
  /** Prepare geometries and physical nodes for bricks and cannon balls. */
  //private RigidBodyControl    brick_phy;
  //private static final Box    box;
  private RigidBodyControl    ball_phy;
  private static final Sphere sphere;
  private RigidBodyControl    floor_phy;
  private static final Box    floor;
  
 
   // Iteration (a bit of abstraction from ticks)
  private static final Integer maxIterations = 100000;
  private static final Integer ticksPerIteration = 1;
  private Integer ic = 0;
  private Integer tic = 0;
  
  private Boolean includeFloorCollisions = false;
  
  
  static {
    /** Initialize the cannon ball geometry */
    sphere = new Sphere(32, 32, 1.4f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    
    bulletCollisionShape = new SphereCollisionShape(1.4f);
   
    /** Initialize the brick geometry */
    //box = new Box(brickLength, brickHeight, brickWidth);
    //box.scaleTextureCoordinates(new Vector2f(1f, .5f));
    /** Initialize the floor geometry */
    floor = new Box(70f, 0.1f, 70f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
 
  }
 
    
    
    @Override
    public void simpleInitApp() {
/** Set up Physics Game */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.setDebugEnabled(true);
 
    /** Configure cam to look at scene */
    cam.setLocation(new Vector3f(6f, 16f, 6f));
    cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
    /** Add InputManager action: Left click triggers shooting. */
    inputManager.addMapping("shoot", 
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addListener(actionListener, "shoot");
    /** Initialize the scene, materials, and physics space */
    
    numberOfObjectsInScene = 0;
    BitmapTextArray = new BitmapText[100];

    initMaterials();
    initFloor();
    initBox();
    initHUD(guiNode);
    initSphereGenerator();
    
    };

    
    private void initSphereGenerator(){
    
        for (int i = 0; i < numberOfStartingSpheres; i++) 
        {
        
        //Vector3f vec = new Vector3f(2, 2+(i*sphereOffset), 0);    
        makeCannonBall( new Vector3f(2, 2+(i*sphereOffset), 0));
        
        }
    
    };
    
    
    
 /**
   * Every time the shoot action is triggered, a new cannon ball is produced.
   * The ball is set up to fly from the camera position in the camera direction.
   */
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("shoot") && !keyPressed) {
        //makeCannonBall();
        initSphereGenerator();
          
      }
    }
  };
  

  public void initHUD(Node hud) {
          /** Write text on the screen (HUD) */
        hud.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt"); 
  };
  
 /** updateHUD() 
  *  Updates any items in the HUD that need to be tracked
  * 
   */
  public void trackSpheres() {
  
      int count = BitmapTextArray.length;
      
      if (count > 4) {
                      
        for (int c = 0; c < count; c++){
            
            if (rootNode.getChild("sphere"+c)!=null ) StatDisplayer(guiNode, "sphere"+c, c);
               
        }
      }
      
  };
  
  public float formmatted(float num){
  
      int startnum = (int) Math.ceil(num * 10);
      return (float) startnum / 10;
  };
  
  public float getXof(String name){
      float x = rootNode.getChild(name).getLocalTranslation().x;
      return formmatted(x);
  };
  
  public float getYof(String name){
      float y = rootNode.getChild(name).getLocalTranslation().y;
      return formmatted(y);
  };
  
  public float getZof(String name){
      float z = rootNode.getChild(name).getLocalTranslation().z;
      return formmatted(z);
  };
  
  public BitmapText StatDisplayer(Node hud, String name, int num){
  
        BitmapText statDisplayer;
        
        if (BitmapTextArray[num] == null && name !="") {
            
            /** Item is new so create a new hud stat for it **/
            statDisplayer = new BitmapText(guiFont, false);
            statDisplayer.setSize(guiFont.getCharSet().getRenderedSize());
            statDisplayer.setText( name + ": ");
            statDisplayer.setLocalTranslation(tp, settings.getHeight()-(numberOfObjectsInScene*statDisplayer.getLineHeight()), 0);
            guiNode.attachChild(statDisplayer);   
            numberOfObjectsInScene += 1;
            BitmapTextArray[num] = statDisplayer;
        }else{
            /** Item exists so read and update the stat **/
            statDisplayer = BitmapTextArray[num];
            statDisplayer.removeFromParent();
            statDisplayer.setSize(guiFont.getCharSet().getRenderedSize());
            statDisplayer.setText( name + ": {X: "+ getXof(name) +"} {Y:"+ getYof(name) +"} {Z:"+ getZof(name) +"} !"+ic+"/"+numberOfObjectsInScene );
            guiNode.attachChild(statDisplayer);
        }
  
        return statDisplayer;
        
  };

  
  
  public void initBox() {

        // create a box
        mainbox = new Box(7.0f, 16.0f, 7.0f);

        boxGeometry = new Geometry("Box", mainbox);
        boxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxTexture = assetManager.loadTexture("Textures/grid_green_on_blackb.jpg");
        boxTexture.setWrap(WrapMode.Repeat);
        //boxTexture.setAnisotropicFilter(4);
        mainbox.scaleTextureCoordinates(new Vector2f(4.0f, 4.0f));

        // render the inside of the box
        boxMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        boxMaterial.setTexture("ColorMap", boxTexture);
        boxGeometry.setMaterial(boxMaterial);
        boxGeometry.setLocalTranslation(0.0f, 15.0f, 0.0f);

        // physics for the box
        CollisionShape boxShape = CollisionShapeFactory.createMeshShape(boxGeometry);
        boxPhysics = new RigidBodyControl(boxShape, 0.0f);
        bulletAppState.getPhysicsSpace().add(boxPhysics);
        
        rootNode.attachChild(boxGeometry);
    };

  
  /** Initialize the materials used in this scene. */
  public void initMaterials() {
    wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
    key.setGenerateMips(true);
    Texture tex = assetManager.loadTexture(key);
    wall_mat.setTexture("ColorMap", tex);
 
    stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key2 = new TextureKey("Textures/grid_red_white.jpg");
    key2.setGenerateMips(true);
    Texture tex2 = assetManager.loadTexture(key2);
    stone_mat.setTexture("ColorMap", tex2);
 
    floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/floor.jpg");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    
    tex3.setWrap(WrapMode.Repeat);
    floor_mat.setTexture("ColorMap", tex3);
    //floor_mat.setColor("Color", ColorRGBA.Gray);// .setColor("ColorMap", new ColorRGBA(1f,0f,1f, 1f));
    
  }
 
  /** Make a solid floor and add it to the scene. */
  public void initFloor() {
    Geometry floor_geo = new Geometry("Floor", floor);
    floor_geo.setMaterial(floor_mat);
    floor_geo.setLocalTranslation(0, -0.1f, 0);
    floor_geo.setLocalScale(.1f);
    //floor_geo.scaleTextureCoordinates(new Vector2f(10f, 10f));
   
    this.rootNode.attachChild(floor_geo);
    /* Make the floor physical with mass 0.0f! */
    floor_phy = new RigidBodyControl(0.0f);
    
    //floor_phy.scaleTextureCoordinates(new Vector2f(10f, 10f));
    floor_geo.addControl(floor_phy);
    floor_phy.setRestitution(bounciness);
    bulletAppState.getPhysicsSpace().add(floor_phy);
  }
 
  /** This method creates one individual physical cannon ball.
   * By defaul, the ball is accelerated and flies
   * from the camera position in the camera direction.*/
   public void makeCannonBall(Vector3f vec) {
    /** Create a cannon ball geometry and attach to scene graph. */
    Geometry ball_geo = new Geometry( "sphere"+numberOfObjectsInScene, sphere);
    ball_geo.setMaterial(stone_mat);
    rootNode.attachChild(ball_geo);
    /** Position the cannon ball  */
    ball_geo.setLocalTranslation(cam.getLocation());
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(.1f);
     
    ball_phy.setCollisionShape(bulletCollisionShape);
    bulletAppState.getPhysicsSpace().addCollisionListener(this);
    
    /** Add physical ball to physics space. */
    ball_geo.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    /** Accelerate the physcial ball to shoot it. */
    ball_phy.setRestitution(bounciness);
    //ball_phy.setLinearVelocity(cam.getDirection().mult(25));
    ball_phy.setLinearVelocity(vec.mult(100));
    StatDisplayer(guiNode, ball_geo.getName(), numberOfObjectsInScene);
  }
  
   @Override
   public void simpleUpdate(float tpf) {
   
 
    if (totalIterationsComplete()) {
       // end it all
       bulletAppState.getPhysicsSpace().removeAll(rootNode);
       
    } else {
       trackSpheres();  
    }    
   }
    
   private Boolean totalIterationsComplete() {   
       Boolean isComplete = false;
       
       // if we don't have enough objects yet, just return default isComplete (false)
       if (BitmapTextArray[minimumNumberOfSpheresToBeginCountingIterations-1] == null) return isComplete;
       
       // if we have not finished the total iterations
       if (ic < maxIterations){
            //increment the ticks 
            tic++;
            // if the ticks are maxed then reset and increment the count
            if (tic > ticksPerIteration) { 
                  tic=0; 
                  ic++; 
            } 
            
            // otherwise just keep counting
            isComplete = false;
            
      } else {
           // if we have reached maxIteration then we should say so
             isComplete = true;
      } 
       
      return isComplete;   
   } 
   

    public void collision(PhysicsCollisionEvent event) {
        try {
            
         final Spatial nodea = event.getNodeA();
         final Spatial nodeb = event.getNodeB();
         
         if (!includeFloorCollisions){
            if (nodea.getName()=="Floor" || nodeb.getName()=="Floor") return;
         }    
         
         System.out.println("boom!! "+nodea.getName()+" and "+nodeb.getName());
         System.out.println(cam.getDirection().toString());
         addCollisionToQueue(nodea.getName(),nodeb.getName());
         
         
        } catch (Exception e) {
            // dreams computers compute when asked "do nothing"
        }
    };
    
    
    public void addCollisionToQueue(String a, String b){
    // not yet implemented    

    
    };
   

  
}
