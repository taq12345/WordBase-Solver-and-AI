package wb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

import processing.core.PApplet;

public class Game {
	Square [][] squares;
	Stack<Move> history;
	Stack<Connected> nodes;
	HashSet<String> words;
	ArrayList<Word> possible;
	HashSet<String> [] subsets;
	HashSet<String> played;
	//boolean [][] visited = new boolean[13][10];
	int turn;
	PApplet parent; 
	
	Game(PApplet p) throws IOException
	{
		parent = p;
		turn = 1;
		history = new Stack<Move>();
		squares = new Square[13][10];
		words = new HashSet<String>();
		nodes = new Stack<Connected>();
		possible = new ArrayList<Word>();
		subsets = new HashSet[16];
		played = new HashSet<String>();
		for (int i = 0; i < 16; i ++)
		{
			subsets[i] = new HashSet<String>();
		}
		int rand= (int)parent.random(65,91);
		for (int i = 1; i < 12; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				rand= (int)parent.random(65,91);
				squares[i][j] = new Square(0,rand);
			}
		}
		for (int j = 0; j < 10; j++)
		{
			rand= (int)parent.random(65,91);
			squares[0][j] = new Square(3,rand);
		}
		for (int j = 0; j < 10; j++)
		{
			rand= (int)parent.random(65,91);
			squares[12][j] = new Square(1,rand);
		}
		
