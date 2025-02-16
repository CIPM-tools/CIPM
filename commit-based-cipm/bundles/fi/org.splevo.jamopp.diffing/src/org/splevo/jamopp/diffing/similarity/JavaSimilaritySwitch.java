/*******************************************************************************
 * Copyright (c) 2014
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Klatt - initial API and implementation and/or initial documentation
 *    Martin Armbruster - extension for expanded JaMoPP
 *******************************************************************************/
package org.splevo.jamopp.diffing.similarity;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.base.ecore.AbstractComposedSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.AnnotationsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ArraysSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ClassifiersSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.CommonsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ContainersSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ExpressionsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.GenericsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ImportsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.InstantiationsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.LayoutSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.LiteralsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.MembersSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ModifiersSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ModulesSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.OperatorsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ParametersSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.ReferencesSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.StatementsSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.TypesSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.switches.VariablesSimilaritySwitch;

/**
 * Internal switch class to prove element similarity.
 * 
 * <p>
 * The similarity case methods do not need to check for null values. It is
 * assumed that the calling class does a null value check for the elements to
 * compare in advanced, such as done by the SimilarityChecker class.
 * </p>
 * 
 * <p>
 * Check strategy:<br>
 * First all "not-similar"-criteria are checked. If none hits, true will be
 * returned.
 * </p>
 */
public class JavaSimilaritySwitch extends AbstractComposedSimilaritySwitch implements IJavaSimilaritySwitch {
	/**
	 * Constructs an instance with the given request handler and the flag. Adds
	 * default inner switches to the constructed instance.
	 * 
	 * @param srh                    The request handler, to which all incoming
	 *                               {@link ISimilarityRequest} instances will be
	 *                               delegated.
	 * @param checkStatementPosition The flag, which denotes whether this switch
	 *                               should take positions of statements while
	 *                               comparing.
	 */
	public JavaSimilaritySwitch(ISimilarityRequestHandler srh, boolean checkStatementPosition) {
		super(srh);

		addSwitch(new AnnotationsSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new ArraysSimilaritySwitch());
		addSwitch(new ClassifiersSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new CommonsSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new ContainersSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new ExpressionsSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new GenericsSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new ImportsSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new InstantiationsSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new LiteralsSimilaritySwitch(this));
		addSwitch(new MembersSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new ModifiersSimilaritySwitch());
		addSwitch(new OperatorsSimilaritySwitch());
		addSwitch(new ParametersSimilaritySwitch(this));
		addSwitch(new ReferencesSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new StatementsSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new TypesSimilaritySwitch(this, checkStatementPosition));
		addSwitch(new VariablesSimilaritySwitch(this));
		addSwitch(new LayoutSimilaritySwitch());
		addSwitch(new ModulesSimilaritySwitch(this, checkStatementPosition));
	}
}
