package com.yourname.pojo2jsts.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public abstract class BaseGenerateAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        PsiClass psiClass = getPsiClass(e);
        if (psiClass == null) {
            showErrorMessage(project, "Please select a Java class file.\n\nMake sure you right-click on a .java file containing a class definition.");
            return;
        }
        
        String className = psiClass.getName();
        if (className == null) {
            showErrorMessage(project, "Unable to determine class name.\n\nPlease ensure the Java file contains a valid class definition.");
            return;
        }

        // Run generation in background with progress indicator
        ProgressManager.getInstance().run(new Task.Backgroundable(project, getProgressTitle(className), false) {
            private String generated;
            private Exception error;
            
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Analyzing class: " + className);
                indicator.setFraction(0.1);
                
                try {
                    Thread.sleep(100); // Brief pause to show progress
                    indicator.setText("Generating " + getOutputType() + "...");
                    indicator.setFraction(0.3);
                    
                    generated = generate(psiClass, project);
                    
                    indicator.setText("Formatting output...");
                    indicator.setFraction(0.8);
                    
                    Thread.sleep(50); // Brief pause
                    indicator.setFraction(1.0);
                    
                } catch (Exception ex) {
                    error = ex;
                }
            }
            
            @Override
            public void onSuccess() {
                if (error != null) {
                    handleError(project, error, className);
                    return;
                }
                
                if (StringUtil.isEmpty(generated)) {
                    showWarningMessage(project, "No content was generated.\n\nThis might happen if the class has no suitable fields or if there was a processing issue.");
                    return;
                }
                
                // Copy to clipboard with enhanced feedback
                copyToClipboard(generated, project, className);
            }
            
            @Override
            public void onThrowable(@NotNull Throwable error) {
                handleError(project, new Exception(error), className);
            }
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiClass psiClass = getPsiClass(e);
        boolean hasValidClass = psiClass != null;
        
        e.getPresentation().setEnabledAndVisible(hasValidClass);
        
        // Update description based on context
        if (hasValidClass) {
            String className = psiClass.getName();
            e.getPresentation().setDescription(getActionDescription() + 
                (className != null ? " for class: " + className : ""));
        } else {
            e.getPresentation().setDescription("Right-click on a Java class to " + getActionDescription());
        }
    }

    private void copyToClipboard(String content, Project project, String className) {
        try {
            // Use both system clipboard and IntelliJ's clipboard manager
            CopyPasteManager.getInstance().setContents(new StringSelection(content));
            
            // Also try system clipboard as backup
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(new StringSelection(content), null);
            
            // Calculate some stats for user feedback
            int lines = content.split("\n").length;
            int chars = content.length();
            
            String statsMessage = String.format(
                "%s generated successfully for class '%s'!\n\n" +
                "📋 Content copied to clipboard\n" +
                "📊 Stats: %d lines, %d characters\n\n" +
                "💡 Tip: You can paste this directly into your code or documentation.",
                getOutputType(), className, lines, chars
            );
            
            showSuccessMessage(project, statsMessage);
            
        } catch (Exception e) {
            // Fallback to simple copy if enhanced copy fails
            try {
                CopyPasteManager.getInstance().setContents(new StringSelection(content));
                showSuccessMessage(project, getOutputType() + " generated and copied to clipboard!");
            } catch (Exception fallbackError) {
                showErrorMessage(project, "Failed to copy to clipboard: " + fallbackError.getMessage());
            }
        }
    }
    
    private void handleError(Project project, Exception error, String className) {
        String errorMessage;
        String errorDetails = error.getMessage();
        
        if (errorDetails == null || errorDetails.trim().isEmpty()) {
            errorMessage = String.format(
                "An unexpected error occurred while processing class '%s'.\n\n" +
                "This might be due to:\n" +
                "• Complex class structure\n" +
                "• Missing dependencies\n" +
                "• Circular references\n\n" +
                "Please try with a simpler class first.",
                className
            );
        } else {
            errorMessage = String.format(
                "Error generating %s for class '%s':\n\n%s\n\n" +
                "If this persists, please try with a different class or report this issue.",
                getOutputType(), className, errorDetails
            );
        }
        
        showErrorMessage(project, errorMessage);
    }
    
    private void showSuccessMessage(Project project, String message) {
        UIUtil.invokeLaterIfNeeded(() -> 
            Messages.showInfoMessage(project, message, "Success"));
    }
    
    private void showWarningMessage(Project project, String message) {
        UIUtil.invokeLaterIfNeeded(() -> 
            Messages.showWarningDialog(project, message, "Warning"));
    }
    
    private void showErrorMessage(Project project, String message) {
        UIUtil.invokeLaterIfNeeded(() -> 
            Messages.showErrorDialog(project, message, "Error"));
    }

    @Nullable
    private PsiClass getPsiClass(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        
        // Try to get from editor
        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
        if (psiFile instanceof PsiJavaFile) {
            PsiClass[] classes = ((PsiJavaFile) psiFile).getClasses();
            if (classes.length > 0) {
                return classes[0];
            }
        }
        
        // Try to get from project view
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        if (virtualFile != null && virtualFile.getName().endsWith(".java")) {
            Project project = e.getProject();
            if (project != null) {
                psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(virtualFile);
                if (psiFile instanceof PsiJavaFile) {
                    PsiClass[] classes = ((PsiJavaFile) psiFile).getClasses();
                    if (classes.length > 0) {
                        return classes[0];
                    }
                }
            }
        }
        
        return null;
    }

    protected abstract String generate(PsiClass psiClass, Project project);
    
    protected abstract String getSuccessMessage();
    
    protected abstract String getOutputType();
    
    protected abstract String getProgressTitle(String className);
    
    protected abstract String getActionDescription();
}