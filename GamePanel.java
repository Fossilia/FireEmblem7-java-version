import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import java.util.*;
import javax.swing.Timer;
import java.awt.geom.Point2D;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.awt.Font;
import sun.audio.*;

class GamePanel extends JPanel implements KeyListener{
  private String mode = "title";
    private int x,y;
    private int gridX, gridY;
    private Unit selectedUnit = null;
    private Unit movingUnit = null;
    private int oldX, oldY;
    //private ArrayList<Integer> xlist = new ArrayList<Integer>();
    //private ArrayList<Integer> ylist = new ArrayList<Integer>();
    private boolean []keys;
    private Image titleImage, menuImage, tutorialImage, map, sprite, battleBackground, battlePlatform, battleUI, idleBattleFrame, victory;
    private ArrayList<Image> spriteList = new ArrayList<Image>();
   // private ArrayList<Image> spriteList = new ArrayList<Image>();
    //private ArrayList<Image> spriteList = new ArrayList<Image>();
    private boolean freeMove = false;
    private FireEmblem mainFrame;
    private String turn = "player";

    //battle
    private ArrayList<Image> battleFrames = new ArrayList<Image>();
    private ArrayList<Image> cursorPics = new ArrayList<Image>();
    private ArrayList<Image> blueSquarePics = new ArrayList<Image>();
    private ArrayList<Image> redSquarePics = new ArrayList<Image>();
    private ArrayList<Image> btnSelectPics = new ArrayList<Image>();
    private ArrayList<Image> arrowUpPics = new ArrayList<Image>();
    private ArrayList<Image> arrowDownPics = new ArrayList<Image>();
    private int [] battleFrameLengths = {5, 3, 4, 4, 8, 2, 1, 25, 1, 3, 5, 8, 48, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 4, 3};
    private int dmgFrame;

    /*private final int GROUND = 0;
    private final int TREES = 1;
    private final int WATER = 2;
    private final int MOUNTAIN1 = 3;
    private final int MOUNTAIN2 = 4;
    private final int WALL = 5;
    private final int RIDGE = 6;*/


    private int btnPos = 1;


    private ArrayList<Unit> playerUnits = new ArrayList<Unit>();
    private ArrayList<Unit> enemyUnits = new ArrayList<Unit>();
    //public Unit(String name, int x, int y, String type, int level, int[] baseStats, int[] baseCaps, int[] baseGrowths, int[] mastery, String items, String deathQuote){
    //private boolean stoppath = false;
    private int[][] terrainMap =
       {{5,0,5,5,5,1,0,3,3,3,0,2,4,4,4},
        {5,0,5,5,5,0,0,0,0,0,0,2,0,0,4},
        {0,0,0,1,0,0,1,6,0,0,1,0,0,0,0},
        {1,0,0,0,0,0,0,0,6,0,0,2,0,0,0},
        {0,0,1,0,0,1,0,1,0,0,0,2,2,0,0},
        {0,0,0,1,0,0,0,0,1,0,0,0,0,0,0},
        {1,0,1,1,0,0,1,0,1,1,0,0,2,2,0},
        {1,0,0,1,0,0,1,0,0,1,0,0,0,2,0},
        {0,0,0,1,0,1,1,1,0,0,0,0,0,2,2},
        {0,1,0,0,0,0,1,0,1,0,2,2,2,2,2}};

    private int[][] pathmap = emptymap();
    private int[][] possibleMap = emptymap();


    Timer moveTimer = new Timer(100, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            freeMove = true;
            moveTimer.stop();
        }
    });

    public Image loadImage(String path){ //loads images cleanly and also tells you if you are missing an image
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        }
        catch (IOException e) {
            System.out.println("Did you misplace "+path+"?");
            return null;
        }

        Image image = new ImageIcon(img).getImage();
        image = image.getScaledInstance(image.getWidth(null)*3, image.getHeight(null)*3, Image.SCALE_SMOOTH);
        return image;
    }

