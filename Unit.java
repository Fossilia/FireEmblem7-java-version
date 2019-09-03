import java.util.*;
import java.awt.geom.Point2D;
import java.awt.*;
import javax.swing.*;

public class Unit{
    private final int GROUND = 0;
    private final int TREES = 1;
    private final int WATER = 2;
    private final int MOUNTAIN = 3;
    private final int PEAK = 4;
    private final int WALL = 5;
    private final int CLIFF = 6;
    private final int HP = 0;
    private final int STR = 1;
    private final int MAG = 2;
    private final int SKL = 3;
    private final int SPD = 4;
    private final int LCK = 5;
    private final int DEF = 6;
    private final int RES = 7;
    private final int MOV = 8;
    private final int MT = 0;
    private final int ACC = 1;
    private final int CRT = 2;
    private final int WT = 3;
    private final int RNG = 4;

  private UnitClass UnitClass;
  private Weapon Weapon;
  private String movType, weakness, name, badTerrain, slowTerrain, classString, wepString, dmgType, type;
  private String dir = "idle";
  private ArrayList<Integer> terrain = new ArrayList<Integer>();
  private ArrayList<Image> sprites = new ArrayList<Image>();
  private ArrayList<Image> idleSprites, upSprites, downSprites, rightSprites, selectedSprites, leftSprites, attackSprites;
  private int x, y, level, exp, moveX, moveY, counter3, counter4;
  private int curHP, atk, atkspd, hitRate, crtRate, avo, battleAtk, battleHit, battleCrt, crt, dmgFrame;
  private int[] stats = {0, 0, 0, 0, 0, 0, 0, 0, 0};
  private int [] battleFrameLengths;
  private int attackRange;
  private int wepTri = 0;
  private boolean active = true;
  private boolean moving = false;
  private boolean hit = false;
  private boolean selected = false;
  //private boolean drawBattle = false;
  private Image idleBattleSprite;
  private Image bossIcon;
  private boolean boss = false;


  public Unit(String name, int x, int y, String classType, String wep, int[] stats, boolean boss, String type){
    //HP/STR/MAG/SKL/SPD/LCK/DEF/RES/MOV
   //MT, ACC, CRT, WT, RNG
   this.name = name;
    this.x = x;
    this.y = y;
    this.stats = stats;
    this.boss = boss;
    this.type = type;
    bossIcon = new ImageIcon("images/Overworld/bossIcon.png").getImage();
    bossIcon = bossIcon.getScaledInstance(bossIcon.getWidth(null)*3, bossIcon.getHeight(null)*3, Image.SCALE_SMOOTH);
    curHP = stats[HP];
    this.UnitClass = new UnitClass(classType);

    this.Weapon = new Weapon(wep);
    System.out.println("Unit: " + name);
    System.out.println("Weapon: " + wep);
    updateStats();
    System.out.println("Attack: " + atk);
    System.out.println("Attack Speed: " + atkspd);
    System.out.println("Hit Rate: " + hitRate);
    System.out.println("Critical Rate: " + crtRate);
    System.out.println("Avoid: " + avo);
    System.out.println("Description: " + Weapon.desc + "\n");
  }

  class UnitClass{
    private String movType;
    private ArrayList<String> weaknesses = new ArrayList<String>();
    private ArrayList<Integer> badTerrain = new ArrayList<Integer>();
    private ArrayList<Integer> slowTerrain = new ArrayList<Integer>();

    public UnitClass(String classString){
        if(classString.equals("Lord")){
            badTerrain.add(WALL);
            badTerrain.add(WATER);
            badTerrain.add(MOUNTAIN);
            badTerrain.add(PEAK);
            badTerrain.add(CLIFF);
            slowTerrain.add(TREES);
            movType = "Cavalry";
            weaknesses.add("Beast");
            attackRange += 1;
        }

        if(classString.equals("Swordmaster")){
            badTerrain.add(WALL);
            slowTerrain.add(WATER);
            badTerrain.add(MOUNTAIN);
            badTerrain.add(PEAK);
            badTerrain.add(CLIFF);
            slowTerrain.add(TREES);
            movType = "Infantry";
            attackRange+=1;
        }

        if(classString.equals("Brigand")){
            badTerrain.add(WALL);
            badTerrain.add(WATER);
            badTerrain.add(MOUNTAIN);
            badTerrain.add(PEAK);
            badTerrain.add(CLIFF);
            slowTerrain.add(TREES);
            movType = "Infantry";
            attackRange+=1;
        }

        if(classString.equals("Archer")){
            badTerrain.add(WALL);
            badTerrain.add(WATER);
            badTerrain.add(MOUNTAIN);
            badTerrain.add(PEAK);
            badTerrain.add(CLIFF);
            slowTerrain.add(TREES);
            movType = "Infantry";
            attackRange+=1;
        }
    }
  }

