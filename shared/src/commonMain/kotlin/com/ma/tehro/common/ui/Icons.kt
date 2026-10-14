package com.ma.tehro.common.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser

private const val ICON_VIEWPORT = 960f

const val PLAY_ARROW_PATH_DATA =
    "M320,687L320,273Q320,256 332,244.5Q344,233 360,233Q365,233 370.5,234.5Q376,236 381,239L707,446Q716,452 720.5,461Q725,470 725,480Q725,490 720.5,499Q716,508 707,514L381,721Q376,724 370.5,725.5Q365,727 360,727Q344,727 332,715.5Q320,704 320,687Z"

const val FLAG_PATH_DATA =
    "M280,560L280,800Q280,817 268.5,828.5Q257,840 240,840Q223,840 211.5,828.5Q200,817 200,800L200,200Q200,183 211.5,171.5Q223,160 240,160L527,160Q541,160 552,169Q563,178 566,192L576,240L760,240Q777,240 788.5,251.5Q800,263 800,280L800,600Q800,617 788.5,628.5Q777,640 760,640L553,640Q539,640 528,631Q517,622 514,608L504,560L280,560Z"

fun DrawScope.drawVectorIcon(
    pathData: String,
    center: Offset,
    size: Float,
    color: Color,
) {
    val path = PathParser().parsePathString(pathData).toPath()
    val scaleFactor = size / ICON_VIEWPORT
    val half = size / 2f

    translate(left = center.x - half, top = center.y - half) {
        scale(scale = scaleFactor, pivot = Offset.Zero) {
            drawPath(path = path, color = color)
        }
    }
}