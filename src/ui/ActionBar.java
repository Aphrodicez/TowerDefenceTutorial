package ui;

import static main.GameState.*;
import static utilities.Constants.Towers.*;

import java.text.DecimalFormat;

import entity.tower.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import scenes.Playing;
import utilities.Constants;

public class ActionBar extends Bar {
	
	@SuppressWarnings("unused")
	private Playing playing;
	private MyButton bMenu;
	
	private MyButton[] towerButtons;
	private Tower selectedTower;
	
	private MyButton bSellTower, bUpgradeTower;
	
	private Tower displayedTower;
	
	private DecimalFormat decimalFormatter;
	private int gold;
	private boolean showTowerCost;
	private int buyingTowerType;
	private MyButton bPause;
	
	private int lives;
	
	public ActionBar(int x, int y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		this.decimalFormatter = new DecimalFormat("0.0");
		this.gold = 1000;
		this.lives = 1;
		initButtons();
	}
	
	private void initButtons() {
		bMenu = new MyButton("Menu", 2, 642, 100, 30);
		bPause = new MyButton("Pause", 2, 682, 100, 30);
		
		towerButtons = new MyButton[3];
		
		int w = 50;
		int h = 50;
		int xStart = 110;
		int yStart = 650;
		int xOffset = (int) (w * 1.1f);

		for(int i = 0; i < towerButtons.length; i++) {
			towerButtons[i] = new MyButton("", xStart + xOffset * i, yStart, w, h, i);
		}
		
		this.bSellTower = new MyButton("Sell", 420, 702, 80, 25);
		this.bUpgradeTower = new MyButton("Upgrade", 545, 702, 80, 24);
	}
	
	private void drawButtons(GraphicsContext gc) {
		bMenu.draw(gc);
		bPause.draw(gc);
		
		for(MyButton b : towerButtons) {
			gc.setFill(Color.GRAY);
			gc.fillRect(b.x, b.y, b.width, b.height);
			gc.drawImage(playing.getTowerManager().getTowerImages()[b.getId()], b.x, b.y, b.width, b.height);
		
			drawButtonFeedback(gc, b);
		}
	}
	
	public void draw(GraphicsContext gc) {

		// Background
		gc.setFill(Color.rgb(220, 123, 15));
		gc.fillRect(x, y, width, height);

		// Buttons
		drawButtons(gc);
		
		// Displayed Tower
		drawDisplayedTower(gc);
		
		Font oldFont = gc.getFont();
		gc.setFont(Font.font("LucidaSans", FontWeight.BOLD, 20));

		gc.setFill(Color.BLACK);
		
		// Wave Info
		drawWaveInfo(gc);
		
		// Gold Info
		drawGoldAmount(gc);
		
		if(showTowerCost) {
			// Draw Tower Cost
			drawTowerCost(gc);
		}
		
		if (playing.isGamePaused()) {
			gc.setFill(Color.BLACK);
			gc.fillText("Game is Paused!", 110, 790);
		}
		
		// Lives
		gc.setFill(Color.GREEN);
		gc.fillText("Lives: " + lives, 110, 750);
		
		gc.setFont(oldFont);
	}
	
	private void drawTowerCost(GraphicsContext gc) {
		gc.setFill(Color.GRAY);
		gc.fillRect(280, 650, 120, 50);
		gc.setStroke(Color.BLACK);
		gc.strokeRect(280, 650, 120, 50);
		
		gc.setFill(Color.BLACK);
		gc.fillText("" + getBuyingTowerName(), 285, 670);
		gc.fillText("Cost: " + getBuyingTowerCost(), 285, 695);
		
		// Show this if player cannot afford selected tower
		if(!isGoldEnoughForPurchase(getTowerCostByType(buyingTowerType))) {
			gc.setFill(Color.RED);
			gc.fillText("Can't Afford", 270, 725);
		}
	}

	private String getBuyingTowerName() {
		return Constants.Towers.getConstantTowerName(buyingTowerType);
	}

	private int getBuyingTowerCost() {
		// TODO Auto-generated method stub
		return Constants.Towers.getConstantTowerCost(buyingTowerType);
	}

	private void drawGoldAmount(GraphicsContext gc) {
		gc.setFill(Color.GOLD);
		gc.fillText("Gold: " + gold + "g", 110, 725);
	}

