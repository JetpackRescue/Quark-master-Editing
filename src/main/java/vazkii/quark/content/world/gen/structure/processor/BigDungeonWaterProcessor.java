package vazkii.quark.content.world.gen.structure.processor;

import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import vazkii.quark.content.world.gen.structure.BigDungeonStructure;

public class BigDungeonWaterProcessor extends StructureProcessor {
	
    public BigDungeonWaterProcessor() { 
    	// NO-OP
    }
    
    @Override
    public BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, BlockPos otherposidk, BlockInfo p_215194_3_, BlockInfo blockInfo, PlacementSettings placementSettingsIn, Template template) {
    	if(blockInfo.state.getProperties().contains(BlockStateProperties.WATERLOGGED) && blockInfo.state.get(BlockStateProperties.WATERLOGGED))
            return new BlockInfo(blockInfo.pos, blockInfo.state.with(BlockStateProperties.WATERLOGGED, false), blockInfo.nbt);
    	
    	return blockInfo;
    }
    
	@Override
	protected IStructureProcessorType<?> getType() {
		return BigDungeonStructure.WATER_PROCESSOR_TYPE;
	}

}

