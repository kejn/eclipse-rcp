package com.starterkit.tasklist.todo.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/*
 * The content provider class is responsible for providing objects to the
 * view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view,
 * or ignore it and always show the same content (like Task List, for
 * example).
 */
public class ViewContentTasksProvider implements IStructuredContentProvider {
	private List<TableViewer> parentViews;
	private List<Task> elements;
	private Long idGenerator;
	
	private static ViewContentTasksProvider instance = new ViewContentTasksProvider();
	
	public static ViewContentTasksProvider INSTANCE(TableViewer parent) {
		if(parent != null) {
			instance.parentViews.add(parent);
		}
		return instance;
	}
	
	private ViewContentTasksProvider() {
		System.out.println("Called once!");
		parentViews = new ArrayList<>();
		elements = new ArrayList<>();
		idGenerator = 0L;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return elements.toArray();
	}
	
	public Task getTaskById(Long id) {
	    for (Task todo : elements) {
	      if (todo.getId().equals(id)) {
	        return todo;
	      }
	    }
	    return null;
	  }

	public Long nextIdValue() {
		return idGenerator++;
	}

	public void addElement(Task task) {
		elements.add(task);
		refreshParentViews();
	}

	public void removeElement(Task task) {
		elements.remove(task);
		refreshParentViews();
	}
	
	public void refreshParentViews() {
		for (TableViewer tableViewer : parentViews) {
			tableViewer.refresh();
		}
	}

}
