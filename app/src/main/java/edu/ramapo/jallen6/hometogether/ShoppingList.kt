package edu.ramapo.jallen6.hometogether

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import kotlinx.android.synthetic.main.activity_shopping_list.*
import org.json.JSONArray
import org.json.JSONObject


class ShoppingList : AppCompatActivity() {

    val itemManager:PantryItemViewManager = PantryItemViewManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        val houseId = intent.getStringExtra(Household.ExtraHouseID)
        val textView:TextView = TextView(this)
        textView.text = houseId
        shoppingListTable.addView(textView)

        val url:String = NetworkManager.getHostAsBuilder().appendPath("household")
                .appendPath("pantry")
                .appendQueryParameter("id", houseId)
                .appendQueryParameter("shopping", "true").toString()

        val request:JsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> {response ->

                    val pantryItems = response.getJSONArray("pantry")
                    val keys = arrayOf(PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD)
                    //TODO: TableRow Header


                    for(i in 0 until pantryItems.length()){
                        val current = pantryItems.getJSONObject(i)
                        val pantryItemView = PantryItemView(PantryItem(current), TableRow(this))
                        pantryItemView.setKeys(keys)
                        shoppingListTable.addView(pantryItemView.displayRow)
                        itemManager.addView(pantryItemView)
                    }

                //Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
                     },
                Response.ErrorListener { Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show() })

        NetworkManager.getInstance(this).addToRequestQueue(request)


    }



}
