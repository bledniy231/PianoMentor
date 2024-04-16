package teachingsolutions.presentation_layer.interfaces

interface ISelectRecyclerViewItemListener<T: IItemUIModel> {
    fun onItemSelected(itemModel: T)
}