package com.github.anupdey99.stepindicatorlib

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.github.anupdey99.stepindicatorlib.R
import kotlin.math.abs
import kotlin.math.roundToInt

class StepIndicator : View {

    private val DEFAULT_STEP_RADIUS = 12 //DP
    private val DEFAULT_STOKE_WIDTH = 6 //DP
    private val DEFAULT_STEP_COUNT = 10 //DP
    private val DEFAULT_BACKGROUND_COLOR: Int = R.color.stepper_line_color
    private val DEFAULT_STEP_COLOR: Int = R.color.stepper_completed_step
    private val DEFAULT_CURRENT_STEP_COLOR: Int = R.color.stepper_current_step
    private val DEFAULT_INACTIVE_TITLE: Int = R.color.stepper_text_color_inactive
    private val DEFAULT_TEXT_COLOR: Int = R.color.stepper_text_color
    private val DEFAULT_SECONDARY_TEXT_COLOR: Int = R.color.stepper_text_color_secondary
    val DEFAULT_LINE_HEIGHT = 6.0f
    val DEFAULT_STROKE_ALPHA = 255
    private val DEFAULT_TITLE_SIZE = 12

    private var titles: Array<String> = arrayOf()

    private var radius = 0
    private var pageStrokeAlpha = 0
    private var pageTitleId = 0
    private val isTitleClickable = false
    private var pageActiveTitleColor = 0
    private var pageInActiveTitleColor = 0
    private var titleTextSize = 0f
    private var defaultTitleSize = 0f
    private var mLineHeight = 0f
    private var strokeWidth = 0
    private var currentStepPosition = 0
    private var stepsCount = 2
    private var viewBackgroundColor = 0
    private var stepColor = 0
    private var currentColor = 0
    private var textColor = 0
    private var secondaryTextColor = 0

    private var centerY = 0
    private var startX = 0
    private var endX = 0
    private var stepDistance = 0
    private val offset = 0f
    private val offsetPixel = 0
    private val pagerScrollState = 0

    private lateinit var paint: Paint
    private lateinit var pStoke: Paint
    private lateinit var pText: Paint
    private lateinit var tText: Paint
    private val titleSize = 0
    private val textBounds = Rect()
    private val hsvCurrent = FloatArray(3)
    private val hsvBG = FloatArray(3)
    private val hsvProgress = FloatArray(3)
    private val clickable = true
    private val withViewpager = false
    private val disablePageChange = false

    constructor(context: Context): super(context) {
        init(context, null)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes){
        init(context, attrs)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        initAttributes(context, attributeSet)
        paint = Paint()
        pStoke = Paint()
        pText = Paint()
        tText = Paint()
        defaultTitleSize = radius * 1.2f

        paint.setColor(stepColor)
        paint.setFlags(Paint.ANTI_ALIAS_FLAG)
        paint.setStrokeWidth(mLineHeight)
        pStoke.setColor(stepColor)
        pStoke.setStrokeWidth(strokeWidth.toFloat())
        pStoke.setStyle(Paint.Style.STROKE)
        pStoke.setFlags(Paint.ANTI_ALIAS_FLAG)

        tText.setTextSize(titleTextSize)
        tText.setColor(pageInActiveTitleColor)
        tText.setTextAlign(Paint.Align.CENTER)
        tText.setFlags(Paint.ANTI_ALIAS_FLAG)
        tText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
        pText.setColor(textColor)
        pText.setTextSize(radius * 1.2f)
        pText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        pText.setTextAlign(Paint.Align.CENTER)
        pText.setFlags(Paint.ANTI_ALIAS_FLAG)
        minimumHeight = radius * 7
        Color.colorToHSV(currentColor, hsvCurrent)
        Color.colorToHSV(viewBackgroundColor, hsvBG)
        Color.colorToHSV(stepColor, hsvProgress)
        //animateView(tText, currentColor, currentColor);
        initAnimation()
        invalidate()
    }

