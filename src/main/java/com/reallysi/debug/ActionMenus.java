package com.reallysi.debug;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.extensions.Plugin;
import com.reallysi.rsuite.api.extensions.PluginAware;
import com.reallysi.rsuite.api.remoteapi.CallArgumentList;
import com.reallysi.rsuite.api.remoteapi.RemoteApiDefinition;
import com.reallysi.rsuite.api.remoteapi.RemoteApiExecutionContext;
import com.reallysi.rsuite.api.remoteapi.RemoteApiHandler;
import com.reallysi.rsuite.api.remoteapi.RemoteApiResult;
import com.reallysi.rsuite.api.remoteapi.result.PlainTextResult;
import com.reallysi.rsuite.api.rule.Action;
import com.reallysi.rsuite.api.rule.ConfiguredMenuItemRuleSet;
import com.reallysi.rsuite.api.rule.ContextMenuItem;
import com.reallysi.rsuite.api.rule.ContextRule;

public class ActionMenus implements RemoteApiHandler, PluginAware {
	public static JSONObject represent(ContextRule rule) {
		JSONObject obj = new JSONObject();
		obj.put("filter", rule.getFilterType().getInternalName());
		obj.put("rule", rule.getRuleType().getInternalName());
		obj.put("data",  rule.getData());
		return obj;
	}
	public static JSONObject represent(ConfiguredMenuItemRuleSet ruleSet, ContextMenuItem action) {
		JSONObject obj = new JSONObject();
		obj.put("actionName", action.getActionName());
		obj.put("label", action.getLabel());
		String group = (String) action.getPropertyValue("rsuite-group");
		if (group == null) {
			group = (String) action.getPropertyValue("rsuite:group");
		}
		obj.put("id",  action.getId());
		obj.put("group", group);
		obj.put("propertyMap", action.getPropertyMap());
		obj.put("scope", ruleSet.getScope());
		JSONArray rules = new JSONArray();
		obj.put("rules", rules);
		for (ContextRule rule : ruleSet.getRules()) {
			rules.put(represent(rule));
		}
		return obj;
	}
	RemoteApiDefinition definition = null;
	RemoteApiExecutionContext context = null;
	CallArgumentList arguments = null;
	RemoteApiResult result = null;
	Plugin plugin = null;

	@Override
	public RemoteApiResult execute(RemoteApiExecutionContext context, CallArgumentList arguments) throws RSuiteException {
		this.context = context;
		this.arguments = arguments;
		run();
		return this.result;
	}

	@Override
	public void initialize(RemoteApiDefinition definition) {
		this.definition = definition;
	}

	public void run() throws RSuiteException {
		JSONArray actions = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("menu", actions);
		obj.put("order", new JSONArray());
		List<Object> providers = context.getPluginManager().getExtensionProviderList("rsuite.ContextMenu", ConfiguredMenuItemRuleSet.class);
		for (Object provider : providers) {
			if (provider instanceof ConfiguredMenuItemRuleSet) {
				ConfiguredMenuItemRuleSet ruleSet = (ConfiguredMenuItemRuleSet) provider;
				for (Action action : ruleSet.getActions()) {
					actions.put(represent(ruleSet, (ContextMenuItem) action));
				}
			}
		}
		PlainTextResult ptr = new PlainTextResult(actions.toString(4));
		ptr.setContentType("application/json");
		this.result = ptr;
	}

	@Override
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

}
