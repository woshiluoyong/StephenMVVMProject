package com.qeeyou.accelerator.overseas.overwall.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.qeeyou.accelerator.overseas.overwall.utils.ToolUtils
import com.qeeyou.accelerator.overseas.overwall.utils.doJudge

/**
 * 主界面连接进度view
 */
class MainConnectProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {


    companion object {
        const val STATE_NULL = 0        //未连接状态
        const val STATE_CONNECTING = 1  //正在连接中状态
        const val STATE_CONNECTED = 2   //连接成功状态
    }

    // 当前状态
    var state: Int = STATE_NULL
        set(value) {
            if (field == value) return
            field = value
            when (value) {
                STATE_CONNECTING -> {
                    anim.start()
                }
                else -> {
                    anim.cancel()
                }
            }
            invalidate()
        }


    var timeMill: Long? = 0
        set(value) {
            field = value
            val hour = field!! / 1000 / 60 / 60
            val minute = field!! / 1000 / 60 % 60
            val second = field!! / 1000 % 60
            msg = ("${{ hour < 10 }.doJudge({ "0$hour" }, { hour })}:${{ minute < 10 }.doJudge({ "0$minute" },
                { minute })}:${{ second < 10 }.doJudge({ "0$second" }, { second })}")
            invalidate()
        }

    // 连接成功状态（STATE_CONNECTED）显示的字符串信息
    var msg: String? = "00:00:00"

    private var firstRectF: RectF? = null   //最外第一层圆环区域
    private var secondRectF: RectF? = null  //第二层圆环区域
    private var thirdRectF: RectF? = null   //第三层圆区域
    private var powerRectF: RectF? = null   // 电源图标区域
    private var roundCircleRectF: RectF? = null // 连接过程中，最中间的转圈圆环区域
    private var boxRectF: RectF? = null //连接成功中间方框区域

    private var viewWidth = 0F  //view宽度
    private var viewHeight = 0F //view高度
    private var centerX: Float = 0f //中心点X
    private var centerY: Float = 0f //中心点Y

    private var initializedParams = false //已经初始化了相关参数

    private val themeColor = Color.parseColor("#637AFF") //主题色

    //第一层圆环颜色
    private val firstCircleColor by lazy { Color.parseColor("#FFFFFF") }

    //第二层圆环颜色
    private val secondCircleColor by lazy { Color.parseColor("#F7F7FC") }

    //第三层圆颜色
    private val thirdCircleColor: Int
        get() = if (state == STATE_NULL) {
            Color.parseColor("#ffffff")
        } else {
            themeColor
        }

    //第一层圆环宽度
    private val firstCircleWidth by lazy { ToolUtils.instance.dp2px(context, 22f).toFloat() }

    //第二层圆环宽度
    private val secondCircleWidth by lazy { ToolUtils.instance.dp2px(context, 25f).toFloat() }

    // 电源图标画笔宽度
    private val powerPaintStrokeWidth by lazy { ToolUtils.instance.dp2px(context, 5f).toFloat() }

    // 电源图标竖线长度
    private val powerLineLen by lazy { ToolUtils.instance.dp2px(context, 20f).toFloat() }

    // 电源图标宽度
    private val powerIconWidth by lazy { ToolUtils.instance.dp2px(context, 40f).toFloat() }

    // 电源竖线起始和终止Y坐标
    private var powerLineStartY = 0F
    private var powerLineEndY = 0F

    // 转圈圆环区域宽度
    private val roundCircleRectFWidth by lazy { ToolUtils.instance.dp2px(context, 48f).toFloat() }

    // 转圈圆环的背景圆半径
    private val roundCircleBgRadius by lazy {
        roundCircleRectFWidth / 2 + roundCircleStrokeWidth / 2
    }

    // 转圈圆环宽度
    private val roundCircleStrokeWidth by lazy { ToolUtils.instance.dp2px(context, 6f).toFloat() }

    // 转圈圆环的旋转角度
    private var roundCircleAngle: Float = 0F
        set(angle) {
            field = angle
            invalidate()
        }

    //连接成功中间方框圆角
    private var boxCorner = ToolUtils.instance.dp2px(context, 8f).toFloat()

    //连接成功中间方框宽度
    private val boxRectFWidth = ToolUtils.instance.dp2px(context, 30f).toFloat()

    // 连接成功中间文本画笔距离上面方框距离
    private val marginTop = ToolUtils.instance.dp2px(context, 30f).toFloat()

    //最外第一层圆环画笔
    private val firstCirclePain: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = firstCircleWidth
            isAntiAlias = true
            isDither = true
            color = firstCircleColor
        }
    }

    //第二层圆环画笔
    private val secondCirclePain: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = secondCircleWidth
            isAntiAlias = true
            isDither = true
            color = secondCircleColor
        }
    }

    //第三层圆画笔
    private val thirdCirclePaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
            isDither = true
            // 设置阴影
            setShadowLayer(
                ToolUtils.instance.dp2px(context, 40f).toFloat(),
                0f,
                ToolUtils.instance.dp2px(context, 10f).toFloat(),
                Color.parseColor("#61637AFF")
            )
        }
    }

    // 电源图标画笔
    private val powerPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = powerPaintStrokeWidth
            isAntiAlias = true
            isDither = true
            color = themeColor
        }
    }

    // 转圈圆环画笔
    private val roundCirclePaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = roundCircleStrokeWidth
            isAntiAlias = true
            isDither = true
            // 转圈圆环渐变色
            shader = SweepGradient(
                centerX, centerY, intArrayOf(
                    Color.parseColor("#00000000"),
                    Color.parseColor("#FFFFFF")
                ), null
            )
        }
    }

    // 转圈圆环的背景圆画笔
    private val roundCircleBgPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            isDither = true
            color = Color.parseColor("#7186FE")
        }
    }

    // 连接成功中间方框画笔
    private val boxPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = ToolUtils.instance.dp2px(context, 4f).toFloat()
            isAntiAlias = true
            isDither = true
            color = Color.parseColor("#FFFFFF")
        }
    }

    // 连接成功中间文本画笔
    private val textPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = ToolUtils.instance.dp2px(context, 1f).toFloat()
            isAntiAlias = true
            isDither = true
            color = Color.parseColor("#E6FFFFFF")
            textSize = ToolUtils.instance.sp2px(context, 14f).toFloat()
            textAlign = Paint.Align.CENTER
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = measuredWidth.toFloat()
        viewHeight = measuredHeight.toFloat()

        //AppLogger.i("view", "---onMeasure---$viewWidth------$viewHeight")
    }

    private fun initParams() {
        if (initializedParams || viewWidth <= 0f || viewHeight <= 0f) {
            return
        }
        //AppLogger.i("view", "---initParams---")
        centerX = viewWidth / 2
        centerY = viewHeight / 2

        var padding = firstCircleWidth / 2
        firstRectF = RectF(padding, padding, viewWidth - padding, viewHeight - padding)
        padding = firstCircleWidth + secondCircleWidth / 2
        secondRectF = RectF(
            padding,
            padding,
            viewWidth - padding,
            viewHeight - padding
        )
        padding = firstCircleWidth + secondCircleWidth
        thirdRectF = RectF(padding, padding, viewWidth - padding, viewHeight - padding)

        // 电源图标
        val powerHalf = powerIconWidth / 2
        powerRectF = RectF(
            centerX - powerHalf,
            centerY - powerHalf,
            centerX + powerHalf,
            centerY + powerHalf
        )
        powerLineStartY = powerRectF!!.top - powerLineLen / 4
        powerLineEndY = powerRectF!!.top + powerLineLen * 3 / 4

        // 旋转的圆环
        val roundCircleHalf = roundCircleRectFWidth / 2
        roundCircleRectF = RectF(
            centerX - roundCircleHalf,
            centerY - roundCircleHalf,
            centerX + roundCircleHalf,
            centerY + roundCircleHalf
        )

        //
        val boxHalf = boxRectFWidth / 2
        boxRectF = RectF(centerX - boxHalf, centerY - boxRectFWidth, centerX + boxHalf, centerY)

        initializedParams = true
    }

    private val anim by lazy {
        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                roundCircleAngle = animation.animatedValue as Float
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        initParams()

        // 绘制最外第一层圆环
        firstRectF?.let {
            canvas.drawArc(it, 0f, 360f, false, firstCirclePain)
        }
        // 绘制最外第二层圆环
        secondRectF?.let {
            canvas.drawArc(it, 0f, 360f, false, secondCirclePain)
        }
        // 绘制最外第三层圆
        thirdRectF?.let {
            thirdCirclePaint.color = thirdCircleColor
            canvas.drawArc(it, 0f, 360f, false, thirdCirclePaint)
        }
        // 根据状态绘制最中间的内容
        when (state) {
            STATE_NULL -> {
                // 绘制电源图标
                powerRectF?.let {
                    canvas.drawArc(it, -60f, 300f, false, powerPaint)
                    canvas.drawLine(
                        centerX,
                        powerLineStartY,
                        centerX,
                        powerLineEndY,
                        powerPaint
                    )
                }
            }
            STATE_CONNECTING -> {
                // 绘制转圈圆环
                roundCircleRectF?.let {
                    canvas.drawCircle(
                        centerX,
                        centerY,
                        roundCircleBgRadius,
                        roundCircleBgPaint
                    )
                    roundCirclePaint.shader.setLocalMatrix(
                        Matrix().apply {
                            setRotate(roundCircleAngle - 90f, centerX, centerY)
                        }
                    )
                    canvas.drawArc(
                        it,
                        roundCircleAngle,
                        180f,
                        false,
                        roundCirclePaint
                    )
                }
            }
            STATE_CONNECTED -> {
                // 绘制文本和方框
                boxRectF?.let {
                    canvas.drawRoundRect(it, boxCorner, boxCorner, boxPaint)
                    if (!msg.isNullOrEmpty()) {
                        canvas.drawText(msg!!, centerX, centerY + marginTop, textPaint)
                    }
                }
            }
        }
    }
}