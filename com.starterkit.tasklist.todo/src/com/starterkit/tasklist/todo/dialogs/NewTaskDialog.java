package com.starterkit.tasklist.todo.dialogs;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.starterkit.tasklist.todo.model.Task;

public class NewTaskDialog extends TitleAreaDialog {
	private final String MESSAGE_DEFAULT = "Create new or rename selected task";
	private final String MESSAGE_EMPTY_NAME = "Task name is empty!";

	private Text txtTaskName;
	private Task task;

	public Task getTask() {
		return task;
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
		setTitle("Create/Rename task");
		setMessage(MESSAGE_DEFAULT, IMessageProvider.INFORMATION);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite containter = new Composite(area, SWT.NONE);
		containter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		containter.setLayout(new GridLayout(2, false));
		createTaskName(containter);
		
		return area;
	}

	private void createTaskName(Composite container) {
		Label labelTaskName = new Label(container, SWT.NONE);
		labelTaskName.setText("Task name");

		GridData dataTaskName = new GridData(GridData.FILL_HORIZONTAL);

		txtTaskName = new Text(container, SWT.BORDER);
		txtTaskName.setLayoutData(dataTaskName);
		txtTaskName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				boolean nameIsValid = !txtTaskName.getText().isEmpty(); 
				if(!nameIsValid) {
					setErrorMessage(MESSAGE_EMPTY_NAME);
				} else {
					setErrorMessage(null);
				}
				getButton(IDialogConstants.OK_ID).setEnabled(nameIsValid);
			}
		});
		if(task == null) {
			task = new Task();
		}
		DataBindingContext ctx = new DataBindingContext();
		IObservableValue target = WidgetProperties.text(SWT.Modify).observe(txtTaskName);
		IObservableValue model = BeanProperties.value(Task.class,"name").observe(task);
		ctx.bindValue(target, model);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void okPressed() {
		if(txtTaskName.getText().isEmpty()) {
			task = null;
		}
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		task = null;
		super.cancelPressed();
	}
	
	

}
