package com.starterkit.tasklist.todo.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class AddEditModifyListener implements ModifyListener {

	private final String MESSAGE;
	private final NewTaskDialog parent;
	private boolean messageInQueue;

	private static List<String> errorMessages = new ArrayList<>();

	public AddEditModifyListener(NewTaskDialog parent, String message) {
		this.parent = parent;
		this.MESSAGE = message;
		messageInQueue = false;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		boolean isValid = false;
		if (e.getSource() instanceof Text) {
			Text t = (Text) e.getSource();
			isValid = !t.getText().isEmpty();
		}
		if (!isValid) {
			if (!messageInQueue) {
				errorMessages.add(MESSAGE);
				messageInQueue = true;
			}
			parent.setErrorMessage(MESSAGE);
		} else {
			String previousMessage = null;
			if (messageInQueue) {
				errorMessages.remove(MESSAGE);
				messageInQueue = false;
			}
			if (errorMessages.size() > 0) {
				previousMessage = errorMessages.get(errorMessages.size() - 1);
			}
			parent.setErrorMessage(previousMessage);
		}
		parent.getOK().setEnabled(isValid && errorMessages.isEmpty());
	}

}
