package com.starterkit.tasklist.todo.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.starterkit.tasklist.todo.model.Status;
import com.starterkit.tasklist.todo.model.Task;

public class TaskFilter extends ViewerFilter {
	
	private Status status;
	
	public TaskFilter(Status status) {
		this.status = status;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Task) {
			Task task = (Task) element;
			if(task.getStatus().equals(status)) {
				return true;
			}
		}
		return false;
	}
}
