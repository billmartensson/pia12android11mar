package se.magictechnology.pia12android11mar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import se.magictechnology.pia12android11mar.ui.theme.Pia12android11marTheme

class MainActivity : ComponentActivity() {

    var trellokey = "AAA"
    var trellotoken = "BBB"

    val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pia12android11marTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }


        getlists()
    }


    fun testapi() {

        val request = Request.Builder()
            .url("https://api-extern.systembolaget.se/site/V2/Search/Site?q=banan")
            .header("Ocp-Apim-Subscription-Key", "abc")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responsetext = response.body!!.string()

                }
            }
        })
    }

    fun getlists() {
        // https://api.trello.com/1/boards/LlZObqEW/lists?key=029b744484b076d4740b381d4a3e83f0&token=ATTA37f695fd6a9e098a2a0e2efb90d6b2033219c731eeb64acfd02ab7352c8f8b1f25925809

        var trellourl = "https://api.trello.com/1/boards/LlZObqEW/lists?key=$trellokey&token=$trellotoken"

        val request = Request.Builder()
            .url(trellourl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responsetext = response.body!!.string()

                    val alllists = Json { ignoreUnknownKeys = true }.decodeFromString<List<Trellolist>>(responsetext)

                    for(tlist in alllists) {
                        Log.i("PIA12DEBUG", tlist.name)
                    }

                    getcards(alllists[0])

                    //addcard(alllists[0], "Kort från app", "Beskrivning här")
                }
            }
        })

    }

    fun getcards(getlist : Trellolist) {
        var trellourl = "https://api.trello.com/1/lists/${getlist.id}/cards?key=$trellokey&token=$trellotoken"

        val request = Request.Builder()
            .url(trellourl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responsetext = response.body!!.string()

                    val allcards = Json { ignoreUnknownKeys = true }.decodeFromString<List<Trellocard>>(responsetext)

                    for(card in allcards) {
                        Log.i("PIA12DEBUG", card.name)
                    }

                    //deletecard(allcards[0])

                }
            }
        })
    }

    fun addcard(addtolist : Trellolist, addname : String, adddescription : String) {

        var trellourl = "https://api.trello.com/1/cards?idList=${addtolist.id}&key=$trellokey&token=$trellotoken&name=$addname&desc=$adddescription"

        val request = Request.Builder()
            .url(trellourl)
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responsetext = response.body!!.string()

                    val addedcard = Json { ignoreUnknownKeys = true }.decodeFromString<Trellocard>(responsetext)




                }
            }
        })
    }

    fun deletecard(card : Trellocard) {
        var trellourl = "https://api.trello.com/1/cards/${card.id}?key=$trellokey&token=$trellotoken"

        val request = Request.Builder()
            .url(trellourl)
            .delete("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responsetext = response.body!!.string()

                }
            }
        })
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Pia12android11marTheme {
        Greeting("Android")
    }
}


@Serializable
data class Trellolist(val id : String, val name : String)

@Serializable
data class Trellocard(val id : String, val name : String, val desc : String)
