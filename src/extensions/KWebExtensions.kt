package extensions

import io.kweb.dom.attributes.attr
import io.kweb.dom.element.Element
import io.kweb.dom.element.creation.ElementCreator

open class IFrameElement(parent: Element) : Element(parent)
fun ElementCreator<Element>.iframe(attributes: Map<String, Any> = attr) =
    IFrameElement(element("extensions.iframe", attributes))