/*******************************************************************************
 * Copyright (c) 2014
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian Busch
 *******************************************************************************/

package org.splevo.ui.vpexplorer.explorer;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;
import org.splevo.vpm.VPMUtil;

public class VPExplorer extends CommonNavigator {
	
	/** The logger for this class. */
    private Logger logger = Logger.getLogger(VPExplorer.class);
	
	private File vpmFileLocation = null;
	private ResourceSet resSet = null;

	@Override
	protected Object getInitialInput() {		
		return null;
	}
	
	
	public void displayModel() {
		Job job = new Job("Generate Resource Set for VPExplorer") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Generate Resource Set for VPExplorer", 100);
				
				try {
					logger.debug("Will load VPM");
					VPMUtil.loadVariationPointModel(vpmFileLocation, resSet);
				} catch (IOException e) {
					logger.error("Could not load Variation Point Model");
				}
				
				// TODO Build new model?
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						getCommonViewer().setInput(resSet);
						
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public void setVpmFile(File vpmFileLocation) {
		this.vpmFileLocation = vpmFileLocation;
	}
	
	public void setResourceSet(ResourceSet resSet) {
		this.resSet = resSet;
	}

}