	private void drawWaveInfo(GraphicsContext gc) {
		drawWaveTimerInfo(gc);
		drawEnemiesLeftInfo(gc);
		drawWavesLeftInfo(gc);
	}

	private void drawWavesLeftInfo(GraphicsContext gc) {
		int current = playing.getWaveManager().getWaveIndex();
		int numberOfWaves = playing.getWaveManager().getWaves().size();
		
		gc.fillText("Wave " + (current + 1) + " / " + numberOfWaves, 425, 770);
	}

	private void drawEnemiesLeftInfo(GraphicsContext gc) {
		int remaining = playing.getEnemyManager().getAmountOfAliveEnemies();
		
		gc.fillText("Enemies Left: " + remaining, 425, 790);
	}

	private void drawWaveTimerInfo(GraphicsContext gc) {
		if(playing.getWaveManager().isWaveTimerStarted()) {
			float timeLeft = playing.getWaveManager().getTimeLeft();
			String formattedText = decimalFormatter.format(timeLeft);
			gc.fillText("Time Left : " + formattedText, 425, 750);
		}
	}

	private void drawDisplayedTower(GraphicsContext gc) {
		if(displayedTower == null) {
			return ;
		}
		gc.setFill(Color.GRAY);
		gc.fillRect(410, 645, 220, 85);
		
		gc.setStroke(Color.BLACK);
		gc.strokeRect(410, 645, 220, 85);
		gc.strokeRect(420, 650, 50, 50);
		
		gc.drawImage(playing.getTowerManager().getTowerImages()[displayedTower.getTowerType()], 420, 650, 50, 50);
		drawSelectedColorBorder(gc);
		
		gc.setFill(Color.BLACK);
		Font oldFont = gc.getFont();
		gc.setFont(Font.font("LucidaSans", FontWeight.BOLD, 15));
		gc.fillText("" + displayedTower.getName(), 480, 660);
		gc.fillText("ID: " + displayedTower.getId(), 480, 675);
		gc.fillText("Tier: " + displayedTower.getTier(), 560, 660);
		
		// setFont only set the font going forward
		gc.setFont(oldFont);
		
		gc.setStroke(Color.WHITE);
		gc.strokeOval(displayedTower.getX() + (32 / 2) - displayedTower.getRange(), displayedTower.getY() + (32 / 2) - displayedTower.getRange(), 2 * displayedTower.getRange(), 2 * displayedTower.getRange());
	
		// Sell
		bSellTower.draw(gc);
		drawButtonFeedback(gc, bSellTower);
		
		if(bSellTower.isMouseOver()) {
			gc.setFill(Color.RED);
			gc.setFont(Font.font("LucidaSans", FontWeight.BOLD, 15));
			gc.fillText("Sell for: " + displayedTower.getSellPrice() + "g", 480, 690);
			gc.setFont(oldFont);
		}
		
		if(displayedTower.getTier() < 2) {
			// Upgrade
			bUpgradeTower.draw(gc);
			drawButtonFeedback(gc, bUpgradeTower);
			if(bUpgradeTower.isMouseOver()) {
				gc.setFill(Color.SPRINGGREEN);
				gc.setFont(Font.font("LucidaSans", FontWeight.BOLD, 15));
				gc.fillText("Upgrade for: " + displayedTower.getUpgradeCost() + "g", 480, 690);
				gc.setFont(oldFont);
			}
		}
		
	}

	private void drawSelectedColorBorder(GraphicsContext gc) {
		gc.setStroke(Color.CYAN);
		gc.strokeRect(displayedTower.getX(), displayedTower.getY(), 32, 32);
	}

	private void sellTowerClicked() {
		playing.getTowerManager().removeTower(displayedTower);
		addGold(displayedTower.getSellPrice());
		displayedTower = null;
	}
	
	private void upgradeTowerClicked() {
		playing.getTowerManager().upgradeTower(displayedTower);
		decreaseGold(displayedTower.getUpgradeCost());
	}
	
	private void togglePause() {
		playing.setGamePaused(!playing.isGamePaused());

		if (playing.isGamePaused())
			bPause.setText("Unpause");
		else
			bPause.setText("Pause");

	}
	
