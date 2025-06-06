package dev.emi.emi.search;

import shims.java.net.minecraft.text.Style;

import java.util.function.Function;

public enum QueryType {
	DEFAULT("", 0xffffff, 0xcc3737, 0xff5555, 0xfca955, NameQuery::new, RegexNameQuery::new),
	PINYIN("", 0xffffff, 0xcc3737, 0xff5555, 0xfca955, PinyinQuery::new, RegexPinyinQuery::new),
	MOD("@", 0x5555ff, 0x5555ff, 0x9b5cdb, 0xf22bf2, ModQuery::new, RegexModQuery::new),
	TOOLTIP("$", 0xffff55, 0xffff55, 0xbdf486, 0xf4bd86, TooltipQuery::new, RegexTooltipQuery::new),
	TAG("#", 0x55ff55, 0x55ff55, 0x41eace, 0x0098ea, TagQuery::new, RegexTagQuery::new),
	ITEM_ID("^", 0xff55ee, 0xff55ee, 0xfc74a6, 0xff2676, ItemIDQuery::new, RegexIDQuery::new),
	;

	public final String prefix;
	public final Style color, slashColor, regexColor, escapeColor;
	public final Function<String, Query> queryConstructor, regexQueryConstructor;

	private QueryType(String prefix, int color, int slashColor, int regexColor, int escapeColor,
					  Function<String, Query> queryConstructor, Function<String, Query> regexQueryConstructor) {
		this.prefix = prefix;
		this.color = Style.EMPTY.withColor(color);
		this.slashColor = Style.EMPTY.withColor(slashColor);
		this.regexColor = Style.EMPTY.withColor(regexColor);
		this.escapeColor = Style.EMPTY.withColor(escapeColor);
		this.queryConstructor = queryConstructor;
		this.regexQueryConstructor = regexQueryConstructor;
	}

	public static QueryType fromString(String s) {
		for (int i = QueryType.values().length - 1; i >= 0; i--) {
			QueryType type = QueryType.values()[i];
			if (s.startsWith(type.prefix)) {
				return type;
			}
		}
		return null;
	}
}
