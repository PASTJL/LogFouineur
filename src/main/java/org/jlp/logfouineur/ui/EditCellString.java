/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
package org.jlp.logfouineur.ui;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

// TODO: Auto-generated Javadoc
/**
 * The Class EditCellString.
 *
 * @param <T> the generic type
 * @param <E> the element type
 */
public class EditCellString<T, E> extends TableCell<T, String> {
	
	/** The text field. */
	private TextField textField;

	

	/**
	 * Start edit.
	 */
	@Override
	public void startEdit() {
	    if (!isEmpty()) {
	        super.startEdit();
	        createTextField();
	        setText(null);
	        setGraphic(textField);
	        textField.selectAll();
	    }
	}

	/**
	 * Cancel edit.
	 */
	@Override
	public void cancelEdit() {
	    super.cancelEdit();
	    
	    setText((String) getItem().toString());
	    setGraphic(null);
	}

	/**
	 * Update item.
	 *
	 * @param item the item
	 * @param empty the empty
	 */
	@Override
	public void updateItem(String item, boolean empty) {
	super.updateItem(item, empty);

	if (empty) {
	    setText(null);
	    setGraphic(null);
	    } else {
	        if (isEditing()) {
	            if (textField != null) {
	            textField.setText(getString());
	            }
	            setText(null);
	            setGraphic(textField);
	        } else {
	            setText(getString());
	            setGraphic(null);
	        }
	    }
	}

	/**
	 * Creates the text field.
	 */
	private void createTextField() {
	    textField = new TextField(getString());
	    textField.setOnAction(evt -> { // enable ENTER commit
	        commitEdit(textField.getText());
	    });

	    textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
	    
	    ChangeListener<? super Boolean> changeListener = (observable, oldSelection, newSelection) ->
	    {
	        if (! newSelection) {
	            commitEdit(textField.getText());
	        }
	    };
	    textField.focusedProperty().addListener(changeListener);
	    
	    textField.setOnKeyPressed((ke) -> {
	        if (ke.getCode().equals(KeyCode.ESCAPE)) {
	            textField.focusedProperty().removeListener(changeListener);
	            cancelEdit();
	        }
	    });
	}

	/**
	 * Gets the string.
	 *
	 * @return the string
	 */
	private String getString() {
	    return getItem() == null ? "" : getItem().toString();
	}


	/**
	 * Commit edit.
	 *
	 * @param item the item
	 */
	@Override
	public void commitEdit(String item) {
System.out.println("CommitEdit");
	if (isEditing()) {
	    super.commitEdit(item);
	} else {
	    final TableView<?> table = getTableView();
	    if (table != null) {
	        @SuppressWarnings("unchecked")
			TablePosition<?, String> position =( TablePosition<?, String>) new TablePosition(getTableView(), getTableRow().getIndex(), getTableColumn());
	        CellEditEvent<?, String> editEvent = new CellEditEvent(table, position, TableColumn.editCommitEvent(), item);
	        Event.fireEvent(getTableColumn(), editEvent);
	    }
	        updateItem(item, false);
	        if (table != null) {
	            table.edit(-1, null);
	        }

	    }
	}

}
