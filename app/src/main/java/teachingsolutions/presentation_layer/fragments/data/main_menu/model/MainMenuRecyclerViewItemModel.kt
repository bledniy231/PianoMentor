package teachingsolutions.presentation_layer.fragments.data.main_menu.model

import teachingsolutions.presentation_layer.interfaces.IItemUIModel

data class MainMenuRecyclerViewItemModel (
    public var iconId: Int,
    public var titleText: String
) : IItemUIModel