package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class FilesToExportDialog extends DialogWrapper {

    private Project project;
    private PsiDirectory psiDirectory;
    private Language language;

    private Tree tree;

    private PsiFileSystemItem[] selected;

    public FilesToExportDialog(Project project, PsiDirectory psiDirectory, Language language) {
        super(true);
        this.project = project;
        this.psiDirectory = psiDirectory;
        this.language = language;
        init();
        setTitle("Select Files to Export");
    }

    public PsiFileSystemItem[] getSelectedItems() {
        return selected;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        ArrayList<Action> list = new ArrayList<>();

        Action select = new DialogWrapperExitAction("Select", OK_EXIT_CODE) {
            @Override
            protected void doAction(ActionEvent e) {
                DefaultMutableTreeNode[] selectedTreeNodes = tree.getSelectedNodes(DefaultMutableTreeNode.class, null);
                selected = Arrays.stream(selectedTreeNodes).map(node -> (PsiFileSystemItem) node.getUserObject()).toArray(PsiFileSystemItem[]::new);
                super.doAction(e);
            }
        };
        select.putValue(DEFAULT_ACTION, true);

        Action selectAll = new DialogWrapperExitAction("Select All", OK_EXIT_CODE) {
            @Override
            protected void doAction(ActionEvent e) {
                // TODO: Get all nodes
                super.doAction(e);
            }
        };

        Action cancel = new DialogWrapperExitAction("Cancel", CANCEL_EXIT_CODE);

        list.add(select);
        list.add(selectAll);
        list.add(cancel);

        return list.toArray(new Action[list.size()]);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel();

        DefaultMutableTreeNode root = psiDirectoryToTreeNode(psiDirectory);
        TreeModel treeModel = new DefaultTreeModel(root);
        tree = new Tree(treeModel);

        JBScrollPane scrollPane = new JBScrollPane(tree, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(350, 450));

        mainPanel.add(scrollPane);
        dialogPanel.add(mainPanel);
        return dialogPanel;
    }

    private DefaultMutableTreeNode psiDirectoryToTreeNode(PsiDirectory directory) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(directory) {
            @Override
            public String toString() {
                return ((PsiDirectory) userObject).getName();
            }
        };

        for (PsiDirectory subdirectory : directory.getSubdirectories()) {
            root.add(psiDirectoryToTreeNode(subdirectory));
        }

        // Filter only files that are in current language
        Stream<PsiFile> files = Arrays.stream(directory.getFiles())
                .filter(psiFile -> psiFile.getLanguage().is(language));

        files.forEach(file -> {
            root.add(new DefaultMutableTreeNode(file) {
                @Override
                public String toString() {
                    return ((PsiFile) userObject).getName();
                }
            });
        });

        return root;
    }
}
