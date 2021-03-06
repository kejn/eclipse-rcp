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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.starterkit.tasklist.todo.dialogs.NewTaskDialog;
import com.starterkit.tasklist.todo.filters.TaskFilter;
import com.starterkit.tasklist.todo.model.Status;
import com.starterkit.tasklist.todo.model.Task;
import com.starterkit.tasklist.todo.model.ViewContentTasksProvider;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class TaskList extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.starterkit.tasklist.todo.views.TaskList";

	private TableViewer tableViewer;
	private Action actionNewTask;
	private Action actionEditTask;
	private Action actionArchiveTask;
	private Action actionDeleteTask;
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
	public TaskList() {
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
		tableViewer.setFilters(new TaskFilter[]{new TaskFilter(Status.TODO)});
		
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
				TaskList.this.fillContextMenu(manager);
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
		manager.add(actionNewTask);
		manager.add(new Separator());
		manager.add(actionEditTask);
		manager.add(new Separator());
		manager.add(actionArchiveTask);
		manager.add(new Separator());
		manager.add(actionDeleteTask);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionNewTask);
		manager.add(actionEditTask);
		manager.add(actionArchiveTask);
		manager.add(actionDeleteTask);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionNewTask);
		manager.add(actionEditTask);
		manager.add(actionArchiveTask);
		manager.add(actionDeleteTask);
	}

	private void makeActions() {
		actionNewTask = new Action() {
			public void run() {
				newTaskDialog();
			}
		};
		actionNewTask.setId("actionNewTask");
		actionNewTask.setText("New task");
		actionNewTask.setToolTipText("Create new task with list of TODOs");
		actionNewTask.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actionEditTask = new Action() {
			public void run() {
				if (tableViewer.getSelection().isEmpty()) {
					showMessage("No items selected");
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				Task task = (Task) selection.getFirstElement();
				editTaskDialog(task);
			}
		};
		actionEditTask.setId("actionEditTask");
		actionEditTask.setText("Edit task");
		actionEditTask.setToolTipText("Edit selected task");
		actionEditTask.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));

		actionArchiveTask = new Action() {
			public void run() {
				if (tableViewer.getSelection().isEmpty()) {
					showMessage("No items selected");
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				Task task = (Task) selection.getFirstElement();
				task.setStatus(Status.DONE);
				ViewContentTasksProvider.INSTANCE(null).refreshParentViews();
				
			}
		};
		actionArchiveTask.setId("actionArchiveTask");
		actionArchiveTask.setText("Mark task as DONE");
		actionArchiveTask.setToolTipText("Task DONE action executed");
		actionArchiveTask.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		
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

	private void newTaskDialog() {
		NewTaskDialog dialog = new NewTaskDialog(tableViewer.getControl().getShell());
		dialog.open();
		Task task = dialog.getTask();
		if(task != null) {
			((ViewContentTasksProvider) tableViewer.getContentProvider())
					.addElement(task);
		}

	}

	private void editTaskDialog(Task taskEdit) {
		NewTaskDialog dialog = new NewTaskDialog(tableViewer.getControl().getShell(), taskEdit);
		dialog.open();
		ViewContentTasksProvider.INSTANCE(null).refreshParentViews();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
}