  class Weapon{
    private String wepString, wepType, dmgType, effType, desc;
    private int[] stats = {0, 0, 0, 0, 0};
    private Image icon;

    public Weapon(String wepString){
      if(wepString.equals("Killing Edge")){
        icon = new ImageIcon("images/Weapon Sprites/Killing Edge.png").getImage();
        icon = icon.getScaledInstance(icon.getWidth(null)*3, icon.getHeight(null)*3, Image.SCALE_SMOOTH);
        wepType = "Sword";
        dmgType = "Physical";
        desc = "Increases Critical Rate";
        stats = new int[]{5, 80, 25, 7, 1};
      }
      if(wepString.equals("Frost Brand")){
        icon = new ImageIcon("images/Weapon Sprites/Frost Brand.png").getImage();
        icon = icon.getScaledInstance(icon.getWidth(null)*3, icon.getHeight(null)*3, Image.SCALE_SMOOTH);
        wepType = "Sword";
        dmgType = "Magical";
        desc = "Deals magical damage based on Mag";
        attackRange = 3;
        stats = new int[]{8, 80, 0, 10, 1};
      }
      if(wepString.equals("Iron Lance")){
        icon = new ImageIcon("images/Weapon Sprites/Iron Lance.png").getImage();
        icon = icon.getScaledInstance(icon.getWidth(null)*3, icon.getHeight(null)*3, Image.SCALE_SMOOTH);
        wepType = "Lance";
        dmgType = "Physical";
        desc = "-";
        stats = new int[]{6, 90, 0, 8, 1};
      }
      if(wepString.equals("Regal Lance")){
        icon = new ImageIcon("images/Weapon Sprites/Regal Lance.png").getImage();
        icon = icon.getScaledInstance(icon.getWidth(null)*3, icon.getHeight(null)*3, Image.SCALE_SMOOTH);
        wepType = "Lance";
        dmgType = "Physical";
        desc = "Lance with ornate craftsmanship";
        stats = new int[]{8, 120, 10, 10, 1};
      }
      if(wepString.equals("Iron Axe")){
        icon = new ImageIcon("images/Weapon Sprites/Iron Axe.png").getImage();
        icon = icon.getScaledInstance(icon.getWidth(null)*3, icon.getHeight(null)*3, Image.SCALE_SMOOTH);
        wepType = "Axe";
        dmgType = "Physical";
        desc = "-";
        stats = new int[]{7, 80, 0, 10, 1};
      }
      if(wepString.equals("Sniper Bow")){
        icon = new ImageIcon("images/Weapon Sprites/Sniper Bow.png").getImage();
        icon = icon.getScaledInstance(icon.getWidth(null)*3, icon.getHeight(null)*3, Image.SCALE_SMOOTH);
        wepType = "Bow";
        dmgType = "Physical";
        desc = "Heavy yet accurate bow";
        attackRange = 4;
        stats = new int[]{8, 105, 0, 12, 1};
      }
    }
  }

  public void attack(Unit enemy){
    updateStats();
    //drawBattle = true;
    if(Weapon.wepType == "Sword" && enemy.Weapon.wepType == "Axe" || Weapon.wepType == "Lance" && enemy.Weapon.wepType == "Sword" || Weapon.wepType == "Axe" && enemy.Weapon.wepType == "Lance"){
      wepTri = +1;
    }
    else if(Weapon.wepType == "Sword" && enemy.Weapon.wepType == "Lance"|| Weapon.wepType == "Lance" && enemy.Weapon.wepType == "Axe" || Weapon.wepType == "Axe" && enemy.Weapon.wepType == "Sword"){
      wepTri = -1;
    }

    battleHit = (hitRate + wepTri*10) - (((enemy.stats[SKL]*3+enemy.stats[LCK])/2));
    if(battleHit<0){
      battleHit = 0;
    }

    battleCrt = crtRate - enemy.stats[LCK];
    if(battleCrt<0){
      battleCrt = 0;
    }

    if(Math.random()*100<battleHit){
      hit = true;
    }
    if(Math.random()*100<battleCrt){
      crt = 3;
    }
    else{
      crt = 1;
    }

    if(Weapon.dmgType.equals("Physical")){
      battleAtk = (atk+wepTri - enemy.stats[DEF])*crt;
    }
    else if(Weapon.dmgType.equals("Magical")){
      battleAtk = (atk+wepTri - enemy.stats[RES])*crt;
    }

    System.out.println(name + "'s Hit Rate: " + battleHit + ", Crit Rate: " + battleCrt + ", Attack: " + (atk+wepTri) + ", WepTri: " + wepTri);
    System.out.println(enemy.name + "'s HP: " + enemy.curHP + ", DEF: " + enemy.stats[DEF]);

    if(hit == true){
      if(enemy.curHP < battleAtk){
        enemy.curHP = 0;
      }
      else{
        enemy.curHP -= battleAtk;
      }
      System.out.println("Hit! " + name + " dealt " + battleAtk + " dmg to " + enemy.name);
      System.out.println(enemy.name + "'s HP: " + enemy.curHP);
    }
    else if(hit == false){
      System.out.println("Missed! ");
    }
  }

