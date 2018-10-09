package com.github.syuchan1005.gitprefix;

import com.google.gson.Gson;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "GitPrefixData", storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)})
public class GitPrefixData implements PersistentStateComponent<GitPrefixData> {
	public String isPathType = "DEFAULT";
	public String gitPrefixPath = "";

	public GitPrefixData() {
	}

	public GitPrefixData(String isPathType, String gitPrefixPath) {
		this.isPathType = isPathType;
		this.gitPrefixPath = gitPrefixPath;
	}

	@Nullable
	public static GitPrefixData getInstance(Project project) {
		return ServiceManager.getService(project, GitPrefixData.class);
	}

	@Nullable
	@Override
	public GitPrefixData getState() {
		return this;
	}

	@Override
	public void loadState(@NotNull GitPrefixData state) {
		XmlSerializerUtil.copyBean(state, this);
	}
}