		int num = (int)parent.random(1,11);
		fillLetters(Integer.toString(num) + ".txt");
	}
	public void fillLetters(String name) throws IOException
	{
		 BufferedReader reader = new BufferedReader(new FileReader(name));
	     String currentLine = "";
	     int i = 0;
	     int j = 0;
	     while(true)
	     {
	    	 currentLine = reader.readLine();
	    	 if (currentLine == null)
	    	 {
	    		 break;
	    	 }
	    	 char x = currentLine.charAt(0);
	    	 squares[i][j].letter = (int)x;
	    	 if (j == 9)
	    	 {
	    		 j = 0;
	    		 i++;
	    		 j--;
	    	 }
	    	 j++;
	     }
	     reader.close();
	}
	public void createGrid()
	{
		parent.stroke(0);
		for (int i = 0; i < 13; i++)
		{
			parent.line(0, i*50, parent.width, i*50);
		}
		for (int i = 0; i < 10; i++)
		{
			parent.line(i*50, 0, i*50, parent.height);
		}
	}
	public void fillGrid()
	{
		parent.textSize(30);
		for (int i = 0; i < 13; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				if (squares[i][j].color == 0)
				{
					parent.fill(255);
				}
				else if (squares[i][j].color == 1)
				{
					parent.fill(255,0,0);
				}
				else if (squares[i][j].color == 2)
				{
					parent.fill(64,0,0);
				}
				else if (squares[i][j].color == 3)
				{
					parent.fill(0,0,255);
				}
				else if (squares[i][j].color == 4)
				{
					parent.fill(0,0,64);
				}
				parent.rect(j*50, i*50, 50, 50);
				parent.fill(0);
				parent.text((char)squares[i][j].letter, j*50+15, i*50+37);
			}
		}
	}
	
	public int mapX(int x)
	{
		return x/50;
	}
	public int mapY(int y)
	{
		return y/50;
	}
	public boolean moveStarted()
	{
		for (int i = 0; i < 13; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				if (squares[i][j].color == 2 || squares[i][j].color == 4)
				{
					return true;
				}
			}
		}
		return false;
	}
	public void update(int i, int j)
	{
		
		if (squares[i][j].color == (2*turn)-1 && moveStarted() == false)
		{
			history.push(new Move(i,j,squares[i][j].color));
			squares[i][j].color = turn*2;
		}
		else if (moveStarted() == true)
		{
			if (i == history.peek().i && j == history.peek().j)
			{
				squares[i][j].color = history.peek().color;
				history.pop();
			}
			else
			{
				if (squares[i][j].color == turn*2)
				{
					while(history.peek().i != i || history.peek().j != j)
					{
						squares[history.peek().i][history.peek().j].color = history.peek().color;
						history.pop();
					}
					squares[history.peek().i][history.peek().j].color = history.peek().color;
					history.pop();
				}
				else
				{
					for (int x = history.peek().i-1; x < history.peek().i+2; x++)
					{
						for (int y = history.peek().j-1; y< history.peek().j+2; y++)
						{
							if (x >=0 && y >= 0 & x<13 && y <10)
							{
								if (i == x && j == y)
								{
									history.push(new Move(i,j,squares[i][j].color));
									squares[i][j].color = turn*2;
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	public int checkConnected(int i, int j)
	{
		if(i<0 || i >12 || j<0 || j>9 || squares[i][j].color != (turn*2)-1)
		{
			return 0;
		}
		if (containsNode(i,j))
		{
			return 0;
		}
		if (squares[i][j].color == (turn*2)-1)
		{
			nodes.add(new Connected(i,j));
		}
		checkConnected(i-1, j-1);
		checkConnected(i-1, j);
		checkConnected(i-1, j+1);
		checkConnected(i, j-1);
		checkConnected(i, j+1);
		checkConnected(i+1, j-1);
		checkConnected(i+1, j);
		checkConnected(i+1, j+1);
		return 0;
	}
	public void cascade()
	{
		if (turn == 2)
		{
			checkConnected(0,0);
		}
		else
		{
			checkConnected(12,0);
		}
		for (int i = 0; i < 13; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				if (!containsNode(i,j) && squares[i][j].color == (2*turn)-1)
				{
					squares[i][j].color = 0;
				}
			}
		}
		nodes.removeAll(nodes);
	}
	public boolean doMove()
	{
		//IF WORD EXISTS
		String word = "";
		for (int i = 0; i < history.size();i++)
		{
			word += (char)squares[history.get(i).i][history.get(i).j].letter;
		}
		
		if (isWord(word) && !played.contains(word))
		{
			for (int i = 0; i < 13; i++)
			{
				for (int j = 0; j < 10; j++)
				{
					if(squares[i][j].color == turn*2)
					{
						squares[i][j].color = turn*2-1;
					}
				}
			}
			if(hasWon() > 0)
			{
				System.out.println("Player " + turn + " WON");
				createGrid();
				fillGrid();
				parent.noLoop();
				return false;
			}
			turn = (turn%2)+1;
			nodes.removeAll(nodes);
			history.removeAll(history);
			played.add(word);
			return true;
		}
		else
		{
			return false;
		}
		//if (win() == 1)
		
	}
	public boolean containsNode(int x, int y)
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).i == x && nodes.get(i).j == y)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean containsPossible(String word)
	{
		for (int i = 0; i < possible.size(); i++)
		{
			if (possible.get(i).word.equals(word))
			{
				return true;
			}
		}
		return false;
	}
	
	public void readWords(String fileName)
	{
        // This will reference one line at a time
        String line = null;
        String sub = null;
        try 
        {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) 
            {
            	words.add(line);
            	for (int i = 1; i < 16; i++)
            	{
	                if (line.length()>=i)
	                {
	                	sub = line.substring(0, i);
	                	subsets[i].add(sub);
	                }
	                else
	                {
	                	break;
	                }
            	}
            }   
            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex)
        {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) 
        {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
	        
	  }
	
	public boolean isWord(String word)
	{
		return words.contains(word);
	}
	public int computePossible(int i, int j, String word) 
	{
		if(i<0 || i >12 || j<0 || j>9)
		{
			return 0;
		}
//		if (visited[i][j] == true)
//		{
//			return 0;
//		}
		if (containsNode(i,j) == true)
		{
			return 0;
		}
		if (word.length() > 15)
		{
			return 0;
		}
		if (played.contains(word))
		{
			return 0;
		}
		if (!subsets[word.length()].contains(word) && word != "")
		{
			return 0;
		}
		//if word is not subset, return 0
		if (words.contains(word))
		{
			if (!containsPossible(word)) 
			{
				possible.add(new Word(word, nodes));
				//System.out.println(word);
			}
		}
		nodes.push(new Connected(i,j));
		//visited[i][j] = true;
		
		
		word += (char)squares[i][j].letter;
		
		computePossible(i-1, j-1, word);
		computePossible(i-1, j,word);
		computePossible(i-1, j+1, word);
		computePossible(i, j-1, word);
		computePossible(i, j+1, word);
		computePossible(i+1, j-1, word);
		computePossible(i+1, j, word);
		computePossible(i+1, j+1, word);
		//visited[i][j] = false;
		nodes.pop();
		return 0;
	}
//	public void makeVisitedFalse()
//	{
//		for (int i = 0; i < 13; i++)
//		{
//			for (int j = 0; j < 10; j++)
//			{
//				visited[i][j] = false;
//			}
//		}
//		possible.removeAll(possible);
//	}
	public int getOverlaps(ArrayList<Connected> path)
	{
		int overlaps = 0;
		for (int x = 0; x < path.size(); x++)
		{
			if (squares[path.get(x).i][path.get(x).j].color == 0)
			{
				overlaps += 1;
			}
			if (squares[path.get(x).i][path.get(x).j].color == 1)
			{
				overlaps += 2;
				if (path.get(x).i == 12)
				{
					overlaps += 100;
				}
			}
			
			if (squares[path.get(x).i][path.get(x).j].color == 3)
			{
				overlaps -= 1;
			}
		}
		return overlaps;
	}
	public void makeOptimalMove()
	{
		for (int x = 0; x < possible.size(); x++)
		{
			//possible.get(x).score = possible.get(x).word.length();
			possible.get(x).score += possible.get(x).getVerticality();
			possible.get(x).score += getOverlaps(possible.get(x).path);
		}
		Collections.sort(possible);
		System.out.println(possible.size());
		System.out.println(possible.size()*29/30);
		System.out.println(possible.get(possible.size()-1).getVerticality());
		System.out.println(getOverlaps(possible.get(possible.size()-1).path));
		
		//int index = (int) parent.random(possible.size()*29/30,possible.size());
		int index = possible.size()-1;
		for (int x = 0; x < possible.get(index).word.length(); x++)
		{
			update(possible.get(index).path.get(x).i, possible.get(index).path.get(x).j);
			createGrid();
			fillGrid();
		}
		createGrid();
		fillGrid();
		doMove();
		cascade();
		createGrid();
		fillGrid();
		
		System.out.println(possible.get(index).word + " " +possible.get(index).score);
	}
	public void makeHardMove()
	{
		for (int x = 0; x < possible.size(); x++)
		{
			//possible.get(x).score = possible.get(x).word.length();
			possible.get(x).score += possible.get(x).getVerticality();
			possible.get(x).score += getOverlaps(possible.get(x).path);
		}
		Collections.sort(possible);
		System.out.println(possible.size());
		System.out.println(possible.size()*29/30);
		System.out.println(possible.get(possible.size()-1).getVerticality());
		System.out.println(getOverlaps(possible.get(possible.size()-1).path));
		
		int index = (int) parent.random(possible.size()*29/30,possible.size());
		//int index = possible.size()-1;
		for (int x = 0; x < possible.get(index).word.length(); x++)
		{
			update(possible.get(index).path.get(x).i, possible.get(index).path.get(x).j);
			createGrid();
			fillGrid();
		}
		createGrid();
		fillGrid();
		doMove();
		cascade();
		createGrid();
		fillGrid();
		
		System.out.println(possible.get(index).word + " " +possible.get(index).score);
	}	
	public void makeMediumMove()
	{
		for (int x = 0; x < possible.size(); x++)
		{
			//possible.get(x).score = possible.get(x).word.length();
			possible.get(x).score += possible.get(x).getVerticality();
			possible.get(x).score += getOverlaps(possible.get(x).path);
		}
		Collections.sort(possible);
		System.out.println(possible.size());
		System.out.println(possible.size()*25/30);
		System.out.println(possible.get(possible.size()-1).getVerticality());
		System.out.println(getOverlaps(possible.get(possible.size()-1).path));
		
		int index = (int) parent.random(possible.size()*25/30,possible.size());
		//int index = possible.size()-1;
		for (int x = 0; x < possible.get(index).word.length(); x++)
		{
			update(possible.get(index).path.get(x).i, possible.get(index).path.get(x).j);
			createGrid();
			fillGrid();
		}
		createGrid();
		fillGrid();
		doMove();
		cascade();
		createGrid();
		fillGrid();
		
		System.out.println(possible.get(index).word + " " +possible.get(index).score);
	}	
	public void makeEasyMove()
	{
		for (int x = 0; x < possible.size(); x++)
		{
			//possible.get(x).score = possible.get(x).word.length();
			possible.get(x).score += possible.get(x).getVerticality();
			possible.get(x).score += getOverlaps(possible.get(x).path);
		}
		Collections.sort(possible);
		System.out.println(possible.size());
		System.out.println(possible.size()*2/3);
		System.out.println(possible.get(possible.size()-1).getVerticality());
		System.out.println(getOverlaps(possible.get(possible.size()-1).path));
		
		int index = (int) parent.random(possible.size()*2/3,possible.size());
		//int index = possible.size()-1;
		for (int x = 0; x < possible.get(index).word.length(); x++)
		{
			update(possible.get(index).path.get(x).i, possible.get(index).path.get(x).j);
			createGrid();
			fillGrid();
		}
		createGrid();
		fillGrid();
		doMove();
		cascade();
		createGrid();
		fillGrid();
		
		System.out.println(possible.get(index).word + " " +possible.get(index).score);
	}		
	public int hasWon()
	{
		for (int j = 0; j < 10; j++)
		{
			if (squares[0][j].color == 1)
			{
				return 1;
			}
		}
		
		for (int j = 0; j < 10; j++)
		{
			if (squares[12][j].color == 3)
			{
				return 2;
			}
		}
		return 0;
	}
}
