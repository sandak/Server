package model;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

public class MazeDB {
	protected String name;
	protected Maze3d maze;
	protected Solution<Position> solution;
	
	
	public MazeDB(String name,Maze3d maze)
	{
		this.name=name;
		this.maze=maze;
	}
	public MazeDB(String name,Maze3d maze,Solution<Position> solution)
	{
		this.name=name;
		this.maze=maze;
		this.solution=solution;
	}

}
