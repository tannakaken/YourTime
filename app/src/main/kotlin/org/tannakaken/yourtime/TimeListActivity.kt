package org.tannakaken.yourtime

import android.content.Context
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
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button


/**
 * Created by kensaku on 2017/06/03.
 * 時計システムをリスト表示するための[AppCompatActivity]。
 * 右上のボタンで時計システムを追加。
 * リストの選択ボタンを押して[MainActivity]へ。
 * リストをクリックして[TimeConfActivity]へ。
 * リストのアイテムをドラッグして順番を変え、左右にスワイプして削除。
 */
class TimeListActivity : AppCompatActivity() {
    /**
     * @param aSavedInstanceState [AppCompatActivity]が再表示されたときに、以前の状態を復元するための情報
     */
    override fun onCreate(aSavedInstanceState: Bundle?) {
        super.onCreate(aSavedInstanceState)
        setContentView(R.layout.activity_time_list)
        val tRecyclerView = findViewById(R.id.time_list_view) as RecyclerView
        // ブロックでリストがクリックされたときの挙動を定めている
        tRecyclerView.adapter = TimeAdapter(tRecyclerView) {
            aView : View ->
            ClockList.currentClockIndex = tRecyclerView.getChildAdapterPosition(aView)
            startActivity(Intent(application, TimeConfActivity::class.java))
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }
        // 画面左上の時間システム追加ボタンを押したときの挙動を定めている。
        findViewById(R.id.list_add_button).setOnClickListener {
            ClockList.add(MyClock("新しい時間", MyClock.Ampm.AMPM, 12, 60, 60, true, true))
            ClockList.currentClockIndex = ClockList.size - 1
            startActivity(Intent(application, TimeConfActivity::class.java))
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }
        // リストがスワイプやドラッグされたときの挙動を定めている。
        val tCallback = object : SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            // ドラッグでリストの中の位置を交換する。機体の戻るボタンで戻ったときは以前選択した時間システムに戻る。すなわち現在選択中の時計システムが移動しても選択中のままである。
            override fun onMove(aRecycleView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                if (viewHolder != null && target != null) {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    ClockList.swap(fromPos, toPos)
                    if (ClockList.currentClockIndex == fromPos) {
                        ClockList.currentClockIndex = toPos
                    } else if (ClockList.currentClockIndex == toPos) {
                        ClockList.currentClockIndex = fromPos
                    }
                    tRecyclerView.adapter.notifyItemMoved(fromPos, toPos)
                }
                return true
            }
            // スワイプで削除。
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                if (viewHolder != null) {
                    val tPosition = viewHolder.adapterPosition
                    alertDeletion(tRecyclerView, tPosition)
                }
            }
        }
        ItemTouchHelper(tCallback).attachToRecyclerView(tRecyclerView)
        tRecyclerView.addItemDecoration(DividerItemDecoration(this, (tRecyclerView.layoutManager as LinearLayoutManager).orientation))
    }

    /**
     * スワイプで時計システムの削除をしたとき、アラートダイアログを表示して削除するかどうか選ばせ、もしYesが選択されたら[RecyclerView]にデータの削除を通知する。
     * @param aRecyclerView データの削除を通知するための[RecyclerView]
     * @param aPosition スワイプされたリストの位置
     */
    private fun alertDeletion(aRecyclerView : RecyclerView, aPosition : Int) {
        val alert = AlertDialog.Builder(this)
        // 一つしか時計システムがないときは削除できない。
        if (ClockList.size == 1) {
            alert.setTitle("もう時間がありません")
            alert.setMessage("時間がもうないので「" + ClockList[aPosition].name + "」を消せません")
            alert.setPositiveButton("OK", null)
            aRecyclerView.adapter.notifyItemChanged(aPosition)
        } else {
            alert.setTitle("時間を消そうとしています")
            alert.setMessage("本当に「" + ClockList[aPosition].name + "」を消していいですか？")
            alert.setPositiveButton("Yes", { _, _ ->
                ClockList.removeAt(aPosition)
                aRecyclerView.adapter.notifyItemRemoved(aPosition)
                // 直前に選択していた時計システムが削除されると、先頭の時計システムが選択される。
                if (ClockList.currentClockIndex > aPosition) {
                    ClockList.currentClockIndex--
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

    /**
     * 非表示に映るときに設定の変化を記録する。
     */
    override fun onPause() {
        super.onPause()
        ClockList.save(this)
    }

    /**
     * 設定が変化した時のために、毎回表示前に再生成
     */
    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    /**
     * リスト表示をコントロールするアダプター
     * @property mRecyclerVIew リストを表示するための[RecyclerView]
     * @property mOnItemViewClickListener リストがクリックされたときの挙動を定めるリスナー
     */
    inner class TimeAdapter(val mRecyclerVIew: RecyclerView, val mOnItemViewClickListener: (View) -> Unit) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {
        /**
         * @property mItemView リストの中身の[View]
         */
        inner class ViewHolder(val mItemView : View) : RecyclerView.ViewHolder(mItemView) {
            /**
             * 時計システムの名前を表す文字列
             */
            val mTextView : TextView by lazy {
                mItemView.findViewById(R.id.time_name) as TextView
            }
            /**
             * 時計を表示するためのボタン
             */
            val mSelectCurrentTimeButton : Button by lazy {
                mItemView.findViewById(R.id.select_current_time) as Button
            }
        }

        /**
         * 表示時に[ViewHolder]を作るときの挙動
         * @param aParent [Context]を取得するためだけに使う
         * @param aViewType 今回は使わない
         */
        override fun onCreateViewHolder(aParent: ViewGroup?, aViewType: Int): ViewHolder {
            val tView = LayoutInflater.from(aParent?.context).inflate(R.layout.time_row, aParent, false)
            tView.setOnClickListener(mOnItemViewClickListener)
            return ViewHolder(tView)
        }

        /**
         * [ViewHolder]が表示されるときの挙動
         * @param aHolder 表示される[ViewHolder]
         * @param aPosition 表示される場所
         */
        override fun onBindViewHolder(aHolder: ViewHolder?, aPosition: Int) {
            aHolder ?: return
            val tText = ClockList[aPosition].name + if (aPosition == ClockList.currentClockIndex) "✔" else ""
            aHolder.mTextView.text = tText
            aHolder.mSelectCurrentTimeButton.setOnClickListener {
                ClockList.currentClockIndex = mRecyclerVIew.getChildAdapterPosition(aHolder.mItemView)
                startActivity(Intent(application, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
            }
        }

        override fun getItemCount() : Int {
            return ClockList.size
        }
    }

}