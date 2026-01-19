package tools.cipm.views.measurements;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.framework.views.ChangeableViewSource;
import tools.vitruv.framework.views.impl.AbstractViewType;
import tools.vitruv.framework.views.impl.ModifiableView;
import tools.vitruv.framework.views.util.ResourceCopier;

public abstract class BaseMeasurementsViewType extends AbstractViewType<MeasurementsViewSelector> implements MeasurementsViewType {
	protected BaseMeasurementsViewType(String name) {
		super(name);
	}

	@Override
	public void updateView(ModifiableView view) {
		view.modifyContents((viewResourceSet) -> {
			viewResourceSet.getResources().forEach(Resource::unload);
			viewResourceSet.getResources().clear();
			var viewSources = view.getViewSource().getViewSourceModels();
			viewSources = viewSources.stream().filter(getMeasurementsModelFilter()).collect(Collectors.toSet());
			ResourceCopier.copyViewSourceResources(viewSources, viewResourceSet, (obj) -> true);
		});
	}
	
	protected abstract Predicate<Resource> getMeasurementsModelFilter();

	@Override
	public MeasurementsViewSelector createSelector(ChangeableViewSource viewSource) {
		return new FullMeasurementsViewSelector(this, viewSource);
	}
	
	@Override
	public abstract BaseMeasurementsView createView(MeasurementsViewSelector selector);
}
