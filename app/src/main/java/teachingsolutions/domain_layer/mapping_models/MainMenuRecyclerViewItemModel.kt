package teachingsolutions.domain_layer.mapping_models

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

data class MainMenuRecyclerViewItemModel (
    public var iconId: Int,
    public var titleText: String
) : IItemUIModel