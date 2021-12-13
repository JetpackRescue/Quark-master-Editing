package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IConfigElement;

public abstract class AbstractConfigElement implements IConfigElement {

	public final String name;
	public final String comment;
	public final IConfigCategory parent;
	
	public AbstractConfigElement(String name, String comment, IConfigCategory parent) {
		this.name = name;
		this.comment = comment;
		this.parent = parent;
	}
	
	@Override
	public void print(String pad, PrintStream out) {
		String[] lines = comment.split("\n");
		if(lines.length > 0 && !lines[0].isEmpty())
			for(String s : lines)
				out.printf("%s#%s%n", pad, s);		
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public IConfigCategory getParent() {
		return parent;
	}
	
	@Override
	public List<String> getTooltip() {
		String[] lines = comment.split("\n");
		if(lines.length > 0 && (lines.length > 1 || !lines[0].isEmpty())) {
			List<String> tooltip = new LinkedList<>();
			
			for(int i = 0; i < lines.length; i++) {
				String s = lines[i];
				if(i == 0 && s.isEmpty())
					continue;
				
				tooltip.add(s);
			}
			return tooltip;
		}
		
		return null;
	}
	
}
