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
package org.netbeans.modules.mongodb.ui.components.result_panel.views.treetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.netbeans.modules.mongodb.options.RenderingOptions.PrefsRenderingOptions;
import org.netbeans.modules.mongodb.options.RenderingOptions.RenderingOptionsItem;

/**
 *
 * @author Yann D'Isanto
 */
public class BsonNodeRenderer extends JPanel implements TreeCellRenderer {

    final JLabel indexLabel = new JLabel();

    final JLabel keyLabel = new JLabel();

    final JLabel valueLabel = new JLabel();

    final JLabel simpleLabel = new JLabel();

    final Border selectionBorder = BorderFactory.createLineBorder(Color.BLACK);

    final Border nonSelectionBorder = BorderFactory.createEmptyBorder();

    /**
     * Color to use for the foreground for selected nodes.
     */
    private Color textSelectionColor;

    /**
     * Color to use for the background when a node is selected.
     */
    private Color backgroundSelectionColor;

    /**
     * Color to use for the background when the node isn't selected.
     */
    private Color backgroundNonSelectionColor;

    /**
     * Set to true after the constructor has run.
     */
    private final boolean inited;

    public BsonNodeRenderer() {
        super(new GridBagLayout());
        add(indexLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 3), 0, 0));
        add(keyLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 2), 0, 0));
        add(valueLabel, new GridBagConstraints(2, 0, 1, 1, 10.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 1), 2, 0));
        setOpaque(true);
        indexLabel.setOpaque(true);
        keyLabel.setOpaque(true);
        valueLabel.setOpaque(true);
        inited = true;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object node, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (node instanceof RootNode) {
            // doesn't matter because not shown
            return this;
        }
        setBackground(selected ? getBackgroundSelectionColor() : getBackgroundNonSelectionColor());
        setBorder(selected ? selectionBorder : nonSelectionBorder);

        indexLabel.setText("");
        keyLabel.setText("");
        valueLabel.setText("");
        PrefsRenderingOptions renderingOptions = PrefsRenderingOptions.INSTANCE;
        RenderingOptionsItem indexRendering = renderingOptions.comment();
        indexLabel.setFont(indexRendering.getFont());
        if (selected) {
            indexLabel.setForeground(getTextSelectionColor());
            indexLabel.setBackground(getBackgroundSelectionColor());
        } else {
            indexLabel.setForeground(indexRendering.getForeground());
            indexLabel.setBackground(indexRendering.getBackground());
        }
        BsonValueNode bsonNode = (BsonValueNode) node;
        TreeTableNode parent = (TreeTableNode) bsonNode.getParent();
        boolean noIndex = true;
        if (parent instanceof BsonValueNode && ((BsonValueNode) parent).getValue().isArray()) {
            int index = parent.getIndex((TreeNode) node);
            if (index > -1) {
                indexLabel.setText(String.format("%d -", index));
                noIndex = false;
            }
        }

        boolean noKey = true;
        boolean idNode = false;
        if (node instanceof BsonPropertyNode) {
            BsonPropertyNode propertyNode = (BsonPropertyNode) node;
            String propertyName = propertyNode.getPropertyName();
            keyLabel.setText(buildJsonKey(propertyName));
            noKey = false;
            idNode = "_id".equals(propertyName)
                    && parent.getParent() instanceof RootNode;
        }

        BsonValue value = bsonNode.getValue();
        RenderingOptionsItem keyRendering = renderingOptions.key();
        RenderingOptionsItem valueRendering = renderingOptions.get(value.getBsonType());
        if (value.isDocument() && parent instanceof RootNode) {
            BsonDocument document = value.asDocument();
            BsonValue id = document.get("_id");
            if(id != null) {
                valueLabel.setText(stringifyValue(id));
            } else if(document.isEmpty()) {
                valueLabel.setText("{ }");
            } else {
                Entry<String, BsonValue> field = document.entrySet().iterator().next();
                valueLabel.setText(
                        new StringBuilder(field.getKey())
                                .append(":")
                                .append(stringifyValue(field.getValue()))
                                .toString()
                );
            }
            valueRendering = renderingOptions.documentRoot();
        } else {
            if (idNode) {
                valueRendering = renderingOptions.documentId();
            }
            valueLabel.setText(stringifyValue(value));
        }

        keyLabel.setFont(keyRendering.getFont());
        valueLabel.setFont(valueRendering.getFont());
        if (selected) {
            keyLabel.setForeground(getTextSelectionColor());
            keyLabel.setBackground(getBackgroundSelectionColor());
            valueLabel.setForeground(getTextSelectionColor());
            valueLabel.setBackground(getBackgroundSelectionColor());
        } else {
            keyLabel.setForeground(keyRendering.getForeground());
            keyLabel.setBackground(keyRendering.getBackground());
            valueLabel.setForeground(valueRendering.getForeground());
            valueLabel.setBackground(valueRendering.getBackground());
        }
        if (noIndex && noKey) {
            simpleLabel.setText(valueLabel.getText());
            simpleLabel.setFont(valueLabel.getFont());
            simpleLabel.setBackground(valueLabel.getBackground());
            simpleLabel.setForeground(valueLabel.getForeground());
            simpleLabel.setBorder(getBorder());
            return simpleLabel;
        }
        return this;
    }

    private String buildJsonKey(Object value) {
        return new StringBuilder().append(value).append(":").toString();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.7
     */
    @Override
    public void updateUI() {
        super.updateUI();
        // To avoid invoking new methods from the constructor, the
        // inited field is first checked. If inited is false, the constructor
        // has not run and there is no point in checking the value. As
        // all look and feels have a non-null value for these properties,
        // a null value means the developer has specifically set it to
        // null. As such, if the value is null, this does not reset the
        // value.
        if (!inited || (getTextSelectionColor() instanceof UIResource)) {
            setTextSelectionColor(
                    UIManager.getColor("Tree.selectionForeground"));
        }
        if (!inited || (getBackgroundSelectionColor() instanceof UIResource)) {
            setBackgroundSelectionColor(
                    UIManager.getColor("Tree.selectionBackground"));
        }
        if (!inited
                || (getBackgroundNonSelectionColor() instanceof UIResource)) {
            setBackgroundNonSelectionColor(
                    UIManager.getColor("Tree.textBackground"));
        }
    }

    /**
     * Sets the color the text is drawn with when the node is selected.
     */
    public void setTextSelectionColor(Color newColor) {
        textSelectionColor = newColor;
    }

    /**
     * Returns the color the text is drawn with when the node is selected.
     */
    public Color getTextSelectionColor() {
        return textSelectionColor;
    }

    /**
     * Sets the color to use for the background if node is selected.
     */
    public void setBackgroundSelectionColor(Color newColor) {
        backgroundSelectionColor = newColor;
    }

    /**
     * Returns the color to use for the background if node is selected.
     */
    public Color getBackgroundSelectionColor() {
        return backgroundSelectionColor;
    }

    /**
     * Sets the background color to be used for non selected nodes.
     */
    public void setBackgroundNonSelectionColor(Color newColor) {
        backgroundNonSelectionColor = newColor;
    }

    /**
     * Returns the background color to be used for non selected nodes.
     */
    public Color getBackgroundNonSelectionColor() {
        return backgroundNonSelectionColor;
    }

    static interface BsonStringifier<T extends BsonValue> {

        String stringify(T value);
    }

    @SuppressWarnings("unchecked")
    static String stringifyValue(BsonValue value) {
        BsonStringifier<BsonValue> stringifier = (BsonStringifier<BsonValue>) stringifiers.get(value.getBsonType());
        if (stringifier == null) {
            stringifier = defaultBsonStringifier;
        }
        return stringifier.stringify(value);
    }

    private static final Map<BsonType, BsonStringifier<? extends BsonValue>> stringifiers;

    private static BsonStringifier<BsonValue> defaultBsonStringifier = new BsonStringifier<BsonValue>() {

        @Override
        public String stringify(BsonValue value) {
            return Bsons.shell(value);
        }
    };

    static {
        stringifiers = new EnumMap<>(BsonType.class);
        stringifiers.put(BsonType.DOCUMENT, new BsonStringifier<BsonDocument>() {

            @Override
            public String stringify(BsonDocument value) {
                return "{ }";
            }
        });
        stringifiers.put(BsonType.ARRAY, new BsonStringifier<BsonArray>() {

            @Override
            public String stringify(BsonArray value) {
                return new StringBuilder()
                        .append('[')
                        .append(value.size())
                        .append(']')
                        .toString();
            }
        });
    }
}
