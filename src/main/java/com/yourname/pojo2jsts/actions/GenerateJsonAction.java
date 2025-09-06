package com.yourname.pojo2jsts.actions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.yourname.pojo2jsts.generators.JsonGenerator;

public class GenerateJsonAction extends BaseGenerateAction {

    @Override
    protected String generate(PsiClass psiClass, Project project) {
        JsonGenerator generator = new JsonGenerator();
        return generator.generate(psiClass, project);
    }

    @Override
    protected String getSuccessMessage() {
        return "JSON example generated successfully!";
    }
    
    @Override
    protected String getOutputType() {
        return "JSON";
    }
    
    @Override
    protected String getProgressTitle(String className) {
        return "Generating JSON for " + className;
    }
    
    @Override
    protected String getActionDescription() {
        return "generate JSON example";
    }
}