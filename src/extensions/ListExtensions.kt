package extensions

import models.reddit.CondensedVideoItem

fun List<CondensedVideoItem>.returnNowPlayingListTuple(element: CondensedVideoItem): List<CondensedVideoItem?> {
    val elementIndex = indexOf(element)
    //Check the floor
    if (elementIndex == 0) {
        return listOf(null, this[0], this[1])
    }
    //Check the ceiling
    val nextVideoIndex = elementIndex + 2
    if(nextVideoIndex == size){
        return listOf(this[size-3], this[size-2], this[size-1])
    }
    //Take one video from either side of the current element.
    return subList(elementIndex - 1, nextVideoIndex)
}