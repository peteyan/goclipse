/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.dialogs;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import melnorme.lang.ide.ui.preferences.LangProjectBuildConfigurationComponent;
import melnorme.util.swt.SWTFactoryUtil;

public abstract class LangBuildConfigurationPropertyPage extends PropertyPage {
	
	protected LangProjectBuildConfigurationComponent buildOptionsComponent;
	
	public LangBuildConfigurationPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}
	
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		buildOptionsComponent = createProjectBuildConfigComponent(getProject());
	}
	
	protected IProject getProject() {
		IAdaptable adaptable = getElement();
		if(adaptable instanceof IProject) {
			return (IProject) adaptable;
		}
		return (IProject) adaptable.getAdapter(IProject.class);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		IProject project = getProject();
		if(project == null) {
			return SWTFactoryUtil.createLabel(parent, SWT.LEFT, "No project available", null);
		}
		return buildOptionsComponent.createComponent(parent);
	}
	
	/* -----------------  ----------------- */
	
	protected abstract LangProjectBuildConfigurationComponent createProjectBuildConfigComponent(IProject project);
	
	@Override
	public void applyData(Object data) {
		if(data instanceof String) {
			String targetName = (String) data;
			buildOptionsComponent.getBuildTargetField().setFieldValue(targetName);
		}
	}
	
	@Override
	public boolean performOk() {
		return buildOptionsComponent.performOk();
	}
	
	@Override
	protected void performDefaults() {
		buildOptionsComponent.restoreDefaults();
	}
	
}