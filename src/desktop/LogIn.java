package desktop;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class LogIn extends BasicGameState {

    private Image qrCode, start;
    static private int nbPlayers = 0;

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
        try {
            QRCodeGenerator.generateQRCodeImageWithIPsAndPort(600, 600);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        qrCode = new Image("img/qr.jpg");
        start = new Image("img/Start.png");
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) {
        g.drawString("Connexion", arg0.getWidth() / 2 - 50, 50);
        qrCode.draw(arg0.getWidth() / 2 - 300, 75);
        start.draw(arg0.getWidth() / 2 - 50, 685);
        g.drawString("Players connected : " + nbPlayers + "/4",
                arg0.getWidth() / 2 - 100, 740);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame arg1, int arg2) throws SlickException {
        int x = Mouse.getX();
        int y = gc.getHeight() - Mouse.getY();

        // Start button is pressed
        if((x > gc.getWidth() / 2 - 50 && x < gc.getWidth() / 2 + 50) && (y > 700 && y < 750)) {
            if(Mouse.isButtonDown(0)) {
                arg1.getState(1).init(gc, arg1);
                arg1.enterState(1);
            }
        }
    }

    @Override
    public int getID() {
        return 3;
    }

    static public void setNbPlayers(int nb) {
        nbPlayers = nb;
    }
}