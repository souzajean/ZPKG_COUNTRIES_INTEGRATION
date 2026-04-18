# 🚀 ZPKG_COUNTRIES_INTEGRATION
## 🚀 SAP BTP CPI | Integração Inteligente com API de Países + Notificação em Tempo Real no Discord

<br> 

## 🎯 Objetivo da solução   

Consumir dados de países a partir de uma API pública e enviar automaticamente essas informações, já tratadas e estruturadas, para um canal do Discord via Webhook.

💡 Mais do que uma simples integração, este iFlow representa um padrão reutilizável para observabilidade, notificações e enriquecimento de dados em integrações SAP.

## 🧩 O que este iFlow faz na prática?

✔ Recebe requisições via endpoint HTTP no SAP CPI  
✔ Consome dinamicamente a API REST de países  
✔ Processa e transforma os dados com Groovy Script  
✔ Enriquece informações (capital, população, moeda, idioma, continente)  
✔ Envia uma mensagem formatada em tempo real para o Discord  

<br>

![Fluxo](imagens/capa-linkedin.png)

---

<br>

# 🏗️ 🔧 Arquitetura do iFlow

<br><br>

# 🔄 1. Fluxo da Integração

<br>

### 🧱 Criando o Package
![Fluxo](imagens/Screenshot_1.png)

<br><br>

### 🏷️ Nome do Package
```
ZPKG_COUNTRIES_INTEGRATION
```
![Fluxo](imagens/Screenshot_2.png)

<br>

### ➕ Adicionando o Artefato
![Fluxo](imagens/Screenshot_3.png)

<br>

### 🏷️ Nome do iFlow
```
IF_SEND_MESSAGE_DISCORD
```
![Fluxo](imagens/Screenshot_4.png)

<br>

### ➕ Adicionando o Adapter
![Fluxo](imagens/Screenshot_5.png)

<br> 

# 🔹 2. HTTPS Sender (Trigger)
```
Endpoint: /discord
```
![Fluxo](imagens/Screenshot_6.png)

# 🔹 3. Content Modifier

### ➕ Adicionando o Content Modifier
![Fluxo](imagens/Screenshot_7.png)

<br>

### 🏷️ Renomeando o Content Modifier
```
Nome: setCountries
```
![Fluxo](imagens/Screenshot_8.png)


<br>

### ⚙️ Configuração do Content Modifier
Message Header
```
| Campo     | Tipo Valor  |    Valor    |      Tipo        |
| --------- | ------------|-------------|------------------|
| Name      | Source Type |Source Value | Data Type        |
| country   | XPath       | /name       | java.lang.String |

```
![Fluxo](imagens/Screenshot_9.png)

<br>

# 🔹 4. Request Reply (Chamada API)

### ➕ Adicionando Request Reply
![Fluxo](imagens/Screenshot_10.png)

<br>

### 🏷️ Renomeando o Request Reply
```
Nome: API_Countries
```
![Fluxo](imagens/Screenshot_11.png)

<br>

### ➕ Adicionando o Adapter
![Fluxo](imagens/Screenshot_12.png)

<br>

### ⚙️ Configuração do Request Reply
Mehod: GET
```
URL: [https://](https://restcountries.com/v3.1/name/${property.country})
```
![Fluxo](imagens/Screenshot_13.png)

<br>

# 🔹 5. Content Modifier

### ➕ Adicionando o Content Modifier
![Fluxo](imagens/Screenshot_14.png)

<br>

### 🏷️ Renomeando o Content Modifier
```
Nome: CM_Payload_Original
```
![Fluxo](imagens/Screenshot_15.png)


<br>

### ⚙️ Configuração do Content Modifier
Message Header
```
|     Campo     |     Tipo    |      Valor       |
| ------------- | ----------- | ---------------- |
|      Name     | Source Type |   Source Value   |
| Content Type  |   Constant  | application/json |

```
![Fluxo](imagens/Screenshot_16.png)

<br>

### ⚙️ Configuração do Content Modifier
Message Body
```
Type: Expression
Body: ${body}
```
![Fluxo](imagens/Screenshot_17.png)

<br>

# 🔹 6. Groovy Script

### ➕ Adicionando Groovy Script
![Fluxo](imagens/Screenshot_18.png)

<br>

### 🏷️ Renomeando o Groovy Script
![Fluxo](imagens/Screenshot_19.png)
```
GS_Countries
```

<br>

### ➕ Adicionando Groovy Script
![Fluxo](imagens/Screenshot_20.png)

<br>

### ➕ Adicionando Groovy Script
![Fluxo](imagens/Screenshot_21.png)
```
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
```

<br>

# 🔹 7. Request Reply (Chamada API)

### ➕ Adicionando Request Reply
![Fluxo](imagens/Screenshot_22.png)

<br>

### 🏷️ Renomeando o Request Reply
```
Nome: API_Discord
```
![Fluxo](imagens/Screenshot_23.png)

<br>

# 🔹 8. Receiver (Chamada API)
### ➕ Adicionando o Receiver
![Fluxo](imagens/Screenshot_24.png)

<br>

### ➕ Adicionando o Adapter Requet Replay
![Fluxo](imagens/Screenshot_25.png)

<br>

### ⚙️ Configuração do Request Reply

Mehod: POST
```
URL: https://discord.com/api/webhooks/{{DISCORD_WEBHOOK_ID}}/{{DISCORD_WEBHOOK_TOKEN}}
```
![Fluxo](imagens/Screenshot_26.png)

<br>

### ⚙️ Configuração da Externilazação
![Fluxo](imagens/Screenshot_27.png)
```
Colar o código do DISCORD_WEBHOOK_ID
```

<br>

### ⚙️ Configuração da Externilazação
![Fluxo](imagens/Screenshot_28.png)
```
Colar o código do DISCORD_WEBHOOK_TOKEN
```

<br>

# 🔹 8. Discord

### ➕ Configurando o Canal
![Fluxo](imagens/Screenshot_29.png)

<br>

### ➡️ Integrações
![Fluxo](imagens/Screenshot_30.png)

<br>

### ⚙️ Configuração do Webhooks 
![Fluxo](imagens/Screenshot_31.png)

<br>

### ➕ Adicionando o Webhooks
![Fluxo](imagens/Screenshot_32.png)

<br>

### 🏷️ Renomeandoo o Webhooks

![Fluxo](imagens/Screenshot_33.png)

<br>

# 🔹 9. Iflow final
### ⚙️ Configuração final do Iflow 
![Fluxo](imagens/Screenshot_34.png)

<br>

# 🔹 10. Postman

### ➕ Enviando o POST
BRASIL
![Fluxo](imagens/Screenshot_35.png)



<br>

### ➕ Enviando o POST
Germany
![Fluxo](imagens/Screenshot_36.png)

<br>

### ➕ Enviando o POST
Africa
![Fluxo](imagens/Screenshot_37.png)

<br>

### 🏷️ Resultado no DISCORD
![Fluxo](imagens/Screenshot_38.png)


<br>
<br>

---

## 📦 Exemplo prático – iFlow para baixar

📦 [Download do iFlow – CPI_ZPKG_COUNTRIES_INTEGRATION](https://github.com/souzajean/ZPKG_COUNTRIES_INTEGRATION/raw/main/Package/IFL_COUNTRIES_INTEGRATION_DISCORD.zip)
