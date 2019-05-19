package desktop;

import java.util.LinkedList;
import java.util.Random;

import common.spells.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import common.Wizard;
import util.Vector;

public class Game extends BasicGameState {
	private GameContainer container;
	private TiledMap map;
	private LinkedList<Wizard> wizards;
	private LinkedList<AttackSpell> attackSpells, attackSpellstoRemove;
	private LinkedList<ShieldSpell> shieldSpells, shieldSpellstoRemove;
	private LinkedList<ElementalOrb> elementalOrbs, elementalOrbstoRemove;
	private static String[] args = new String[3];
	private static boolean changed;
	private static int id;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		container = gc;
	    map = new TiledMap("img/tiles/map1.tmx");
	    wizards = new LinkedList<>();
	    attackSpellstoRemove = new LinkedList<>();
	    shieldSpellstoRemove = new LinkedList<>();
	    shieldSpells = new LinkedList<>();
	    attackSpells = new LinkedList<>();
	    elementalOrbs = new LinkedList<>();
		elementalOrbstoRemove = new LinkedList<>();
	    wizards.add(new Wizard(135, 245));
	    wizards.add(new Wizard(489, 245));
	    wizards.add(new Wizard(312, 68));
	    wizards.add(new Wizard(312, 422));
		setId();
	}

	private void setId(){
		int id = 0;
		for(Wizard w : wizards){
			w.setId(id);
			id++;
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.scale(2, 2);
		map.render(0, 0);
		for(Wizard wizard : wizards) {
			wizard.render(g);
		}
		for(AttackSpell as : attackSpells) {
			as.render(g);
		}
		for(ElementalOrb orb : elementalOrbs){
			orb.render(g);
		}

	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int a) throws SlickException {
		remove();
		checkUpdate();
		for(AttackSpell as : attackSpells) {
			as.move();
			for(Wizard wizard : wizards) {
				if(wizard.checkCollision(as)) {
					wizard.getHit(as);
					if(!as.isBounce()) {
						attackSpellstoRemove.add(as);
						if (wizard.getOrbs().size() != 0) {
							for (int i = 0; i < wizard.getOrbs().size(); i++) {
								elementalOrbstoRemove.add(wizard.getOrbs().get(i));
							}
							wizard.getOrbs().clear();
						}
					}
				}
			}
			if(as.isOver())
				attackSpellstoRemove.add(as);
		}

		for(ShieldSpell sp : shieldSpells){
			if(sp.isOver()){
				shieldSpellstoRemove.add(sp);
			}
		}
		
		for(Wizard wizard : wizards) {

			for(ElementalOrb orb : wizard.getOrbs()){
				orb.move();
				if(orb.isCast()){
					throwAttack(orb.getTarget(), wizard);
				}
			}
			wizard.getOrbs().removeAll(elementalOrbstoRemove);
		}
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 1;
	}

	public void mouseClicked(int button, int x, int y, int clickCount) {
		if (button == 0) {
			castAttack(new Vector(wizards.get(0).getX(), x, wizards.get(0).getY(), y), wizards.get(0));
		}

		if (button == 1) {
			Random r = new Random();
			int i = r.nextInt(4);
			castOrb(wizards.get(0), Quality.PERFECT, MagicType.values()[0]);
		}
	}

	public void keyPressed(int key, char c){
		if(null != wizards) {
			if (c == 'q') {
				Random r = new Random();
				int i = r.nextInt(4);
				castOrb(wizards.get(1), Quality.PERFECT, MagicType.values()[0]);
			}
		}
		if(null != wizards) {
			if (c == 'w') {
				castShield(wizards.get(1));
			}
		}
	}

	private void castShield(Wizard caster){
		if(caster.getOrbs().size() != 0) {
			for (int i = 0; i < caster.getShield().size(); i++) {
				shieldSpellstoRemove.add(caster.getShield().get(i));
				caster.getShield().get(i).setOver(true);
				caster.getShield().clear();
			}
		}
		LinkedList<ElementalOrb> orbs = caster.getOrbs();
		for(int i = 0; i < orbs.size(); i++) {
			ElementalOrb orb = orbs.get(i);
			shieldSpells.add(new ShieldSpell(orb));
			elementalOrbstoRemove.add(orb);
		}
		orbs.clear();
	}
	
	private void throwAttack(Wizard target, Wizard caster){
		if(target != null) {
			LinkedList<ElementalOrb> orbs = caster.getOrbs();
			if (orbs != null) {
				for (int i = 0; i < orbs.size(); i++) {
					if (orbs.get(i).isCast()) {
						ElementalOrb orb = orbs.get(i);
						attackSpells.add(new AttackSpell(orb, target));
						elementalOrbstoRemove.add(orb);
					}
				}
			}
		}
	}

	private void castAttack(Vector v, Wizard caster){
		Wizard target = findTarget(v, caster);
		if(target != null) {
			for (ElementalOrb orb : caster.getOrbs()) {
				orb.setPrepare();
				orb.setTarget(target);
			}
		}
	}

	private Wizard findTarget (Vector v, Wizard caster){
		Wizard result = null;
		double minAngle = 10;
		for(Wizard target : wizards){
			if(target != caster) {
				double angle = Math.abs(v.getAngle(new Vector(caster.getX(), target.getX(), caster.getY(), target.getY())));
				if (angle < minAngle && angle < Math.PI/4) {
					minAngle = angle;
					result = target;
				}
			}
		}
		return result;
	}

	private void castOrb(Wizard caster, Quality qual, MagicType type) {
			ElementalOrb orb = new ElementalOrb(caster, qual, type);
			if (caster.addOrb(orb)) {
				elementalOrbs.add(orb);
			}

	}

	public void parse(int id, byte[] request){
		String[] requestStringSplit = new String[3];
		for(int i = 0; i < requestStringSplit.length; i++){
			requestStringSplit[i] = "";
		}
		int indexString = 0;

		for(int i = 0; i < request.length; i++){
			if(((char)request[i])!='$') {
				if (((char) request[i]) == ' ') {
					indexString += 1;
				} else {
					requestStringSplit[indexString] += (char) request[i];
				}
			} else {
				break;
			}
		}

		args[0] = requestStringSplit[0];
		if(request.length > 1) {
			if (args[0].equals("ATT")) {
				args[1] = requestStringSplit[1];
				args[2] = requestStringSplit[2];
				this.id = id;
				changed = true;
			} else if (args[0].equals("SHI")) {
				this.id = id;
				changed = true;
			} else if (args[0].equals("CHA")){
				this.id = id;
				args[1] = requestStringSplit[1];
				args[2] = requestStringSplit[2];
				changed = true;
			}
		}
	}

	private void checkUpdate(){
		if(changed){
			changed = false;
			if(args[0].equals("ATT")){
				castAttack(new Vector(Double.parseDouble(args[1]), Double.parseDouble(args[2])), wizards.get(id));
			} else if(args[0].equals("SHI")){
				castShield(wizards.get(id));
			} else if(args[0].equals("CHA")){
				castOrb(wizards.get(id), Quality.values()[Integer.parseInt(args[2].charAt(0) + "")], MagicType.values()[Integer.parseInt(args[1])]);
			}
		}
	}

	private void remove(){
		for(AttackSpell as : attackSpellstoRemove) {
			attackSpells.remove(as);
		}
		attackSpellstoRemove.clear();

		for(ShieldSpell sp : shieldSpellstoRemove) {
			shieldSpells.remove(sp);
		}
		shieldSpellstoRemove.clear();

		for(ElementalOrb orb : elementalOrbstoRemove) {
			elementalOrbs.remove(orb);
		}
		elementalOrbstoRemove.clear();
	}

}
