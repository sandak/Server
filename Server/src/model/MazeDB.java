package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;


@Entity
public class MazeDB {
	protected String name;
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	protected Maze3d maze;
	@Lob()
	@Column(columnDefinition = "LONGBLOB")
	protected Solution<Position> solution;
	
	public MazeDB()
	{
	}
	
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
	@Id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Maze3d getMaze() {
		return maze;
	}
	public void setMaze(Maze3d maze) {
		this.maze = maze;
	}
	public Solution<Position> getSolution() {
		return solution;
	}
	public void setSolution(Solution<Position> solution) {
		this.solution = solution;
	}

}
