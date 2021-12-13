package vazkii.quark.base.client.config.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import vazkii.quark.base.client.config.obj.AbstractStringInputObject;

public class StringInputScreen<T> extends AbstractInputScreen<T> {

	private final AbstractStringInputObject<T> object;
	
	private TextFieldWidget input;
	
	public StringInputScreen(Screen parent, AbstractStringInputObject<T> object) {
		super(parent);
		this.object = object;
	}
	
	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		super.render(mstack, mouseX, mouseY, pticks);
		
		drawCenteredString(mstack, font, new StringTextComponent(object.getGuiDisplayName()).mergeStyle(TextFormatting.BOLD), width / 2, 20, 0xFFFFFF);
		drawCenteredString(mstack, font, I18n.format("quark.gui.config.defaultvalue", object.defaultObj),  width / 2, 30, 0xFFFFFF);
		
		input.render(mstack, mouseX, mouseY, pticks);
	}
	
	@Override
	void onInit() {
		input = new TextFieldWidget(font, width / 2 - 100, 60, 200, 20, new StringTextComponent(""));
		input.setValidator(object::isStringValid);
		input.setMaxStringLength(object.getMaxStringLength());
		input.setResponder(this::update);
		setFocusedDefault(input);
		children.add(input);
	}
	
	@Override
	public void tick() {
		input.tick();
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if(input.mouseClicked(x, y, button))
			return true;
		
		return super.mouseClicked(x, y, button);
	}
	
	@Override
	void setDefault() {
		object.reset(true);
	}
	
	@Override
	void reset() {
		object.reset(false);
	}
	
	@Override
	void update() {
		input.setText(object.getCurrentObj().toString());
		super.update();
	}
	
	private void update(String s) {
		super.update();
		input.setTextColor(errored ? 0xFF0000 : 0xFFFFFF);
	}

	@Override
	T compute() {
		return object.fromString(input.getText());
	}

	@Override
	boolean isErrored() {
		return object.restriction != null && !object.restriction.test(val);
	}

	@Override
	boolean isDirty() {
		return object.wouldBeDirty(val);
	}
	
	@Override
	void commit() {
		object.setCurrentObj(object.fromString(input.getText()));
	}
	
}
