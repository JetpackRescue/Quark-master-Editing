package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;

public class StructureBlockReplacementHandler {

	public static List<StructureFunction> functions = new ArrayList<>();

	private static ThreadLocal<StructureHolder> structureHolder = new ThreadLocal<>();

	public static BlockState getResultingBlockState(BlockState blockstate) {
		StructureHolder curr = getCurrentSturctureHolder();

		if(curr != null && curr.currentStructure != null)
			for(StructureFunction fun : functions) {
				
				BlockState res = fun.apply(blockstate, curr);
				if(res != null)
					return res;
			}

		return blockstate;
	}

	private static StructureHolder getCurrentSturctureHolder() {
		return structureHolder.get();
	}

	public static void setActiveStructure(Structure<?> structure, List<StructurePiece> components) {
		StructureHolder curr = getCurrentSturctureHolder();
		if(curr == null) {
			curr = new StructureHolder();
			structureHolder.set(curr);
		}

		curr.currentStructure = structure;
		curr.currentComponents = components;
	}

	public static interface StructureFunction extends BiFunction<BlockState, StructureHolder, BlockState> {}

	public static class StructureHolder {
		public Structure<?> currentStructure;
		public List<StructurePiece> currentComponents;
	}

}
