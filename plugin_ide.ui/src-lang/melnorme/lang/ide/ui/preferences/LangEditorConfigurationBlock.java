/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation (JDT)
 *     DLTK team ? - DLTK modifications 
 *     Bruno Medeiros - Lang rewrite
 *******************************************************************************/
package melnorme.lang.ide.ui.preferences;

import melnorme.lang.ide.ui.ContentAssistConstants;
import melnorme.lang.ide.ui.EditorSettings_Actual.EditorPrefConstants;
import melnorme.lang.ide.ui.preferences.EditorAppearanceColorsComponent.EditorColorItem;
import melnorme.lang.ide.ui.preferences.common.AbstractComponentsPrefPage;
import melnorme.lang.ide.ui.preferences.common.AbstractPreferencesBlock;
import melnorme.util.swt.components.fields.CheckBoxField;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LangEditorConfigurationBlock extends AbstractPreferencesBlock {
	
	public LangEditorConfigurationBlock(AbstractComponentsPrefPage mainPreferencePage) {
		super(mainPreferencePage);
	}
	
	@Override
	public int getPreferredLayoutColumns() {
		return 1;
	}
	
	@Override
	protected void createContents(Composite topControl) {
		createAppearanceGroup(topControl);
	}
	
	protected void createAppearanceGroup(Composite parent) {
		createBooleanField(parent,
			EditorPrefConstants.MATCHING_BRACKETS,
			new CheckBoxField(PreferencesMessages.EditorPreferencePage_matchingBrackets));
		
		createEditorAppearanceColorsComponent(parent);
	}
	
	protected void createEditorAppearanceColorsComponent(Composite parent) {
		EditorColorItem[] editorColorItems = createEditorAppearanceColorEntries();
		if(editorColorItems.length == 0) {
			return;
		}
		EditorAppearanceColorsComponent appearanceItemsCp = new EditorAppearanceColorsComponent(editorColorItems);
		appearanceItemsCp.createComponentInlined(parent);
		addPrefComponent(appearanceItemsCp);
	}
	
	protected EditorColorItem[] createEditorAppearanceColorEntries() {
		return new EditorColorItem[] { 
			new EditorColorItem(
				PreferencesMessages.EditorPreferencePage_matchingBracketsHighlightColor,
				EditorPrefConstants.MATCHING_BRACKETS_COLOR),
			new EditorColorItem(
				PreferencesMessages.EditorPreferencePage_backgroundForMethodParameters,
				ContentAssistConstants.PARAMETERS_BACKGROUND),
			new EditorColorItem(
				PreferencesMessages.EditorPreferencePage_foregroundForMethodParameters,
				ContentAssistConstants.PARAMETERS_FOREGROUND),
			new EditorColorItem(
				PreferencesMessages.EditorPreferencePage_sourceHoverBackgroundColor,
				EditorPrefConstants.SOURCE_HOVER_BACKGROUND_COLOR_rgb.key,
				EditorPrefConstants.SOURCE_HOVER_BACKGROUND_COLOR_UseSystemDefault.key,
				SWT.COLOR_INFO_BACKGROUND),
		};
	}
	
}