    private fun initAttributes(context: Context, attributeSet: AttributeSet?) {

        /*titleTextSize =  dp2px(DEFAULT_TITLE_SIZE.toFloat())
        radius = dp2px(DEFAULT_STEP_RADIUS.toFloat()).toInt()
        strokeWidth = dp2px(DEFAULT_STOKE_WIDTH.toFloat()).toInt()
        stepsCount = DEFAULT_STEP_COUNT
        mLineHeight = DEFAULT_LINE_HEIGHT
        stepColor = ContextCompat.getColor(context, DEFAULT_STEP_COLOR)
        currentColor = ContextCompat.getColor(context, DEFAULT_CURRENT_STEP_COLOR)
        viewBackgroundColor = ContextCompat.getColor(context, DEFAULT_BACKGROUND_COLOR)
        textColor = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
        secondaryTextColor = ContextCompat.getColor(context, DEFAULT_SECONDARY_TEXT_COLOR)
        pageInActiveTitleColor = ContextCompat.getColor(context, DEFAULT_INACTIVE_TITLE)
        pageActiveTitleColor = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
        pageTitleId = NO_ID
        pageStrokeAlpha = DEFAULT_STROKE_ALPHA*/

        val attr = context.obtainStyledAttributes(attributeSet, R.styleable.StepIndicator, 0, 0) ?: return
        try {
            titleTextSize = attr.getDimension(R.styleable.StepIndicator_siTitleTextSize, dp2px(DEFAULT_TITLE_SIZE.toFloat()))
            pageStrokeAlpha = attr.getInteger(R.styleable.StepIndicator_siPgStrokeAlpha, DEFAULT_STROKE_ALPHA)
            radius = attr.getDimension(R.styleable.StepIndicator_siRadius, dp2px(DEFAULT_STEP_RADIUS.toFloat())).toInt()
            strokeWidth = attr.getDimension(R.styleable.StepIndicator_siStrokeWidth, dp2px(DEFAULT_STOKE_WIDTH.toFloat())).toInt()
            stepsCount = attr.getInt(R.styleable.StepIndicator_siStepCount, DEFAULT_STEP_COUNT)
            mLineHeight = attr.getDimension(R.styleable.StepIndicator_siPgLineHeight, DEFAULT_LINE_HEIGHT)
            stepColor = attr.getColor(R.styleable.StepIndicator_siStepColor, ContextCompat.getColor(context, DEFAULT_STEP_COLOR))
            currentColor = attr.getColor(R.styleable.StepIndicator_siCurrentStepColor, ContextCompat.getColor(context, DEFAULT_CURRENT_STEP_COLOR))
            viewBackgroundColor = attr.getColor(R.styleable.StepIndicator_siBackgroundColor, ContextCompat.getColor(context, DEFAULT_BACKGROUND_COLOR))
            textColor = attr.getColor(R.styleable.StepIndicator_siTextColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR))
            secondaryTextColor = attr.getColor(R.styleable.StepIndicator_siSecondaryTextColor, ContextCompat.getColor(context, DEFAULT_SECONDARY_TEXT_COLOR))
            pageInActiveTitleColor = attr.getColor(R.styleable.StepIndicator_siInActiveTitleColor, ContextCompat.getColor(context, DEFAULT_INACTIVE_TITLE))
            pageActiveTitleColor = attr.getColor(R.styleable.StepIndicator_siActiveTitleColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR))
            pageTitleId = attr.getResourceId(R.styleable.StepIndicator_siTitles, NO_ID)
        } finally {
            attr.recycle()
        }
    }

    private fun dp2px(dp: Float): Float {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt().toFloat()
    }

    fun getStepsCount(): Int {
        return stepsCount
    }

    fun setStepsCount(stepsCount: Int) {
        this.stepsCount = stepsCount
        calculateStepDistance()
        invalidate()
    }

    fun getCurrentStepPosition(): Int {
        return currentStepPosition
    }

    fun setCurrentStepPosition(currentStepPosition: Int) {
        this.currentStepPosition = currentStepPosition
        invalidate()
    }

    private fun initAnimation() {
        val animation = AlphaAnimation(0f, 1f)
        animation.duration = 7500L
        animation.interpolator = LinearInterpolator()
        //startAnimation(animation);
    }

    override fun onDraw(canvas: Canvas?) {
        if (stepsCount <= 1) {
            visibility = GONE
            return
        }
        if (canvas == null) return
        super.onDraw(canvas)
        //Timber.d("viewDebug onDraw called")

        var pointX = startX
        val pointOffset: Int
        /** draw Line  */
        for (i in 0 until stepsCount - 1) {
            if (i < currentStepPosition) {
                paint.color = stepColor
                canvas.drawLine(pointX.toFloat(), centerY.toFloat(), (pointX + stepDistance).toFloat(), centerY.toFloat(), paint)
            } else if (i == currentStepPosition) {
                paint.color = viewBackgroundColor
                canvas.drawLine(pointX.toFloat(), centerY.toFloat(), (pointX + stepDistance).toFloat(), centerY.toFloat(), paint)
            } else {
                paint.color = viewBackgroundColor
                canvas.drawLine(pointX.toFloat(), centerY.toFloat(), (pointX + stepDistance).toFloat(), centerY.toFloat(), paint)
            }
            pointX += stepDistance
        }

        /**draw progress Line   */
        if (offsetPixel != 0 && pagerScrollState == 1) {
            pointOffset = startX + currentStepPosition * stepDistance
            val drawOffset = pointOffset + offsetPixel
            if (drawOffset in startX..endX) {
                if (offsetPixel < 0) {
                    paint.color = viewBackgroundColor
                } else {
                    paint.color = stepColor
                }
                canvas!!.drawLine(pointOffset.toFloat(), centerY.toFloat(), drawOffset.toFloat(), centerY.toFloat(), paint)
            }
        }

        /**draw Circle  */
        pointX = startX
        for (i in 0 until stepsCount) {
            if (i < currentStepPosition) {
                //draw previous step
                paint.color = stepColor
                canvas!!.drawCircle(pointX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)

                //draw transition
                if (i == currentStepPosition - 1 && offsetPixel < 0 && pagerScrollState == 1) {
                    pStoke.alpha = pageStrokeAlpha
                    pStoke.strokeWidth = (strokeWidth - Math.round(strokeWidth * offset)).toFloat()
                    canvas.drawCircle(pointX.toFloat(), centerY.toFloat(), radius.toFloat(), pStoke)
                }
                pText.color = secondaryTextColor
                tText.color = pageInActiveTitleColor
                animateView(tText, pageActiveTitleColor, pageInActiveTitleColor, canvas)
            } else if (i == currentStepPosition) {
                //draw current step
                if (offsetPixel == 0 || pagerScrollState == 0) {
                    //set stroke default
                    paint.color = currentColor
                    pStoke.strokeWidth = Math.round(strokeWidth.toFloat()).toFloat()
                    pStoke.alpha = pageStrokeAlpha
                } else if (offsetPixel < 0) {
                    pStoke.strokeWidth = Math.round(strokeWidth * offset).toFloat()
                    pStoke.alpha = Math.round(offset * 11f)
                    paint.color = getColorToBG(offset)
                } else {
                    //set stroke transition
                    paint.color = getColorToProgress(offset)
                    pStoke.strokeWidth = (strokeWidth - Math.round(strokeWidth * offset)).toFloat()
                    pStoke.alpha = 255 - Math.round(offset * pageStrokeAlpha)
                }
                canvas!!.drawCircle(pointX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)
                canvas.drawCircle(pointX.toFloat(), centerY.toFloat(), radius.toFloat(), pStoke)
                pText.color = textColor
                tText.color = pageActiveTitleColor
                animateView(tText, pageInActiveTitleColor, pageActiveTitleColor, canvas)
            } else {
                //draw next step
                paint.color = viewBackgroundColor
                canvas!!.drawCircle(pointX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)
                pText.color = secondaryTextColor
                tText.color = pageInActiveTitleColor
                animateView(tText, pageActiveTitleColor, pageInActiveTitleColor, canvas)

                //draw transition
                if (i == currentStepPosition + 1 && offsetPixel > 0 && pagerScrollState == 1) {
                    pStoke.strokeWidth = (strokeWidth * offset).roundToInt().toFloat()
                    pStoke.alpha = (offset * pageStrokeAlpha).roundToInt()
                    canvas.drawCircle(pointX.toFloat(), centerY.toFloat(), radius.toFloat(), pStoke)
                }
            }
            //Draw title text
            if (pageTitleId != NO_ID) {
                //titles = context.resources.getStringArray(pageTitleId)
                //Draw titles
                //drawTextBottom(canvas, tText, titles[i], pointX.toFloat(), height - titleTextSize)
            }
            drawTextCentred(canvas, pText, (i + 1).toString(), pointX.toFloat(), centerY.toFloat())
            pointX += stepDistance
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Timber.d("viewDebug onMeasure($widthMeasureSpec, $heightMeasureSpec)")
        calculateStepDistance()
        //Timber.d("viewDebug onMeasure stepsCount $stepsCount stepDistance $stepDistance")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //Timber.d("viewDebug onSizeChanged($w, $h, $oldw, $oldh)")
        calculateStepDistance()
        invalidate()
    }

    private fun calculateStepDistance() {
        centerY = height / 2
        startX = radius * 2
        endX = width - radius * 2
        stepDistance = (endX - startX) / (stepsCount - 1)
        //Timber.d("viewDebug onSizeChanged stepsCount $stepsCount stepDistance $stepDistance")
    }

    private fun animateView(target: Paint, @ColorInt defaultColor: Int, @ColorInt toColor: Int, canvas: Canvas){
        val animator = ObjectAnimator.ofObject(target,"color", ArgbEvaluator(),   toColor, defaultColor)
        animator.duration = 2000
        animator.addUpdateListener { animation ->
            val animFrac = animation?.animatedFraction
            tText.setColor(Color.BLACK)
            invalidate()
            // canvas.translate(0, 50)
        }
        animator.start()
    }

    private fun drawTextCentred(canvas: Canvas, paint: Paint, text: String, cx: Float, cy: Float) {
        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint)
    }

    private fun drawTextBottom(canvas: Canvas, paint: Paint, text: String, cx: Float, cy: Float) {
        paint.getTextBounds(text, 0, text.length, textBounds)
        //val path = Path()
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint)
    }


    private fun getColorToBG(offset1: Float): Int {
        var offset = offset1
        offset = abs(offset)
        val hsv = FloatArray(3)
        hsv[0] = hsvBG[0] + (hsvCurrent[0] - hsvBG[0]) * offset
        hsv[1] = hsvBG[1] + (hsvCurrent[1] - hsvBG[1]) * offset
        hsv[2] = hsvBG[2] + (hsvCurrent[2] - hsvBG[2]) * offset
        return Color.HSVToColor(hsv)
    }

    private fun getColorToProgress(offset: Float): Int {
        var offset = offset
        offset = Math.abs(offset)
        val hsv = FloatArray(3)
        hsv[0] = hsvCurrent[0] + (hsvProgress[0] - hsvCurrent[0]) * offset
        hsv[1] = hsvCurrent[1] + (hsvProgress[1] - hsvCurrent[1]) * offset
        hsv[2] = hsvCurrent[2] + (hsvProgress[2] - hsvCurrent[2]) * offset
        return Color.HSVToColor(hsv)
    }

}