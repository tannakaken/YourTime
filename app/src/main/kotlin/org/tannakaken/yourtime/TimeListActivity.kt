package org.tannakaken.yourtime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by kensaku on 2017/06/03.
 */
class TimeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_list)
        val tRecycleView = findViewById(R.id.time_list_view) as RecyclerView
        tRecycleView.setHasFixedSize(true)

        tRecycleView.adapter = TimeAdapter {
            aView : View ->
            val tIntent = Intent(application, TimeConfActivity::class.java)
            tIntent.putExtra("index", tRecycleView.getChildAdapterPosition(aView))
            startActivity(tIntent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    class TimeAdapter(val onItemViewClickListener: (View) -> Unit) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {
        class ViewHolder(aItemView : View) : RecyclerView.ViewHolder(aItemView) {
            val mTextView : TextView by lazy {
                aItemView.findViewById(R.id.time_name) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val tView = LayoutInflater.from(parent!!.context)!!.inflate(R.layout.time_row, parent, false)
            tView.setOnClickListener(onItemViewClickListener)
            return ViewHolder(tView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder!!.mTextView.setText(ClockList.get(position).name)
        }

        override fun getItemCount() : Int {
            return ClockList.size
        }
    }


}