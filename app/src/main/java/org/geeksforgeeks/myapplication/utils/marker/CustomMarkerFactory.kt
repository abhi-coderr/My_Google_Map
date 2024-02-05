package org.geeksforgeeks.myapplication.utils.marker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import org.geeksforgeeks.myapplication.R
import org.geeksforgeeks.myapplication.databinding.CurrentLocationMarkerLayoutBinding
import org.geeksforgeeks.myapplication.databinding.CustomMarkerLayoutBinding


/**
 * Description: This custom marker class is used for getting custom marker bitmap as per the provided type and text.
 *
 * @author Ankit Mishra
 * @since 14/06/23
 */
class CustomMarkerFactory(private val context: Context) {
    enum class Type {
        Institute, Artist
    }

    fun getMarkerBitmap(text: String, type: Type): Bitmap? {
        // Inflate the custom marker layout
        val markerBinding = DataBindingUtil.inflate<CustomMarkerLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.custom_marker_layout,
            null,
            false
        )
        markerBinding.apply {
            textTv.text = text
            leftExtensionPadding.isVisible = type == Type.Institute
            rightExtensionPadding.isVisible = type == Type.Artist
            markerCl.setBackgroundWithCurrentPadding(
                when (type) {
                    Type.Institute -> R.drawable.ic_marker_institute
                    Type.Artist    -> R.drawable.ic_marker_institute
                }
            )
        }

        return getBitmapFromView(markerBinding.root)
    }

    fun View.setBackgroundWithCurrentPadding(@DrawableRes drawableResource: Int) {
        val pL: Int = paddingLeft
        val pT: Int = paddingTop
        val pR: Int = paddingRight
        val pB: Int = paddingBottom

        background = ContextCompat.getDrawable(context, drawableResource)
        setPadding(pL, pT, pR, pB)
    }

    @Suppress("DEPRECATION")
    fun getBitmapFromView(view: View): Bitmap? {
        return elseNull {
            view.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            view.layout(
                0,
                0,
                view.measuredWidth,
                view.measuredHeight
            )
            view.buildDrawingCache()
            val returnedBitmap = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(returnedBitmap)
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
            val drawable = view.background
            drawable?.draw(canvas)
            view.draw(canvas)
            returnedBitmap
        }
    }

    fun <T> elseNull(onExecution: () -> T) = try {
        onExecution()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun getCurrentLocationMarker(userImage: Bitmap? = null) = getBitmapFromView(
        DataBindingUtil.inflate<CurrentLocationMarkerLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.current_location_marker_layout,
            null,
            false
        ).apply {
            if (userImage != null) {
                imageView.setImageBitmap(userImage)
            } else {
                imageView.setImageResource(R.color.quantum_googgreen)
            }

        }.root
    )
}