package com.starterkit.tasklist.todo.dialogs;


import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.starterkit.tasklist.todo.model.Status;
import com.starterkit.tasklist.todo.model.Task;

public class NewTaskDialog extends TitleAreaDialog {
	private final String MESSAGE_DEFAULT = "Create new or edit selected task";
	private final String MESSAGE_EMPTY_NAME = "Task name is empty!";
	private final String MESSAGE_EMPTY_DESCRIPTION = "Task description is empty!";
	
	private DataBindingContext ctx = new DataBindingContext();
	
	private Text txtTaskId;
	private Text txtTaskName;
	private Text txtTaskDescription;
	private DateTime dateTimeTaskDueDate;
	private Button[] checkTaskStatusDone;
	private Task task;

	public Task getTask() {
		return task;
	}
	
	public Button getOK() {
		return getButton(IDialogConstants.OK_ID);
	}

	public NewTaskDialog(Shell parentShell) {
		super(parentShell);
	}

	public NewTaskDialog(Shell parentShell, Task task) {
		super(parentShell);
		this.task = task;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create/Edit task");
		setMessage(MESSAGE_DEFAULT, IMessageProvider.INFORMATION);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite containter = new Composite(area, SWT.NONE);
		containter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		containter.setLayout(new GridLayout(2, false));
		createTask(containter);
		bindFields();
		return area;
	}
	
	@Override
	protected void setShellStyle(int arg0){
	    super.setShellStyle(SWT.TITLE);
	}

	private void createTask(Composite container) {
		GridData inLineData = new GridData(GridData.FILL_HORIZONTAL);
		
		Label labelTaskId = new Label(container, SWT.NONE);
		labelTaskId.setText("Id");
		txtTaskId = new Text(container, SWT.BORDER);
		txtTaskId.setLayoutData(inLineData);
		txtTaskId.setEnabled(false);

		Label labelTaskName = new Label(container, SWT.NONE);
		labelTaskName.setText("Name");
		txtTaskName = new Text(container, SWT.BORDER);
		txtTaskName.setLayoutData(inLineData);
		txtTaskName.addModifyListener(new AddEditModifyListener(this, MESSAGE_EMPTY_NAME));
		
		Label labelTaskDescription = new Label(container, SWT.NONE);
		labelTaskDescription.setText("Description");
		txtTaskDescription = new Text(container, SWT.BORDER);
		txtTaskDescription.setLayoutData(inLineData);
		txtTaskDescription.addModifyListener(new AddEditModifyListener(this, MESSAGE_EMPTY_DESCRIPTION));
		
		Label labelTaskDueDate = new Label(container, SWT.NONE);
		labelTaskDueDate.setText("Due date");
		dateTimeTaskDueDate = new DateTime(container, SWT.CALENDAR);
		dateTimeTaskDueDate.setLayoutData(new GridData(SWT.NONE, SWT.FILL,true, true));
		dateTimeTaskDueDate.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getOK().setEnabled(getErrorMessage()==null);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		Label labelTaskStatus = new Label(container, SWT.NONE);
		labelTaskStatus.setText("Status");
		
		checkTaskStatusDone = new Button[Status.values().length];
		int i = 0;
		for (Status status : Status.values()) {
			checkTaskStatusDone[i] = new Button(container, SWT.RADIO);
			checkTaskStatusDone[i].setText(status.toString());
			checkTaskStatusDone[i].setData(status);
			checkTaskStatusDone[i].addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					getOK().setEnabled(getErrorMessage()==null);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			new Label(container, SWT.NONE);
			++i;
		}
		

		if(task == null) {
			task = new Task();
		}
	}
	
	private void bindFields() {
		txtTaskId.setText(task.getId().toString());
		bindText(txtTaskName, Task.FIELD_NAME);
		bindText(txtTaskDescription, Task.FIELD_DESCRIPTION);

		IObservableValue target = WidgetProperties.selection().observe(dateTimeTaskDueDate);
		IObservableValue model = BeanProperties.value(Task.class,Task.FIELD_DUEDATE).observe(task);
		ctx.bindValue(target, model);
		
		model = BeanProperties.value(Task.class,Task.FIELD_STATUS).observe(task);
		SelectObservableValue val = new SelectObservableValue(Status.class);
		int i = 0;
		for (Status status : Status.values()) {
			val.addOption(status, SWTObservables.observeSelection(checkTaskStatusDone[i]));
			++i;
		}
		ctx.bindValue(val, model);
	}
	
	private void bindText(Text text, String field) {
		IObservableValue target = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue model = BeanProperties.value(Task.class,field).observe(task);
		ctx.bindValue(target, model);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		task = null;
		super.cancelPressed();
	}

}
