package entity.enemy;

import static utilities.Constants.Enemies.KNIGHT;

public class Knight extends Enemy {

	public Knight(float x, float y, int ID, int incarnationLeft) {
		super(x, y, ID, KNIGHT, incarnationLeft);
	}
}
