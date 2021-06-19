package com.example.kalkulatorkalorii.ui.main


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kalkulatorkalorii.R
import com.example.kalkulatorkalorii.model.db.Database
import com.example.kalkulatorkalorii.model.db.entities.DishEntity
import com.example.kalkulatorkalorii.model.db.MealType
import com.example.kalkulatorkalorii.model.db.entities.HydrationEntity
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val database = Database.database!!
    private var selectedProduct: Int = -1
    private lateinit var inflater: LayoutInflater
    private var meals: MutableList<DishEntity> = mutableListOf()
    private var hydration: HydrationEntity = HydrationEntity(0, 0, Date())
    private lateinit var products: Array<out String>
    private lateinit var productsNames: List<String>
    private val myCalendarChangesObserver = object : CalendarChangesObserver {
        override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
            super.whenSelectionChanged(isSelected, position, date)
            fetchMeals(date)
            updateMeals()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inflater = LayoutInflater.from(this)

        main_single_row_calendar.apply {
            calendarViewManager = CalendarManagers.myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = CalendarManagers.mySelectionManager

            futureDaysCount = 30
            includeCurrentDate = true
            init()
            select(120)
        }

        ivAddBreakfast.setOnClickListener { onAddDishClick(MealType.BREAKFAST) }
        ivAddDinner.setOnClickListener { onAddDishClick(MealType.DINNER) }
        ivAddSupper.setOnClickListener { onAddDishClick(MealType.SUPPER) }
        ivEditHydration.setOnClickListener { showEditHydration() }

        products = resources.getStringArray(R.array.products)
        productsNames = products.map {
            val p = it.split(":")
            "${p[0]} (${p[1]} kcal)"
        }
    }

    private fun onAddDishClick(type: Int) {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("Dodaj produkt/potrawę")
            setView(layoutInflater.inflate(R.layout.dialog_add_dish, null))
            setPositiveButton("Dodaj", null)
            setNegativeButton("Anuluj") { _, _ -> }
        }.create()
        dialog.show()

        val acProducts = dialog.findViewById<AutoCompleteTextView>(R.id.acProducts)
        acProducts?.setAdapter(ArrayAdapter(this, android.R.layout.select_dialog_item, productsNames))
        acProducts?.setOnItemClickListener { _, _, position, _ ->
            val product = acProducts.text.toString()
            selectedProduct = productsNames.indexOf(product)
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val etCount = dialog.findViewById<EditText>(R.id.etDialogDishCount)
            if(acProducts?.text?.isEmpty() == true) {
                acProducts.error = "Proszę wybrać produkt"
                return@setOnClickListener
            }
            if(etCount?.text?.isEmpty() == true) {
                etCount.error = "Ilość produktu musi być większa od 0"
                return@setOnClickListener
            }

            val dish = DishEntity(
                uid = 0,
                name = products[selectedProduct].split(":")[0],
                calories = products[selectedProduct].split(":")[1].toInt(),
                count = etCount?.text?.toString()?.toInt() ?: 1,
                date = main_single_row_calendar.getSelectedDates()[0],
                mealType = type
            )
            selectedProduct = -1

            meals.add(dish)
            database.dishDao().insert(dish)
            updateMeals()

            dialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showEditDishDialog(dish: DishEntity) {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("Edytuj produkt/potrawę")
            setView(layoutInflater.inflate(R.layout.dialog_add_dish, null))
            setPositiveButton("Dodaj", null)
            setNegativeButton("Anuluj") { _, _ -> }
        }.create()
        dialog.show()

        selectedProduct = products.indexOf("${dish.name}:${dish.calories}")

        val acProducts = dialog.findViewById<AutoCompleteTextView>(R.id.acProducts)
        val etCount = dialog.findViewById<EditText>(R.id.etDialogDishCount)

        etCount?.setText(dish.count.toString())
        acProducts?.setAdapter(ArrayAdapter(this, android.R.layout.select_dialog_item, productsNames))
        acProducts?.setText("${dish.name} (${dish.calories} kcal)")
        acProducts?.setOnItemClickListener { _, _, position, _ ->
            val product = acProducts.text.toString()
            selectedProduct = productsNames.indexOf(product)
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            if(acProducts?.text?.isEmpty() == true) {
                acProducts.error = "Proszę wybrać produkt"
                return@setOnClickListener
            }
            if(etCount?.text?.isEmpty() == true || etCount?.text?.equals("0") == true) {
                etCount.error = "Ilość produktu musi być większa od 0"
                return@setOnClickListener
            }

            dish.name = products[selectedProduct].split(":")[0]
            dish.calories = products[selectedProduct].split(":")[1].toInt()
            dish.count = etCount?.text?.toString()?.toInt() ?: 1
            selectedProduct = -1

            database.dishDao().update(dish)
            updateMeals()

            dialog.dismiss()
        }
    }

    private fun fetchMeals(date: Date) {
        val dateFrom = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time
        val dateTo = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time

        meals = database.dishDao().byDate(dateFrom, dateTo).toMutableList()
        val hydrations = database.hydrationDao().byDate(dateFrom, dateTo)
        if(hydrations.isNotEmpty())
            hydration = hydrations[0]
        else {
            val hTemp = HydrationEntity(0, 0, date)
            database.hydrationDao().insert(hTemp)
            hydration = database.hydrationDao().byDate(dateFrom, dateTo)[0]
        }
    }

    private fun showEditHydration() {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("Edytuj nawodnienie")
            setView(layoutInflater.inflate(R.layout.dialog_hydration, null))
            setPositiveButton("Zapisz", null)
            setNegativeButton("Anuluj") { _, _ -> }
        }.create()
        dialog.show()

        val etHydration = dialog.findViewById<EditText>(R.id.etHydration)

        etHydration?.setText(hydration.milliliters.toString())

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if(etHydration?.text?.isEmpty() == true || etHydration?.text?.equals("0") == true) {
                etHydration.error = "Ilość musi być większa od 0"
                return@setOnClickListener
            }

            hydration.milliliters = etHydration?.text?.toString()?.toInt() ?: 0

            database.hydrationDao().update(hydration)
            updateMeals()

            dialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateMeals() {
        llMainBreakfast.removeAllViews()
        llMainDinner.removeAllViews()
        llMainSupper.removeAllViews()
        meals.filter { it.mealType == MealType.BREAKFAST }.forEach { insertDishItem(it, llMainBreakfast) }
        meals.filter { it.mealType == MealType.DINNER }.forEach { insertDishItem(it, llMainDinner) }
        meals.filter { it.mealType == MealType.SUPPER }.forEach { insertDishItem(it, llMainSupper) }

        tvHydration.text = "${hydration.milliliters} ml"
        val sum = meals.sumBy { it.calories * it.count }
        tvCaloriesSum.text = "$sum kcal"
    }

    @SuppressLint("SetTextI18n")
    private fun insertDishItem(dish: DishEntity, viewGroup: ViewGroup) {
        val view = inflater.inflate(R.layout.item_dish, viewGroup, false)
        view.findViewById<TextView>(R.id.tvDishItemName).text = "${dish.name} x ${dish.count}"
        val calories = dish.count * dish.calories
        view.findViewById<TextView>(R.id.tvDishItemCal).text = "$calories kcal"
        view.tag = dish.uid

        view.findViewById<ImageView>(R.id.ivDishDelete).setOnClickListener {
            meals.remove(dish)
            database.dishDao().delete(dish)
            updateMeals()
        }

        view.findViewById<ImageView>(R.id.ivDishEdit).setOnClickListener {
            showEditDishDialog(dish)
        }

        viewGroup.addView(view)
    }
}