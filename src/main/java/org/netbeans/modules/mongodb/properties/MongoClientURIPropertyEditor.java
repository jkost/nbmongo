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
package org.netbeans.modules.mongodb.properties;

import com.mongodb.MongoClientURI;
import java.awt.Component;
import java.beans.PropertyEditorSupport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.mongodb.ui.components.MongoURIEditorPanel;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoClientURIPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {

    private PropertyEnv env;

    private final boolean rw;

    public MongoClientURIPropertyEditor() {
        this(true);
    }

    
    public MongoClientURIPropertyEditor(boolean rw) {
        this.rw = rw;
    }
    
    
    @Override
    public String getAsText() {
        final MongoClientURI uri = (MongoClientURI) getValue();
        return uri.getURI();
    }

    @Override
    public void setAsText(String uri) throws IllegalArgumentException {
        setValue(new MongoClientURI(uri.trim()));
    }

    @Override
    public Component getCustomEditor() {
        final MongoURIEditorPanel editor = new MongoURIEditorPanel((MongoClientURI) getValue());
        editor.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (editor.valid()) {
                    setEnvState(PropertyEnv.STATE_VALID);
                    setValue(editor.getMongoURI());
                } else {
                    setEnvState(PropertyEnv.STATE_INVALID);
                }
            }
        });
        return editor;
    }

    @Override
    public boolean supportsCustomEditor() {
        return rw;
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        this.env = env;
    }

    private void setEnvState(Object state) {
        if(env != null) {
            env.setState(state);
        }
    }

}
