package org.tannakaken.yourtime

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.Button


/**
 * Created by kensaku on 2017/06/03.
 */
class TimeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_list)
        val tRecyclerView = findViewById(R.id.time_list_view) as RecyclerView

        tRecyclerView.adapter = TimeAdapter(tRecyclerView) {
            aView : View ->
            ClockList.currentClockIndex = tRecyclerView.getChildAdapterPosition(aView)
            startActivity(Intent(application, TimeConfActivity::class.java))
        }

        findViewById(R.id.list_add_button).setOnClickListener {
            ClockList.add(MyClock("新しい時間", MyClock.Ampm.AMPM, 12, 60, 60, true))
            ClockList.currentClockIndex = ClockList.size - 1
            startActivity(Intent(application, TimeConfActivity::class.java))
        }
        val callback = object : SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(aRecycleView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                if (viewHolder != null && target != null) {
                    val fromPos = viewHolder.getAdapterPosition();
                    val toPos = target.getAdapterPosition();
                    ClockList.swap(fromPos, toPos)
                    if (ClockList.currentClockIndex == fromPos) {
                        ClockList.currentClockIndex = toPos
                    } else if (ClockList.currentClockIndex == toPos) {
                        ClockList.currentClockIndex = fromPos
                    }
                    tRecyclerView.adapter.notifyItemMoved(fromPos, toPos);
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                if (viewHolder != null) {
                    val tPosition = viewHolder.adapterPosition
                    alertDeletion(tRecyclerView, tPosition)
                }
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(tRecyclerView)
    }

    private fun alertDeletion(aRecyclerView : RecyclerView, aPosition : Int) {
        val alert = AlertDialog.Builder(this)
        if (ClockList.size == 1) {
            alert.setTitle("もう時間がありません")
            alert.setMessage("時間がもうないので「" + ClockList.get(aPosition).name + "」を消せません")
            alert.setPositiveButton("OK", null)
            aRecyclerView.adapter.notifyItemChanged(aPosition)
        } else {
            alert.setTitle("時間を消そうとしています")
            alert.setMessage("本当に「" + ClockList.get(aPosition).name + "」を消していいですか？")
            alert.setPositiveButton("Yes", { _, _ ->
                ClockList.removeAt(aPosition)
                aRecyclerView.adapter.notifyItemRemoved(aPosition)
                if (ClockList.currentClockIndex > aPosition) {
                    ClockList.currentClockIndex--
                    aRecyclerView.adapter.notifyItemChanged(ClockList.currentClockIndex)
                    aRecyclerView.adapter.notifyItemChanged(ClockList.currentClockIndex + 1)
                } else if (ClockList.currentClockIndex == aPosition) {
                    ClockList.currentClockIndex = 0
                }
            })
            alert.setNegativeButton("No", { _, _ ->
                aRecyclerView.adapter.notifyItemChanged(aPosition)
            })
        }
        alert.show()
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    inner class TimeAdapter(val mRecyclerVIew: RecyclerView, val onItemViewClickListener: (View) -> Unit) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {
        inner class ViewHolder(val mItemView : View) : RecyclerView.ViewHolder(mItemView) {
            val mTextView : TextView by lazy {
                mItemView.findViewById(R.id.time_name) as TextView
            }
            val mSelectCurrentTimeButton : Button by lazy {
                mItemView.findViewById(R.id.select_current_time) as Button
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val tView = LayoutInflater.from(parent!!.context)!!.inflate(R.layout.time_row, parent, false)
            tView.setOnClickListener(onItemViewClickListener)
            return ViewHolder(tView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            if (holder != null) {
                holder.mTextView.setText(ClockList.get(position).name + if (position == ClockList.currentClockIndex) "✔" else "")
                holder.mSelectCurrentTimeButton.setOnClickListener {
                    ClockList.currentClockIndex = mRecyclerVIew.getChildAdapterPosition(holder.mItemView)
                    startActivity(Intent(application, MainActivity::class.java))
                }
            }
        }

        override fun getItemCount() : Int {
            return ClockList.size
        }
    }


}