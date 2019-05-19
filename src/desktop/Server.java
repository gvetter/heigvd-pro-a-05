package desktop;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Server extends StateBasedGame{
	private static final int MENU = 0;
	private static final int GAME = 1;
	private static final int CREDITS = 2;
	private static final int LOG_IN = 3;

	private static Server instance;

	public static Server getInstance(){
		if(instance == null){
			instance = new Server("PRO");
		}
		return instance;
	}
	
	private Server(String name) {
		super(name);
		this.addState(new MainMenu());
		this.addState(new Game());
		this.addState(new LogIn());
		this.addState(new Credits());
	}
	
	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		this.getState(MENU).init(gc, this);
		this.getState(GAME).init(gc, this);
		this.getState(CREDITS).init(gc, this);
		this.getState(LOG_IN).init(gc, this);
	}


}
