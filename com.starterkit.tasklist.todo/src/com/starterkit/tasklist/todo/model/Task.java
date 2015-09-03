package com.starterkit.tasklist.todo.model;

public class Task extends ModelObject {
	private String name;
	

	public Task() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		firePropertyChange("name", this.name, this.name = name);
		System.out.println("aktualizacja! " + this.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
