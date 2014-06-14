package com.github.odinasen.gui.notification.dialog.message;

import com.github.odinasen.gui.notification.dialog.AbstractDialog;

import java.io.IOException;

public class MessageDialog extends AbstractDialog {
    public MessageDialog(String message) throws IOException {
      super("message_dialog.fxml", new MessageDialogController(message));
    }
  }