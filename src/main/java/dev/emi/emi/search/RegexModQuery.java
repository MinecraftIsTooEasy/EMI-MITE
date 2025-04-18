package dev.emi.emi.search;

import dev.emi.emi.api.stack.EmiStack;
import moddedmite.emi.util.ModIdentification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexModQuery extends Query {
	private final Pattern pattern;
	
	public RegexModQuery(String name) {
		Pattern p = null;
		try {
			p = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
		}
		catch (Exception e) {
		}
		pattern = p;
	}
	
	@Override
	public boolean matches(EmiStack stack) {
		if (pattern == null) {
			return false;
		}
		String namespace = stack.getId().getResourceDomain();
		String mod = ModIdentification.getMod(stack.getItemStack());
		Matcher m = pattern.matcher(mod);
		return m.find();
	}
}
