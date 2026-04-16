import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) {

    def body = message.getBody(String)
    def json = new JsonSlurper().parseText(body)
    def country = json[0]

    // Dados básicos
    def nomePais   = country.name?.common ?: "N/A"
    def capital    = country.capital ? country.capital[0] : "N/A"
    def population = country.population ?: "N/A"
    def idioma     = country.languages ? country.languages.values().toList()[0] : "N/A"
    def continente = country.continents ? country.continents[0] : "N/A"

    // 🔥 Moeda dinâmica (funciona para qualquer país)
    def currencyEntry = country.currencies ? country.currencies.entrySet().toList()[0] : null

    def moedaCodigo = currencyEntry?.key ?: ""
    def moedaSymbol = currencyEntry?.value?.symbol ?: ""
    def moedaName   = currencyEntry?.value?.name ?: ""

    // Monta mensagem para Discord
    def output = [
        content: "🌎 País: ${nomePais}\n" +
                 "🏛️ Capital: ${capital}\n" +
                 "👥 População: ${population}\n" +
                 "💰 Moeda: ${moedaCodigo} - ${moedaSymbol} (${moedaName})\n" +
                 "🗣️ Idioma: ${idioma}\n" +
                 "🌍 Continente: ${continente}"
    ]

    def jsonOutput = JsonOutput.toJson(output)
    message.setBody(jsonOutput)

    return message
}
