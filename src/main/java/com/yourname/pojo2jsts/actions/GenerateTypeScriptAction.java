package com.yourname.pojo2jsts.actions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.yourname.pojo2jsts.generators.TypeScriptGenerator;

public class GenerateTypeScriptAction extends BaseGenerateAction {

    @Override
    protected String generate(PsiClass psiClass, Project project) {
        TypeScriptGenerator generator = new TypeScriptGenerator();
        return generator.generate(psiClass, project);
    }

    @Override
    protected String getSuccessMessage() {
        return "TypeScript interface generated successfully!";
    }
    
    @Override
    protected String getOutputType() {
        return "TypeScript Interface";
    }
    
    @Override
    protected String getProgressTitle(String className) {
        return "Generating TypeScript Interface for " + className;
    }
    
    @Override
    protected String getActionDescription() {
        return "generate TypeScript interface";
    }
}