//===================================sound================================================

    public void playSound(String path){ //plays sounds
        try{
            InputStream in = new FileInputStream(path);
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    //USE LIKE THIS: playSound("sounds/coin.wav");
//-----------------------------------------------------------------------------------

    public GamePanel(FireEmblem m){
        keys = new boolean[KeyEvent.KEY_LAST+1];
        mainFrame = m;
        x = 0;
        y = 3;
        gridX = 0;
        gridY = 0;
        setSize(240*3+6,160*3+29);
        moveTimer.start();
        aniTimer.start();
        aniTimer2.start();
        addKeyListener(this);

        try{ //importing custom font
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("font\\GBA.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("font\\GBA.ttf")));
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch(FontFormatException e) {
            e.printStackTrace();
        }

        for(int i=1; i<=8; i++){ //sprites
            sprite = loadImage("images/Menu/Button select"+i+".png");
            btnSelectPics.add(sprite);
        }

        for(int i=1; i<=3; i++){ //sprites
            sprite = loadImage("images/map assets/ArrowDown"+i+".png");
            arrowDownPics.add(sprite);
        }

        for(int i=1; i<=3; i++){ //sprites
            sprite = loadImage("images/map assets/ArrowUp"+i+".png");
            arrowUpPics.add(sprite);
        }

        for(int i=1; i<5; i++){ //sprites
            sprite = loadImage("images/map assets/cursor"+i+".png");
            cursorPics.add(sprite);
        }

        for(int i=1; i<17; i++){ //sprites
            sprite = loadImage("images/map assets/colour squares/BlueSquare"+i+".png");
            blueSquarePics.add(sprite);
        }

        for(int i=1; i<17; i++){ //sprites
            sprite = loadImage("images/map assets/colour squares/RedSquare"+i+".png");
            redSquarePics.add(sprite);
        }

        for(int i=1; i<17; i++){ //sprites
            sprite = loadImage("images/map assets/colour squares/BlueSquare"+i+".png");
            blueSquarePics.add(sprite);
        }

    }

    public void loadMap(){ //loads images and units for map1

     if(mode == "map1"){
        terrainMap = new int [][] //used to check for terrain
       {{5,0,5,5,5,1,0,3,3,3,0,2,4,4,4},
        {5,0,5,5,5,0,0,0,0,0,0,2,0,0,4},
        {0,0,0,1,0,0,1,6,0,0,1,0,0,0,0},
        {1,0,0,0,0,0,0,0,6,0,0,2,0,0,0},
        {0,0,1,0,0,1,0,1,0,0,0,2,2,0,0},
        {0,0,0,1,0,0,0,0,1,0,0,0,0,0,0},
        {1,0,1,1,0,0,1,0,1,1,0,0,2,2,0},
        {1,0,0,1,0,0,1,0,0,1,0,0,0,2,0},
        {0,0,0,1,0,1,1,1,0,0,0,0,0,2,2},
        {0,1,0,0,0,0,1,0,1,0,2,2,2,2,2}};

        //playerunits
        Unit areis = new Unit("Areis", 1, 3, "Lord", "Regal Lance", new int[] {10,10,10,10,10,10,15,10,5}, false, "player");
        Unit ruri = new Unit("Ruri", 2, 6, "Swordmaster", "Frost Brand", new int[] {10,10,10,10,10,10,10,10,3}, false, "player");
        Unit ari = new Unit("Ari", 4, 7, "Swordmaster", "Killing Edge", new int[] {10,10,10,20,20,10,10,10,3}, false, "player");
        playerUnits.add(areis);
        playerUnits.add(ruri);
        playerUnits.add(ari);

        //enemyunits
        Unit brigand1 = new Unit("Brigand", 8, 8, "Brigand", "Iron Axe", new int[] {10,10,10,10,10,10,10,10,3}, false, "enemy");
        Unit brigand2 = new Unit("Brigand", 13, 3, "Brigand", "Iron Axe", new int[] {10,10,10,10,10,10,10,10,3}, false, "enemy");
        Unit brigand3 = new Unit("Brigand", 13, 5, "Brigand", "Iron Axe", new int[] {10,10,10,10,10,10,10,10,3}, false, "enemy");
        Unit bossBrigand = new Unit("Brigand", 14, 4, "Brigand", "Iron Axe", new int[] {15,12,10,10,10,10,10,10,3}, true, "enemy");
        enemyUnits.add(brigand1);
        enemyUnits.add(brigand2);
        enemyUnits.add(brigand3);
        enemyUnits.add(bossBrigand);


/////////////////////////////////////sprites//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //beta battle sprites
        battleBackground = loadImage("images/battle/Plains Background.png");
        battlePlatform = loadImage("images/battle/Plains Close.png");
        battleUI = loadImage("images/battle/Battle Scene 2.png");

//-------------------------------------------aries-----------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Ares/Idle.png");

        for(int i=1; i<31; i++){ //sprites
            sprite = loadImage("images/battle/Ares/attack"+i+".png");
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 22, 2, 2, 3, 3, 3, 3, 3, 3, 3}; //how long each battle frame takes
        dmgFrame = 20; //what exact frame unit hits some1

        areis.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//--------------------------------------------ari----------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Ari/Idle.png");

        for(int i=1; i<36; i++){ //sprites
            sprite = loadImage("images/battle/Ari/attack"+i+".png");
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {5, 3, 4, 4, 8, 2, 1, 25, 1, 3, 5, 8, 48, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 4};
        dmgFrame = 11;

        ari.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//--------------------------------------------ruri----------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Ruri/Idle.png");

        for(int i=1; i<36; i++){ //sprites
            sprite = loadImage("images/battle/Ruri/attack"+i+".png");
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {5, 3, 4, 4, 8, 2, 1, 25, 1, 3, 5, 8, 48, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 4};
        dmgFrame = 11;

        ruri.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//---------------------------------------------brigands---------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Brigand/Idle.png");

        for(int i=1; i<36; i++){ //sprites
            sprite = new ImageIcon("images/battle/Brigand/attack"+i+".png").getImage();
            sprite = sprite.getScaledInstance(sprite.getWidth(null)*3, sprite.getHeight(null)*3, Image.SCALE_SMOOTH);
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {6, 6, 3, 1, 1, 1, 1, 1, 1, 4, 2, 3, 42, 2, 3, 3, 6, 2, 2, 6, 6, 6, 4, 4, 3, 3, 3};
        dmgFrame = 12;

        brigand1.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        brigand2.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        brigand3.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        bossBrigand.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//------------------------------------------------------------------------------------------------------------------------------

        //sprites
        map = loadImage("images/map1.png");

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Brigand/idle"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //up
            sprite = loadImage("images/Overworld/Brigand/up"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //down
            sprite = loadImage("images/Overworld/Brigand/down"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //left
            sprite = loadImage("images/Overworld/Brigand/left"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Brigand/select"+i+".png");
            spriteList.add(sprite);
        }

        victory = loadImage("images/victory.png");

        brigand1.setOverWorldSprites(spriteList);
        brigand2.setOverWorldSprites(spriteList);
        brigand3.setOverWorldSprites(spriteList);
        bossBrigand.setOverWorldSprites(spriteList);
        spriteList = new ArrayList<Image>();

//-----------------------------------------------aries---------------------------------------------------------------------

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Ares/idle"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //up
            sprite = loadImage("images/Overworld/Ares/up"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //down
            sprite = loadImage("images/Overworld/Ares/down"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //left
            sprite = loadImage("images/Overworld/Ares/left"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Ares/select"+i+".png");
            spriteList.add(sprite);
        }

        areis.setOverWorldSprites(spriteList);
        spriteList = new ArrayList<Image>();

//-----------------------------------------------swordmaster-----------------------------------------------------------------
        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Swordmaster/idle"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //up
            sprite = loadImage("images/Overworld/Swordmaster/up"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //down
            sprite = loadImage("images/Overworld/Swordmaster/down"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //left
            sprite = loadImage("images/Overworld/Swordmaster/left"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Swordmaster/select"+i+".png");
            spriteList.add(sprite);
        }

        ari.setOverWorldSprites(spriteList);
        ruri.setOverWorldSprites(spriteList);
        spriteList = new ArrayList<Image>();
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        System.out.println(turn+" turn");
     }

        if(mode == "map2"){
        terrainMap = new int [][]
       {{0,0,1,1,0,0,0,0,3,3,3,5,0,5,0},
        {0,0,0,0,0,1,0,1,3,3,1,0,0,0,0},
        {0,0,1,1,0,0,0,3,3,1,5,1,0,0,0},
        {1,0,0,0,0,0,3,3,3,0,0,1,0,0,0},
        {1,1,0,1,0,5,3,3,3,3,1,0,0,0,1},
        {3,3,1,0,0,0,0,3,3,0,0,0,1,1,1},
        {3,3,3,3,0,0,0,1,0,0,0,3,3,3,1},
        {5,5,5,3,3,1,5,0,0,3,3,3,3,3,3},
        {5,5,5,3,3,3,0,0,3,3,3,3,3,3,3},
        {5,5,5,0,3,0,0,3,3,3,3,3,3,3,3}};

        //Player Units
        Unit areis = new Unit("Areis", 1, 3, "Lord", "Regal Lance", new int[] {10,10,10,10,10,10,15,10,5}, false, "player");
        Unit ruri = new Unit("Ruri", 2, 4, "Swordmaster", "Frost Brand", new int[] {10,10,10,10,10,10,10,10,3}, false, "player");
        Unit ari = new Unit("Ari", 3, 5, "Swordmaster", "Killing Edge", new int[] {10,10,10,20,20,10,10,10,3}, false, "player");
        playerUnits.add(areis);
        playerUnits.add(ruri);
        playerUnits.add(ari);

        //Enemy Units
        Unit brigand1 = new Unit("Brigand", 12, 2, "Brigand", "Iron Axe", new int[] {10,10,10,10,10,10,10,10,3}, false, "enemy");
        Unit brigand2 = new Unit("Brigand", 13, 3, "Brigand", "Iron Axe", new int[] {10,10,10,10,10,10,10,10,3}, false, "enemy");
        Unit soldier1 = new Unit("Soldier", 13, 5, "Brigand", "Iron Axe", new int[] {10,11,10,10,10,10,13,10,3}, false, "enemy");
        Unit soldier2 = new Unit("Soldier", 13, 1, "Brigand", "Iron Axe", new int[] {10,11,10,10,10,10,13,10,3}, false, "enemy");
        Unit bossSoldier = new Unit("Soldier", 13, 0, "Brigand", "Iron Spear", new int[] {20,10,10,10,10,10,13,10,3}, true, "enemy");
        enemyUnits.add(brigand1);
        enemyUnits.add(brigand2);
        enemyUnits.add(soldier1);
        enemyUnits.add(soldier2);
        enemyUnits.add(bossSoldier);


/////////////////////////////////////sprites//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //beta battle sprites
        battleBackground = loadImage("images/battle/Plains Background.png");
        battlePlatform = loadImage("images/battle/Plains Close.png");
        battleUI = loadImage("images/battle/Battle Scene 2.png");

//-------------------------------------------aries-----------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Ares/Idle.png");

        for(int i=1; i<31; i++){ //sprites
            sprite = loadImage("images/battle/Ares/attack"+i+".png");
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 22, 3, 3, 3, 3, 3, 3, 3};
        dmgFrame = 11;

        areis.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//--------------------------------------------ari----------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Ari/Idle.png");

        for(int i=1; i<36; i++){ //sprites
            sprite = loadImage("images/battle/Ari/attack"+i+".png");
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {5, 3, 4, 4, 8, 2, 1, 25, 1, 3, 5, 8, 48, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 4};
        dmgFrame = 22;

        ari.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//--------------------------------------------ruri----------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Ruri/Idle.png");

        for(int i=1; i<36; i++){ //sprites
            sprite = loadImage("images/battle/Ruri/attack"+i+".png");
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {5, 3, 4, 4, 8, 2, 1, 25, 1, 3, 5, 8, 48, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 4};
        dmgFrame = 11;

        ruri.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//---------------------------------------------brigands---------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Brigand/Idle.png");

        for(int i=1; i<36; i++){ //sprites
            sprite = new ImageIcon("images/battle/Brigand/attack"+i+".png").getImage();
            sprite = sprite.getScaledInstance(sprite.getWidth(null)*3, sprite.getHeight(null)*3, Image.SCALE_SMOOTH);
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {6, 6, 3, 1, 1, 1, 1, 1, 1, 4, 2, 3, 4, 2, 42, 3, 6, 2, 2, 6, 6, 6, 4, 4, 3, 3, 3};
        dmgFrame = 15;

        brigand1.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        brigand2.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        //brigand3.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        //bossBrigand.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//---------------------------------------------soldiers---------------------------------------------------------------------------------

        idleBattleFrame = loadImage("images/battle/Soldier/Idle.png");

        for(int i=1; i<12; i++){ //sprites
            sprite = new ImageIcon("images/battle/Soldier/attack"+i+".png").getImage();
            sprite = sprite.getScaledInstance(sprite.getWidth(null)*3, sprite.getHeight(null)*3, Image.SCALE_SMOOTH);
            battleFrames.add(sprite);
        }

        battleFrameLengths = new int[] {5, 4, 9, 48, 5, 2, 6, 3, 6, 6, 11};
        dmgFrame = 4;

        soldier1.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        soldier2.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        bossSoldier.setBattleSprites(idleBattleFrame, battleFrames, battleFrameLengths, dmgFrame);
        battleFrames = new ArrayList<Image>();

//------------------------------------------------------------------------------------------------------------------------------

        //sprites
        map = loadImage("images/map2.png");

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Brigand/idle"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //up
            sprite = loadImage("images/Overworld/Brigand/up"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //down
            sprite = loadImage("images/Overworld/Brigand/down"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //left
            sprite = loadImage("images/Overworld/Brigand/left"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Brigand/select"+i+".png");
            spriteList.add(sprite);
        }

        brigand1.setOverWorldSprites(spriteList);
        brigand2.setOverWorldSprites(spriteList);
        //brigand3.setOverWorldSprites(spriteList);
        //bossBrigand.setOverWorldSprites(spriteList);
        spriteList = new ArrayList<Image>();

//-----------------------------------------------soldiers-------------------------------------------------------------------------------

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Soldier/idle"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //up
            sprite = loadImage("images/Overworld/Soldier/up"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //down
            sprite = loadImage("images/Overworld/Soldier/down"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //left
            sprite = loadImage("images/Overworld/Soldier/left"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Soldier/select"+i+".png");
            spriteList.add(sprite);
        }

        soldier1.setOverWorldSprites(spriteList);
        soldier2.setOverWorldSprites(spriteList);
        bossSoldier.setOverWorldSprites(spriteList);
        //brigand3.setOverWorldSprites(spriteList);
        //bossBrigand.setOverWorldSprites(spriteList);
        spriteList = new ArrayList<Image>();

//-----------------------------------------------aries---------------------------------------------------------------------

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Ares/idle"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //up
            sprite = loadImage("images/Overworld/Ares/up"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //down
            sprite = loadImage("images/Overworld/Ares/down"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //left
            sprite = loadImage("images/Overworld/Ares/left"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Ares/select"+i+".png");
            spriteList.add(sprite);
        }

        areis.setOverWorldSprites(spriteList);
        spriteList = new ArrayList<Image>();

//-----------------------------------------------swordmaster-----------------------------------------------------------------
        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Swordmaster/idle"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //up
            sprite = loadImage("images/Overworld/Swordmaster/up"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //down
            sprite = loadImage("images/Overworld/Swordmaster/down"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<5; i++){ //left
            sprite = loadImage("images/Overworld/Swordmaster/left"+i+".png");
            spriteList.add(sprite);
        }

        for(int i=1; i<4; i++){ //sprites
            sprite = loadImage("images/Overworld/Swordmaster/select"+i+".png");
            spriteList.add(sprite);
        }

        ari.setOverWorldSprites(spriteList);
        ruri.setOverWorldSprites(spriteList);
        spriteList = new ArrayList<Image>();
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        System.out.println(turn+" turn");

    }


    }


    //private boolean battleMode = false;
    private int battleAniCounter;
    private int expCounter;
    private Unit attacking = null;
    private Unit defending = null;
    private int battleTurnCounter = 1;

    Timer battleTimer = new Timer(50, new ActionListener() { //keeps track of the counters used to show battle
        public void actionPerformed(ActionEvent e) {
            if(battleAniCounter == attacking.getFrameLengths().length-1){ //checks of a battle animation is finished
                battleTurnCounter +=1;
                if(battleTurnCounter == 2 && defending.getCurHP() >0 && attacking.getCurHP() >0){
                    Unit attacker = attacking; //switching to retaliation
                    Unit defender = defending;
                    attacking = defender;
                    defending = attacker;
                    battleAniCounter = 0;
                }
                else if(battleTurnCounter == 3 && defending.updateAtkSpd() >= attacking.updateAtkSpd()+5 && defending.getCurHP() >0 && attacking.getCurHP() >0){
                    Unit attacker = defending; //switching to doubles if attackspd is high enough
                    Unit defender = attacking;
                    attacking = attacker;
                    defending = defender;
                    battleAniCounter = 0;
                }
                else{ //this makes battle wait a bit after the animations are over
                    expCounter+=1;
                }
            }
            else{
               battleAniCounter+=1;
               battleTimer.setDelay(17*attacking.getFrameLengths()[battleAniCounter-1]);
               if(battleAniCounter-1 == attacking.getDmgFrame()){ //checks if dmg should be dealt
                   attacking.attack(defending); //where dmg is calculated and applied
                   if(playerUnits.contains(attacking) && attacking.getCurHP()<=0){ //checking if unit is dead now
                       playerUnits.remove(attacking);
                   }
                   if(playerUnits.contains(defending) && defending.getCurHP()<=0){
                       playerUnits.remove(defending);
                   }
                   if(enemyUnits.contains(attacking) && attacking.getCurHP()<=0){
                       enemyUnits.remove(attacking);
                   }
                   if(enemyUnits.contains(defending) && defending.getCurHP()<=0){
                       enemyUnits.remove(defending);
                   }
               }
            }
            if(expCounter == 40){ //closes battle
                battleTurnCounter = 1;
                expCounter = 0;
                //battleMode = false;
                battleAniCounter = 0;
                battleTimer.stop();
                defending = null;
                attacking = null;
                checkForEnemyTurn();
            }
        }
    });

    public void battle(Unit attacking, Unit defending){
        //battleMode = true;
        this.attacking = attacking;
        this.defending = defending;
    }

    public void drawBattle(Graphics g){ //draws everything that happens in battle
        if(battleTimer.isRunning()){
            g.drawImage(battleBackground,0,0,this);
            g.drawImage(battlePlatform,0,0,this);
            //g.drawImage(pic , x + width, y, -width, height, null);
            g.drawImage(battlePlatform, battlePlatform.getWidth(null),0, -battlePlatform.getWidth(null), battlePlatform.getHeight(null), this);
            g.drawImage(battleUI,0,0,this);

            if(playerUnits.contains(attacking) && expCounter == 0){
                if(defending.getCurHP()>0){
                    g.drawImage(defending.getIdleBattleSprite(), defending.getIdleBattleSprite().getWidth(null),0, -defending.getIdleBattleSprite().getWidth(null), defending.getIdleBattleSprite().getHeight(null), this);
                }

                attacking.getCurHP();
                defending.getCurHP();
                attacking.getAttackerWepTri(defending);
                defending.getAttackerWepTri(attacking);
                attacking.getAttackerHit(defending);
                defending.getAttackerHit(attacking);
                attacking.getAttackerAtk(defending);
                defending.getAttackerAtk(attacking);
                attacking.getAttackerCrt(defending);
                defending.getAttackerCrt(attacking);

                Font newFont = g.getFont().deriveFont(Font.BOLD, 24f);
                g.setFont(newFont);
                g.setColor(Color.black);

                g.drawString(attacking.getName(), 202*3, 25*3);
                g.drawString(defending.getName(), 18*3, 25*3);




                //g.drawString(""+attacking.updateAtk(), 128*3, 122*3);
                //g.drawString(attacking.getName(), 18*3, 112*3);
                if(attacking.getAttackerWepTri(defending) == 1){
                        g.drawImage(arrowUpPics.get(arrowCounter), 123*3, 121*3,this);
                }
                if(attacking.getAttackerWepTri(defending) == -1){
                        g.drawImage(arrowDownPics.get(arrowCounter), 123*3, 121*3,this);
                }
                if(defending.getAttackerWepTri(attacking) == 1){
                        g.drawImage(arrowUpPics.get(arrowCounter), 44*3, 121*3,this);
                }
                if(defending.getAttackerWepTri(attacking) == -1){
                        g.drawImage(arrowDownPics.get(arrowCounter), 44*3, 121*3,this);
                }

                //g.drawString(""+attacking.getAttackerWepTri(defending), 232*3, 112*3);
                g.drawString(""+attacking.getAttackerHit(defending), 225*3, 119*3);
                g.drawString(""+attacking.getAttackerAtk(defending), 225*3, 127*3);
                g.drawString(""+attacking.getAttackerCrt(defending), 225*3, 135*3);

                g.drawString(""+defending.getAttackerHit(attacking), 25*3, 119*3);
                g.drawString(""+defending.getAttackerAtk(attacking), 25*3, 127*3);
                g.drawString(""+defending.getAttackerCrt(attacking), 25*3, 135*3);
                //g.drawString(attacking.getAttackerWepTri(defending), 18*3, 112*3);
                //g.drawString(defending.getName(), 202*3, 10*3);




                g.drawImage(attacking.getAttackSprites().get(battleAniCounter),0,0,this);
                g.setColor(Color.black);
                g.fillRect(440,440, attacking.getStats()[0]*5,20); //player health bar
                g.fillRect(100,440, defending.getStats()[0]*5,20); //enemy health bar
                g.drawString(""+attacking.getCurHP(), 140*3, 153*3);
                g.drawString(""+defending.getCurHP(), 25*3, 153*3);
                g.setColor(Color.blue);
                g.fillRect(440,440, attacking.getCurHP()*5,20); //player health bar
                g.fillRect(100,440, defending.getCurHP()*5,20); //enemy health bar
            }
            else if(enemyUnits.contains(attacking) && expCounter == 0){
                if(defending.getCurHP()>0){
                    g.drawImage(defending.getIdleBattleSprite(),0,0,this);
                }

                attacking.getCurHP();
                defending.getCurHP();
                attacking.getAttackerWepTri(defending);
                defending.getAttackerWepTri(attacking);
                attacking.getAttackerHit(defending);
                defending.getAttackerHit(attacking);
                attacking.getAttackerAtk(defending);
                defending.getAttackerAtk(attacking);
                attacking.getAttackerCrt(defending);
                defending.getAttackerCrt(attacking);

                Font newFont = g.getFont().deriveFont(Font.BOLD, 24f);
                g.setFont(newFont);
                g.setColor(Color.black);

                g.drawString(defending.getName(), 202*3, 25*3);
                g.drawString(attacking.getName(), 18*3, 25*3);




                if(attacking.getAttackerWepTri(defending) == 1){
                        g.drawImage(arrowUpPics.get(arrowCounter), 44*3, 121*3,this);
                }
                if(attacking.getAttackerWepTri(defending) == -1){
                        g.drawImage(arrowDownPics.get(arrowCounter), 44*3, 121*3,this);
                }
                if(defending.getAttackerWepTri(attacking) == 1){
                        g.drawImage(arrowUpPics.get(arrowCounter), 44*3, 121*3,this);
                }
                if(defending.getAttackerWepTri(attacking) == -1){
                        g.drawImage(arrowDownPics.get(arrowCounter), 44*3, 121*3,this);
                }

                g.drawString(""+attacking.getAttackerHit(defending), 25*3, 119*3);
                g.drawString(""+attacking.getAttackerAtk(defending), 25*3, 127*3);
                g.drawString(""+attacking.getAttackerCrt(defending), 25*3, 135*3);

                g.drawString(""+defending.getAttackerHit(attacking), 225*3, 119*3);
                g.drawString(""+defending.getAttackerAtk(attacking), 225*3, 127*3);
                g.drawString(""+defending.getAttackerCrt(attacking), 225*3, 135*3);



                g.drawImage(attacking.getAttackSprites().get(battleAniCounter), attacking.getAttackSprites().get(battleAniCounter).getWidth(null), 0, -attacking.getAttackSprites().get(battleAniCounter).getWidth(null), attacking.getAttackSprites().get(battleAniCounter).getHeight(null), this);
                g.setColor(Color.black);
                g.fillRect(440,440, defending.getStats()[0]*5,20); //player health bar
                g.fillRect(100,440, attacking.getStats()[0]*5,20); //enemy health bar
                g.drawString(""+defending.getCurHP(), 140*3, 153*3);
                g.drawString(""+attacking.getCurHP(), 25*3, 153*3);
                g.setColor(Color.blue);
                g.fillRect(440,440, defending.getCurHP()*5,20); //player health bar
                g.fillRect(100,440, attacking.getCurHP()*5,20); //enemy health bar
            }
            else if(expCounter>0){
                if(playerUnits.contains(attacking)){
                    Font newFont = g.getFont().deriveFont(24f);
                    g.setFont(newFont);
                    g.setColor(Color.white);
                    g.drawString(attacking.getName(), 500, 100);
                    g.drawString(defending.getName(), 100, 100);
                    if(defending.getCurHP()>0){
                        g.drawImage(defending.getIdleBattleSprite(), defending.getIdleBattleSprite().getWidth(null),0, -defending.getIdleBattleSprite().getWidth(null), defending.getIdleBattleSprite().getHeight(null), this);
                    }
                    if(attacking.getCurHP()>0){
                        g.drawImage(attacking.getIdleBattleSprite(),0,0,this);
                    }
                    g.drawImage(attacking.getAttackSprites().get(battleAniCounter),0,0,this);
                    g.setColor(Color.black);
                    g.fillRect(440,440, attacking.getStats()[0]*5,20); //player health bar
                    g.fillRect(100,440, defending.getStats()[0]*5,20); //enemy health bar
                    g.setColor(Color.blue);
                    g.fillRect(440,440, attacking.getCurHP()*5,20); //player health bar
                    g.fillRect(100,440, defending.getCurHP()*5,20); //enemy health bar
                }
                else{
                    Font newFont = g.getFont().deriveFont(24f);
                    g.setFont(newFont);
                    g.setColor(Color.white);
                    g.drawString(attacking.getName(), 100, 100);
                    g.drawString(defending.getName(), 500, 100);
                    if(attacking.getCurHP()>0){
                        g.drawImage(attacking.getIdleBattleSprite(), attacking.getIdleBattleSprite().getWidth(null),0, -attacking.getIdleBattleSprite().getWidth(null), attacking.getIdleBattleSprite().getHeight(null), this);
                    }
                    if(defending.getCurHP()>0){
                        g.drawImage(defending.getIdleBattleSprite(),0,0,this);
                    }
                    g.setColor(Color.black);
                    g.fillRect(440,440, defending.getStats()[0]*5,20); //player health bar
                    g.fillRect(100,440, attacking.getStats()[0]*5,20); //enemy health bar
                    g.setColor(Color.blue);
                    g.fillRect(440,440, defending.getCurHP()*5,20); //player health bar
                    g.fillRect(100,440, attacking.getCurHP()*5,20); //enemy health bar
                }
            }
        }
    }



    public int [][] emptymap(){
        int[][] map = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
        return map;

    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

    Timer aniTimer = new Timer(200, new ActionListener() { //timer for animations, resets and increases counters
        public void actionPerformed(ActionEvent e) {

            if(spriteCounter < 3 && spriteFlag == true){
                spriteCounter += 1;
            }
            if(spriteCounter == 0){
                spriteFlag = true;
            }
            if(spriteCounter > 0 && spriteFlag == false){
                spriteCounter -= 1;
            }
            if(spriteCounter == 2){
                spriteFlag = false;
            }

            moveCounter+=1;
            if(moveCounter == 4){
                moveCounter = 0;
            }

            arrowCounter+=1;
            if(arrowCounter == 3){
            	arrowCounter = 1;
            }
        }
    });

    Timer aniTimer2 = new Timer(50, new ActionListener() { //another animation timer
        public void actionPerformed(ActionEvent e) {

            btnCounter+=1;
            if(btnCounter == 8){
                btnCounter = 1;
            }

            shimmerCounter+=1;
            if(shimmerCounter == 16){
                shimmerCounter = 1;
            }
        }
    });

    private int blockCounter, gridCounter, endMoveX, endMoveY, spriteCounter, moveCounter, btnCounter, arrowCounter, shimmerCounter;
    private boolean spriteFlag = false;
    private boolean turnChange = false;
    private int enemyCounter = -1;

    Timer unitMover = new Timer(20, new ActionListener() { //used to move units on the map
        public void actionPerformed(ActionEvent e) {

            String [] dirs = directions.split(" ");
            movingUnit.setDir(dirs[gridCounter]);

            if(dirs[gridCounter].equals("right")){ //checks which way the unit needs to go
                movingUnit.setMoveX(movingUnit.getMoveX() + 4);
            }
            else if(dirs[gridCounter].equals("left")){
                movingUnit.setMoveX(movingUnit.getMoveX() - 4);
            }
            else if(dirs[gridCounter].equals("down")){
                movingUnit.setMoveY(movingUnit.getMoveY() + 4);
            }
            else if(dirs[gridCounter].equals("up")){
                movingUnit.setMoveY(movingUnit.getMoveY() - 4);
            }

            blockCounter+=4;
            if(blockCounter >= 16*3){
                blockCounter = 0;
                gridCounter+=1;
            }

            if(gridCounter>=dirs.length){ //checks if unit has reached its destination
                movingUnit.setMoving(false);
                unitMover.stop();
                movingUnit.setX(endMoveX);
                movingUnit.setY(endMoveY);
                //movingUnit.setActive(false);

                if(turn.equals("enemy")){ //if its enemies turn, either changes the turn to players or goes onto the next enemy
                    enemyCounter+=1;
                    if(defending != null){ //if some1 was targeted
                        //battleMode = true;
                        battleTimer.start();
                    }
                    else{ //if no one was go onto the next enemy
                        if(enemyCounter<enemyUnits.size()){
                            enemyMove(enemyUnits.get(enemyCounter), closestUnitTo(enemyUnits.get(enemyCounter)));
                        }
                        else{
                            enemyCounter=-1;
                            turn = "player";
                            System.out.println(turn+" turn");
                            for(Unit pUnit:playerUnits){
                                pUnit.setActive(true);
                            }
                        }
                        turnChange = false;
                    }
                }
            }
        }
    });

    //AI
    public Unit closestUnitTo(Unit unit){ //finds the closest unit to another unit
        Unit closeUnit = null;
        double closeDistance = 100.0;
        Point2D.Double enemyPos = unit.getPoint();
        for(Unit pUnit:playerUnits){
            Point2D.Double playerPos = pUnit.getPoint();
            if(enemyPos.distance(playerPos) <= closeDistance){
                closeDistance = enemyPos.distance(playerPos);
                closeUnit = pUnit;
            }
        }
        return closeUnit;
    }


    public void pushUnitTo(Unit unit, int x, int y){ //sets variables needed to use unitMover
        movingUnit = unit;
        unit.setMoveX(unit.getX()*16*3+9);
        unit.setMoveY(unit.getY()*16*3+14+3);
        unit.setMoving(true);
        blockCounter = 0;
        gridCounter = 0;
        endMoveX = x;
        endMoveY = y;
        unitMover.start();
    }

    public void checkForEnemyTurn(){ //checks if it should be enemies turn
        turn = "enemy";
        for(Unit unit : playerUnits){ //checking if any units are active
            //System.out.println(unit.getActive());
            if(unit.getActive()){
                turn = "player";
            }
        }
        if(turn.equals("enemy")){
            System.out.println(turn+" turn");
            turnChange = true;

        }

        if(turn.equals("enemy")){
            //enemyCounter+=1;
            if(defending != null){ //if some1 was targeted
                //battleMode = true;
                battleTimer.start();
            }
            else{ //if no one was go onto the next enemy
                if(enemyCounter<enemyUnits.size()){
                    enemyMove(enemyUnits.get(enemyCounter), closestUnitTo(enemyUnits.get(enemyCounter)));
                }
                else{
                    enemyCounter=-1;
                    turn = "player";
                    System.out.println(turn+" turn");
                    for(Unit pUnit:playerUnits){
                        pUnit.setActive(true);
                    }
                }
                turnChange = false;
            }
        }
    }

    private boolean stopMode = false;
    private boolean attackMode = false;
    private Point.Double gridPoint = null;

    public void move(){ //used to deal with key inputs
     if(freeMove){

        if(mode == "title"){

          if(keys[KeyEvent.VK_A] || keys[KeyEvent.VK_Z] || keys[KeyEvent.VK_X] || keys[KeyEvent.VK_ENTER] || keys[KeyEvent.VK_ESCAPE]){
            mode = "menu";
          }
        }

        if(mode == "menu"){
          if(keys[KeyEvent.VK_DOWN] && btnPos < 3){
            btnPos += 1;
            System.out.println(btnPos);
          }
          else if(keys[KeyEvent.VK_UP] && btnPos > 1){
            btnPos -= 1;
            System.out.println(btnPos);
          }

          if(keys[KeyEvent.VK_SPACE] && btnPos == 1){
            mode = "map1";
            loadMap();
          }
          else if(keys[KeyEvent.VK_SPACE] && btnPos == 2){
            mode = "map2";
            loadMap();
          }
          else if(keys[KeyEvent.VK_SPACE] && btnPos == 3){
            mode = "tutorial";
          }
        }

        if(mode == "tutorial"){
          if(keys[KeyEvent.VK_ESCAPE]){
            mode = "menu";
          }
        }

     }
        if(freeMove && !unitMover.isRunning() && !battleTimer.isRunning()){ //checks if nothing is happening

            if(!stopMode){
                if(keys[KeyEvent.VK_Z] && !battleTimer.isRunning() && !unitMover.isRunning()){
                    //battleMode = true;
                    if(playerUnitAtPoint(gridX, gridY)!=null){
                        System.out.println(playerUnitAtPoint(gridX, gridY).getStatus());
                    }
                    //picking a active unit to move
                    if(playerUnitAtPoint(gridX, gridY)!=null && playerUnitAtPoint(gridX, gridY).getActive() && selectedUnit == null){
                        selectedUnit = playerUnitAtPoint(gridX, gridY);
                        selectedUnit.setSelected(true);
                        System.out.println(selectedUnit.getName());
                        oldX = gridX;
                        oldY = gridY;
                        possiblePath(oldX, oldY, selectedUnit);
                        path(oldX, oldY, gridX, gridY, selectedUnit);
                        System.out.println(selectedUnit.getStatus());
                        //showMap(possibleMap);
                        //showMap(pathmap);
                    }


                    //selecting where the player unit goes
                    else if(selectedUnit!=null && playerUnitAtPoint(gridX, gridY) == null && pathmap[gridY][gridX] == 3){
                        pushUnitTo(selectedUnit, gridX, gridY);
                        turn = "enemy";
                        for(Unit unit : playerUnits){
                            if(unit.getActive()){
                                turn = "player";
                            }
                        }
                        System.out.println("Press Z to attack or X to wait");
                        stopMode = true;
                        //selectedUnit.setSelected(false);
                        //selectedUnit = null;
                    }
                }
            }

            if(keys[KeyEvent.VK_Z] && attackMode && selectedUnit!=null && stopMode){ //attacking a enemy unit
				selectedUnit.setActive(false);
                System.out.println("ff");
                System.out.println(attackPoints);
                System.out.println(gridPoint);
                if(attackPoints.contains(gridPoint) && enemyUnitAtPoint(gridX, gridY)!=null){
                    System.out.println("fight");
                    attacking = selectedUnit;
                    defending = enemyUnitAtPoint(gridX, gridY);
                    battleTimer.start();
                    selectedUnit.setSelected(false);
                    selectedUnit = null;
                    stopMode = false;
                    attackMode = false;
                }
            }

            if(keys[KeyEvent.VK_Z] && stopMode && !unitMover.isRunning()){ //confirms attackinging in the menu and generates all the points where the unit can attack
                attackMode = true;
                attackPoints = new ArrayList<Point2D.Double>();
                if(selectedUnit.getAttackRange() == 1){
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()+1), (double)(selectedUnit.getY())));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()-1), (double)(selectedUnit.getY())));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()), (double)(selectedUnit.getY()+1)));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()), (double)(selectedUnit.getY()-1)));
                }
                if(selectedUnit.getAttackRange() == 2){
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()+2), (double)(selectedUnit.getY())));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()-2), (double)(selectedUnit.getY())));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()), (double)(selectedUnit.getY()+2)));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()), (double)(selectedUnit.getY()-2)));

                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()+1), (double)(selectedUnit.getY()+1)));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()-1), (double)(selectedUnit.getY()-1)));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()-1), (double)(selectedUnit.getY()+1)));
                    attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()+1), (double)(selectedUnit.getY()-1)));
                }
                if(selectedUnit.getAttackRange() == 3){ //for units with 1 and 2 attackrange
                    for(int i=1; i<3; i++){
                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()+i), (double)(selectedUnit.getY())));
                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()-i), (double)(selectedUnit.getY())));
                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()), (double)(selectedUnit.getY()+i)));
                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()), (double)(selectedUnit.getY()-i)));

                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()+1), (double)(selectedUnit.getY()+1)));
                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()-1), (double)(selectedUnit.getY()-1)));
                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()-1), (double)(selectedUnit.getY()+1)));
                        attackPoints.add(new Point2D.Double((double)(selectedUnit.getX()+1), (double)(selectedUnit.getY()-1)));
                    }
                }
                //stopMode = false;
            }

            if(keys[KeyEvent.VK_X] && !stopMode && !attackMode && selectedUnit != null){ //cancels unit turn
                selectedUnit.setSelected(false);
                //selectedUnit.setActive(false);
                selectedUnit = null;
            }

            if(keys[KeyEvent.VK_X] && stopMode && !attackMode){ //ends unit turn
                selectedUnit.setSelected(false);
                selectedUnit.setActive(false);
                selectedUnit = null;
                stopMode = false;
                //if(turn.equals("player")){ //check if turn needs to switch
                //checkForEnemyTurn();
                turn = "enemy";
                for(Unit unit : playerUnits){ //checking if any units are active
                    //System.out.println(unit.getActive());
                    if(unit.getActive()){
                        turn = "player";
                    }
                }
                if(turn.equals("enemy")){
                    System.out.println(turn+" turn");
                    turnChange = true;

                }

                if(turn.equals("enemy")){
                    enemyCounter+=1;
                    System.out.println(enemyCounter);
                    if(defending != null){ //if some1 was targeted
                        //battleMode = true;
                        battleTimer.start();
                    }
                    else{ //if no one was go onto the next enemy
                        if(enemyCounter<enemyUnits.size()){
                            enemyMove(enemyUnits.get(enemyCounter), closestUnitTo(enemyUnits.get(enemyCounter)));
                        }
                        else{
                            enemyCounter=-1;
                            turn = "player";
                            System.out.println(turn+" turn");
                            for(Unit pUnit:playerUnits){
                                pUnit.setActive(true);
                            }
                        }
                        turnChange = false;
                    }
                }
                //}
            }

            if(keys[KeyEvent.VK_X] && stopMode && attackMode){ //exits out of attackmode
                attackMode = false;
            }

            if(keys[KeyEvent.VK_RIGHT] && (gridX+1)!=15 && mode == "map1" || keys[KeyEvent.VK_RIGHT] && (gridX+1)!=15 && mode == "map2"){ //used to move cursor
                x += 16*3;
                gridX +=1;
                if(selectedUnit!=null){ //display path made by moving cursor
                    path(oldX, oldY, gridX, gridY, selectedUnit);
                }
            }
            if(keys[KeyEvent.VK_LEFT] && (gridX-1)!=-1 && mode == "map1" ||keys[KeyEvent.VK_LEFT] && (gridX-1)!=-1 && mode == "map2"){
                x -= 16*3;
                gridX -=1;
                if(selectedUnit!=null){
                    path(oldX, oldY, gridX, gridY, selectedUnit);
                }
            }
            if(keys[KeyEvent.VK_UP] && (gridY-1)!=-1 && mode == "map1" ||keys[KeyEvent.VK_UP] && (gridY-1)!=-1 && mode == "map2"){
                y -= 16*3;
                gridY -=1;
                if(selectedUnit!=null){
                    path(oldX, oldY, gridX, gridY, selectedUnit);
                }
            }
            if(keys[KeyEvent.VK_DOWN] && (gridY+1)!=10 && mode == "map1" || keys[KeyEvent.VK_DOWN] && (gridY+1)!=10 && mode == "map2"){
                y += 16*3;
                gridY +=1;
                if(selectedUnit!=null){
                    path(oldX, oldY, gridX, gridY, selectedUnit);
                }
            }
            if(selectedUnit == null){
                oldX = gridX;
                oldY = gridY;
            }
            freeMove = false;
            moveTimer.start();
            gridPoint = new Point2D.Double((double)(gridX), (double)(gridY));
        }

        //Point mouse = MouseInfo.getPointerInfo().getLocation();
        //Point offset = getLocationOnScreen();
        //System.out.println("("+(mouse.x-offset.x)+", "+(mouse.y-offset.y)+")");
    }

    public void showMap(int [][] map){
        System.out.println("***************");
        for(int i=0; i<10; i++){
            for(int j=0; j<15; j++){
                System.out.print(map[i][j]);
            }
            System.out.println("");
        }
    }


    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    //public ArrayList path(int srtx, int srty, int endx, int endy){
    // path(srtx, srty, endx, endy, new ArrayList());
    //}

    public int [][] stringtomap(String x, String y){ //converts an x and y string to a map
        int [][] newmap = emptymap();
        if(x.equals("") && y.equals("")){
            return newmap;
        }
        String [] xarray = x.split(" ");
        String [] yarray = y.split(" ");
        for(int i = 0; i<xarray.length; i++){
            newmap[Integer.parseInt(yarray[i])][Integer.parseInt(xarray[i])] = 3;
        }
        return newmap;
    }

    public void path(int srtx, int srty, int endx, int endy, Unit unit){ //used to show where the player unit is going
        mincount = 100;
        path(srtx, srty, endx, endy, "", "", "", 0, unit);
    }

    private int mincount = 1000;
    private String directions;

    public void path(int srtx, int srty, int endx, int endy, String xpath, String ypath, String dirs, int count, Unit unit){
        int limit = unit.getStats()[8]-1;
        if(srtx == endx && srty == endy){
            if(count<=mincount){
                //System.out.println("hit");
                //int [][] newmap = emptymap();
                mincount = count;
                pathmap = stringtomap(xpath, ypath);
                directions = dirs;
            }
        }
        else{
            if(count<mincount && count<=limit){ //all this stuff checks which path is the best to take and if the desired movement postition is possible
                if(srtx+1<15 && stringtomap(xpath, ypath)[srty][srtx+1]!=3 && !unit.getBadTerrain().contains(terrainMap[srty][srtx+1]) && enemyUnitAtPoint(srtx+1, srty) == null){
                    int slow = 1;
                    if(unit.getSlowTerrain().contains(terrainMap[srty][srtx+1])){
                        slow = 2;
                    }
                    path(srtx+1, srty, endx, endy, xpath+Integer.toString(srtx+1)+" ", ypath+Integer.toString(srty)+" ", dirs+"right ", count+slow, unit);
                }
                if(srtx-1>-1 && stringtomap(xpath, ypath)[srty][srtx-1]!=3 && !unit.getBadTerrain().contains(terrainMap[srty][srtx-1]) && enemyUnitAtPoint(srtx-1, srty) == null){
                    int slow = 1;
                    if(unit.getSlowTerrain().contains(terrainMap[srty][srtx-1])){
                        slow = 2;
                    }
                    path(srtx-1, srty, endx, endy, xpath+Integer.toString(srtx-1)+" ", ypath+Integer.toString(srty)+" ", dirs+"left ", count+slow, unit);
                }
                if(srty+1<10 && stringtomap(xpath, ypath)[srty+1][srtx]!=3 && !unit.getBadTerrain().contains(terrainMap[srty+1][srtx]) && enemyUnitAtPoint(srtx+1, srty) == null){
                    int slow = 1;
                    if(unit.getSlowTerrain().contains(terrainMap[srty+1][srtx])){
                        slow = 2;
                    }
                    path(srtx, srty+1, endx, endy, xpath+Integer.toString(srtx)+" ", ypath+Integer.toString(srty+1)+" ", dirs+"down ", count+slow, unit);
                }
                if(srty-1>-1 && stringtomap(xpath, ypath)[srty-1][srtx]!=3 && !unit.getBadTerrain().contains(terrainMap[srty-1][srtx]) && enemyUnitAtPoint(srtx-1, srty) == null){
                    int slow = 1;
                    if(unit.getSlowTerrain().contains(terrainMap[srty-1][srtx])){
                        slow = 2;
                    }
                    path(srtx, srty-1, endx, endy, xpath+Integer.toString(srtx)+" ", ypath+Integer.toString(srty-1)+" ", dirs+"up ", count+slow, unit);
                }
            }
        }
    }





    public void possiblePath(int srtx, int srty, Unit unit){ //works like path except it is used to make a map showing where a player unit can go once selected
        possibleMap = emptymap();
        possiblePath(srtx, srty, "", "", 0, unit);
    }

    public void possiblePath(int srtx, int srty, String xpath, String ypath,int count, Unit unit){
        int limit = unit.getStats()[8]-1;
        int [][]tmpMap = stringtomap(xpath, ypath);
        if(count<=limit){
            if(srtx+1<15 && stringtomap(xpath, ypath)[srty][srtx+1]!=3 && !unit.getBadTerrain().contains(terrainMap[srty][srtx+1]) && enemyUnitAtPoint(srtx+1, srty) == null){
                possibleMap[srty][srtx+1] = 3;
                int slow = 1;
                if(unit.getSlowTerrain().contains(terrainMap[srty][srtx+1])){
                    slow = 2;
                }
                possiblePath(srtx+1, srty, xpath+Integer.toString(srtx+1)+" ", ypath+Integer.toString(srty)+" ", count+slow, unit);
            }

            if(srtx-1>-1 && stringtomap(xpath, ypath)[srty][srtx-1]!=3 && !unit.getBadTerrain().contains(terrainMap[srty][srtx-1]) && enemyUnitAtPoint(srtx-1, srty) == null){
                possibleMap[srty][srtx-1] = 3;
                int slow = 1;
                if(unit.getSlowTerrain().contains(terrainMap[srty][srtx-1])){
                    slow = 2;
                }
                possiblePath(srtx-1, srty, xpath+Integer.toString(srtx-1)+" ", ypath+Integer.toString(srty)+" ", count+slow, unit);
            }

            if(srty+1<10 && stringtomap(xpath, ypath)[srty+1][srtx]!=3 && !unit.getBadTerrain().contains(terrainMap[srty+1][srtx]) && enemyUnitAtPoint(srtx, srty-1) == null){
                possibleMap[srty+1][srtx] = 3;
                int slow = 1;
                if(unit.getSlowTerrain().contains(terrainMap[srty+1][srtx])){
                    slow = 2;
                }
                possiblePath(srtx, srty+1, xpath+Integer.toString(srtx)+" ", ypath+Integer.toString(srty+1)+" ", count+slow, unit);
            }

            if(srty-1>-1 && stringtomap(xpath, ypath)[srty-1][srtx]!=3 && !unit.getBadTerrain().contains(terrainMap[srty-1][srtx]) && enemyUnitAtPoint(srtx, srty-1) == null){
                possibleMap[srty-1][srtx] = 3;
                int slow = 1;
                if(unit.getSlowTerrain().contains(terrainMap[srty-1][srtx])){
                    slow = 2;
                }
                possiblePath(srtx, srty-1, xpath+Integer.toString(srtx)+" ", ypath+Integer.toString(srty-1)+" ", count+slow, unit);
            }
        }
    }






    private int [][] enemyPathMap = emptymap();

    public void enemyMove(Unit enemyUnit, Unit playerUnit){ //all AI unit stuff, enemies try to take the best path that will lead the closest to the closest unit to them or attack them if they can
        mindist = 100.0;
        enemyMove(enemyUnit.getX(), enemyUnit.getY(), playerUnit.getX(), playerUnit.getY(), "", "", "", 0, enemyUnit, playerUnit);
    }

    private double mindist = 100.0;

    public void enemyMove(int srtx, int srty, int endx, int endy, String xpath, String ypath, String dirs, int count, Unit eUnit, Unit pUnit){
        int limit = eUnit.getStats()[8]-1;
        if(Point2D.distance(srtx, srty, endx, endy) == 1 || count == limit+1 && Point2D.distance(srtx, srty, endx, endy)<=mindist){
            //System.out.println("hit enemy "+count+" "+limit);
            if(Point2D.distance(srtx, srty, endx, endy) == 1){ //checks if it can attack a unit
                System.out.println(eUnit.getName()+" attacked "+pUnit.getName());

                battle(eUnit, pUnit);
            }
            enemyPathMap = stringtomap(xpath, ypath);
            //System.out.print(Point2D.distance(srtx, srty, endx, endy));
            mindist = Point2D.distance(srtx, srty, endx, endy);
            pushUnitTo(eUnit, srtx, srty);
            //eUnit.setX(srtx);
            //eUnit.setY(srty);
            directions = dirs;
        }
        else{
            if(Point2D.distance(srtx, srty, endx, endy)<=mindist && count<=limit){
                if(srtx+1<15 && stringtomap(xpath, ypath)[srty][srtx+1]!=3 && !eUnit.getBadTerrain().contains(terrainMap[srty][srtx+1]) && playerUnitAtPoint(srtx+1, srty) == null && enemyUnitAtPoint(srtx+1, srty) == null){
                    int slow = 1;
                    if(eUnit.getSlowTerrain().contains(terrainMap[srty][srtx+1])){
                        slow = 2;
                    }
                    enemyMove(srtx+1, srty, endx, endy, xpath+Integer.toString(srtx+1)+" ", ypath+Integer.toString(srty)+" ", dirs+"right ", count+slow, eUnit, pUnit);
                }
                if(srtx-1>-1 && stringtomap(xpath, ypath)[srty][srtx-1]!=3 && !eUnit.getBadTerrain().contains(terrainMap[srty][srtx-1]) && playerUnitAtPoint(srtx-1, srty) == null && enemyUnitAtPoint(srtx-1, srty) == null){
                    int slow = 1;
                    if(eUnit.getSlowTerrain().contains(terrainMap[srty][srtx-1])){
                        slow = 2;
                    }
                    enemyMove(srtx-1, srty, endx, endy, xpath+Integer.toString(srtx-1)+" ", ypath+Integer.toString(srty)+" ", dirs+"left ", count+slow, eUnit, pUnit);
                }
                if(srty+1<10 && stringtomap(xpath, ypath)[srty+1][srtx]!=3 && !eUnit.getBadTerrain().contains(terrainMap[srty+1][srtx]) && playerUnitAtPoint(srtx, srty+1) == null && enemyUnitAtPoint(srtx, srty+1) == null){
                    int slow = 1;
                    if(eUnit.getSlowTerrain().contains(terrainMap[srty+1][srtx])){
                        slow = 2;
                    }
                    enemyMove(srtx, srty+1, endx, endy, xpath+Integer.toString(srtx)+" ", ypath+Integer.toString(srty+1)+" ", dirs+"down ", count+slow, eUnit, pUnit);
                }
                if(srty-1>-1 && stringtomap(xpath, ypath)[srty-1][srtx]!=3 && !eUnit.getBadTerrain().contains(terrainMap[srty-1][srtx]) && playerUnitAtPoint(srtx, srty-1) == null && enemyUnitAtPoint(srtx, srty-1) == null){
                    int slow = 1;
                    if(eUnit.getSlowTerrain().contains(terrainMap[srty-1][srtx])){
                        slow = 2;
                    }
                    enemyMove(srtx, srty-1, endx, endy, xpath+Integer.toString(srtx)+" ", ypath+Integer.toString(srty-1)+" ", dirs+"up ", count+slow, eUnit, pUnit);
                }
            }
        }
    }











    public Unit playerUnitAtPoint(int x, int y){
        for(Unit unit : playerUnits){
            if(unit.getX() == x && unit.getY() == y){
                return unit;
            }
        }
        return null;
    }

    public Unit enemyUnitAtPoint(int x, int y){
        for(Unit unit : enemyUnits){
            if(unit.getX() == x && unit.getY() == y){
                return unit;
            }
        }
        return null;
    }

    private ArrayList<Point2D.Double> attackPoints = new ArrayList<Point2D.Double>();
    private Font customFont;

   public void paintComponent(Graphics g){ //actually draws everything

      Font newFont = g.getFont().deriveFont(40f);
      g.setFont(newFont);
      g.setColor(Color.white);

      if(mode == "title"){
        titleImage = loadImage("images/Menu/Title.png");
        g.drawImage(titleImage,0,0,this);
        g.drawString("Press 'A'", 55*3, 39*3);
      }

      if(mode == "menu"){
        menuImage = loadImage("images/Menu/Main Menu.png");
        g.drawImage(menuImage,0,0,this);
        g.drawString("Chapter 1", 90*3, 58*3);
        g.drawString("Chapter 2", 90*3, 83*3);
        g.drawString("Tutorial", 97*3, 108*3);
        g.drawString("Press 'Space' to select a button", 55*3, 39*3);

        if(btnPos == 1){
          g.drawImage(btnSelectPics.get(btnCounter), 37*3, 43*3, this);
          g.drawString("Chapter 1", 90*3, 58*3);
        }
        else if(btnPos == 2){
          g.drawImage(btnSelectPics.get(btnCounter), 37*3, 68*3, this);
          g.drawString("Chapter 2", 90*3, 83*3);
        }
        else if(btnPos == 3){
          g.drawImage(btnSelectPics.get(btnCounter), 37*3, 93*3, this);
          g.drawString("Tutorial", 97*3, 108*3);
        }
      }

      if(mode == "tutorial"){
        tutorialImage = loadImage("images/Menu/Tutorial.png");
        g.drawImage(tutorialImage,0,0,this);
      newFont = g.getFont().deriveFont(20f);
      g.setFont(newFont);
      g.setColor(Color.white);
        g.drawString("Press 'Z' to move unit", 55*3, 39*3);
        g.drawString("Press 'ESC' to go back to main menu", 10, 20);
      }

        if(mode == "map1" || mode == "map2"){
        g.drawImage(map,0,0,this);

        for(int x=0; x<15; x++){ //draws path and possible path using given maps from the other methods
            for(int y = 0; y<10; y++){
                //g.drawRect(0+x*16*3,3+y*16*3,15*3,15*3); //grid

                if(selectedUnit!=null){
                    if(possibleMap[y][x] == 3){ //possible path
                        g.setColor(Color.blue);
                        //g.fillRect(0+x*16*3+10,3+y*16*3+10,20,20);
                        g.drawImage(blueSquarePics.get(shimmerCounter),0+x*16*3,3+y*16*3,this);
                    }
                    if(pathmap[y][x] == 3 && !stopMode){ //path
                        g.setColor(Color.yellow);
                        g.fillRect(0+x*16*3+14,3+y*16*3+14,16,16);
                    }
                }

                if(attackMode){ //showing where the unit cn attack depending on their attack range
                    g.setColor(Color.red);
                    if(selectedUnit.getAttackRange() == 1){
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3+48, 3+selectedUnit.getY()*16*3, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3-48, 3+selectedUnit.getY()*16*3, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3, 3+selectedUnit.getY()*16*3+48, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3, 3+selectedUnit.getY()*16*3-48, this);
                    }
                    if(selectedUnit.getAttackRange() == 2){
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3+90, 3+selectedUnit.getY()*16*3, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3-90, 3+selectedUnit.getY()*16*3, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3, 3+selectedUnit.getY()*16*3+96, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3, 3+selectedUnit.getY()*16*3-96, this);

                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3+48, 3+selectedUnit.getY()*16*3+48, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3-48, 3+selectedUnit.getY()*16*3+48, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3+48, 3+selectedUnit.getY()*16*3-48, this);
                        g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3-48, 3+selectedUnit.getY()*16*3-48, this);
                    }
                    if(selectedUnit.getAttackRange() == 3){ //for units with 1 and 2 attackrange
                        for(int i=1; i<3; i++){
                            g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3+48*i, 3+selectedUnit.getY()*16*3, this);
                            g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3-48*i, 3+selectedUnit.getY()*16*3, this);
                            g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3, 3+selectedUnit.getY()*16*3+48*i, this);
                            g.drawImage(redSquarePics.get(shimmerCounter),selectedUnit.getX()*16*3, 3+selectedUnit.getY()*16*3-48*i, this);

                            g.drawImage(redSquarePics.get(shimmerCounter),selectedUnit.getX()*16*3+48, 3+selectedUnit.getY()*16*3+48, this);
                            g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3-48, 3+selectedUnit.getY()*16*3+48, this);
                            g.drawImage(redSquarePics.get(shimmerCounter), selectedUnit.getX()*16*3+48, 3+selectedUnit.getY()*16*3-48, this);
                            g.drawImage(redSquarePics.get(shimmerCounter),selectedUnit.getX()*16*3-48, 3+selectedUnit.getY()*16*3-48, this);
                        }
                    }
                }

                g.setColor(Color.white); //drawing player units
                for(Unit unit : playerUnits){
                    unit.setCounter3(spriteCounter);
                    unit.setCounter4(moveCounter);
                    unit.draw(g);
                }

                g.setColor(Color.black); //drawing enemy units
                for(Unit unit : enemyUnits){
                    unit.setCounter3(spriteCounter);
                    unit.setCounter4(moveCounter);
                    unit.draw(g);
                }

                g.setColor(Color.black);
            }
        }

        if(stopMode && selectedUnit!=null && !unitMover.isRunning() && !attackMode){ //drawing menu that is displayed after a player unit is moved
            g.setFont(customFont);
            g.setColor(Color.red);
            g.fillRect(selectedUnit.getX()*16*3+50, 3+selectedUnit.getY()*16*3,100,70);
            g.setColor(Color.yellow);
            g.drawRect(selectedUnit.getX()*16*3+50, 3+selectedUnit.getY()*16*3,100,70);
            g.drawString("Attack: Z", selectedUnit.getX()*16*3+60, 3+selectedUnit.getY()*16*3+30);
            g.drawString("Wait:   X", selectedUnit.getX()*16*3+60, 3+selectedUnit.getY()*16*3+60);
        }

        g.drawImage(cursorPics.get(moveCounter),x-24,y-24,this); //draws cursor

        if(playerUnitAtPoint(gridX, gridY)!=null){ //draws a box showing name and hp of a unit when hovered over
            g.setFont(customFont);
            g.setColor(Color.black);
            g.fillRect(30,30,150,80);
            g.setColor(Color.white);
            g.drawString(playerUnitAtPoint(gridX, gridY).getName(),60, 70);
            g.drawString("HP: "+Integer.toString(playerUnitAtPoint(gridX, gridY).getCurHP()),60, 100);
        }
        if(enemyUnitAtPoint(gridX, gridY)!=null){
            g.setFont(customFont);
            g.setColor(Color.black);
            g.fillRect(30,30,150,80);
            g.setColor(Color.white);
            g.drawString(enemyUnitAtPoint(gridX, gridY).getName(),60, 70);
            g.drawString("HP: "+Integer.toString(enemyUnitAtPoint(gridX, gridY).getCurHP()),60, 100);
        }
        if(playerUnits.isEmpty()){ //gameover
            System.out.println("GameOver");
        }
        if(enemyUnits.isEmpty()){ //beating a level
            g.drawImage(victory,0,0,this);
        }
        drawBattle(g);
        }
    }
}

//float alpha = (frame%256)/255f;
//g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));