  //getter and setter

  public int getAttackerWepTri(Unit enemy){
  	updateStats();
    if(Weapon.wepType == "Sword" && enemy.Weapon.wepType == "Axe" || Weapon.wepType == "Lance" && enemy.Weapon.wepType == "Sword" || Weapon.wepType == "Axe" && enemy.Weapon.wepType == "Lance"){
      wepTri = +1;
    }
    else if(Weapon.wepType == "Sword" && enemy.Weapon.wepType == "Lance"|| Weapon.wepType == "Lance" && enemy.Weapon.wepType == "Axe" || Weapon.wepType == "Axe" && enemy.Weapon.wepType == "Sword"){
      wepTri = -1;
    }
    return wepTri;
  }

  public int getAttackerAtk(Unit enemy){
  	updateStats();
    if(Weapon.dmgType.equals("Physical")){
      battleAtk = (atk+wepTri - enemy.stats[DEF]);
    }
    else if(Weapon.dmgType.equals("Magical")){
      battleAtk = (atk+wepTri - enemy.stats[RES]);
    }
    return battleAtk;
  }

  public int getAttackerCrt(Unit enemy){
  	updateStats();
    battleCrt = crtRate - enemy.stats[LCK];
    if(battleCrt<0){
      battleCrt = 0;
    }
    return battleCrt;
  }

  public int getAttackerHit(Unit enemy){
  	updateStats();
    battleHit = (hitRate + wepTri*10) - (((enemy.stats[SKL]*3+enemy.stats[LCK])/2));
    if(battleHit<0){
      battleHit = 0;
    }
    return battleHit;
  }

  public void updateStats(){
    updateAtk();
    updateAtkSpd();
    updateHit();
    updateCrt();
    updateAvo();
  }

  public int updateAtk(){
    if(Weapon.dmgType.equals("Physical")){
     atk = Weapon.stats[MT] + stats[STR];
    }
    else if(Weapon.dmgType.equals("Magical")){
     atk = Weapon.stats[MT] + stats[MAG];
    }
    return atk;
  }

  public int updateAtkSpd(){
   return atkspd = stats[SPD] - Weapon.stats[WT];
  }

  public int updateHit(){
   return hitRate = Weapon.stats[ACC] + (stats[SKL]*2 + stats[LCK])/2;
  }

  public int updateCrt(){
   return crtRate = Weapon.stats[CRT] + stats[SKL]/2;
  }

  public int updateAvo(){
   return avo = (stats[SKL]*3+stats[LCK])/2;
  }

  public String getMoveType(){
      return UnitClass.movType;
  }

  public int [] getStats(){
      return stats;
  }

  public int getCurHP(){
      return curHP;
  }

  public ArrayList<Integer> getBadTerrain(){
      return UnitClass.badTerrain;
  }

   public ArrayList<Integer> getSlowTerrain(){
      return UnitClass.slowTerrain;
  }

  public boolean getActive(){
   return active;
  }

  public void setActive(boolean a){
   active = a;
  }

  public Point2D.Double getPoint(){
      return new Point2D.Double((double)(x), (double)(y));
  }