	public void mouseClicked(int x, int y) {
		if (bMenu.getBounds().contains(x, y)) {
			bMenu.resetBooleans();
			setGameState(MENU);
		}
		else if (bPause.getBounds().contains(x, y)) {
			togglePause();
		}
		else {
			
			if(displayedTower != null) {
				if(bSellTower.getBounds().contains(x, y)) {
					sellTowerClicked();
					return ;
				}
				else if(bUpgradeTower.getBounds().contains(x, y) && displayedTower.getTier() < 2) {
					if(!isGoldEnoughForPurchase(displayedTower.getUpgradeCost())) {
						return ;
					}
					upgradeTowerClicked();
					return ;
				}
			}
			
			for(MyButton b : towerButtons) {
				if(b.getBounds().contains(x, y)) {
					b.setMouseOver(true);
					int towerType = b.getId();
					
					if(!isGoldEnoughForPurchase(getTowerCostByType(towerType))) {
						return ;
					}
					
					switch (towerType) {
						case CANNON:
							selectedTower = new Cannon(0, 0, -1);
							break;
						case ARCHER:
							selectedTower = new Archer(0, 0, -1);
							break;
						case WIZARD:
							selectedTower = new Wizard(0, 0, -1);
							break;
						default:
							selectedTower = null;
					}
					playing.setSelectedTower(selectedTower);
					return ;
				}
			}
		}
	}

	public void mouseMoved(int x, int y) {
		bMenu.setMouseOver(false);
		bPause.setMouseOver(false);
		bSellTower.setMouseOver(false);
		bUpgradeTower.setMouseOver(false);
		
		this.showTowerCost = false;
		for(MyButton b : towerButtons) {
			b.setMouseOver(false);
		}
		if (bMenu.getBounds().contains(x, y)) {
			bMenu.setMouseOver(true);
		}
		else if(bPause.getBounds().contains(x, y)) {
			bPause.setMouseOver(true);
		}
		else {
			
			if(displayedTower != null) {
				if(bSellTower.getBounds().contains(x, y)) {
					bSellTower.setMouseOver(true);
				}
				if(bUpgradeTower.getBounds().contains(x, y)) {
					bUpgradeTower.setMouseOver(true);
				}
			}
			
			for(MyButton b : towerButtons) {
				if(b.getBounds().contains(x, y)) {
					b.setMouseOver(true);
					this.showTowerCost = true;
					this.buyingTowerType = b.getId();
					return ;
				}
			}
		}
	}

	private boolean isGoldEnoughForPurchase(int price) {
		return this.gold - price >= 0;
	}

	public void mousePressed(int x, int y) {
		if (bMenu.getBounds().contains(x, y)) {
			bMenu.setMousePressed(true);
		}
		else if (bPause.getBounds().contains(x, y)) {
			bPause.setMousePressed(true);
		}
		else {
			
			if(displayedTower != null) {
				if(bSellTower.getBounds().contains(x, y)) {
					bSellTower.setMouseOver(true);
					bSellTower.setMousePressed(true);
				}
				if(bUpgradeTower.getBounds().contains(x, y)) {
					bUpgradeTower.setMouseOver(true);
					bUpgradeTower.setMousePressed(true);
				}
			}
			
			for(MyButton b : towerButtons) {
				if(b.getBounds().contains(x, y)) {
					b.setMousePressed(true);
					return ;
				}
			}
		}
	}

	public void mouseReleased(int x, int y) {
		bMenu.resetBooleans();
		bPause.resetBooleans();
		for(MyButton b : towerButtons) {
			b.resetBooleans();
		}
		bSellTower.resetBooleans();
		bUpgradeTower.resetBooleans();
	}

	public void displayTower(Tower tower) {
		// TODO Auto-generated method stub
		displayedTower = tower;
	}

	public int getGold() {
		return gold;
	}
	
	public void addGold(int gold) {
		this.gold += gold;
	}
	
	public void decreaseGold(int gold) {
		this.gold -= gold;
	}
	
	public int getTowerCostByType(int towerType) {
		return Constants.Towers.getConstantTowerCost(towerType);
	}

	public int getLives() {
		return lives;
	}

	public void removeOneLife() {
		lives--;
		if(lives <= 0) {
			setGameState(GAME_OVER);
		}
	}

	public void resetEverything() {
		lives = 25;
		
		//towerCostType = 0;
		
		showTowerCost = false;
		gold = 100;
		selectedTower = null;
		displayedTower = null;
	}
}
