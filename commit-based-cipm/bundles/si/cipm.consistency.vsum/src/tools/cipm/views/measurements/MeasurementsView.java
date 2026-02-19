package tools.cipm.views.measurements;

import tools.vitruv.framework.views.CommittableView;

public interface MeasurementsView extends CommittableView {
	@Override
	MeasurementsViewType getViewType();

	void addMeasurement(Object measurement);

	/**
	 * Clear all records from the current (latest) block in the VSUM model.
	 * Call this after committing to free memory. The removal changes won't
	 * trigger insertion-based reactions.
	 *
	 * @return number of records cleared
	 */
	int clearCurrentBlock();

	/**
	 * Start a new empty block in the measurements repository.
	 * Call this after clearing the current block so subsequent
	 * {@link #addMeasurement} calls go into a fresh block.
	 * The previous (cleared) block stays in the model as a lightweight
	 * placeholder, preserving the block structure.
	 */
	void startNewBlock();

	/**
	 * Replace the current (cleared) block with an EMF proxy that references
	 * the persisted block file on disk. This turns the empty block into an
	 * {@code href} reference, so the VSUM's measurements model contains
	 * cross-references to the actual block data files.
	 *
	 * <p>Call this after {@link #clearCurrentBlock()} and persisting the
	 * block to disk, but before {@link #startNewBlock()}.</p>
	 *
	 * @param blockFileAbsolutePath absolute path to the persisted block file
	 */
	void replaceCurrentBlockWithProxy(String blockFileAbsolutePath);
}
