package heig.pro.a05.common.spells;

import javafx.util.Pair;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Define a defensive spell
 */
public class ShieldSpell extends Spell {

    private ElementalOrb orb;
    private int frameCounter = computePower() * 25;
    private boolean over = false;
    private int id;
    private static int counter = 0;

    /**
     * Constructor taking an orb as parameter.
     *
     * @param orb the magic orb defining the spell.
     */
    public ShieldSpell(ElementalOrb orb) {
        super(orb);
        counter += 1;
        id = counter;
        this.orb = orb;
        orb.getCaster().addShield(this);
    }

    /**
     * Helper method used to compute the angle at which the shield needs to be oriented
     *
     * @return a Pair of integers that represents the starting and ending angles of the arc of the shield.
     */
    private Pair<Integer, Integer> getArcBounds() {
        switch (orb.getCaster().getId()) {
            case 0:
                return new Pair<>(270, 90);
            case 1:
                return new Pair<>(90, 270);
            case 2:
                return new Pair<>(0, 180);
            case 3:
                return new Pair<>(180, 0);
            default:
                return new Pair<>(0, 0);
        }
    }

    /**
     * Method used to render a shield
     *
     * @param g graphics
     * @throws SlickException in case of emergency.
     */
    public void render(Graphics g) throws SlickException {
        Image s = selectSprite();
        float scale = 0.7f + 0.3f * id;
        s.draw((int)(orb.getCaster().getX() -24*scale), (int)(orb.getCaster().getY() -24 * scale), scale);

        frameCounter--;
        if (!orb.getCaster().getShield().isEmpty() && frameCounter == 0) {
            orb.getCaster().getShield().clear();
            over = true;
        }
    }

    /**
     * Method used to check if the shield is over.
     *
     * @return true if the shield is over, false otherwise.
     */
    public boolean isOver() {
        counter = -1;
        return over;
    }

    private Image selectSprite() throws SlickException {
        switch(getType()){
            case EARTH:
                return new Image("img/earth_shield.png");
            case WATER:
                return new Image("img/water_shield.png");
            case FIRE:
                return new Image("img/fire_shield.png");
            case LIGHTNING:
                return new Image("img/lightning_shield.png");
            default:
                return new Image("");
        }
    }

}
