package com.example.clippingapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.View

class ClippedView @JvmOverloads constructor(
    context:Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    private val path = Path()

    //

    // >> In ClippedView, below the path, add variables for dimensions for a clipping rectangle around the whole set of shapes.
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    // >> Add variables for the inset of a rectangle and the offset of a small rectangle.
    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    // >> Add a variable for the radius of a circle. This is the circle that is drawn inside the rectangle.
    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    // >> Add an offset and a text size for text that is drawn inside the rectangle.
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    // >> Set up the coordinates for two columns.
    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    // >> Add the coordinates for each row, including the final row for the transformed text.
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)
    //

    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    private val rejectRow = rowFour + rectInset + 2*clipRectBottom

    //


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {
            drawBackAndUnclippedRectangle(canvas)
            drawDifferenceClippingExample(canvas)
            drawCircularClippingExample(canvas)
            drawIntersectionClippingExample(canvas)
            drawCombinedClippingExample(canvas)
            drawRoundedRectangleClippingExample(canvas)
            drawOutsideClippingExample(canvas)
            drawSkewedTextExample(canvas)
            drawTranslatedTextExample(canvas)

            drawQuickRejectExample(canvas)
        }

    }
    private fun drawClippedRectangle(canvas: Canvas) {
        // >> Apply a clipping rectangle that constrains to drawing only the square.
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )

        // >> Add code to fill the canvas with white color. Only the region inside the clipping rectangle will be filled!
        canvas.drawColor(Color.WHITE)
        // >> Change the color to red and draw a diagonal line inside the clipping rectangle.
        paint.color = Color.RED
        canvas.drawLine(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom,paint
        )

        // >> Set the color to green and draw a circle inside the clipping rectangle.
        paint.color = Color.GREEN
        canvas.drawCircle(
            circleRadius,clipRectBottom - circleRadius,
            circleRadius,paint
        )

        // >> Set the color to blue and draw text aligned with the right edge of the clipping rectangle. Use Canvas.drawText() to draw text.
        paint.color = Color.BLUE
        // Align the RIGHT side of the text with the origin.
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            context.getString(R.string.clipping),
            clipRectRight,textOffset,paint
        )
    }

    /*
    //>> function sets the canvas background to gray, translates the canvas,
    draws a clipped rectangle, and then restores the canvas to its original state.
    //>> This allows you to isolate the drawing of the clipped rectangle within
     a specific region on the canvas without affecting other elements.
     */
    private fun drawBackAndUnclippedRectangle(canvas: Canvas){
        canvas.drawColor(Color.GRAY)
        /*
        >> saves the current state of the canvas, allowing you to apply transformations,
        and later restore the canvas to its previous state.
         */
        canvas.save()

        /*
        >> It translates (moves) the canvas by the specified distances columnOne (horizontal),
         and rowOne (vertical). This effectively shifts the coordinate system for drawing subsequent shapes.
         */
        canvas.translate(columnOne,rowOne)

        drawClippedRectangle(canvas)

        /*
         //>> After drawing the clipped rectangle and performing any transformations,
         this method restores the canvas to its previous state, effectively undoing the translation applied earlier.
         This ensures that any further drawings are not affected by the previous translation.
         */
        canvas.restore()
    }

    /*
    >> drawDifferenceClippingExample,
    the order of operations is important.
    You set up the clipping region first to define where you want to draw,
    and then you actually perform the drawing (in this case, drawClippedRectangle(canvas))
    within that region.
    >> Depending on the Android API version,
    you use different methods to achieve the same clipping effect, but the overall structure remains the same.
     */

    private fun drawDifferenceClippingExample(canvas: Canvas) {
        canvas.save()
        // >> Move the origin to the right for the next rectangle.
        canvas.translate(columnTwo,rowOne)

        // >> Use the subtraction of two clipping rectangles to create a frame.

        /*
        // >>  "canvas.clipRect" This line starts the process of defining a rectangular clipping region on the canvas.
        // >> By setting up this clipping region,
        any subsequent drawing operations on the canvas will be constrained to
        this specified rectangular area.
        Anything drawn outside this region will be clipped and not visible on the canvas.
        This can be useful for creating visually interesting effects or limiting the drawing area
        for specific elements within a larger canvas.
         */

        canvas.clipRect(
            2  *rectInset,2*  rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset)



        // >> The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)

            /*
                >> Region.Op.DIFFERENCE: This parameter specifies the operation to be performed on the clipping region.
                In this case, it's set to DIFFERENCE, which means that the clipping region
                will be formed by taking the difference between the original canvas content
                and the specified rectangular region.
                Anything within the specified rectangle will be excluded from the canvas content.

                >> By setting up this clipping region with the Region.Op.DIFFERENCE operation,
                you're effectively creating a "hole" in the canvas where anything drawn within the specified rectangle will not be visible.
                 This can be useful for creating complex shapes or effects in your canvas drawing.
             */
            canvas.clipRect(
                4  *rectInset,4*  rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                Region.Op.DIFFERENCE)
        else {
            canvas.clipOutRect(
                4  *rectInset,4*  rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawCircularClippingExample(canvas: Canvas) {

        canvas.save()
        canvas.translate(columnOne, rowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.

        /*
        >> rewind(): This method is called on the Path object.
        When invoked, it clears any previously defined path data within the Path object, making it empty.

        >> Typically, you use path.rewind() when you want to reuse a Path object to create a new path
         or shape from scratch without having to create a new Path object.
         It's a way to reset the path's state so that you can start defining a new path from a clean slate.
         */

        path.rewind()
        path.addCircle(
            circleRadius,clipRectBottom - circleRadius,
            circleRadius,Path.Direction.CCW
        )
        // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
        // API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in
        // API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(path)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawIntersectionClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo,rowTwo)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawCombinedClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
            circleRadius,Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset,Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo,rowThree)
        path.rewind()
        path.addRoundRect(
            rectF,clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    private fun drawOutsideClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne,rowFour)
        canvas.clipRect(2  *rectInset,2*  rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawTranslatedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.GREEN
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        canvas.translate(columnTwo,textRow)
        // Draw text.
        canvas.drawText(context.getString(R.string.translated),
            clipRectLeft,clipRectTop,paint)
        canvas.restore()
    }

    private fun drawSkewedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT
        // Position text.
        canvas.translate(columnTwo, textRow)
        // Apply skew transformation.
        canvas.skew(0.2f, 0.3f)
        canvas.drawText(context.getString(R.string.skewed),
            clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }


    private fun drawQuickRejectExample(canvas: Canvas) {
        val inClipRectangle = RectF(clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2)

        val notInClipRectangle = RectF(RectF(clipRectRight+1,
            clipRectBottom+1,
            clipRectRight * 2,
            clipRectBottom * 2))

        canvas.save()
        canvas.translate(columnOne, rejectRow)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
        if (canvas.quickReject(
                inClipRectangle, Canvas.EdgeType.AA)) {
            canvas.drawColor(Color.WHITE)
        }
        else {
            canvas.drawColor(Color.BLACK)
            canvas.drawRect(inClipRectangle, paint
            )
        }
        canvas.restore()
    }




}
























