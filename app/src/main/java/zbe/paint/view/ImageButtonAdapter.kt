package zbe.paint.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.Spinner
import com.warkiz.tickseekbar.*
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener
import zbe.paint.model.OnAppStateChangedListener
import zbe.paint.R
import zbe.paint.model.AppState
import zbe.paint.model.OnSizeChangedListener

class ImageButtonAdapter(private val context: Context) : BaseAdapter() {

    private val buttons = arrayListOf<ImageButton>()
    var appState = AppState(HashMap(), -1, 1, Color.BLACK, false)
        set(value) {
            field = value
            if (value.buttonPressed in arrayOf(0, 1, 2, 3)) {
                buttons[value.buttonPressed].setBackgroundColor(
                        ResourcesCompat.getColor(context.resources, R.color.lt_gray, null))
            }
            if (value.fill) {
                buttons[4].setImageResource(R.mipmap.fill)
            }
        }
    var onAppStateChangedListener: OnAppStateChangedListener? = null
    private var colorDialog: ChromaDialog = ChromaDialog.Builder()
            .initialColor(Color.BLACK)
            .colorMode(ColorMode.RGB)
            .onColorSelected(object : ColorSelectListener {
                override fun onColorSelected(color: Int) {
                    appState.color = color
                    onAppStateChangedListener?.onAppStateChanged()
                    Log.d("SELECTEDCOLOR", color.toString())
                }
            })
            .create()

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Get buttons
        val sizeButton = inflater.inflate(R.layout.size_button, null) as ImageButton
        val lineButton = inflater.inflate(R.layout.line_button, null) as ImageButton
        val rectButton = inflater.inflate(R.layout.rect_button, null) as ImageButton
        val ovalButton = inflater.inflate(R.layout.oval_button, null) as ImageButton
        val fillButton = inflater.inflate(R.layout.fill_button, null) as ImageButton
        val colorButton = inflater.inflate(R.layout.color_button, null) as ImageButton
        val clearButton = inflater.inflate(R.layout.clear_button, null) as ImageButton

        // Add buttons
        buttons.add(sizeButton)
        buttons.add(lineButton)
        buttons.add(rectButton)
        buttons.add(ovalButton)
        buttons.add(fillButton)
        buttons.add(colorButton)
        buttons.add(clearButton)

        // Add click listeners
        sizeButton.setOnClickListener {
            val dialog = SizeDialog().build(context, appState.size)
            dialog.onSizeChangedListener = object : OnSizeChangedListener {
                override fun onSizeChanged(size: Int) {
                    appState.size = size
                    onAppStateChangedListener?.onAppStateChanged()
                }
            }
            dialog.show((context as FragmentActivity).supportFragmentManager, "SizeDialog")
        }

        lineButton.setOnClickListener {
            updateButton(buttons.indexOf(lineButton))
        }

        rectButton.setOnClickListener {
            updateButton(buttons.indexOf(rectButton))
        }

        ovalButton.setOnClickListener {
            updateButton(buttons.indexOf(ovalButton))
        }

        fillButton.setOnClickListener {
            appState.fill = !appState.fill

            if (appState.fill)
                fillButton.setImageResource(R.mipmap.fill)
            else
                fillButton.setImageResource(R.mipmap.no_fill)

            onAppStateChangedListener?.onAppStateChanged()
        }

        colorButton.setOnClickListener {
            colorDialog = ChromaDialog.Builder()
                    .initialColor(appState.color)
                    .colorMode(ColorMode.RGB)
                    .onColorSelected(object : ColorSelectListener {
                        override fun onColorSelected(color: Int) {
                            appState.color = color
                            onAppStateChangedListener?.onAppStateChanged()
                        }
                    })
                    .create()

            colorDialog.show((context as FragmentActivity).supportFragmentManager, "ChromaDialog")
        }

        clearButton.setOnClickListener {
            if (appState.buttonPressed != -1)
                buttons[appState.buttonPressed].setBackgroundColor(
                        ResourcesCompat.getColor(context.resources, R.color.gray, null))

            appState.buttonPressed = buttons.indexOf(clearButton)
            onAppStateChangedListener?.onAppStateChanged()
        }
    }

    override fun getItem(position: Int): Any = buttons[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = buttons.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
            getItem(position) as ImageButton

    private fun updateButton(buttonPosition: Int) {
        appState.buttonPressed = if (appState.buttonPressed == buttonPosition) {
            // If buttonPosition was already selected
            buttons[buttonPosition].setBackgroundColor(
                    ResourcesCompat.getColor(context.resources, R.color.gray, null))
            -1
        } else { // If buttonPosition was not already selected
            if (appState.buttonPressed != -1)
                buttons[appState.buttonPressed].setBackgroundColor(
                        ResourcesCompat.getColor(context.resources, R.color.gray, null))

            buttons[buttonPosition].setBackgroundColor(
                    ResourcesCompat.getColor(context.resources, R.color.lt_gray, null))
            buttonPosition
        }

        onAppStateChangedListener?.onAppStateChanged()
    }
}