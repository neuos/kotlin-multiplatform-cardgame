import io.ktor.client.*
import io.ktor.client.request.*

class ApiClient(val httpClient: HttpClient, val apiUrl: String) {
    suspend inline fun <reified T> post(endpoint: String, vararg parameters: Parameter): T {
        val response :T= httpClient.post {
            url("$apiUrl/$endpoint")
            parameters.forEach {
                parameter(it.key, it.value)
            }
        }
        console.log(response)
        return response
    }

    class Parameter(val key: String, val value: String)
}
