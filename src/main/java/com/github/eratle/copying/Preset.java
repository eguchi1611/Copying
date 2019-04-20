package com.github.eratle.copying;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class Preset {

	@Getter@Setter
	Map<String, List<IOPath>> presets = new LinkedHashMap<String, List<IOPath>>();

	@Getter@Setter@ToString
	static final class IOPath {

		private String source;
		private String to;
	}
}