  public void draw(Graphics g){
      //sprite stuff
      if(!moving && !selected){
          //g.fillRect(x*16*3+14,y*16*3+14+3,16,16);
          g.drawImage(idleSprites.get(counter3) , x*16*3-9*3, y*16*3-16*3, idleSprites.get(counter3).getWidth(null), idleSprites.get(counter3).getHeight(null), null);
          if(boss){
               g.drawImage(bossIcon , x*16*3-9*3, y*16*3-16*3, bossIcon.getWidth(null), bossIcon.getHeight(null), null);
          }
      }
      /*if(!active && type.equals("player") && !moving){
          g.setColor(Color.black);
          g.drawRect(x*16*3-9*3, y*16*3-16*3, 48, 48);
      }*/
      else if(!moving && selected){
         //System.out.println("selected");
          g.drawImage(selectedSprites.get(counter3) , x*16*3-9*3, y*16*3-16*3, selectedSprites.get(counter3).getWidth(null), selectedSprites.get(counter3).getHeight(null), null);
      }
      else{
          //g.fillRect(moveX, moveY,16,16);
          if(dir.equals("up")){
              g.drawImage(upSprites.get(counter4) , moveX-13*3, moveY-21*3, upSprites.get(counter4).getWidth(null), upSprites.get(counter4).getHeight(null), null);
              if(boss){
                  g.drawImage(bossIcon , moveX-13*3, moveY-21*3, bossIcon.getWidth(null), bossIcon.getHeight(null), null);
              }
          }
          else if(dir.equals("down")){
              g.drawImage(downSprites.get(counter4) , moveX-36, moveY-21*3, downSprites.get(counter4).getWidth(null), downSprites.get(counter4).getHeight(null), null);
              if(boss){
                  g.drawImage(bossIcon , moveX-36, moveY-21*3, bossIcon.getWidth(null), bossIcon.getHeight(null), null);
              }
          }
          else if(dir.equals("right")){
            //g.drawImage(pic , x + 18, y, -18, 24, null);

              g.drawImage(leftSprites.get(counter4) , moveX-11*3 + leftSprites.get(counter4).getWidth(null), moveY-65, + -leftSprites.get(counter4).getWidth(null), leftSprites.get(counter4).getHeight(null), null);
              if(boss){
                  g.drawImage(bossIcon , moveX-11*3, moveY-65, bossIcon.getWidth(null), bossIcon.getHeight(null), null);
              }
          }

          else if(dir.equals("left")){
              g.drawImage(leftSprites.get(counter4) , moveX-9*3, moveY-21*3, leftSprites.get(counter4).getWidth(null), leftSprites.get(counter4).getHeight(null), null);
              if(boss){
                  g.drawImage(bossIcon , moveX-9*3, moveY-21*3, bossIcon.getWidth(null), bossIcon.getHeight(null), null);
              }
          }
      }

      /*if(drawBattle == true){
      	//g.drawString(battleHit, 18*3, 10*3);
      }*/




  }

  public String getName(){
      return name;
  }

  public int getX(){
      return x;
  }

  public int getY(){
      return y;
  }

  public int getMoveX(){
      return moveX;
  }

  public int getMoveY(){
      return moveY;
  }

  public int getAttackRange(){
      return attackRange;
  }

  public void setMoving(boolean m){
      moving = m;
  }

   public String getStatus(){
      return "moving: "+moving+" selected: "+selected;
  }

  //setting Sprites

  public void setOverWorldSprites(ArrayList<Image> sprites){
      idleSprites = new ArrayList<Image>(sprites.subList(0, 3));
      upSprites = new ArrayList<Image>(sprites.subList(3, 7));
      downSprites = new ArrayList<Image>(sprites.subList(7, 11));
      //rightSprites = new ArrayList<Image>(sprites.subList(11, 15));
      leftSprites = new ArrayList<Image>(sprites.subList(11, 15));
      selectedSprites = new ArrayList<Image>(sprites.subList(15, 18));
  }

  public void setBattleSprites(Image idle, ArrayList<Image> attackSprites, int [] frameLengths, int dmgFrame){
      idleBattleSprite = idle;
      this.attackSprites = attackSprites;
      battleFrameLengths = frameLengths;
      this.dmgFrame = dmgFrame;
  }

  //getting Sprites
  public Image getIdleBattleSprite(){
      return idleBattleSprite;
  }

  public ArrayList<Image> getAttackSprites(){
      return attackSprites;
  }

   public int[] getFrameLengths(){
      return battleFrameLengths;
  }

  public int getDmgFrame(){
      return dmgFrame;
  }

  public void setX(int x){
      this.x = x;
  }

  public void setY(int y){
      this.y = y;
  }

  public void setSelected(boolean s){
      selected = s;
  }

  public void setCounter3(int c){
      counter3 = c;
  }

   public void setCounter4(int c){
      counter4 = c;
  }

   public void setMoveX(int x){
      moveX = x;
  }

  public void setMoveY(int y){
      moveY = y;
  }

   public void setDir(String dir){
      this.dir = dir;
  }

}