package cipm.consistency.commitintegration.diff.util.pcm;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.CompositeComponent;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.util.RepositorySwitch;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.CollectionIteratorAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

/**
 * A SimilarityChecker for PCM repository models.
 * 
 * @author Martin Armbruster
 */
public class PCMRepositorySimilarityChecker extends SimilarityChecker {
    @Override
    protected Boolean checkSimilarityForResolvedAndSameType(EObject element1, EObject element2,
            boolean checkStatementPosition) {
        return new PCMRepositorySimilaritySwitch(element1).doSwitch(element2);
    }

    class PCMRepositorySimilaritySwitch extends ComposedSwitch<Boolean> {
        private EObject compareElement;

        PCMRepositorySimilaritySwitch(EObject compareElement) {
            this.compareElement = compareElement;
            this.addSwitch(new SimilarityRepositorySwitch());
            this.addSwitch(new SimilaritySeffSwitch());
        }

        class SimilarityRepositorySwitch extends RepositorySwitch<Boolean> {
            @Override
            public Boolean caseRepository(Repository repo1) {
                Repository repo2 = (Repository) compareElement;

                if (!repo1.getEntityName()
                    .equals(repo2.getEntityName())) {
                    return Boolean.FALSE;
                }

                return Boolean.TRUE;
            }

            @Override
            public Boolean caseOperationInterface(OperationInterface opInterface1) {
                OperationInterface opInterface2 = (OperationInterface) compareElement;

                if (!opInterface1.getEntityName()
                    .equals(opInterface2.getEntityName())) {
                    return Boolean.FALSE;
                }

                return PCMRepositorySimilarityChecker.this.areSimilar(opInterface1.getParentInterfaces__Interface(),
                        opInterface2.getParentInterfaces__Interface());
            }

            @Override
            public Boolean caseOperationSignature(OperationSignature sign1) {
                OperationSignature sign2 = (OperationSignature) compareElement;

                if (!sign1.getEntityName()
                    .equals(sign2.getEntityName())) {
                    return Boolean.FALSE;
                }

                var result = PCMRepositorySimilarityChecker.this.areSimilar(sign1.getParameters__OperationSignature(),
                        sign2.getParameters__OperationSignature());

                if (!result) {
                    return Boolean.FALSE;
                }

                return PCMRepositorySimilarityChecker.this.isSimilar(sign1.getReturnType__OperationSignature(),
                        sign2.getReturnType__OperationSignature());
            }

            @Override
            public Boolean caseParameter(Parameter param1) {
                Parameter param2 = (Parameter) compareElement;

                if (!param1.getParameterName()
                    .equals(param2.getParameterName())) {
                    return Boolean.FALSE;
                }

                return PCMRepositorySimilarityChecker.this.isSimilar(param1.getDataType__Parameter(),
                        param2.getDataType__Parameter());
            }

            @Override
            public Boolean casePrimitiveDataType(PrimitiveDataType type1) {
                PrimitiveDataType type2 = (PrimitiveDataType) compareElement;
                return type1.getType() == type2.getType();
            }

            @Override
            public Boolean caseCollectionDataType(CollectionDataType type1) {
                CollectionDataType type2 = (CollectionDataType) compareElement;

                if (!type1.getEntityName()
                    .equals(type2.getEntityName())) {
                    return Boolean.FALSE;
                }

                return PCMRepositorySimilarityChecker.this.isSimilar(type1.getInnerType_CollectionDataType(),
                        type2.getInnerType_CollectionDataType());
            }

            @Override
            public Boolean caseCompositeDataType(CompositeDataType type1) {
                CompositeDataType type2 = (CompositeDataType) compareElement;

                if (!type1.getEntityName()
                    .equals(type2.getEntityName())) {
                    return Boolean.FALSE;
                }

                return PCMRepositorySimilarityChecker.this.areSimilar(type1.getParentType_CompositeDataType(),
                        type2.getParentType_CompositeDataType());
            }

            @Override
            public Boolean caseInnerDeclaration(InnerDeclaration decl1) {
                InnerDeclaration decl2 = (InnerDeclaration) compareElement;

                if (!decl1.getEntityName()
                    .equals(decl2.getEntityName())) {
                    return Boolean.FALSE;
                }

                return PCMRepositorySimilarityChecker.this.isSimilar(decl1.getDatatype_InnerDeclaration(),
                        decl2.getDatatype_InnerDeclaration());
            }

            @Override
            public Boolean caseBasicComponent(BasicComponent com1) {
                BasicComponent com2 = (BasicComponent) compareElement;

                return com1.getEntityName()
                    .equals(com2.getEntityName());
            }

