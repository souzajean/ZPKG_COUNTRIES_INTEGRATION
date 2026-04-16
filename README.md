# 🚀 ZPKG_COUNTRIES_INTEGRATION
## SAP BTP Integration Suite (CPI) – Integração com Discord via Webhook 

<br> 

## 🎯 Objetivo

Este iFlow tem como objetivo demonstrar uma integração simples, eficiente e reutilizável utilizando o SAP BTP CPI, permitindo o envio de mensagens automáticas para canais do Discord via Webhook.

A solução pode ser utilizada como base para:

- Monitoramento de integrações
- Notificações de erro/sucesso
- Alertas operacionais em tempo real
- Integração com sistemas externos

<br>

## 🧩 Visão Geral da Solução

A integração expõe um endpoint HTTP que recebe requisições externas e processa uma mensagem que será enviada diretamente para um canal do Discord.

Essa abordagem desacopla sistemas SAP de ferramentas de comunicação modernas, permitindo maior visibilidade operacional.

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
