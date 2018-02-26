package com.kotkot.usuario.noticiasrss

import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.kotkot.usuario.noticiasrss.modelo.RootObject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var urlRSS = ""
    private val rssTOjson = "https://api.rss2json.com/v1/api.json?rss_url="
    private var rssList:ArrayList<String>? = null
    private lateinit var mainMenu:Menu

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        barra.title = "Lector RSS"
        setSupportActionBar(barra)

        val layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recycler.layoutManager = layoutManager

        loadRssArray()

        if(rssList != null)
            updateRssList()
    }

    private fun rssToRecycler()
    {
        val cargarRSS = object:AsyncTask<String, String, String>()
        {
            internal var dialogo = ProgressDialog(this@MainActivity)

            override fun onPreExecute()
            {
                dialogo.setMessage("Cargando RSS")
                dialogo.show()
            }

            override fun onPostExecute(result: String?)
            {
                dialogo.dismiss()
                try
                {
                    var rootObject: RootObject = Gson().fromJson<RootObject>(result, RootObject::class.java)
                    val adaptador = FeederRecyclerAdapter(rootObject, baseContext)
                    recycler.adapter = adaptador
                    adaptador.notifyDataSetChanged()
                }
                catch(e: IllegalStateException)
                {
                    Toast.makeText(this@MainActivity, "Dirección inválida", Toast.LENGTH_LONG).show()

                    rssList!!.remove(urlRSS)
                    if(rssList!!.size == 0)
                    {
                        rssList = null
                        urlRSS = ""
                    }

                    return updateRssEntries()
                }
            }

            override fun doInBackground(vararg p0: String): String {
                val result:String
                val httpCall = HTTPHelper()
                result = httpCall.getFlujoHTTP(p0[0])
                return result
            }
        }

        cargarRSS.execute(rssTOjson + urlRSS)
    }

    private fun addButton()
    {
        val alertBuilder = AlertDialog.Builder(this)
        val alertLayout = LinearLayout(this)
        val textView = TextView(alertLayout.context)
        val editText = EditText(alertLayout.context)

        textView.text = "Introduzca la direccion del RSS que desee añadir"
        editText.setSingleLine(true)
        alertLayout.orientation = LinearLayout.VERTICAL
        alertLayout.addView(textView)
        alertLayout.addView(editText)
        alertLayout.setPadding(50, 40, 50, 10)

        alertBuilder.setView(alertLayout)

        alertBuilder.setNegativeButton("Cancelar", {dialog, which ->
            dialog.cancel()
        })

        alertBuilder.setPositiveButton("Aceptar", {dialog, which ->
            if(rssList == null)
            {
                rssList = ArrayList<String>()
                rssList!!.add(editText.text.toString())
            }
            else
            {
                rssList!!.add(editText.text.toString())
            }

            updateRssEntries()

            dialog.cancel()
        })

        alertBuilder.create().show()
    }

    fun removeButton()
    {
        rssList!!.remove(spinner.selectedItem.toString())
        if(rssList!!.size == 0)
        {
            rssList = null
            urlRSS = ""
        }

        updateRssEntries()
    }

    fun updateRssEntries()
    {
        val sharedPreferences = getSharedPreferences("com.noticiasrss.sharedpref", MODE_PRIVATE)
        val sharedEditor = sharedPreferences.edit()

        if(rssList != null)
        {
            val stringSet = HashSet<String>()
            stringSet.addAll(rssList!!)
            sharedEditor.remove("RSSLIST")
            sharedEditor.putStringSet("RSSLIST", stringSet)
            sharedEditor.commit()

            enableBar()
        }
        else
        {
            sharedEditor.remove("RSSLIST")
            sharedEditor.commit()

            disableBar()
        }

        updateRssList()
    }

    fun updateRssList()
    {
        if(rssList != null)
        {
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, rssList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    urlRSS = rssList!!.get(p2)
                    rssToRecycler()
                }
            }
            adapter.notifyDataSetChanged()
        }
        else
        {
            //val adaptador = FeederRecyclerAdapter(rootObject, baseContext)
            //recycler.adapter = adaptador
            //adaptador.notifyDataSetChanged()

            recycler.adapter = null
            recycler.removeAllViewsInLayout()

            spinner.adapter = null
        }
    }

    fun enableBar()
    {
        spinner.isEnabled = true
        mainMenu.findItem(R.id.eliminar).isEnabled = true
        mainMenu.findItem(R.id.actualizar).isEnabled = true
    }

    fun disableBar()
    {
        spinner.isEnabled = false
        mainMenu.findItem(R.id.eliminar).isEnabled = false
        mainMenu.findItem(R.id.actualizar).isEnabled = false
    }

    fun loadRssArray()
    {
        val sharedPreferences = getSharedPreferences("com.noticiasrss.sharedpref", MODE_PRIVATE)

        if(sharedPreferences.contains("RSSLIST"))
        {
            val rssSet = sharedPreferences.getStringSet("RSSLIST", null)
            rssList = ArrayList<String>()
            rssList!!.addAll(rssSet)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        mainMenu = menu

        if(rssList == null)
        {
            spinner.isEnabled = false
            mainMenu.findItem(R.id.eliminar).isEnabled = false
            mainMenu.findItem(R.id.actualizar).isEnabled = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if(item.itemId == R.id.actualizar)
            rssToRecycler()
        else if(item.itemId == R.id.aniadir)
            addButton()
        else if(item.itemId == R.id.eliminar)
            removeButton()

        return true
    }
}
