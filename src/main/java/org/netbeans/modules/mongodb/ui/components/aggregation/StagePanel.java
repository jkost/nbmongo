/*
 * Copyright (C) 2016 Yann D'Isanto
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
package org.netbeans.modules.mongodb.ui.components.aggregation;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.netbeans.modules.mongodb.aggregation.PipelineStage;
import org.netbeans.modules.mongodb.aggregation.PipelineStage.Stage;
import org.netbeans.modules.mongodb.bson.Bsons;
import org.netbeans.modules.mongodb.resources.Images;
import org.netbeans.modules.mongodb.ui.util.BsonDocumentEditor;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.netbeans.modules.mongodb.ui.util.ValidatingInputLine.InputValidator;

/**
 *
 * @author Yann D'Isanto
 */
final class StagePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Icon COLLAPSE_ICON = new ImageIcon(Images.TOGGLE_COLLAPSE_ICON);

    private static final Icon EXPAND_ICON = new ImageIcon(Images.TOGGLE_EXPAND_ICON);

    private final JLabel collapseButton = new JLabel(COLLAPSE_ICON);

    private JLabel nameLabel;

    private final JLabel contentDisplayLabel = new JLabel();

    @Getter
    private final Stage stage;

    private BsonValue lastValidContent;

    private boolean collapsed = false;

    private boolean selected;

    public StagePanel(final PipelineStage pipelineStage) {
        this(pipelineStage.getStage(), pipelineStage.getContent());
    }

    public StagePanel(final Stage stage) {
        this(stage, defaultContent(stage));
    }

    public StagePanel(final Stage stage, BsonValue content) {
        this.stage = stage;
        lastValidContent = content;
        setLayout(new BorderLayout(3, 3));
        nameLabel = new JLabel(stage.name());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 12));
        contentDisplayLabel.setFont(new Font("Courrier New", Font.PLAIN, 14));
        updateContentDisplayLabel();
        JPanel titlePanel = new JPanel(new MigLayout());

        titlePanel.add(collapseButton);
        titlePanel.add(nameLabel);
        add(titlePanel, BorderLayout.NORTH);
        add(contentDisplayLabel, BorderLayout.CENTER);
        MouseAdapter collapseMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setCollapsed(!collapsed);
            }

        };
        collapseButton.addMouseListener(collapseMouseListener);
        nameLabel.addMouseListener(collapseMouseListener);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setSelected(true);
            }

        });
        contentDisplayLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        contentDisplayLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    displayEditor();
                }
            }
        });
    }

    public void displayEditor() {
        BsonValue content = null;
        switch (stage) {
            case $limit:  // FALL THROUGH
            case $skip:
                String defaultIntValue = null;
                if (lastValidContent != null) {
                    defaultIntValue = String.valueOf(((BsonInt32) lastValidContent).getValue());
                }
                String intValue = DialogNotification.validatingInput(stage.name(), stage.name(), InputValidator.POSITIVE_INTEGER, defaultIntValue);
                if (intValue != null) {
                    content = new BsonInt32(Integer.parseInt(intValue));
                }
                break;
            case $redact:  // FALL THROUGH
            case $unwind:  // FALL THROUGH
            case $out:
                String defaultValue = null;
                if (lastValidContent != null) {
                    defaultValue = ((BsonString) lastValidContent).getValue();
                }
                String value = DialogNotification.validatingInput(stage.name(), stage.name(), InputValidator.NON_EMPTY, defaultValue);
                if (value != null) {
                    content = new BsonString(value);
                }
                break;
            default:
                content = BsonDocumentEditor.show(stage.name(), (BsonDocument) lastValidContent, false);
        }

        if (content != null) {
            lastValidContent = content;
            updateContentDisplayLabel();
        }
    }

    void updateContentDisplayLabel() {
        String display = "";
        if (lastValidContent != null) {
            String contentAsHtml = Bsons.shellAndPretty(lastValidContent).replace(" ", "&nbsp;").replace("\n", "<br/>");
            display = new StringBuilder()
                    .append("<html>")
                    .append(contentAsHtml)
                    .append("</html>")
                    .toString();
        }
        contentDisplayLabel.setText(display);
    }

    void setCollapsed(boolean collapsed) {
        if (collapsed == this.collapsed) {
            return;
        }
        Icon icon = collapsed ? EXPAND_ICON : COLLAPSE_ICON;
        collapseButton.setIcon(icon);
        if (collapsed) {
            remove(contentDisplayLabel);
        } else {
            add(contentDisplayLabel, BorderLayout.CENTER);
        }
        this.collapsed = collapsed;
        revalidate();
        repaint();
    }

    public void setSelected(boolean selected) {
        if (selected == this.selected) {
            return;
        }
        if (selected) {
        } else {
        }
        this.selected = selected;
    }

    public PipelineStage getPipelineStage() {
        return new PipelineStage(stage, lastValidContent);
    }

    String getStageName() {
        return stage.name();
    }

    private static final Map<Stage, BsonValue> defaultStageContents;

    static {
        defaultStageContents = new EnumMap<>(Stage.class);
        defaultStageContents.put(Stage.$group, new BsonDocument("_id", new BsonString("")));
    }

    private static BsonValue defaultContent(Stage stage) {
        return defaultStageContents.get(stage);
    }
}
