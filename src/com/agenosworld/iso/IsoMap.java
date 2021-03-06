/**
 * 
 */
package com.agenosworld.iso;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.geom.Vector2f;

import com.agenosworld.basicgame.*;
import com.agenosworld.iso.pathfinding.AreaSearcher;

/**
 * @author Michael
 *
 */
public class IsoMap implements MouseListener, Updatable, Renderable {
	
	//Vector2fs which represent the directions where other tiles exist for tiles of an even Y origin.
	public static final Vector2f DIR_UL = new Vector2f(-1, -1);
	public static final Vector2f DIR_UR = new Vector2f(0, -1);
	public static final Vector2f DIR_DL = new Vector2f(-1, 1);
	public static final Vector2f DIR_DR = new Vector2f(0, 1);
	//To determine value for a tile at origin Y, simply add y%2 to the X value of the vector.
	
	//Map Scrolling Variables
	private float xScroll = 0;
	private float yScroll = 0;
	
	private float xRate;
	private float yRate;
	
	private float baseRate = 0.1f;
	//private float yRate = 0, xRate = 0;
	
	private final int SCROLL_BORDER = 60;
	
	//GameBasics Game
	private GameBasics game;
	
	//Map Definition Variables
	private int mapWidth = 7;
	private int mapHeight = 4;
	
	//Tile Definition Values
	
	private Tile[][] tilesArray;

	
	//Create a new IsoMap
	public IsoMap(Tile[][] tiles, int mapWidth, int mapHeight, GameBasics game) {
		this.game = game;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		xScroll = Math.round(game.getWidth()-(mapWidth+0.5f)*TileDef.TILE_WIDTH)/2;
		yScroll = Math.round(game.getHeight()-(mapHeight+1f)*TileDef.TILE_HEIGHT/2)/2;
		
		tilesArray = tiles;
	}
	
	public Tile getTileAt(int x, int y) {
		try {
			return tilesArray[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public int getXScroll() {
		return Math.round(xScroll);
	}
	public int getYScroll() {
		return Math.round(yScroll);
	}
	
	public int getWidth() {
		return mapWidth;
	}
	public int getHeight() {
		return mapHeight;
	}
	
	/**
	 * Render the IsoMap at its current offset.
	 * @throws SlickException
	 */
	
	public void render() throws SlickException {
		for (int y=0; y<mapHeight; y++) {
			for (int x=0; x<mapWidth; x++) {
				tilesArray[x][y].render(Math.round(xScroll), Math.round(yScroll), this);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.newdawn.slick.MouseListener#mouseMoved(int, int, int, int)
	 */
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if (newx > game.getWidth()-SCROLL_BORDER) {
			//SCROLL RIGHT AT VARIABLE RATE
			xRate = -(float)(newx-game.getWidth()+SCROLL_BORDER)/SCROLL_BORDER;
		} else if (newx < SCROLL_BORDER) {
			//SCROLL LEFT AT VARIABLE RATE
			xRate = (1.0f-((float)newx/SCROLL_BORDER));
		} else {
			xRate = 0;
		}
		
		if (newy > game.getHeight()-SCROLL_BORDER) {
			//SCROLL DOWN AT VARIABLE RATE
			yRate = -(float)(newy-game.getHeight()+SCROLL_BORDER)/SCROLL_BORDER;
		} else if (newy < SCROLL_BORDER) {
			//SCROLL UP AT VARIABLE RATE
			yRate = (1-((float)newy/SCROLL_BORDER));
		} else {
			yRate = 0;
		}
		
		/*if (newx < SCROLL_BORDER) {
			xRate = baseRate*(1.0f-((float)newx/SCROLL_BORDER));
			//xRate = baseRate-(baseRate/(SCROLL_BORDERf-newx));
		} else if (newx > game.getWidth()-SCROLL_BORDER) {
			xRate = -baseRate*(1.0f-(float)(game.getWidth()-newx)/SCROLL_BORDER);
			//xRate = -(baseRate-(baseRate/(40f-game.getWidth()+newx)));
		} else {
			xRate = 0;
		}
		
		if (newy < SCROLL_BORDER) {
			yRate = baseRate*(1.0f-((float)newy/SCROLL_BORDER));
			//yRate = baseRate-(baseRate/(SCROLL_BORDERf-newy));
		} else if (newy > game.getHeight()-SCROLL_BORDER) {
			yRate = -baseRate*(1.0f-(float)(game.getHeight()-newy)/SCROLL_BORDER);
			//yRate = -(baseRate-(baseRate/(SCROLL_BORDERf-game.getHeight()+newy)));
		} else {
			yRate = 0;
		}*/
	}
	
	/* (non-Javadoc)
	 * @see com.agenosworld.basicgame.Updatable#update(int)
	 */
	@Override
	public void update(int delta) {
		this.xScroll += xRate*baseRate*delta;
		this.yScroll += yRate*baseRate*delta;
		
		if (xScroll > game.getWidth()-300) {
			xScroll = game.getWidth()-300;
		} else if (xScroll < -mapWidth*TileDef.TILE_WIDTH+300) {
			xScroll = -mapWidth*TileDef.TILE_WIDTH+300;
		}
		
		if (yScroll > game.getHeight()-300) {
			yScroll = game.getHeight()-300;
		} else if (yScroll < -mapHeight*(TileDef.TILE_HEIGHT/2)+300) {
			yScroll = -mapHeight*(TileDef.TILE_HEIGHT/2)+300;
		}
	}
	
	/**
	 * @param areaSearcher
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean blocked(AreaSearcher areaSearcher, int x, int y) {
		try {
			return tilesArray[x][y].isBlocked();
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}
	
	/**
	 * @param areaSearcher
	 * @param tx
	 * @param ty
	 * @return
	 */
	public float getCost(AreaSearcher areaSearcher, int tx, int ty) {
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.ControlledInputReciever#isAcceptingInput()
	 */
	@Override
	public boolean isAcceptingInput() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.newdawn.slick.ControlledInputReciever#setInput(org.newdawn.slick.Input)
	 */
	@Override
	public void setInput(Input input) { }

	/* (non-Javadoc)
	 * @see org.newdawn.slick.ControlledInputReciever#inputEnded()
	 */
	@Override
	public void inputEnded() { }

	/* (non-Javadoc)
	 * @see org.newdawn.slick.ControlledInputReciever#inputStarted()
	 */
	@Override
	public void inputStarted() { }

	/* (non-Javadoc)
	 * @see org.newdawn.slick.MouseListener#mouseWheelMoved(int)
	 */
	@Override
	public void mouseWheelMoved(int change) { }

	/* (non-Javadoc)
	 * @see org.newdawn.slick.MouseListener#mouseClicked(int, int, int, int)
	 */
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) { }

	/* (non-Javadoc)
	 * @see org.newdawn.slick.MouseListener#mousePressed(int, int, int)
	 */
	@Override
	public void mousePressed(int button, int x, int y) { }

	/* (non-Javadoc)
	 * @see org.newdawn.slick.MouseListener#mouseReleased(int, int, int)
	 */
	@Override
	public void mouseReleased(int button, int x, int y) { }

	/* (non-Javadoc)
	 * @see org.newdawn.slick.MouseListener#mouseDragged(int, int, int, int)
	 */
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) { }

}
