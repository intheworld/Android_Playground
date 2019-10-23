package kt

import android.app.Activity
import android.os.Bundle
import kotlinx.coroutines.*
import win.intheworld.treenodedb.R

class KtActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
    }

    override fun onStart() {
        val singleThreadContext = newSingleThreadContext("singleThread")
        val jobs = List(100) {
            GlobalScope.launch(singleThreadContext) {
                println("in launch start: ${Thread.currentThread().name}")

                withContext(Dispatchers.IO) {
                    Thread.sleep(100)
                    println("in IO: ${Thread.currentThread().name}")
                }

                println("in launch end: ${Thread.currentThread().name}")
            }
        }
        super.onStart()
    }

}