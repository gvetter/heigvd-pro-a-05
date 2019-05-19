package common;
import common.spells.ElementalOrb;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import common.spells.AttackSpell;
import common.spells.ShieldSpell;
import org.newdawn.slick.geom.Rectangle;

import java.util.*;

/**
 * Class representing a wizard.
 */
public class Wizard {
	private int x, y;
    private int healthPoint = 100;
    private boolean isDead = false;
    private int id;
    private Image sprite;
    private LinkedList<ShieldSpell> shield = new LinkedList<>();
    private LinkedList<ElementalOrb> orbs = new LinkedList<>();

    /**
     * Default constructor
     */
    public Wizard() {
    	this(0, 0);
    }

    /**
     * Constructor, taking the position of a Wizard.
     * @param x the x coordinate of the wizard
     * @param y the y coordinate of the wizard
     */
    public Wizard(int x, int y) {
    	this.x = x;
    	this.y = y;
        try {
            this.sprite = new Image("img/wizard.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter for the id
     * @param i the new id of the wizard.
     */
    public void setId(int i){
        this.id = i;
    }

    /**
     * Getter for the orbs
     * @return list of the orbs
     */
    public LinkedList<ElementalOrb> getOrbs() {
        return orbs;
    }

    /**
     * Method used to add an orb. A Wizard cannot have more than 4 orbs.
     * @param orb the new orb we want to add
     * @return a boolean that indicate if the adding was successful.
     */
    public boolean addOrb(ElementalOrb orb) {
        if(!isDead) {
            int count = 0;
            for (int i = 0; i < orbs.size(); i++) {
                if (!orbs.get(i).isPrepared()) {
                    count++;
                }
            }
            if (count < 4) {
                orbs.add(orb);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    /**
     * Getter for the shields
     * @return list of the shields the wizard possesses.
     */
    public LinkedList<ShieldSpell> getShield(){
        return shield;
    }

    /**
     * Method used to add a shield to the wizard's shields
     * @param spell the shield we want to add
     */
    public void addShield(ShieldSpell spell) {
    	shield.add(spell);
    }

    /**
     * Method used to check whether a spell has hit the wizard or not
     * @param spell the incoming spell
     * @return a boolean telling if the spell has hit or not.
     */
    public boolean checkCollision(AttackSpell spell) {
        if(spell.getTarget() == this) {
            double deltaX = spell.getX() - x;
            double deltaY = spell.getY() - y;
            if(Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= 8){
                spell.setOver(true);
                return true;
            } else{
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Method used to check whether the wizard has to take damage or not
     * @param spell the spell that hit the wizard
     */
    public void getHit(AttackSpell spell) {
        boolean shielded = false;
        int indexShield = -1;
    	if(!shield.isEmpty()) {
    	    for(int i = 0; i < shield.size(); i++) {
                if (shield.get(i).getType() == spell.getType()) {
                    spell.setBounce(true);
                    shielded = true;
                    indexShield = i;
                    spell.setOver(false);
                    break;
                }
            }
    	    if(shielded) {
                System.out.println("I shielded " + spell.computePower() + " damage with my " + spell.getType().name() + " shield.");
                shield.get(indexShield).setOver(true);
                shield.remove(indexShield);
            } else {
    	        takeDamage(spell);
            }
    	} else {
           takeDamage(spell);
    	}
    }

    /**
     * Method used to compute the damage, and check whether the spell hit or not
     * @param spell the incoming spell
     */
    private void takeDamage(AttackSpell spell){
        System.out.println("Ouch i took "+ spell.computePower() +" damage pts");
        healthPoint -= spell.computePower();

        // Killing blow
        if (healthPoint <= 0) {
            System.out.println("Me dead");
            isDead = true;
        }
    }

    /**
     * Getter for the x coordinate of the wizard
     * @return the x coordinate
     */
    public int getX() {
    	return x;
    }

    /**
     * Getter for the y coordinate of the wizard.
     * @return the y coordinate
     */
    public int getY() {
    	return y;
    }
    
    /**
     * Getter for the health points
     * @return the current number of health points
     */
    public int getHealthPoint() {
        return healthPoint;
    }

    /**
     * Setter for the health points
     * @param healthPoint the new value of the health points
     */
    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    /**
     * Getter for the boolean is dead
     * @return if the wizard is dead or not
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Setter for the boolean is dead
     * @param dead new value of the boolean is dead.
     */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /**
     * Getter for the id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Method used to render a wizard
     * @param g the graphics
     * @throws SlickException in case of emergency.
     */
    public void render(Graphics g) throws SlickException {
        if(!isDead) {
            g.setLineWidth(4);
            g.setColor(Color.red);
            Rectangle life = new Rectangle(x - 25, y - 40, (int)(healthPoint/100.0*48), 12);
            g.fill(life);
            g.setColor(Color.black);
            g.drawRect(x - 25, y - 40, 48, 12);
            this.sprite.draw(x - 16, y - 16);

            if (!shield.isEmpty()) {
                for (int i = 0; i < shield.size(); i++) {
                    shield.get(i).render(g);
                }
            }
        }
    }
}
