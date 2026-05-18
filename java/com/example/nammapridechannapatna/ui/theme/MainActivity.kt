package com.example.nammapridechannapatna

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Auto open Login screen after 3 seconds
        android.os.Handler(mainLooper).postDelayed({
            val intent = android.content.Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}

// ✅ Custom Logo View - Channapatna Doll Design
class LogoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 🎨 Colors
    private val ochreYellow = Color.parseColor("#F5B81C")
    private val lacquerRed = Color.parseColor("#C62828")
    private val forestGreen = Color.parseColor("#2E7D32")
    private val skinColor = Color.parseColor("#FFE0B2")
    private val darkBrown = Color.parseColor("#5D4037")
    private val white = Color.WHITE

    // Background Yellow Circle
    private val yellowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ochreYellow
        style = Paint.Style.FILL
    }

    // Outer border
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = darkBrown
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    // Doll Body Red
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = lacquerRed
        style = Paint.Style.FILL
    }

    // Green
    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = forestGreen
        style = Paint.Style.FILL
    }

    // White
    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = white
        style = Paint.Style.FILL
    }

    // Skin tone
    private val skinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = skinColor
        style = Paint.Style.FILL
    }

    // Dark for eyes/details
    private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = darkBrown
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val r = minOf(width, height) / 2f - 8f

        // 1️⃣ Yellow Background Circle
        canvas.drawCircle(cx, cy, r, yellowPaint)
        canvas.drawCircle(cx, cy, r, borderPaint)

        // 2️⃣ Doll Body (White rounded shape)
        val bodyPath = Path().apply {
            moveTo(cx - r * 0.30f, cy + r * 0.10f)
            cubicTo(
                cx - r * 0.35f, cy - r * 0.20f,
                cx - r * 0.20f, cy - r * 0.40f,
                cx, cy - r * 0.40f
            )
            cubicTo(
                cx + r * 0.20f, cy - r * 0.40f,
                cx + r * 0.35f, cy - r * 0.20f,
                cx + r * 0.30f, cy + r * 0.10f
            )
            lineTo(cx - r * 0.30f, cy + r * 0.10f)
            close()
        }
        canvas.drawPath(bodyPath, whitePaint)

        // 3️⃣ Doll Head (Skin color circle)
        canvas.drawCircle(cx, cy - r * 0.32f, r * 0.18f, skinPaint)

        // 4️⃣ Hair (Dark)
        val hairPath = Path().apply {
            moveTo(cx - r * 0.18f, cy - r * 0.32f)
            cubicTo(
                cx - r * 0.18f, cy - r * 0.55f,
                cx + r * 0.18f, cy - r * 0.55f,
                cx + r * 0.18f, cy - r * 0.32f
            )
            close()
        }
        canvas.drawPath(hairPath, greenPaint)

        // 5️⃣ Eyes
        canvas.drawCircle(cx - r * 0.06f, cy - r * 0.32f, r * 0.018f, darkPaint)
        canvas.drawCircle(cx + r * 0.06f, cy - r * 0.32f, r * 0.018f, darkPaint)

        // 6️⃣ Smile
        val smilePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lacquerRed
            style = Paint.Style.STROKE
            strokeWidth = 3f
            strokeCap = Paint.Cap.ROUND
        }
        val smilePath = Path().apply {
            moveTo(cx - r * 0.04f, cy - r * 0.24f)
            quadTo(cx, cy - r * 0.21f, cx + r * 0.04f, cy - r * 0.24f)
        }
        canvas.drawPath(smilePath, smilePaint)

        // 7️⃣ Red Saree/Body Wrap
        val sareePath = Path().apply {
            moveTo(cx - r * 0.30f, cy + r * 0.10f)
            lineTo(cx + r * 0.30f, cy + r * 0.10f)
            lineTo(cx + r * 0.40f, cy + r * 0.45f)
            lineTo(cx - r * 0.40f, cy + r * 0.45f)
            close()
        }
        canvas.drawPath(sareePath, redPaint)

        // 8️⃣ Green Base/Stand
        val basePath = Path().apply {
            moveTo(cx - r * 0.45f, cy + r * 0.45f)
            lineTo(cx + r * 0.45f, cy + r * 0.45f)
            quadTo(cx + r * 0.50f, cy + r * 0.60f, cx, cy + r * 0.62f)
            quadTo(cx - r * 0.50f, cy + r * 0.60f, cx - r * 0.45f, cy + r * 0.45f)
            close()
        }
        canvas.drawPath(basePath, greenPaint)

        // 9️⃣ Decorative dots on saree
        canvas.drawCircle(cx - r * 0.15f, cy + r * 0.25f, r * 0.02f, whitePaint)
        canvas.drawCircle(cx + r * 0.15f, cy + r * 0.25f, r * 0.02f, whitePaint)
        canvas.drawCircle(cx, cy + r * 0.32f, r * 0.02f, whitePaint)
    }
}