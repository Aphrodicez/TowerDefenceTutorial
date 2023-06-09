package objects;

import static utilities.Constants.Projectiles.ARROW;

import entity.enemy.Enemy;
import javafx.geometry.Point2D;

public class Projectile {
	private Point2D pos;
	private int id, projectileType;
	private boolean active;
	private float xSpeed, ySpeed;
	private float rotationAngle;
	private int damage;
	private Enemy enemy;
	
	public Projectile(float x, float y, float xSpeed, float ySpeed, float rotationAngle, int damage, int id, int projectileType) {
		pos = new Point2D(x, y);
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.rotationAngle = rotationAngle;
		this.damage = damage;
		this.id = id;
		this.active = true;
		this.projectileType = projectileType;
		this.enemy = null;
	}
	
	public Projectile(float x, float y, int damage, int id, int projectileType, Enemy enemy) {
		pos = new Point2D(x, y);
		this.damage = damage;
		this.id = id;
		this.active = true;
		this.projectileType = projectileType;
		this.enemy = enemy;
	}
	
	public void move() {
		if(enemy != null && !enemy.isAlive()) {
			enemy = null;
		}
		if(enemy != null) {
			int xDiff = (int) (this.getPos().getX() - (enemy.getX() + 16));
			int yDiff = (int) (this.getPos().getY() - (enemy.getY() + 16));
			int totalDist = Math.abs(xDiff) + Math.abs(yDiff);
			if(Math.abs(xDiff) <= 16 || Math.abs(yDiff) <= 16) {
				enemy = null;
				this.pos = this.pos.add(xSpeed, ySpeed);
				return ;
			}
			
			// Percent
			float xPer = (float) Math.abs(xDiff) / totalDist;
			float yPer = 1.0f - xPer;

			this.xSpeed = xPer * utilities.Constants.Projectiles.getConstantSpeed(projectileType);
			this.ySpeed = yPer * utilities.Constants.Projectiles.getConstantSpeed(projectileType);
			
			if (this.getPos().getX() > enemy.getX()) {
				this.xSpeed *= -1;
			}
			if (this.getPos().getY() > enemy.getY()) {
				this.ySpeed *= -1;
			}
			
			this.rotationAngle = 0;

			if(projectileType == ARROW) {
				float arctan = (float) Math.atan((float) yDiff / xDiff);
				this.rotationAngle = (float) Math.toDegrees(arctan);
				if (xDiff < 0) {
					this.rotationAngle += 180;
				}
			}
		}
		if(xSpeed == 0 && ySpeed == 0) {
			this.active = false;
		}
		this.pos = this.pos.add(xSpeed, ySpeed);
	}

	public Point2D getPos() {
		return pos;
	}

	public int getId() {
		return id;
	}

	public int getProjectileType() {
		return projectileType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getDamage() {
		return damage;
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setEnemy(Enemy enemy) {
		this.enemy = enemy;
	}
	
	
}
