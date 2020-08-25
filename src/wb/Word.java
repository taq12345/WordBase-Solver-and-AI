package wb;

import java.util.ArrayList;
import java.util.Stack;

public class Word implements Comparable<Word> {
	String word;
	ArrayList<Connected> path;
	int score = 0;
	
	Word(String word, Stack<Connected> indices)
	{
		this.word = word;
		this.path = new ArrayList<Connected>();
		
		for (int x = 0; x < indices.size();x++)
		{
			path.add(new Connected(indices.get(x).i, indices.get(x).j));
		}
	}
	
	public int getVerticality()
	{
		//int minI = 12;
		int maxI = 0;
		for (int i = 0; i < path.size(); i++)
		{
//			if (path.get(i).i < minI)
//			{
//				minI = path.get(i).i;
//			}
			if (path.get(i).i > maxI)
			{
				maxI = path.get(i).i;
			}
		}
		return maxI;
	}

	@Override
	public int compareTo(Word o) {
		// TODO Auto-generated method stub
		return this.score-o.score;
	}
}
