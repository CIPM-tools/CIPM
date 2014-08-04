/*******************************************************************************
 * Copyright (c) 2014
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Kojic - initial API and implementation and initial documentation
 *******************************************************************************/
package org.splevo.jamopp.refactoring.java.ifelse.optor;

import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.expressions.ConditionalAndExpression;
import org.emftext.language.java.expressions.ExpressionsFactory;
import org.emftext.language.java.expressions.NestedExpression;
import org.emftext.language.java.imports.ClassifierImport;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Condition;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsFactory;
import org.splevo.jamopp.refactoring.util.RefactoringUtil;
import org.splevo.jamopp.refactoring.util.SPLConfigurationUtil;
import org.splevo.jamopp.vpm.software.JaMoPPSoftwareElement;
import org.splevo.refactoring.VariabilityRefactoring;
import org.splevo.vpm.realization.RealizationFactory;
import org.splevo.vpm.realization.VariabilityMechanism;
import org.splevo.vpm.variability.Variant;
import org.splevo.vpm.variability.VariationPoint;

/**
 * Refactors variable else-statements.
 */
public class IfElseStaticConfigClassConditionOPTOR implements VariabilityRefactoring {

    private static final String REFACTORING_NAME = "IF-Else with Static Configuration Class (OPTOR): Condition";
    private static final String REFACTORING_ID = "org.splevo.jamopp.refactoring.java.ifelse.optor.IfElseStaticConfigClassConditionOPTOR";

    @Override
    public VariabilityMechanism getVariabilityMechanism() {
        VariabilityMechanism variabilityMechanism = RealizationFactory.eINSTANCE.createVariabilityMechanism();
        variabilityMechanism.setName(REFACTORING_NAME);
        variabilityMechanism.setRefactoringID(REFACTORING_ID);
        return variabilityMechanism;
    }

    @Override
    public ResourceSet refactor(VariationPoint variationPoint, Map<String, String> refactoringOptions) {
        Condition vpLocation = (Condition) ((JaMoPPSoftwareElement) variationPoint.getLocation()).getJamoppElement();

        ClassifierImport splConfImport = SPLConfigurationUtil.getSPLConfigClassImport();
        if (!RefactoringUtil.containsImport(vpLocation.getContainingCompilationUnit(), splConfImport)) {
            vpLocation.getContainingCompilationUnit().getImports().add(splConfImport);
        }

        Condition previousCond = null;
        Block elseBlock = StatementsFactory.eINSTANCE.createBlock();

        for (Variant variant : variationPoint.getVariants()) {
            Statement implementingElement = (Statement) ((JaMoPPSoftwareElement) variant.getImplementingElements().get(
                    0)).getJamoppElement();

            if (!variant.getLeading()) {
                implementingElement = EcoreUtil.copy(implementingElement);
            }

            String groupId = variationPoint.getGroup().getId();
            String variantId = variant.getId();

            if (implementingElement instanceof Condition) {
                Condition currentCondition = (Condition) implementingElement;

                if (previousCond == null && !variant.getLeading()) {
                    vpLocation.setElseStatement(currentCondition);
                }

                while (true) {
                    appendSPLConfigCheck(groupId, variantId, currentCondition);

                    if (previousCond != null) {
                        previousCond.setElseStatement(currentCondition);
                    }

                    if (currentCondition.getElseStatement() == null) {
                        previousCond = currentCondition;
                        break;
                    } else if (currentCondition.getElseStatement() instanceof Condition) {
                        previousCond = currentCondition;
                        currentCondition = (Condition) currentCondition.getElseStatement();
                    } else {
                        Condition condition = createVariabilityIfWithStatement(groupId, variantId,
                                currentCondition.getElseStatement());
                        currentCondition.setElseStatement(condition);
                        previousCond = condition;
                        break;
                    }
                }
            } else {
                RefactoringUtil.assignInitialValueAndRemoveFinalForReferencedLocalVariables(implementingElement);
                Condition condition = createVariabilityIfWithStatement(groupId, variantId, implementingElement);
                elseBlock.getStatements().add(condition);
            }
        }
        if (elseBlock.getStatements().size() > 0) {
            if (previousCond != null) {
                previousCond.setElseStatement(elseBlock);
            } else {
                vpLocation.setElseStatement(elseBlock);
            }
        }

        return RefactoringUtil.wrapInNewResourceSet(vpLocation);
    }

    private void appendSPLConfigCheck(String groupId, String variantId, Condition currentCondition) {
        IdentifierReference splConfigCheck = SPLConfigurationUtil.generateConfigMatchingExpression(variantId, groupId);

        NestedExpression nestedExpression = ExpressionsFactory.eINSTANCE.createNestedExpression();
        nestedExpression.setExpression(currentCondition.getCondition());

        ConditionalAndExpression newExpression = ExpressionsFactory.eINSTANCE.createConditionalAndExpression();
        newExpression.getChildren().add(splConfigCheck);
        newExpression.getChildren().add(nestedExpression);

        currentCondition.setCondition(newExpression);
    }

    private Condition createVariabilityIfWithStatement(String groupId, String variantId, Statement statement) {
        Condition condition = RefactoringUtil.createVariabilityCondition(variantId, groupId);
        ((Block) condition.getStatement()).getStatements().add(statement);
        return condition;
    }

    @Override
    public boolean canBeAppliedTo(VariationPoint variationPoint) {
        Commentable vpLocation = ((JaMoPPSoftwareElement) variationPoint.getLocation()).getJamoppElement();
        boolean correctLocation = vpLocation instanceof Condition;
        boolean allImplementingElementsAreStatements = RefactoringUtil.allImplementingElementsOfType(variationPoint,
                Statement.class);

        return correctLocation && allImplementingElementsAreStatements;
    }

    @Override
    public String getId() {
        return REFACTORING_ID;
    }

}
