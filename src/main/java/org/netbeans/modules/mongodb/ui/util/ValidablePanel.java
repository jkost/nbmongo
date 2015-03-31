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

/**
 *
 * @author Yann D'Isanto
 */
public abstract class ValidablePanel extends NotifierPanel {

    private static final long serialVersionUID = 1L;

    private boolean ok = true;

    private void setValidationSuccess(boolean ok) {
        if (ok != this.ok) {
            this.ok = ok;
            fireChange();
        }
    }

    public final boolean isValidationSuccess() {
        return ok;
    }

    protected final void setValidationProblem(String problem) {
        if (problem == null) {
            clearNotificationLineSupport();
            setValidationSuccess(true);
        } else {
            error(problem);
            setValidationSuccess(false);
        }
    }

    protected final void performValidation() {
        setValidationProblem(computeValidationProblem());
    }
    
    protected abstract String computeValidationProblem();
    
}
