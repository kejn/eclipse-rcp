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
	private TableViewer parent;
	private List<Task> elements;

	private static ViewContentTasksProvider instance;
	
	public static ViewContentTasksProvider INSTANCE(TableViewer parent) {
		if(instance == null) {
			instance = new ViewContentTasksProvider(parent);
		}
		return instance;
	}
	
	private ViewContentTasksProvider(TableViewer parent) {
		this.parent = parent;
		elements = new ArrayList<>();
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return elements.toArray();
	}

	public void addElement(Task task) {
		elements.add(task);
		parent.refresh();
	}

	public void removeElement(Task task) {
		elements.remove(task);
		parent.refresh();
	}

}
