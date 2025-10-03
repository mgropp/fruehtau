package io.gropp.fruehtau.ui.map

import org.mapsforge.core.model.LatLong

sealed interface MapCommand {
    @JvmInline value class SetMapPosition(val position: LatLong) : MapCommand
}
