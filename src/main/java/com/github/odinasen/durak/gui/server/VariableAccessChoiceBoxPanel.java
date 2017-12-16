package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class VariableAccessChoiceBoxPanel<T>
        extends AnchorPane {
    @FXML
    private Label readOnlyField;
    @FXML
    private ChoiceBox<T> choiceBox;

    private boolean readOnly;

    public VariableAccessChoiceBoxPanel() {
        ResourceGetter.loadCustomFXMLPanel(FXMLNames.VARIABLE_ACCCESS_CHOICE_BOX_PANEL, this);
        readOnly = false;
    }

    public void setChoiceBoxEditable(boolean isEditable) {
        readOnly = !isEditable;
        if (readOnly) {
            readOnlyField.setText(choiceBox.getValue().toString());
        }

        readOnlyField.setVisible(readOnly);
        choiceBox.setVisible(!readOnly);
    }

    public void setValue(T value) {
        if (!readOnly) {
            choiceBox.setValue(value);
        }
    }

    public ObservableList<T> getChoiceBoxItems() {
        return choiceBox.getItems();
    }

    public T getValue() {
        return choiceBox.getValue();
    }
}
