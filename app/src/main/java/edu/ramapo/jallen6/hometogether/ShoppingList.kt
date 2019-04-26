package edu.ramapo.jallen6.hometogether

import android.graphics.Color.WHITE
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import kotlinx.android.synthetic.main.activity_shopping_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Activity logic for the Shopping List Activity
 */
class ShoppingList : AppCompatActivity(), PantryItemCrud {


    /**
     * Implementation of move item
     */
    override fun moveItem(v: AbstractItemView, newLoc: String) {
        try {
            itemManager.moveItem(v, newLoc, this)
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to move item", Toast.LENGTH_SHORT).show()
        }

    }

    override fun deleteItem(v: View?) {
        try {
            itemManager.deleteSelectedFromServer(this)
        } catch (e: IllegalStateException) {
            Toast.makeText(this, "Please select only one item",
                    Toast.LENGTH_SHORT).show()
        }

    }

    override fun updateItem(v: View?) {
        //Should not be callable...
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val itemManager:PantryItemViewManager = PantryItemViewManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        val houseId = intent.getStringExtra(Household.ExtraHouseID)

        createTableHeader()
        val url:String = NetworkManager.getHostAsBuilder().appendPath("household")
                .appendPath("pantry")
                .appendQueryParameter("id", houseId)
                .appendQueryParameter("shopping", "true").toString()

        val request:JsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> {response ->

                    val pantryItems = response.getJSONArray("pantry")
                    val keys = arrayOf(PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD)


                    for(i in 0 until pantryItems.length()){
                        val current = pantryItems.getJSONObject(i)
                        val pantryItemView = ShoppingItemView(PantryItem(current), TableRow(this))
                        pantryItemView.setKeys(keys)
                        pantryItemView.drawToRow()
                       // pantryItemView.addViewToRow(CheckBox(this))
                        shoppingListTable.addView(pantryItemView.displayRow)
                        itemManager.addView(pantryItemView)
                    }

                //Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
                     },
                Response.ErrorListener { Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show() })

        NetworkManager.getInstance(this).addToRequestQueue(request)


    }


    private fun createTableHeader(){
        val row = TableRow(this)
       // row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT );
        row.gravity =CENTER
        row.layoutParams= TableRow.LayoutParams(1);
        val headerText = arrayOf("Name",  "Quantity", "Bought")
        for(i in 0 until headerText.size){
            val header = TextView(this)
            header.text = headerText[i]
            header.gravity = CENTER
            header.typeface = Typeface.DEFAULT_BOLD
            row.addView(header)
        }

        shoppingListTable.addView(row)



    }

    fun completeShopping(v: View){
        for(rowNum in 1 until shoppingListTable.childCount){
            val row:View = shoppingListTable.getChildAt(rowNum)
            when(row){
                is ViewGroup ->{
                    val check:View = row.getChildAt(2)
                    when(check){
                        is CheckBox ->{
                            if(check.isChecked){
                                val name = row.getChildAt(0)
                                when(name){
                                    is TextView -> {
                                        itemManager.moveItem(itemManager.findViewByName(
                                                name.text.toString()), "pantry", this)
                                    }
                                }

                            }
                        }
                    }
                }
            }

        }
    }

}
