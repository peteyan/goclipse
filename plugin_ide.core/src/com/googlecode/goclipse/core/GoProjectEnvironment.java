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
package com.googlecode.goclipse.core;

import static melnorme.lang.ide.core.utils.ResourceUtils.loc;
import static melnorme.utilbox.misc.StringUtil.emptyAsNull;
import static melnorme.utilbox.misc.StringUtil.nullAsEmpty;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.googlecode.goclipse.tooling.GoPackageName;
import com.googlecode.goclipse.tooling.env.GoArch;
import com.googlecode.goclipse.tooling.env.GoEnvironment;
import com.googlecode.goclipse.tooling.env.GoEnvironmentConstants;
import com.googlecode.goclipse.tooling.env.GoOs;
import com.googlecode.goclipse.tooling.env.GoPath;
import com.googlecode.goclipse.tooling.env.GoRoot;
import com.googlecode.goclipse.tooling.env.GoWorkspaceLocation;

import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.core.utils.prefs.StringPreference;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;

public class GoProjectEnvironment implements GoEnvironmentConstants {
	
	public static GoRoot getEffectiveGoRoot(IProject project) {
		return new GoRoot(getEffectiveValue_NonNull(GoEnvironmentPrefs.GO_ROOT, project, GOROOT));
	}
	
	public static GoArch getEffectiveGoArch(IProject project) {
		String strValue = emptyAsNull(getEffectiveValue(GoEnvironmentPrefs.GO_ARCH, project, GOARCH));
		return strValue == null ? null : new GoArch(strValue);
	}
	
	public static GoOs getEffectiveGoOs(IProject project) {
		String strValue = emptyAsNull(getEffectiveValue(GoEnvironmentPrefs.GO_OS, project, GOOS));
		return strValue == null ? null : new GoOs(strValue);
	}
	
	public static GoPath getEffectiveGoPath(IProject project) {
		String goPathPref = getEffectiveValue_NonNull(GoEnvironmentPrefs.GO_PATH, project, GOPATH);
		GoPath rawGoPath = new GoPath(goPathPref);
		if(project == null) {
			return rawGoPath;
		}
		
		Location projectLoc = ResourceUtils.getResourceLocation(project);
		if(projectLoc == null) {
			return rawGoPath;
		}
		
		GoWorkspaceLocation goPathEntry = rawGoPath.findGoPathEntryForSourcePath(projectLoc);
		if(goPathEntry != null) {
			// GOPATH already contains project location
			return rawGoPath;
		}
		
		// Implicitly add project location to GOPATH
		ArrayList2<String> newGoPathEntries = new ArrayList2<>(projectLoc.toPathString());
		newGoPathEntries.addAll(rawGoPath.getGoPathEntries());
		return new GoPath(newGoPathEntries);
	}
	
	public static boolean isProjectInsideGoPathSourceFolder(IProject project) throws CoreException {
		GoPath goPath = getEffectiveGoPath(project);
		return isProjectInsideGoPathSourceFolder(project, goPath);
	}
	
	public static boolean isProjectInsideGoPathSourceFolder(IProject project, GoPath goPath) throws CoreException {
		return goPath.findGoPathEntryForSourcePath(ResourceUtils.getProjectLocation(project)) != null;
	}
	
	protected static String getEffectiveValue_NonNull(StringPreference stringPref, IProject project, 
			String envAlternative) {
		return nullAsEmpty(getEffectiveValue(stringPref, project, envAlternative));
	}
	
	protected static String getEffectiveValue(StringPreference stringPref, IProject project, String envAlternative) {
		String prefValue = stringPref.get(project);
		if(prefValue == null || prefValue.isEmpty()) {
			return System.getenv(envAlternative);
		}
		return prefValue;
	}
	
	public static GoEnvironment getGoEnvironmentFromLocation(Location fileLocation) {
		IProject project = ResourceUtils.getProject(fileLocation);
		return getGoEnvironment(project);
	}
	
	/**
	 * @return {@link GoEnvironment} for given project.
	 * @param project - can be null.
	 */
	public static GoEnvironment getGoEnvironment(IProject project) {
		GoRoot goRoot = getEffectiveGoRoot(project);
		GoArch goArch = getEffectiveGoArch(project);
		GoOs goOs = getEffectiveGoOs(project);
		GoPath goPath = getEffectiveGoPath(project);
		return new GoEnvironment(goRoot, goArch, goOs, goPath);
	}

	public static boolean isValid(IProject project) {
		return getGoEnvironment(project).isValid();
	}
	
	public static GoEnvironment getValidatedGoEnvironment(final IProject project) throws CommonException {
		GoEnvironment goEnv = getGoEnvironment(project);
		goEnv.validate();
		return goEnv;
	}
	
	public static ArrayList2<GoPackageName> findSourcePackages(IProject project, GoEnvironment goEnvironment)
			throws CoreException {
		return goEnvironment.getGoPath().findGoSourcePackages(ResourceUtils.getProjectLocation(project));
	}
	
	public static Location getBinFolderLocation(IProject project) throws CommonException {
		GoEnvironment goEnv = getGoEnvironment(project);
		return goEnv.getBinFolderLocationForSubLocation(loc(project.getLocation()));
	}
	
}