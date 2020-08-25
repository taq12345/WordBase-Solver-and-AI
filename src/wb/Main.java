package wb;

import java.io.IOException;

import processing.core.PApplet;

public class Main extends PApplet {

	Game game;
	
	public static void main(String[] args) {
		PApplet.main("wb.Main");
	}
	

	public void settings(){
		size(500,650);
	}

	public void setup(){
		background(255);
		fill(0);
 		try {
			game = new Game(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		game.createGrid();
		game.fillGrid();
		game.readWords("words.txt");

	}

	public void draw(){
		
	}
	
	public void mousePressed() {
		background(255);
		game.update(game.mapY(mouseY),game.mapX(mouseX));
		game.createGrid();
		game.fillGrid();
	}
	
	public void keyPressed(){
		if (keyCode == ENTER) 
		{
			if(game.doMove() == true)
			{
				game.cascade();
				game.createGrid();
				game.fillGrid();
				for (int i = 0; i < 13; i ++)
				{
					for (int j = 0; j < 10; j++)
					{
						if (game.squares[i][j].color == 3)
						{
							game.computePossible(i,j, "");
						}
					}
				}
//				game.makeOptimalMove();
//				game.makeEasyMove();
//				game.makeMediumMove();
				game.makeHardMove();
				//System.out.println(game.possible.size());
				game.possible.removeAll(game.possible);
				//game.makeVisitedFalse();
					
			}
		} 
	}
}
