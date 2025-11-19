package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.net4j.util.collection.Pair;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import de.uka.ipd.sdq.identifier.IdentifierPackage;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

public class CompositeDataTypeCorrespondenceConflictResolutionStrategy extends ConflictResolutionStrategy {
	private Map<String, List<String>> dataTypeIDToInnerDeclIDs = new HashMap<>();
	private Map<String, Pair<String, String>> innerDeclIDToInnerDataTypeAndName = new HashMap<>();
	private Map<String, String> pcmIDToIndexID = new HashMap<>();
	private List<EChange> pcmChangeSequence;

	private List<ConcreteClassifier> targetConcreteClassifiers = new ArrayList<>();
	private Resource targetJavaModel;

	public CompositeDataTypeCorrespondenceConflictResolutionStrategy(List<EChange> pcmChangeSequence,
			Resource targetJavaModel) {
		this.targetJavaModel = targetJavaModel;
		this.targetJavaModel.getAllContents().forEachRemaining((e) -> {
			if (e instanceof ConcreteClassifier) {
				targetConcreteClassifiers.add((ConcreteClassifier) e);
			}
		});
		this.pcmChangeSequence = pcmChangeSequence;
		analyseChanges();
	}

	protected void addFoundField(String compositeDataTypeID, String innerDeclID) {
		if (!dataTypeIDToInnerDeclIDs.containsKey(compositeDataTypeID)) {
			dataTypeIDToInnerDeclIDs.put(compositeDataTypeID, new ArrayList<String>());
		}
		dataTypeIDToInnerDeclIDs.get(compositeDataTypeID).add(innerDeclID);
	}

	protected void analyseChanges() {
		for (int i = 0; i < pcmChangeSequence.size() - 3; i++) {
			var currentChange = pcmChangeSequence.get(i);

			var affectedID = ChangeUtil.getAffectedEObjectID(currentChange);
			var affectedFeat = ChangeUtil.getAffectedFeature(currentChange);
			var createdType = ChangeUtil.getCreatedEObjectType(currentChange);
			var deletedType = ChangeUtil.getDeletedEObjectType(currentChange);
			var oldID = ChangeUtil.getOldValueID(currentChange);
			var newID = ChangeUtil.getNewValueID(currentChange);

			if (currentChange instanceof InsertEReference && affectedFeat
					.equals(RepositoryPackage.Literals.COMPOSITE_DATA_TYPE__INNER_DECLARATION_COMPOSITE_DATA_TYPE)) {
				var affectedDataTypeID = affectedID;
				var innerDeclID = affectedID + "/@" + affectedFeat.getName() + "."
						+ ChangeUtil.getIndexOfValue(currentChange);

				addFoundField(affectedDataTypeID, innerDeclID);
			}

			if (currentChange instanceof ReplaceSingleValuedEReference
					&& affectedFeat.equals(RepositoryPackage.Literals.INNER_DECLARATION__DATATYPE_INNER_DECLARATION)) {
				var innerDeclID = affectedID;
				var innerDataTypeID = newID;
				if (!innerDeclIDToInnerDataTypeAndName.containsKey(innerDeclID)) {
					innerDeclIDToInnerDataTypeAndName.put(innerDeclID, new Pair<>(innerDataTypeID, null));
				} else {
					innerDeclIDToInnerDataTypeAndName.get(innerDeclID).setElement1(innerDataTypeID);
				}
			}

			if (currentChange instanceof ReplaceSingleValuedEAttribute
					&& setsNameOfInnerDeclaration((ReplaceSingleValuedEAttribute<?, ?>) currentChange)) {
				var innerDeclID = affectedID;
				var innerDeclName = (String) ChangeUtil.getNewValue(currentChange);
				if (!innerDeclIDToInnerDataTypeAndName.containsKey(innerDeclID)) {
					innerDeclIDToInnerDataTypeAndName.put(innerDeclID, new Pair<>(null, innerDeclName));
				} else {
					innerDeclIDToInnerDataTypeAndName.get(innerDeclID).setElement2(innerDeclName);
				}
			}

			if (currentChange instanceof ReplaceSingleValuedEAttribute
					&& affectedFeat.equals(IdentifierPackage.Literals.IDENTIFIER__ID)) {
				pcmIDToIndexID.put((String) ChangeUtil.getNewValue(currentChange), affectedID);
			}
		}
	}

	private boolean setsNameOfInnerDeclaration(ReplaceSingleValuedEAttribute<?, ?> change) {
		if (!change.getAffectedFeature().equals(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME)) {
			return false;
		}

		var affectedID = change.getAffectedEObjectID();
		var indexFeatSplit = affectedID.split("\\.|@");
		if (indexFeatSplit.length > 1) {
			return indexFeatSplit[indexFeatSplit.length - 2].equals(
					RepositoryPackage.Literals.COMPOSITE_DATA_TYPE__INNER_DECLARATION_COMPOSITE_DATA_TYPE.getName());
		}
		return false;
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		var castedUI = (JavaCorrespondentDecisionUserInteraction) userInteraction;
		var dt = (CompositeDataType) castedUI.getTriggeringPCMelements().get(0);
		var dtID = dt.eResource().getURIFragment(dt);
		var dtInnerDeclIDs = dataTypeIDToInnerDeclIDs.get(pcmIDToIndexID.get(dtID));
		var dtInnerDeclNames = dtInnerDeclIDs != null
				? dtInnerDeclIDs.stream().map((declID) -> innerDeclIDToInnerDataTypeAndName.get(declID))
						.filter((p) -> p != null).map((p) -> p.getElement2()).collect(Collectors.toList())
				: List.of();
		var innerDeclCount = dtInnerDeclIDs != null ? dtInnerDeclIDs.size() : 0;

		for (var javaObj : castedUI.getAffectedJavaElements()) {
			if (javaObj instanceof ConcreteClassifier) {
				var castedJavaObj = (ConcreteClassifier) javaObj;
				var targetJavaObj = targetConcreteClassifiers.stream()
						.filter((cc) -> cc.getQualifiedName().equals(castedJavaObj.getQualifiedName()))
						.filter((cc) -> cc.getClass().equals(castedJavaObj.getClass())).findFirst().orElse(null);

				if (targetJavaObj == null)
					continue;

				// Compare Java Field count to PCM InnerDeclaration count
				if (targetJavaObj.getFields().size() != innerDeclCount) {
					castedUI.removePossibleJavaCorrespondent(javaObj);
				}

				// Check if any Java Field has no corresponding PCM InnerDeclaration. Only by
				// name, since not all PCM DataTypes might exist
				if (dtInnerDeclIDs != null && targetJavaObj.getFields().stream()
						.anyMatch((jf) -> dtInnerDeclNames.stream().noneMatch((dtF) -> dtF.equals(jf.getName())))) {
					castedUI.removePossibleJavaCorrespondent(javaObj);
				}
			}
		}
		if (castedUI.getAffectedJavaElements().size() == 1) {
			reportDesiredCorrespondence(userInteraction,
					new CorrespondenceEntry(dt, castedUI.getAffectedJavaElements().get(0), ""));
		}
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return userInteraction instanceof JavaCorrespondentDecisionUserInteraction
				&& userInteraction.getTriggeringPCMelements().stream().anyMatch((e) -> e instanceof CompositeDataType);
	}
}