            @Override
            public Boolean caseOperationProvidedRole(OperationProvidedRole opRole1) {
                OperationProvidedRole opRole2 = (OperationProvidedRole) compareElement;

                return PCMRepositorySimilarityChecker.this.isSimilar(
                        opRole1.getProvidedInterface__OperationProvidedRole(),
                        opRole2.getProvidedInterface__OperationProvidedRole());
            }

            @Override
            public Boolean caseOperationRequiredRole(OperationRequiredRole reqRole1) {
                OperationRequiredRole reqRole2 = (OperationRequiredRole) compareElement;

                return PCMRepositorySimilarityChecker.this.isSimilar(
                        reqRole1.getRequiredInterface__OperationRequiredRole(),
                        reqRole2.getRequiredInterface__OperationRequiredRole());
            }

            @Override
            public Boolean caseCompositeComponent(CompositeComponent com1) {
                CompositeComponent com2 = (CompositeComponent) compareElement;

                return com1.getEntityName()
                    .equals(com2.getEntityName());
            }
        }

        class SimilaritySeffSwitch extends SeffSwitch<Boolean> {
            private Boolean checkPositionInContainer(AbstractAction action1, AbstractAction action2) {
                ResourceDemandingBehaviour parent1 = (ResourceDemandingBehaviour) action1.eContainer();
                ResourceDemandingBehaviour parent2 = (ResourceDemandingBehaviour) action2.eContainer();

                return parent1.getSteps_Behaviour()
                    .indexOf(action1) == parent2.getSteps_Behaviour()
                        .indexOf(action2);
            }

            @Override
            public Boolean caseAbstractAction(AbstractAction action1) {
                AbstractAction action2 = (AbstractAction) compareElement;
                return checkPositionInContainer(action1, action2);
            }

            @Override
            public Boolean caseResourceDemandingBehaviour(ResourceDemandingBehaviour behav1) {
                ResourceDemandingBehaviour behav2 = (ResourceDemandingBehaviour) compareElement;
                return PCMRepositorySimilarityChecker.this.areSimilar(behav1.getSteps_Behaviour(),
                        behav2.getSteps_Behaviour());
            }

            @Override
            public Boolean caseResourceDemandingSEFF(ResourceDemandingSEFF seff1) {
                ResourceDemandingSEFF seff2 = (ResourceDemandingSEFF) compareElement;

                return PCMRepositorySimilarityChecker.this.isSimilar(seff1.getDescribedService__SEFF(),
                        seff2.getDescribedService__SEFF());
            }

            @Override
            public Boolean caseCollectionIteratorAction(CollectionIteratorAction action1) {
                CollectionIteratorAction action2 = (CollectionIteratorAction) compareElement;

                var result = PCMRepositorySimilarityChecker.this.isSimilar(
                        action1.getParameter_CollectionIteratorAction(),
                        action2.getParameter_CollectionIteratorAction());

                if (!result) {
                    return Boolean.FALSE;
                }

                return checkPositionInContainer(action1, action2);
            }

            @Override
            public Boolean caseAbstractBranchTransition(AbstractBranchTransition transition1) {
                AbstractBranchTransition transition2 = (AbstractBranchTransition) compareElement;

                BranchAction parent1 = (BranchAction) transition1.eContainer();
                BranchAction parent2 = (BranchAction) transition2.eContainer();

                return parent1.getBranches_Branch()
                    .indexOf(transition1) == parent2.getBranches_Branch()
                        .indexOf(transition2);
            }

            @Override
            public Boolean caseExternalCallAction(ExternalCallAction action1) {
                ExternalCallAction action2 = (ExternalCallAction) compareElement;

                var result = PCMRepositorySimilarityChecker.this.isSimilar(action1.getCalledService_ExternalService(),
                        action2.getCalledService_ExternalService());

                if (!result) {
                    return Boolean.FALSE;
                }

                return checkPositionInContainer(action1, action2);
            }            
        }
            

        /*
         * Because pcm random variables were never matching, I added it here.
         * 
         * Thereby random variables do now always match
         */
        public Boolean casePCMRandomVariable(PCMRandomVariable var1) {
//                PCMRandomVariable var2 = (PCMRandomVariable) compareElement;
            return true;
        }

        @Override
        public Boolean defaultCase(EObject obj) {
            if (obj instanceof PCMRandomVariable) {
                return casePCMRandomVariable((PCMRandomVariable) obj);
            }
            return null;
        }
    }
}
