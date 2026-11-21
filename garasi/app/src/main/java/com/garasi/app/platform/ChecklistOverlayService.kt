package com.garasi.app.platform

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.garasi.app.MainActivity
import com.garasi.app.R

/**
 * A small floating checklist that stays on top of the marketplace app while posting, with copy
 * buttons for the title, description and price (the clipboard only holds one thing at a time).
 * Needs the "Display over other apps" permission; without it the same checklist is shown as a
 * notification instead.
 */
class ChecklistOverlayService : Service() {
    private var view: View? = null
    private val wm by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) { stopSelf(); return START_NOT_STICKY }
        val data = Data.from(intent ?: return START_NOT_STICKY)
        val type = if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        ServiceCompat.startForeground(this, NOTIF_ID, notification(this, data), type)
        if (canDrawOverlays(this)) show(data)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        view?.let { runCatching { wm.removeView(it) } }
        view = null
        super.onDestroy()
    }

    private fun dp(v: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun show(d: Data) {
        view?.let { runCatching { wm.removeView(it) } }
        val dark = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        val fg = if (dark) 0xFFF1F1F1.toInt() else 0xFF1B1B1B.toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(10), dp(14), dp(12))
            background = GradientDrawable().apply {
                cornerRadius = dp(18).toFloat()
                setColor(if (dark) 0xF02B2B2B.toInt() else 0xF7FFFFFF.toInt())
                setStroke(dp(1), 0x33000000)
            }
            elevation = dp(8).toFloat()
        }
        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val title = TextView(this).apply {
            text = getString(R.string.overlay_title, d.platform)
            setTextColor(fg); textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val toggle = Button(this, null, android.R.attr.borderlessButtonStyle).apply {
            text = getString(R.string.overlay_hide); textSize = 13f
            setOnClickListener {
                body.visibility = if (body.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                text = getString(if (body.visibility == View.VISIBLE) R.string.overlay_hide else R.string.overlay_show)
            }
        }
        val close = Button(this, null, android.R.attr.borderlessButtonStyle).apply {
            text = "✕"; textSize = 15f
            setOnClickListener { stopSelf() }
        }
        header.addView(title); header.addView(toggle); header.addView(close)
        root.addView(header)

        fun step(label: String, copyText: String?, copyLabel: Int) {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            row.addView(CheckBox(this).apply {
                text = label; setTextColor(fg); textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            if (copyText != null) row.addView(Button(this, null, android.R.attr.borderlessButtonStyle).apply {
                text = getString(R.string.overlay_copy); textSize = 13f
                setOnClickListener {
                    Clipboard.copy(this@ChecklistOverlayService, getString(copyLabel), copyText)
                    Toast.makeText(this@ChecklistOverlayService, getString(R.string.copied_x, getString(copyLabel)), Toast.LENGTH_SHORT).show()
                }
            })
            body.addView(row)
        }
        step(getString(R.string.step_paste_title), d.title, R.string.label_title)
        step(getString(R.string.step_paste_description), d.description, R.string.label_description)
        step(getString(R.string.step_attach_photos, d.album), null, R.string.label_title)
        step(getString(R.string.step_set_price, d.price), d.priceDigits, R.string.label_price)
        if (d.weight.isNotBlank()) step(getString(R.string.step_set_weight, d.weight), null, R.string.label_title)
        root.addView(body)

        val params = WindowManager.LayoutParams(
            dp(300), WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply { gravity = Gravity.TOP or Gravity.END; x = dp(8); y = dp(120) }

        // Drag by the header.
        var startX = 0; var startY = 0; var touchX = 0f; var touchY = 0f
        title.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { startX = params.x; startY = params.y; touchX = e.rawX; touchY = e.rawY; true }
                MotionEvent.ACTION_MOVE -> {
                    params.x = startX - (e.rawX - touchX).toInt()
                    params.y = startY + (e.rawY - touchY).toInt()
                    runCatching { wm.updateViewLayout(root, params) }; true
                }
                else -> false
            }
        }
        wm.addView(root, params)
        view = root
    }

    data class Data(
        val platform: String, val title: String, val description: String,
        val album: String, val price: String, val priceDigits: String, val weight: String,
    ) {
        fun toIntent(context: Context) = Intent(context, ChecklistOverlayService::class.java)
            .putExtra("platform", platform).putExtra("title", title).putExtra("description", description)
            .putExtra("album", album).putExtra("price", price).putExtra("priceDigits", priceDigits).putExtra("weight", weight)

        companion object {
            fun from(i: Intent) = Data(
                i.getStringExtra("platform").orEmpty(), i.getStringExtra("title").orEmpty(),
                i.getStringExtra("description").orEmpty(), i.getStringExtra("album").orEmpty(),
                i.getStringExtra("price").orEmpty(), i.getStringExtra("priceDigits").orEmpty(),
                i.getStringExtra("weight").orEmpty(),
            )
        }
    }

    companion object {
        private const val CHANNEL = "checklist"
        private const val NOTIF_ID = 42
        const val ACTION_STOP = "com.garasi.app.STOP_CHECKLIST"

        fun canDrawOverlays(context: Context) = Settings.canDrawOverlays(context)

        fun overlayPermissionIntent(context: Context) =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))

        fun start(context: Context, data: Data) {
            androidx.core.content.ContextCompat.startForegroundService(context, data.toIntent(context))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ChecklistOverlayService::class.java))
        }

        private fun notification(context: Context, d: Data): Notification {
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, context.getString(R.string.notif_channel), NotificationManager.IMPORTANCE_LOW)
            )
            val open = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE,
            )
            val stop = PendingIntent.getService(
                context, 1, Intent(context, ChecklistOverlayService::class.java).setAction(ACTION_STOP),
                PendingIntent.FLAG_IMMUTABLE,
            )
            val text = listOf(
                "1. " + context.getString(R.string.step_paste_title),
                "2. " + context.getString(R.string.step_paste_description),
                "3. " + context.getString(R.string.step_attach_photos, d.album),
                "4. " + context.getString(R.string.step_set_price, d.price),
            ).joinToString("\n")
            return NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.overlay_title, d.platform))
                .setContentText(context.getString(R.string.step_set_price, d.price))
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(open)
                .addAction(0, context.getString(R.string.overlay_done), stop)
                .setOngoing(true)
                .build()
        }
    }
}
