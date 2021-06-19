package com.example.kalkulatorkalorii.ui.main

import com.example.kalkulatorkalorii.R
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.util.*

object CalendarManagers {

    val myCalendarViewManager = object : CalendarViewManager {
        override fun setCalendarViewResourceId(position: Int, date: Date, isSelected: Boolean): Int {
            return if(isSelected)
                R.layout.calendar_item_selected
            else
                R.layout.calendar_item
        }

        override fun bindDataToCalendarView(
            holder: SingleRowCalendarAdapter.CalendarViewHolder, date: Date, position: Int, isSelected: Boolean) {
            holder.itemView.tvCalendarDay.text = DateUtils.getDayNumber(date)
            holder.itemView.tvCalendarMonth.text = DateUtils.getMonthName(date)
            holder.itemView.tvCalendarDayName.text = DateUtils.getDayName(date)
        }
    }

    val mySelectionManager = object : CalendarSelectionManager {
        override fun canBeItemSelected(position: Int, date: Date): Boolean {
            return true
        }
    }
}