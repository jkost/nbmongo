/* 
 * Copyright (C) 2015 Yann D'Isanto
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.netbeans.modules.mongodb.ui.util;

import java.awt.Component;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "VALIDATION_empty=can't be empty",
    "# {0} - type",
    "# {1} - name",
    "VALIDATION_exists={0} \"{1}\" already exists",
    "# {0} - prefix",
    "VALIDATION_invalid_prefix=can't start with \"{0}\"",
    "# {0} - forbidden character",
    "VALIDATION_forbidden_character=can't contains \'{0}\'",
    "VALIDATION_invalid_character=invalid character",
    "# {0} - max length",
    "VALIDATION_maxLength=max length is {0} characters"})
public final class ValidatingInputLine extends InputLine {

    private final InputValidator validator;

    public ValidatingInputLine(String text, String title, InputValidator validator) {
        super(text, title);
        this.validator = validator;
        createNotificationLineSupport();
        performValidation();
    }

    private void performValidation() {
        boolean valid = true;
        try {
            validator.validate(getInputText());
            getNotificationLineSupport().setErrorMessage("");
        } catch (IllegalArgumentException ex) {
            valid = false;
            getNotificationLineSupport().setErrorMessage(ex.getLocalizedMessage());
        }
        setValid(valid);
    }

    @Override
    protected Component createDesign(String text) {
        final Component design = super.createDesign(text);
        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                performValidation();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performValidation();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performValidation();
            }
        });
        return design;
    }

    public static interface InputValidator {

        void validate(String inputText) throws IllegalArgumentException;
    }
}
