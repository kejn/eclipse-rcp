package com.starterkit.tasklist.todo.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.starterkit.tasklist.todo.filters.TaskFilter;
import com.starterkit.tasklist.todo.model.Status;
import com.starterkit.tasklist.todo.model.Task;
import com.starterkit.tasklist.todo.model.ViewContentTasksProvider;

public class ArchiveTaskList extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.starterkit.tasklist.todo.views.ArchiveTaskList";
	
	private TableViewer tableViewer;
	private Action actionDeleteTask;
	private Action actionTodoTask;
	private Action doubleClickAction;

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			Task task = (Task) obj;
			String result; 
			switch(index) {
			case 1:
				result = task.getName(); break;
			case 2:
				result = task.getDescription(); break;
			case 3:
				result = task.getDueDate().toString(); break;
			case 4:
				result = task.getStatus().toString(); break;
			default:
				result = task.getId().toString(); break;
			}
			return result;
		}

		public Image getColumnImage(Object obj, int index) {
			Image image = null;
			if(index == 0) {
				image = getImage(obj);
			}
			return image;
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FILE);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public ArchiveTaskList() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		tableViewer = new TableViewer(parent, SWT.NONE | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setContentProvider(ViewContentTasksProvider
				.INSTANCE(tableViewer));
		tableViewer.setFilters(new TaskFilter[]{new TaskFilter(Status.DONE)});
		
		TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
		tblclmnId.setWidth(35);
		tblclmnId.setText("ID");
		
		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnDescription = new TableColumn(table, SWT.NONE);
		tblclmnDescription.setWidth(230);
		tblclmnDescription.setText("Description");
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("Due date");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("Status");
		tableViewer.setLabelProvider(new ViewLabelProvider());
		tableViewer.setSorter(new NameSorter());
		tableViewer.setInput(getViewSite());
		table.setHeaderVisible(true);

		// Create the help context id for the viewer's control
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(tableViewer.getControl(),
						"com.starterkit.tasklist.todo.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ArchiveTaskList.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tableViewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionTodoTask);
		manager.add(new Separator());
		manager.add(actionDeleteTask);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionTodoTask);
		manager.add(actionDeleteTask);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionTodoTask);
		manager.add(actionDeleteTask);
	}

	private void makeActions() {
		actionTodoTask = new Action() {
			public void run() {
				if (tableViewer.getSelection().isEmpty()) {
					showMessage("No items selected");
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				Task task = (Task) selection.getFirstElement();
				task.setStatus(Status.TODO);
				ViewContentTasksProvider.INSTANCE(null).refreshParentViews();
			}
		};
		actionTodoTask.setId("actionTodoTask");
		actionTodoTask.setText("Mark task as TODO");
		actionTodoTask.setToolTipText("Task TODO action executed");
		actionTodoTask.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));

		actionDeleteTask = new Action() {
			public void run() {
				if (tableViewer.getSelection().isEmpty()) {
					showMessage("No items selected");
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				Task task = (Task) selection.getFirstElement();
				String question = "Are you sure to delete this task?\n\n\t" + task
						+ "\n\nYou won't be able to undo this operation!";
				boolean confirmed = MessageDialog.openConfirm(tableViewer
						.getControl().getShell(), "Deleting task", question);
				if (confirmed) {
					((ViewContentTasksProvider) tableViewer
							.getContentProvider()).removeElement(task);
				}
			}
		};
		actionDeleteTask.setId("actionDeleteTask");
		actionDeleteTask.setText("Delete task");
		actionDeleteTask.setToolTipText("Delete task action executed");
		actionDeleteTask.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));

		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				Task task = (Task) selection.getFirstElement();
				
				showMessage("Double-click detected on " + task.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(tableViewer.getControl().getShell(),
				"Task list", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
}
