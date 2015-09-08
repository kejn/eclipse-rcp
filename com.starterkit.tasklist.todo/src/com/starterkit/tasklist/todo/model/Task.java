package com.starterkit.tasklist.todo.model;

import java.util.Date;


public class Task extends ModelObject {
	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_STATUS= "status";
	public static final String FIELD_DUEDATE = "dueDate";
	
	private Long id;
	private String name;
	private String description;
	private Status status;
	private Date dueDate;
	
	public Task() {
		id = ViewContentTasksProvider.INSTANCE(null).nextIdValue();
		status = Status.TODO;
		dueDate = new Date();
		System.out.println(dueDate);
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		firePropertyChange(FIELD_NAME, this.name, this.name = name);
		System.out.println("task name: " + this.name);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		firePropertyChange(FIELD_DESCRIPTION, this.description, this.description = description);
		System.out.println("task description: " + description);
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		firePropertyChange(FIELD_STATUS, this.status, this.status = status);
		System.out.println("task status: " + status);
	}

	public Date getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(Date dueDate) {
		firePropertyChange(FIELD_DUEDATE, this.dueDate, this.dueDate = dueDate);
		System.out.println("task dueDate: " + dueDate);
	}
	
	@Override
	public String toString() {
		return "#" + id + ". " + name;
	}

}
