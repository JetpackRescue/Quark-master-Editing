from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('unique/block_model_speleothem_small.json', 'assets/{modid}/models/block/{name}_speleothem_small.json'),
	('unique/block_model_speleothem_medium.json', 'assets/{modid}/models/block/{name}_speleothem_medium.json'),
	('unique/block_model_speleothem_big.json', 'assets/{modid}/models/block/{name}_speleothem_big.json'),
	('unique/block_item_speleothem.json', 'assets/{modid}/models/item/{name}_speleothem.json'),
	('unique/blockstate_speleothem.json', 'assets/{modid}/blockstates/{name}_speleothem.json'),
	
	('unique/recipe_speleothem.json', 'data/{modid}/recipes/world/crafting/{name}_speleothem.json'),
	('unique/recipe_speleothem_stonecutter.json', 'data/{modid}/recipes/world/stonecutting/{name}_speleothem_stonecutter.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_speleothem'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Speleothem'
))

import update_drop_tables