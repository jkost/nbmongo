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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;
import org.bson.BsonDocument;
import org.netbeans.modules.mongodb.api.aggregation.PipelineStage;
import org.netbeans.modules.mongodb.api.aggregation.PipelineStage.Stage;
import static org.netbeans.modules.mongodb.api.aggregation.PipelineStage.Stage.*;
import org.netbeans.modules.mongodb.ui.util.DialogNotification;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_addStage=add stage",
    "ACTION_editStage=edit stage",
    "ACTION_removeStage=remove stage"
})
public final class PipelinePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Border SELECTION_BORDER = BorderFactory.createLineBorder(Color.BLUE);

    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

    private final List<StagePanel> stagePanels = new ArrayList<>();

    private final AddStageAction addStageAction = new AddStageAction();
    
    private final EditStageAction editStageAction = new EditStageAction();

    private final RemoveStageAction removeStageAction = new RemoveStageAction();

    private int selectedStageIndex = -1;

    private final JPanel pipelinePanel = new JPanel(new MigLayout("fillx"));

    private final JPanel buttonsPanel = new JPanel(new MigLayout("fillx"));

    private final MouseAdapter stageClickListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            StagePanel stagePanel = getEventStagePanel(e);
            if (stagePanel != null) {
                int index = stagePanels.indexOf(stagePanel);
                if (index >= 0 && index != selectedStageIndex) {
                    setSelectedStagePanel(index);
                }
            }
        }

    };

    private final KeyAdapter stageKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            StagePanel stagePanel = getEventStagePanel(e);
            if (stagePanel != null) {
                int index = stagePanels.indexOf(stagePanel);
                if(index < 0) {
                    return;
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if(index > 0) {
                            // TODO
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        // TODO
                        break;
                }
            }
        }
    };

    public PipelinePanel() {
        setPreferredSize(new Dimension(500, 400));

        setLayout(new BorderLayout(3, 3));
        buttonsPanel.add(new JButton(addStageAction), "wrap, growx");
        buttonsPanel.add(new JButton(editStageAction), "wrap, growx");
        buttonsPanel.add(new JButton(removeStageAction), "growx");
        JScrollPane pipelineScrollPane = new JScrollPane(pipelinePanel);
        pipelineScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(pipelineScrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.EAST);

    }

    void addStagePanel(StagePanel panel) {
        stagePanels.add(panel);
        pipelinePanel.add(panel, "wrap, growx");
        panel.addMouseListener(stageClickListener);
        panel.addKeyListener(stageKeyListener);
        repaint();
        panel.displayEditor();
    }

    void removeStagePanel(StagePanel panel) {
        if (stagePanels.remove(panel)) {
            pipelinePanel.remove(panel);
            panel.removeMouseListener(stageClickListener);
            panel.removeKeyListener(stageKeyListener);
        }
        repaint();
    }

    private StagePanel getSelectedStagePanel() {
        return selectedStageIndex >= 0 ? stagePanels.get(selectedStageIndex) : null;
    }

    private void setSelectedStagePanel(int stageIndex) {
        if (selectedStageIndex >= 0) {
            StagePanel stagePanel = stagePanels.get(selectedStageIndex);
            stagePanel.setSelected(false);
            stagePanel.setBorder(EMPTY_BORDER);
        }
        if (stageIndex >= 0) {
            StagePanel stagePanel = stagePanels.get(stageIndex);
            stagePanel.setSelected(true);
            stagePanel.setBorder(SELECTION_BORDER);
        }
        selectedStageIndex = stageIndex;
        updateButtonsState();
    }

    private void clearStagePanelSelection() {
        selectedStageIndex = -1;
        updateButtonsState();
    }
    
    private void updateButtonsState() {
        boolean hasStageSelected = selectedStageIndex >= 0;
        editStageAction.setEnabled(hasStageSelected);
        removeStageAction.setEnabled(hasStageSelected);
    }

    public List<BsonDocument> getPipeline() {
        List<BsonDocument> pipeline = new ArrayList<>(stagePanels.size());
        for (StagePanel stagePanel : stagePanels) {
            PipelineStage stage = stagePanel.getPipelineStage();
            pipeline.add(stage.toBson());
        }
        return pipeline;
    }

    public void setPipeline(List<PipelineStage> pipeline) {
        for (PipelineStage pipelineStage : pipeline) {
            addStagePanel(new StagePanel(pipelineStage));
        }
    }

    class AddStageAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public AddStageAction() {
            super(Bundle.ACTION_addStage());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Stage stage = DialogNotification.select("select stage", Stage.values(), guessNextStage());
            if (stage != null) {
                StagePanel stagePanel = new StagePanel(stage);
                int stageIndex = stagePanels.size();
                addStagePanel(stagePanel);
                setSelectedStagePanel(stageIndex);
            }
        }

        Stage guessNextStage() {
            if (stagePanels.isEmpty()) {
                return $project;
            }
            List<Stage> stages = getStages();
            Stage last = stages.get(stages.size() - 1);
            switch (last) {
                case $project:
                    return stages.contains($match) ? $group : $match;
                case $match:
                    return stages.contains($project) ? $group : $project;
                case $group:
                    return $sort;
                case $sort:
                    return $limit;
                case $skip:
                    return $limit;
                case $limit:
                    return $out;
                default:
                    return null;
            }
        }

        List<Stage> getStages() {
            List<Stage> stages = new ArrayList<>(stagePanels.size());
            for (StagePanel panel : stagePanels) {
                stages.add(panel.getStage());
            }
            return stages;
        }

    }

    class EditStageAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public EditStageAction() {
            super(Bundle.ACTION_editStage());
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int index = selectedStageIndex;
            StagePanel stagePanel = getSelectedStagePanel();
            stagePanel.displayEditor();
        }

    }

    class RemoveStageAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public RemoveStageAction() {
            super(Bundle.ACTION_removeStage());
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int index = selectedStageIndex;
            StagePanel stagePanel = getSelectedStagePanel();

            if (DialogNotification.confirm("remove this " + stagePanel.getStageName() + " stage ?")) {
                clearStagePanelSelection();
                removeStagePanel(stagePanel);
                if (stagePanels.isEmpty() == false) {
                    setSelectedStagePanel(index - 1);
                }
            }
        }

    }

    public static List<BsonDocument> showPanel() {
        PipelinePanel panel = new PipelinePanel();
        boolean doLoop = true;
        while (doLoop) {
            doLoop = false;
            final DialogDescriptor desc = new DialogDescriptor(panel, "Aggregation Pipeline");
            if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
                List<BsonDocument> pipeline = panel.getPipeline();
                return pipeline;
            }
        }
        return null;
    }

    private StagePanel getEventStagePanel(EventObject event) {
        if (event.getSource() instanceof Component) {
            Component c = (Component) event.getSource();
            while (c != null) {
                if (c instanceof StagePanel) {
                    return (StagePanel) c;
                }
                c = c.getParent();
            }
        }
        return null;
    }
}
